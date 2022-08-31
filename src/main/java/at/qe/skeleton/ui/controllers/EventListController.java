package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Scope("view")
public class EventListController {

    @Autowired
     EventService eventService;

    @Autowired
     UserService userService;

    @Autowired
     LocationService locationService;

    @Autowired
    UserVoteLocationService userVoteLocationService;

    @Autowired
    UserVoteTimeslotService userVoteTimeslotService;

    /**Set Services for the JUnit tests
     *
     * @param eventService
     * @param userService
     * @param locationService
     * @param userVoteLocationService
     * @param userVoteTimeslotService
     */
    public void setServices(EventService eventService, UserService userService,
                               LocationService locationService, UserVoteLocationService userVoteLocationService,
                               UserVoteTimeslotService userVoteTimeslotService) {
        this.eventService = eventService;
        this.userService = userService;
        this.locationService = locationService;
        this.userVoteLocationService = userVoteLocationService;
        this.userVoteTimeslotService = userVoteTimeslotService;
    }

    public Collection<User> getUsersExceptCurrent() {

        return userService.getUsersExceptCurrent(userService.getAuthenticatedUser()).stream().filter(User::isEnabled).collect(Collectors.toList());
    }


    /**
    //redudant??
    public Collection<Event> getAllUsersEvents(){
       return this.userService.getAuthenticatedUser().getEvents();
    }
*/

/**Return the Events where that actual user is invited*/

    public Collection<Event> getEventInvitedUsers(){
        return eventService.findEventsByUser(userService.getAuthenticatedUser());
    }

    /**Return all the events where that User has been invited.
     * For the History purpose.
     * @return
     */
    public Collection<Event> getEventInvitedUsersHistory(){
        return eventService.findEventsByUserHistory(userService.getAuthenticatedUser());
    }


    //redudant??
    public Collection<Location> getLocationsFromEvent(Event event){
        return locationService.findLocationsByEvents(event);
    }


    /**Return the selected locations for that Event*/

    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

}