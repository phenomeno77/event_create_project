package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.*;

@Component
@Scope("session")
public class VotingSessionList {

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Autowired
    UserVoteLocationService userVoteLocationService;

    @Autowired
    UserVoteTimeslotService userVoteTimeslotService;

    @Autowired
    LocationService locationService;

    private String messageInfo;
    private String facesMessage;
    private String redirectUrl;

    /**Set Services for the JUnit tests
     *
     * @param eventService
     * @param userService
     * @param userVoteLocationService
     * @param userVoteTimeslotService
     */
    public void setServices(EventService eventService, UserService userService,
                             UserVoteLocationService userVoteLocationService, UserVoteTimeslotService userVoteTimeslotService,LocationService locationService) {
        this.eventService = eventService;
        this.userService = userService;
        this.userVoteLocationService = userVoteLocationService;
        this.userVoteTimeslotService = userVoteTimeslotService;
        this.locationService = locationService;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getFacesMessage() {
        return facesMessage;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    /**Method to get the chosen timeslots
     *
     * @return
     */
    public Collection<Timeslot> getTimeslotsByEvent(){

        return new ArrayList<>(eventService.findTimeslotsByEvent(event));
    }

    /**Method to get the chosen timeslots
     *
     * @return
     */
    public Collection<Location> getLocationsByEvent(){

        return new ArrayList<>(eventService.findLocationsByEvent(event));
    }


    /**Method to get how the current logged in user voted for that Location
     *
     * @param location
     * @return
     */
    public int getCurrentUsersRatingForLocation(Location location) {

        UserVoteLocation fromDBLocation = userVoteLocationService.findUserVoteLocationByUserAndEventAndLocation(userService.getAuthenticatedUser(), event, location);

        if (fromDBLocation == null || !Objects.equals(location.getLocationStatus(), "Active")) {
            return 0;
        } else {
          return fromDBLocation.getVotingCount();
        }

    }

    /**Method to show the overall average voting. Number of users accepted the invitation divided by
     * total number of voting
     * @param location
     * @return
     */
    public int getAverageRatingLocation(Location location) {

        double average = userVoteLocationService.getAverageVoting(event, location);

        if (!Objects.equals(location.getLocationStatus(), "Active")) {
               return 0;
            }else {
            return  (int) (Math.round(average));
        }
    }

    /**Method to get how the current logged in user voted for that timeslot
     *
     * @param timeslot
     * @return
     */

    public int getCurrentUsersRatingForTimeslot(Timeslot timeslot) {

        UserVoteTimeslot fromDBTimeslot = userVoteTimeslotService.findUserVoteTimeslotByUserAndEventAndTimeslot(userService.getAuthenticatedUser(), event, timeslot);

        if (fromDBTimeslot == null) {
            return 0;
        } else {
            return fromDBTimeslot.getVotingCount();
        }

    }

    /**Method to show the overall average voting. Number of users accepted the invitation divided by
     *
     * @param timeslot
     * @return
     */
    public Integer getAverageRatingTimeslot(Timeslot timeslot) {

        double average =  userVoteTimeslotService.getAverageVoting(event,timeslot);

        return (int)(Math.round(average));
    }

    public List<String> getAllUsersVoteForLocation(Location location){

        return userVoteLocationService.getUserLocationVoting(event,location);
    }

    public List<String> getAllUsersVoteForTimeslot(Timeslot timeslot){

        return userVoteTimeslotService.getUserTimeslotVoting(event,timeslot);
    }


    /**Method to Redirect the user to the Voting site of that event.
     *
     * @param pushEvent
     * @throws IOException
     */
    public void pushEventByRedirect(Event pushEvent) throws IOException {

        Date dateNow = new Date();

        if(dateNow.after(pushEvent.getVotingExpiry())){
            messageInfo="Event Expired";
            facesMessage="We're sorry, but that Event has been expired at: " + pushEvent.getVotingExpiry();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageInfo,facesMessage));
            FacesContext.getCurrentInstance().getExternalContext().redirect("/events/eventsHomepage.xhtml");
        }else{
            redirectUrl = "/events/voting.xhtml";
            setEvent(pushEvent);
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirectUrl);
        }
    }


    /**Method to capture the event on click to Vote Now button and pass it to the pushEventByRedirect method
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onVoteClick(ActionEvent actionEvent) throws IOException {

        /**Attribute to capture the eventId and then find that event by its ID
         * and push it to VotingController. The use
         */
        int eventId = (Integer) actionEvent.getComponent().getAttributes().get("eventId");
        Event pushEvent = eventService.loadEvent(eventId);

        pushEventByRedirect(pushEvent);

    }

}
