package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.EventRepository;
import at.qe.skeleton.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserVoteLocationService userVoteLocationService;

    @Autowired
    private UserVoteTimeslotService userVoteTimeslotService;

    /**
     * Returns a collection of all events.
     *
     * @return Collection of all events
     */

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Collection<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Loads a single event identified by its id.
     *
     * @param id the id to search for
     * @return the event with the given id
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Event loadEvent(int id) {
        Optional<Event> loadedEvent = eventRepository.findById(id);
        return loadedEvent.orElse(null);
    }

    /**
     * Saves the event.
     *
     * @param event the event to save
     * @return the updated event
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Deletes the event.
     *
     * @param event the event to delete
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void delete(Event event) {
        Event event2 = findEventByEventId(event.getEventId());
        event2.getEventLocations().removeAll(event2.getEventLocations());
        eventRepository.save(event2);
        eventRepository.delete(event2);
    }

    public void cancelEvent(Event event) {
        event.setActive(false);
        event.setEventStatus("Canceled");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void cancelEventAdmin(Event event) {
        event.setActive(false);
    }

    /**
     * Adds a new Event to H2
     **/

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void addNewEvent(Event request) {
        Event event = new Event();
        saveEvent(event);
    }

    public Event findEventByEventName(String eventName) {
        return eventRepository.findEventByEventName(eventName);
    }

    /**
     * Method to return all active events by the current logged in user
     */
    public Collection<Event> findEventsByUser(User user) {

        return eventRepository.findAll()
                .stream()
                .filter(Event::isActive)
                .flatMap(event -> event.getUserInvited().stream())
                .filter(userInvited -> Objects.equals(user, userInvited.getUser()))
                .filter(UserInvited::getInvitationAccepted)
                .map(UserInvited::getEvent)
                .collect(Collectors.toList());
    }

    public Collection<Timeslot> findTimeslotsByEvent(Event event) {

        return Objects.requireNonNull(eventRepository.findById(event.getEventId()).orElse(null)).getTimeslots();
    }

    public Collection<Location> findLocationsByEvent(Event event) {

        return Objects.requireNonNull(eventRepository.findById(event.getEventId()).orElse(null)).getEventLocations();
    }

    /**
     * Method to return all events where that current user was invited/participated
     */
    public Collection<Event> findEventsByUserHistory(User user) {

        return eventRepository.findAll()
                .stream()
                .filter(event -> !event.isActive() &&
                        (event.getEventStatus().equals("Expired") || event.getEventStatus().equals("Canceled")) )
                .flatMap(event -> event.getUserInvited().stream())
                .filter(userInvited -> Objects.equals(user, userInvited.getUser()))
                .filter(UserInvited::getInvitationAccepted)
                .map(UserInvited::getEvent)
                .collect(Collectors.toList());
    }

    /*
     * Updates the VotingExpiry
     * */

    public Event patchVotingExpiry(Event request, boolean isActive) {
        Event eventFromDb = eventRepository.findEventByEventId(request.getEventId());
        eventFromDb.setActive(isActive);
       return eventRepository.save(eventFromDb);
    }

    /**
     * Patches the event status
     *
     */
    public Event patchEventStatus(Event request, String evenStatus) {
        Event eventFromDb = eventRepository.findEventByEventId(request.getEventId());
        eventFromDb.setEventStatus(evenStatus);
      return   eventRepository.save(eventFromDb);
    }


    /**
     * Return the winning timeslot of the specified event
     *
     */
    public String getMostVotedTimeslot(Event event) {
        List<Integer> allTotalVotings = new ArrayList<>();
        List<Timeslot> mostVotedTimeslots = new ArrayList<>();

        int totalVotingPerTimeslot, maxTotalVoting;

        for (Timeslot timeslot : event.getTimeslots()) {
            totalVotingPerTimeslot = userVoteTimeslotService.getTotalVoting(event, timeslot);

            allTotalVotings.add(totalVotingPerTimeslot);
        }

        if (!allTotalVotings.isEmpty()) {
            maxTotalVoting = Collections.max(allTotalVotings);

            SimpleDateFormat dayFormatter = new SimpleDateFormat("dd.MM.yyy");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

            mostVotedTimeslots = event.getTimeslots().stream()
                    .filter(timeslot -> maxTotalVoting == userVoteTimeslotService.getTotalVoting(event, timeslot))
                    .collect(Collectors.toList());

            Optional<Timeslot> timeslot = mostVotedTimeslots.stream()
                    .findFirst();

            if (timeslot.isPresent()) {
                String day = dayFormatter.format(timeslot.get().getStartTime());
                String startTime = timeFormatter.format(timeslot.get().getStartTime());
                String endTime = timeFormatter.format(timeslot.get().getEndTime());
                return day + "\n From: " + startTime + "\n To: " + endTime;
            } else {
                return " ";
            }
        } else {
            return " ";
        }
    }

    /**
     * Returns the winning location and gets
     *
     */
    public String getMostVotedLocation(Event event) {
        List<Integer> allTotalVotings = new ArrayList<>();
        List<Location> mostVotedLocations = new ArrayList<>();
        int locationId;
        String statusActive = "Active";

        int totalVotingPerLocation, maxTotalVoting;

        for (Location location : event.getEventLocations()) {
            if (Objects.equals(location.getLocationStatus(), statusActive)) {
                totalVotingPerLocation = userVoteLocationService.getTotalVoting(event, location);
                allTotalVotings.add(totalVotingPerLocation);
            }
        }

        if (!allTotalVotings.isEmpty()) {
            maxTotalVoting = Collections.max(allTotalVotings);

            mostVotedLocations = event.getEventLocations().stream()
                    .filter(location -> maxTotalVoting == userVoteLocationService.getTotalVoting(event, location)
                            && Objects.equals(location.getLocationStatus(), statusActive))
                    .collect(Collectors.toList());

            Optional<Location> loadedLocation = mostVotedLocations.stream()
                    .filter(location -> Objects.equals(location.getLocationStatus(), statusActive))
                    .findFirst();

            if (loadedLocation.isPresent()) {
                locationId = loadedLocation.get().getLocationId();
                event.setWinnerLocationId(locationId);
                eventRepository.save(event);
                return locationRepository.findLocationByLocationId(locationId).getLocationName();
            }else{
                return " ";
            }

        } else {
            return " ";
        }
    }

    /**
     * Return the winning location of the specified event
     *
     */
    public Location getWinnerLocation(Event event) {
        return locationRepository.findLocationByLocationId(event.getWinnerLocationId());
    }

    /**
     * Returns the event results
     *
     */
    public String getVotingResults(Event event) {

        if (!event.isVotingDone() && Objects.equals(event.getEventStatus(), "Expired")) {

            event.setEventLocationWinner(getMostVotedLocation(event));


            event.setEventTimeslotWinner(getMostVotedTimeslot(event));
            event.setVotingDone(true);
        }


        if ((Objects.equals(event.getEventLocationWinner(), null) || Objects.equals(event.getEventLocationWinner(), " "))) {
            return "No results found.";
        }

        return event.getEventLocationWinner() + "\n \n" + event.getEventTimeslotWinner();

    }

    public Event findEventByEventId(int id) {
        return eventRepository.findEventByEventId(id);
    }


}