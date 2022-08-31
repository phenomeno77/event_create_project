package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import at.qe.skeleton.tasks.VotingExpiryTask;
import at.qe.skeleton.ui.beans.TimeslotsLocationsValidationBean;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.extensions.event.CloseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Scope("view")
public class EventAddController{

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    TaskScheduler taskScheduler;

    private Event event = new Event();

    private String facesMessage;

    private String messageInfo;


    /**=============================================================================
     *                      Setters and getters to create an Event*
     =============================================================================*/
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;

    }

    public String getFacesMessage() {
        return facesMessage;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    /**Set all services for the JUnit tests
     *
     * @param eventService
     * @param userService
     * @param emailService
     * @param taskScheduler
     * @param timeSlotService
     * @param timeslotsLocationsValidationBean
     */
    public void setServices(EventService eventService, UserService userService, EmailService emailService,
                              TaskScheduler taskScheduler,TimeSlotService timeSlotService,
                              TimeslotsLocationsValidationBean timeslotsLocationsValidationBean) {
        this.eventService = eventService;
        this.userService = userService;
        this.emailService = emailService;
        this.taskScheduler = taskScheduler;
        this.timeSlotService = timeSlotService;
        this.timeslotsLocationsValidationBean = timeslotsLocationsValidationBean;
    }
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**=============================================================================
     *                                    END
     =============================================================================*/

    /**=============================================================================
     *      Auto-initialze the user who created the event, as Event Creator
     * =============================================================================*/

    /**Attribute to assign event creator with PostConstruct bean*/
    private User eventCreator;

    /**Initialize the current user who creates the event as event creator*/
    @PostConstruct
    public void initEventCreator(){
        this.eventCreator = this.userService.getAuthenticatedUser();
        event.setUser(eventCreator);

}

    public User getEventCreator(){
        return eventCreator;
    }

    public void setEventCreator(User eventCreator) {
        this.eventCreator = eventCreator;
    }

    /**=============================================================================
     *                                     END-AUTO INIT EVENT CREATOR
     * =============================================================================*/

    /**=============================================================================
     *             Method to proceed to next tab, Wizards default next button
     * =============================================================================*/
    /**Method to process the Wizard Framework. Validations on selected Locations and Invited Users
     * as well. If the location list or the user invited list are empty, uppon pressing the Next button,
     * the process to the next page will not happen.*/

    public String onFlowProcess(FlowEvent event) throws ParseException {

        String dateTimeslots = "dateTimeslots";
        String locations = "locations";

        if(event.getNewStep().equals(locations) && event.getOldStep().equals(dateTimeslots)){
            doCombineDateTime();

            if(!timeSlotsNotEmpty){
                facesMessage = "Invalid Timeslots! Please check your input again.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        facesMessage,null));

                return dateTimeslots;
            }

        }


        if(event.getNewStep().equals(locations) && event.getOldStep().equals(dateTimeslots) && !locationsSelected.isEmpty()){
            setTimeSlotsNotEmpty(false);

            PrimeFaces.current().executeScript("PF('locations').unselectAllRows()");
        }

        if(event.getNewStep().equals("inviteUsers") && locationsSelected.isEmpty()){

            messageInfo = "Empty Location List";
            facesMessage = "In order to continue, please choose at least one Location from the list bellow.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageInfo,facesMessage));
            return locations;
        }

        if(event.getNewStep().equals("votingExpiryDate") && usersSelected.isEmpty()){

            messageInfo = "Empty Location List";
            facesMessage = "In order to continue, please invite at least one User from the list bellow.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageInfo,facesMessage ));
            return "inviteUsers";
        }

        return event.getNewStep();
    }
    /**=============================================================================
     *                                      END-FLOW LISTENER
     * =============================================================================*/


    /**=================================================================================
     *          METHODS TO COMBINE DATE WITH TIME IN DATE FOR THE TIMESLOTS
     *
     * ================================================================================*/
    @Autowired
    TimeSlotService timeSlotService;

    private boolean timeSlotsNotEmpty;

    public boolean isTimeSlotsNotEmpty() {
        return timeSlotsNotEmpty;
    }

    public void setTimeSlotsNotEmpty(boolean timeSlotsNotEmpty) {
        this.timeSlotsNotEmpty = timeSlotsNotEmpty;
    }

    private List<Timeslot> combinedTimeslotsList = new ArrayList<>();

    public List<Timeslot> getCombinedTimeslotsList() {
        return combinedTimeslotsList;
    }

    /**Method to combine each selected day with all times selected*/
    public void doCombineDateTime() throws ParseException {

        final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat dayFormatter = new SimpleDateFormat("dd.MM.yyy");
        final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyy HH:mm");

        combinedTimeslotsList.clear();//clear the input each time user changes the timeslots before selecting locations

        for(Date day : this.selectedDays){
            for(Timeslot timeslot : selectedTimeslotList){
                if(timeslot.getStartTime() != null && timeslot.getEndTime() !=null) {

                        Timeslot combinedTimeslot = new Timeslot();

                        Date timeFrom = timeslot.getStartTime();
                        Date timeTo = timeslot.getEndTime();

                        combinedTimeslot.setStartTime(dateTimeFormatter.parse(dayFormatter.format(day) + " " + timeFormatter.format(timeFrom)));
                        combinedTimeslot.setEndTime(dateTimeFormatter.parse(dayFormatter.format(day) + " " + timeFormatter.format(timeTo)));

                        combinedTimeslotsList.add(combinedTimeslot);

                        setTimeSlotsNotEmpty(true);

                }else if((timeslot.getStartTime() != null && timeslot.getEndTime() ==null) ||
                        (timeslot.getStartTime() == null && timeslot.getEndTime() !=null) ){
                    setTimeSlotsNotEmpty(false);
                }
            }
        }
    }


        /**=============================================================================
         *                                  END-TAB SELECT DATES & TIMES - TIMESLOTS
         * =============================================================================*/


    /**=================================================================================
     *          METHODS, SETTERS & GETTERS TO SET UP TIMES FROM - TO
     *          TAB TO SELECT TIMESLOTS
     * ================================================================================*/

    /**Initializer to fill the array of max hours so that the user can select a time from
     * 00 - 23 at the first time. then the listener will adapt the min-max hour.
     */


    private List<Date> selectedDays = new ArrayList<>();

    /**Getter & setter for the selected Days, DatePicker multiple selection*/
    public List<Date> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<Date> selectedDays) {
        this.selectedDays = selectedDays;
    }

    @PostConstruct
    public void initTimeslots(){
        selectedTimeslotList = new ArrayList<>();

        for(int i=0; i<3; i++){
            maxHour.add(i,23);
            maxMinute.add(i,55);
            minHour.add(i,0);
            minMinute.add(i,0);
            selectedTimeslotList.add(new Timeslot());
        }
    }

    private List<Timeslot> selectedTimeslotList;

    public List<Timeslot> getSelectedTimeslotList() {
        return selectedTimeslotList;
    }

    public void setSelectedTimeslotList(List<Timeslot> selectedTimeslotList) {
        this.selectedTimeslotList = selectedTimeslotList;
    }


    /**Setter & getter min,max hour,minute and minute to the timeFrom, so that the user cannot pick
     * a timeTo lower than timeFrom.
     * @return
     */

    private List<Integer> maxHour = new ArrayList<>();
    private List<Integer> maxMinute = new ArrayList<>();
    private List<Integer> minHour = new ArrayList<>();
    private  List<Integer> minMinute = new ArrayList<>();

    public List<Integer> getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(List<Integer> maxHour) {
        this.maxHour = maxHour;
    }

    public List<Integer> getMinHour() {
        return minHour;
    }

    public void setMinHour(List<Integer> minHour) {
        this.minHour = minHour;
    }

    public List<Integer> getMaxMinute() {
        return maxMinute;
    }

    public void setMaxMinute(List<Integer> maxMinute) {
        this.maxMinute = maxMinute;
    }

    public List<Integer> getMinMinute() {
        return minMinute;
    }

    public void setMinMinute(List<Integer> minMinute) {
        this.minMinute = minMinute;
    }

    /**Ajax listener which on select a time From or To adapts the min,max hours and minutes.
     * If the User e.g. choose time from 11:00 o'clock, then the min hour to select a time To
     * will be the time From plus 1 hour.
     * @return
     */
    final Calendar calendar = Calendar.getInstance();

    public void timeSelectListenerFrom(final CloseEvent closeEvent) {

        int index = Integer.parseInt(closeEvent.getComponent().getAttributes().get("index").toString());

        if(getSelectedTimeslotList().get(index).getStartTime() != null) {

                calendar.setTime(getSelectedTimeslotList().get(index).getStartTime());
                calendar.add(Calendar.MINUTE, 60);
                getMinHour().set(index, calendar.get(Calendar.HOUR_OF_DAY));
                getMinMinute().set(index, calendar.get(Calendar.MINUTE));
        }
        if(getSelectedTimeslotList().get(index).getStartTime() == null) {

            getMinHour().set(index, 0);
            getMinMinute().set(index, 0);
        }
    }
    /**The same like above, but if the User first selects the time To the ajax listener
     * will adapt the max time for the time From to the time To minus 1 hour.
     * So that if the user e.g choose time To to be 12:00 o'clock, the max time From to choose
     * will be set to 11:00, so that the user cannot select time From 12:00 and time To 10:00.
     * @return
     * */
    public void timeSelectListenerTo(final CloseEvent closeEvent) {

        int index = Integer.parseInt(closeEvent.getComponent().getAttributes().get("index").toString());

        if(getSelectedTimeslotList().get(index).getEndTime() != null) {

                calendar.setTime(getSelectedTimeslotList().get(index).getEndTime());
                calendar.add(Calendar.MINUTE, -60);
                getMaxHour().set(index, calendar.get(Calendar.HOUR_OF_DAY));
                getMaxMinute().set(index, calendar.get(Calendar.MINUTE));
            }
        if(getSelectedTimeslotList().get(index).getEndTime() == null) {

            getMaxHour().set(index, 23);
            getMaxMinute().set(index, 55);
        }


    }

    /**Method to create a nice output for all common valid timeslots.
     * Rendering in the Location Select Tab*/
    public Set<String> getFormattedDateTime(){

        return getFormattedDateTime(validCommonDateTimes);
    }



    private Set<String> getFormattedDateTime(final List<Timeslot> validCommonDateTimes) {
        Set<String> dateTimesString = new HashSet<>();

        final SimpleDateFormat dayFormatter = new SimpleDateFormat("dd.MM.yyy");
        final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        for(Timeslot timeslot : validCommonDateTimes){
           String day =dayFormatter.format( timeslot.getStartTime());
           String startTime = timeFormatter.format(timeslot.getStartTime());
           String endTime = timeFormatter.format(timeslot.getEndTime());


            dateTimesString.add(
                    day + " " + startTime + " - " + endTime + "\n"
            );
        }

        return dateTimesString;
    }

    /**=============================================================================
     *                                  END-TAB TO SELECT TIMESLOTS
     * =============================================================================*/

    /**=============================================================================
     *                     LIST OF ALL AVAILABLE LOCATION FROM THE DATABASE
     *                     ON SELECT OF A LOCATION THE VALIDATOR BEAN WILL
     *                     RETURN ALL COMMON-VALID TIMESLOTS FOR THAT LOCATION
     *                     SELECT LOCATION TAB
     *=============================================================================
     */

    @Autowired
     TimeslotsLocationsValidationBean timeslotsLocationsValidationBean;

    private List<Timeslot> validCommonDateTimes = new ArrayList<>();

    public List<Timeslot> getValidCommonDateTimes() {
        return validCommonDateTimes;
    }


    /**Every time the User selects or unselects a Location, the validation bean will be called to validate
     * all locations selected with the selected timeslots.
     */
    public void onRowSelectUnselect(){
        validCommonDateTimes = this.timeslotsLocationsValidationBean.validateEachLocationTimeslot(getLocationsSelected(), getCombinedTimeslotsList());
    }

    private List<Location> locationsSelected = new ArrayList<>();

    public List<Location> getLocationsSelected() {

        return locationsSelected;
    }


    public void setLocationsSelected(List<Location> locationsSelected) {
        this.locationsSelected = locationsSelected;
        this.event.setEventLocations(new HashSet<>(locationsSelected));
    }
    /**=============================================================================
     *                                      END-TAB SELECT LOCATIONS
     * =============================================================================*/

    /**=================================================================================
     *          METHODS, SETTERS & GETTERS TO SET UP EXPIRY DATE FOR EVENT
     *          EXPIRY DATE TAB
     * ================================================================================*/
    private LocalDateTime expiryDateTime;
    long oneDay = 24 * 90 * 60 * (long)1000;
    private Date minDate = new Date(new Date().getTime() + oneDay);



    /**Getter & setter to set min and max date*/

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxExpiryDate() {


            return Objects.requireNonNull(getCombinedTimeslotsList().stream().min(Comparator.naturalOrder()).orElse(null)).getStartTime();

    }

    private Date minDateExpiry = new Date(new Date().getTime());

    /**Voting Date Expiry Date and Time*/
    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
        event.setVotingExpiry(java.sql.Timestamp.valueOf(expiryDateTime));
    }


    public Date getMinDateExpiry() {
        return minDateExpiry;
    }

    public void setMinDateExpiry(Date minDateExpiry) {
        this.minDateExpiry = minDateExpiry;
    }

    /**Methods to format Date and Time*/

    public String getFormattedExpiryDateTime(){
        return getFormattedExpiryDateTime(expiryDateTime);
    }


    private String getFormattedExpiryDateTime(final LocalDateTime time) {
        if (time == null) {
            return null;
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return formatter.format(time);
    }

    /**=============================================================================
     *                              END - EXPIRY DATE TAB
     =============================================================================*/


    /**==============================================================================
     *                      METHODS TO CREATE AND SAVE USER_INVITED AND TIMESLOTS AFTER
     *                      EVENT CREATING IS FINISHED AND SETTING THE TO THE EVENT
     *===============================================================================
     */
    private List<User> usersSelected;

    /**Methods to set and get the invited users.
     * Then set the invited users into the UserInvited Set.
     * @return
     */


    public List<User> getUsersSelected() {
        return usersSelected;
    }

    public void setUsersSelected(List<User> usersSelected) {

        this.usersSelected = usersSelected;
    }

    private final Set<UserInvited> usersInvited = new HashSet<>();

    private void doCreateUsersInvited(){
        UserInvited userInvited;

        for (User user : usersSelected) {
            userInvited = new UserInvited();
            userInvited.setUser(user);
            userInvited.setEvent(this.event);
            usersInvited.add(userInvited);
        }
        /**
         * Set the actual logged in user as Event Creator in Event class
         * Set all invited users to the List of UserInvited.
         * Put event creator also into the list.
         */

        userInvited = new UserInvited();
        userInvited.setUser(eventCreator);
        userInvited.setEvent(this.event);
        userInvited.setInvitationAccepted(true);
        usersInvited.add(userInvited);

        this.event.setUserInvited(usersInvited);
    }

    /**Method to create and save the timeslots in the database on finishing event*/
    private void doCreateTimeslots(){
        Set<Timeslot> timeslotSet = new HashSet<>();

        for(Timeslot timeslot : getValidCommonDateTimes()){
            timeslot = this.timeSlotService.saveTimeslot(timeslot);
            timeslotSet.add(timeslot);
        }

        this.event.setTimeslots(timeslotSet);
    }

    /**=============================================================================
     *                              END-CREATE/SAVE USER_INVITED AND TIMESLOTS
     =============================================================================*/

    /**=============================================================================
     *                        Method to save Event over EventService and Send emails
     * =============================================================================*/
    public void doSaveEvent() throws IOException {

        /**iff there exists at least one common-valid timeslot the button will proceed to save event*/
        if(validCommonDateTimes.isEmpty()){
            messageInfo="Timeslots not Available";
            facesMessage ="Dear User. The Timeslots you chosen are not available. " +
                    "In order to create a successfully event, please select at least one valid Timeslot.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageInfo,facesMessage ));
        }
        else {

            /**iff at least one timeslot exists, create user_invited and timeslots.
             * that make sense before saving data in the database permanently.
             */
            doCreateUsersInvited();

            doCreateTimeslots();

           this.event = this.eventService.saveEvent(event);

            scheduleVotingExpiry();
            sendInvitation();

            FacesContext.getCurrentInstance().getExternalContext().redirect("/events/eventsHomepage.xhtml");
        }
    }

    public void sendInvitation() {
        List<User> invitedUsers = getUsersSelected();
        for (User user : invitedUsers) {
            this.emailService.sendMail(user, this.event);
        }
    }

    public void scheduleVotingExpiry(){
        VotingExpiryTask task = new VotingExpiryTask(event, this.eventService, getUsersSelected(), this.emailService);
        taskScheduler.schedule(task, event.getVotingExpiry());
    }

/**=============================================================================
 *                                          END
 =============================================================================*/

}