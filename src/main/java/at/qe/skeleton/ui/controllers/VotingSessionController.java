package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import org.primefaces.event.RateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.text.SimpleDateFormat;


@Component
@Scope("view")
public class VotingSessionController {

    @Autowired
    EventService eventService;

    @Autowired
    TimeSlotService timeSlotService;

    @Autowired
    LocationService locationService;

    @Autowired
    UserService userService;

    @Autowired
    UserVoteLocationService userVoteLocationService;

    @Autowired
    UserVoteTimeslotService userVoteTimeslotService;

    private String messageInfo;
    private String facesMessage;
    private final String eventIdString = "eventId";

    /**Set Services for the JUnit tests
     *
     * @param eventService
     * @param timeSlotService
     * @param locationService
     * @param userService
     * @param userVoteLocationService
     * @param userVoteTimeslotService
     */
    public void setServices(EventService eventService, TimeSlotService timeSlotService, LocationService locationService,
                                   UserService userService, UserVoteLocationService userVoteLocationService, UserVoteTimeslotService userVoteTimeslotService) {
        this.eventService = eventService;
        this.timeSlotService = timeSlotService;
        this.locationService = locationService;
        this.userService = userService;
        this.userVoteLocationService = userVoteLocationService;
        this.userVoteTimeslotService = userVoteTimeslotService;
    }

    public String getFacesMessage() {
        return facesMessage;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    private Integer locationRating =0;

    public Integer getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(Integer locationRating) {
        this.locationRating = locationRating;
    }


    private Integer timeslotRating;

    public Integer getTimeslotRating() {
        return timeslotRating;
    }

    public void setTimeslotRating(Integer timeslotRating) {
        this.timeslotRating = timeslotRating;
    }


    /**Method to patch the Rating for the Location on click from the Listener
     *
     * @param event
     * @param location
     * @param rating
     */
    public void patchLocationVoting(Event event,Location location, int rating){

        setLocationRating(rating);

        userVoteLocation.setLocation(location);
        userVoteLocation.setEvent(event);
        userVoteLocation.setVotingCount(getLocationRating());
        userVoteLocation.setUser(userService.getAuthenticatedUser());

        fromDBLocation = userVoteLocationService.findUserVoteLocationByUserAndEventAndLocation(userService.getAuthenticatedUser(), event, location);

        // if voting does not exist yet create a new entry in db - else: only update in the existing row
        if (fromDBLocation == null) {
            userVoteLocationService.voteLocation(userVoteLocation);
        } else {
            userVoteLocationService.patchVoting(fromDBLocation, getLocationRating());
        }

        setLocationRating(0);
    }

    /**Method to reset the Rating for the Location on click from the Listener
     *
     * @param event
     * @param location
     */
    public void patchLocationVotingResetRating(Event event,Location location){

        userVoteLocation.setLocation(location);
        userVoteLocation.setEvent(event);
        userVoteLocation.setVotingCount(0);
        userVoteLocation.setUser(userService.getAuthenticatedUser());

        fromDBLocation = userVoteLocationService.findUserVoteLocationByUserAndEventAndLocation(userService.getAuthenticatedUser(), event, location);

        // if voting does not exist yet create a new entry in db - else: only update in the existing row
        if (fromDBLocation == null) {
            userVoteLocationService.voteLocation(userVoteLocation);
        } else {
            userVoteLocationService.patchVoting(fromDBLocation, 0);
        }

    }

    /**Method to patch the Rating for the Timeslot on click from the Listener
     *
     * @param event
     * @param timeslot
     * @param rating
     */
    public void patchTimeslotVoting(Event event,Timeslot timeslot, int rating){

        setTimeslotRating(rating);

        userVoteTimeslot.setTimeslot(timeslot);
        userVoteTimeslot.setEvent(event);
        userVoteTimeslot.setVotingCount(getTimeslotRating());
        userVoteTimeslot.setUser(userService.getAuthenticatedUser());

        fromDBTimeslot = userVoteTimeslotService.findUserVoteTimeslotByUserAndEventAndTimeslot(userService.getAuthenticatedUser(), event, timeslot);

        // if voting does not exist yet create a new entry in db - else: only update in the existing row
        if (fromDBTimeslot == null) {
            userVoteTimeslotService.voteTimeslot(userVoteTimeslot);
        } else {
            userVoteTimeslotService.patchTimeslotVoting(fromDBTimeslot, getTimeslotRating());
        }

        setTimeslotRating(0);
    }

    /**Method to reset the Rating for the Timeslot on click from the Listener
     *
     * @param event
     * @param timeslot
     */
    public void patchTimeslotVotingResetRating(Event event,Timeslot timeslot){

        userVoteTimeslot.setTimeslot(timeslot);
        userVoteTimeslot.setEvent(event);
        userVoteTimeslot.setVotingCount(0);
        userVoteTimeslot.setUser(userService.getAuthenticatedUser());

        fromDBTimeslot = userVoteTimeslotService.findUserVoteTimeslotByUserAndEventAndTimeslot(userService.getAuthenticatedUser(), event, timeslot);

        // if voting does not exist yet create a new entry in db - else: only update in the existing row
        if (fromDBTimeslot == null) {
            userVoteTimeslotService.voteTimeslot(userVoteTimeslot);
        } else {
            userVoteTimeslotService.patchTimeslotVoting(fromDBTimeslot, 0);
        }

    }





private final UserVoteLocation userVoteLocation = new UserVoteLocation();

  private UserVoteLocation fromDBLocation = new UserVoteLocation();

    /**Listener to catch the rating for the Location and use it for the voting
     *
     * @param rateEvent
     */
    public void onrateLocation(RateEvent<Integer> rateEvent) {

        int locationId =  (Integer) rateEvent.getComponent().getAttributes().get("locationId");
        Location location = locationService.loadLocation(locationId);

        int eventId =  (Integer) rateEvent.getComponent().getAttributes().get(eventIdString);
        Event event = eventService.loadEvent(eventId);

        messageInfo = "You rated:" + rateEvent.getRating();
        facesMessage = location.getLocationName();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,messageInfo , facesMessage));

