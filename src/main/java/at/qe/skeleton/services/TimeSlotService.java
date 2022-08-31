package at.qe.skeleton.services;

import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.repositories.TimeslotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TimeSlotService {
    @Autowired
    private final TimeslotRepository timeslotRepository;

    public TimeSlotService(TimeslotRepository timeslotRepository) {
        this.timeslotRepository = timeslotRepository;
    }

    public Collection<Timeslot> getAllTimeslots() {
        return timeslotRepository.findAll();
    }

    public Timeslot loadTimeslot(int id) {
        return timeslotRepository.findById(id).orElse(null);
    }

    public Timeslot saveTimeslot(Timeslot timeslot) {
        return timeslotRepository.save(timeslot);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void delete(Timeslot timeslot) {
        timeslotRepository.delete(timeslot);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void addNewTimeslot(Timeslot request) {
        saveTimeslot(request);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public int getVotingCount(Timeslot request) {
        List<Timeslot> timeslots = timeslotRepository.findAll();
        for (Timeslot timeslot : timeslots) {
            if (timeslot.equals(request)) {
                return timeslot.getTimeSlotVotingCount();
            }
        }
        return 0;
    }

    /**
     * Increments the value of voting counts for the given timeslot
     **/
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void incrementVotingCount(Timeslot request) {
        Optional<Timeslot> timeslotFromDb = timeslotRepository.findById(request.getTimeslotId());
        if (timeslotFromDb.isPresent()) {
            Timeslot presentTimeslot = timeslotFromDb.get();
            presentTimeslot.setTimeSlotVotingCount(request.getTimeSlotVotingCount() + 1);
            timeslotRepository.save(presentTimeslot);
        }
    }

    /**
     * Decrements the value of voting counts for the given timeslot
     **/
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void decrementVotingCount(Timeslot request) {
        Optional<Timeslot> timeslotFromDb = timeslotRepository.findById(request.getTimeslotId());
        if (timeslotFromDb.isPresent()) {
            Timeslot presentTimeslot = timeslotFromDb.get();
            presentTimeslot.setTimeSlotVotingCount(request.getTimeSlotVotingCount() - 1);
            timeslotRepository.save(presentTimeslot);
        }
    }

    public Timeslot findTimeslotByTimeslotId(int i) {
        return timeslotRepository.findById(i).orElse(null);
    }
}
