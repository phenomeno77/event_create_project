package at.qe.skeleton.services;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.repositories.OpeningHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Service for accessing and manipulating opening hours data.
 */

@Service
public class OpeningHoursService {
    @Autowired
    private OpeningHoursRepository openingHoursRepository;
    @Autowired
    private UserService userService;

    /**
     * Returns a collection of all opening Hours.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER' )")
    public Collection<OpeningHours> getAllOpeningHours() {
        return openingHoursRepository.findAll();
    }

    /**
     * Loads a single opening hour identified by its id.
     *
     * @param id the id to search for
     * @return the timeslot with the given id, if no such timeslot null
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public OpeningHours loadOpeningHours(int id) {
        Optional<OpeningHours> loadedOpeningHours = openingHoursRepository.findById(id);
        return loadedOpeningHours.orElse(null);
    }

    /**
     * Saves the opening hour.
     *
     * @param openingHours the timeslot to save
     * @return the updated openingHour
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public OpeningHours saveOpeningHours(OpeningHours openingHours) {
        return openingHoursRepository.save(openingHours);
    }

    /**
     * Deletes the opening hour.
     *
     * @param openingHours the timeslot to delete
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void delete(OpeningHours openingHours) {
        openingHoursRepository.delete(openingHours);
    }

    /**
     * Adds a new opening hour timeslot to H2
     **/
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public OpeningHours addNewOpeningHours(OpeningHours request) {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpeningHoursId(request.getOpeningHoursId());
        openingHours.setUser(userService.getAuthenticatedUser());
        openingHours.setLocation(request.getLocation());
        openingHours.setDay(request.getDay());
        openingHours.setOpeningTime(request.getOpeningTime());
        openingHours.setClosingTime(request.getClosingTime());
        openingHours.setBreakTimeStart(request.getBreakTimeStart());
        openingHours.setBreakTimeEnd(request.getBreakTimeEnd());
        return saveOpeningHours(openingHours);
    }


    public void deleteByLocation(Location location){
        openingHoursRepository.deleteByLocation(location);
    }


}