        patchLocationVoting(event,location,rateEvent.getRating());


    }

    /**Listener to reset the rating for the Location and use it for the voting
     *
     * @param rateEvent
     */
    public void oncancelLocation(AjaxBehaviorEvent rateEvent) {

        int locationId =  (Integer) rateEvent.getComponent().getAttributes().get("locationId");
        Location location = locationService.loadLocation(locationId);

        int eventId =  (Integer) rateEvent.getComponent().getAttributes().get(eventIdString);
        Event event = eventService.loadEvent(eventId);


        facesMessage ="Rate Reset";
        messageInfo="Rank reset for:"
                + "<br/>" + location.getLocationName();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,messageInfo , facesMessage));

        patchLocationVotingResetRating(event,location);

    }


    private final UserVoteTimeslot userVoteTimeslot = new UserVoteTimeslot();

   private UserVoteTimeslot fromDBTimeslot = new UserVoteTimeslot();

    /**Listener to catch the rating for the Timeslot and use it for the voting
     *
     * @param rateEvent
     */
    public void onrateTimeslot(RateEvent<Integer> rateEvent) {

        int timeslotId =  (Integer) rateEvent.getComponent().getAttributes().get("timeslotId");
        Timeslot timeslot = timeSlotService.loadTimeslot(timeslotId);

        int eventId =  (Integer) rateEvent.getComponent().getAttributes().get(eventIdString);
        Event event = eventService.loadEvent(eventId);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyy HH:mm");

        facesMessage =formatter.format(timeslot.getStartTime());
        messageInfo ="You rated:"  + rateEvent.getRating();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,messageInfo, facesMessage));

        patchTimeslotVoting(event,timeslot,rateEvent.getRating());

    }


    /**Listener to reset the rating for the Timeslot and user it for the voting
     *
     * @param rateEvent
     */
    public void oncancelTimeslot(AjaxBehaviorEvent rateEvent) {

        int timeslotId =  (Integer) rateEvent.getComponent().getAttributes().get("timeslotId");
        Timeslot timeslot = timeSlotService.loadTimeslot(timeslotId);

        int eventId =  (Integer) rateEvent.getComponent().getAttributes().get(eventIdString);
        Event event = eventService.loadEvent(eventId);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyy HH:mm");

    messageInfo= "Rate Reset";
        facesMessage ="Rank reset for:"
                + "<br/>" + formatter.format(timeslot.getStartTime());


        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,messageInfo, facesMessage));

        patchTimeslotVotingResetRating(event,timeslot);
    }


}
