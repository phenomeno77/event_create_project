package at.qe.skeleton.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.AutoInitEvents;
import at.qe.skeleton.ui.beans.AutoInitEvents2;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import at.qe.skeleton.ui.controllers.EventDetailController;
import at.qe.skeleton.ui.controllers.EventListController;
import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@WebAppConfiguration
@SpringBootTest
public class EventDetailControllerTest {


    public JsfMock jsfMock = new JsfMock();

    @Autowired
    UserService userService;

    @Autowired
    LocationService locationService;

    @Autowired
    UserVoteLocationService userVoteLocationService;

    @Autowired
    UserVoteTimeslotService userVoteTimeslotService;

    @Autowired
    EventService eventService;

    @Autowired
    AutoInitEvents autoInitEvents;

    @Autowired
    AutoInitEvents2 autoInitEvents2;

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    UserInvitedService userInvitedService;

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    SessionInfoBean sessionInfoBean;

    @Autowired
    AutoInitLocations autoInitLocations;

    @Autowired
    TimeSlotService timeSlotService;

    @Autowired
            EmailService emailService;

    EventDetailController controller = new EventDetailController();

    EventListController controllerList = new EventListController();




    @BeforeEach
    public void init() {
        jsfMock.init(applicationContext);

        controller.setServices( eventService,  userInvitedService,
                 userService,  emailService,  taskScheduler,sessionInfoBean);

        controllerList.setServices(eventService, userService, locationService, userVoteLocationService,
                userVoteTimeslotService);


        EmailService emailServiceMocked = Mockito.mock(EmailService.class);
        controller.setEmailService(emailServiceMocked);

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoCancelEventAuthenticatedUser() {
    	//EmailService mockEmailService = Mockito.mock(EmailService.class);
    	//controller.setEmailService(mockEmailService);

        autoInitEvents.initializeEvents();

        Event isActiveEvent = eventService.findEventByEventName("Lunch IT-Department");

        Assertions.assertTrue(isActiveEvent.isActive(),"Event should be active");

        controller.setEvent(isActiveEvent);

        controller.doCancelEvent();

        Assertions.assertEquals("Canceled",controller.getMessageInfo(),"Message canceled will be shown");
        Assertions.assertEquals("Canceled",controller.getEvent().getEventStatus(),"The event status will be set to Canceled");
        Assertions.assertFalse(controller.getEvent().isActive(),"Event set isActive to false");

    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testDoCancelEventAuthenticatedUserEventCreator() {
    	//EmailService mockEmailService = Mockito.mock(EmailService.class);
    	//controller.setEmailService(mockEmailService);

        Event event = new Event();

        User eventCreator = userService.getAuthenticatedUser();

        event.setEventName("Lunch IT-Department");

        Set<UserInvited> usersInvited = new HashSet<>();
        UserInvited userInvited;

        userInvited = new UserInvited();
        userInvited.setUser(eventCreator);
        userInvited.setEvent(event);
        userInvited.setInvitationAccepted(true);
        usersInvited.add(userInvited);

        for(User user : controllerList.getUsersExceptCurrent()){
            userInvited = new UserInvited();
            userInvited.setUser(user);
            userInvited.setEvent(event);
            userInvited.setInvitationAccepted(true);
            usersInvited.add(userInvited);
        }

        event.setUserInvited(usersInvited);

        event.setUser(eventCreator);

        event = this.eventService.saveEvent(event);

        controller.setEvent(event);

        controller.doCancelEvent();

        Assertions.assertFalse(controller.getEvent().isActive(),"Event set isActive to false");
        Assertions.assertEquals("Canceled",controller.getMessageInfo());
        Assertions.assertFalse(controller.getEvent().isActive(),"Event set isActive to false");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testDoEndEventEventAuthenticatedUser() {
    	//EmailService mockEmailService = Mockito.mock(EmailService.class);
    	//controller.setEmailService(mockEmailService);

        Event event = new Event();

        User eventCreator = userService.getAuthenticatedUser();

        event.setEventName("Lunch IT-Department");

        Set<UserInvited> usersInvited = new HashSet<>();
        UserInvited userInvited;

        userInvited = new UserInvited();
        userInvited.setUser(eventCreator);
        userInvited.setEvent(event);
        userInvited.setInvitationAccepted(true);
        usersInvited.add(userInvited);

        for(User user : controllerList.getUsersExceptCurrent()){
            userInvited = new UserInvited();
            userInvited.setUser(user);
            userInvited.setEvent(event);
            userInvited.setInvitationAccepted(true);
            usersInvited.add(userInvited);
        }

        event.setUserInvited(usersInvited);

        event.setUser(eventCreator);

        event = this.eventService.saveEvent(event);

        controller.setEvent(event);

        controller.doEndEvent();

        Assertions.assertEquals("Event Ended",controller.getMessageInfo(),"Event ended message will be shown");
        Assertions.assertEquals("Expired",controller.getEvent().getEventStatus(),"Event status set to Expired");
        Assertions.assertFalse(controller.getEvent().isActive(),"Event set to not active");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetEventResults() throws ParseException {

        autoInitLocations.initializeLocations();

        Event event = new Event();

        User eventCreator = userService.loadUser("user1");

        long oneDay = 24 * 60 * 60 * 1000;

        event.setEventName("Lunch IT-Department");

        String time1 = "24-Jan-2022 16:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);



        Timeslot timeslot;
        Set<Timeslot> selectedTimeslots = new HashSet<>();

            timeslot = new Timeslot();
            timeslot.setStartTime(parsedTime1);
            timeslot.setEndTime(parsedTime2);

            selectedTimeslots.add(this.timeSlotService.saveTimeslot(timeslot));


        event.setTimeslots(new HashSet<>(selectedTimeslots));

        Set<Location> locationSet = new HashSet<>();
        Location location1 = locationService.loadLocation(1);

        locationSet.add(location1);
        event.setEventLocations(locationSet);

        Date expiryDate = new Date(new Date().getTime() + oneDay*3);
        event.setVotingExpiry(expiryDate);

        event.setUser(eventCreator);

        event = this.eventService.saveEvent(event);

        controller.setEvent(event);
        controller.doEndEvent();

       String result = eventService.getVotingResults(controller.getEvent());

        Assertions.assertEquals(result,controller.getVotingResults(controller.getEvent()),"Event results from service and controller should be equal");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetEventResultsNoResultsFound() throws ParseException {

        autoInitLocations.initializeLocations();

        Event event = new Event();

        User eventCreator = userService.loadUser("user1");

        long oneDay = 24 * 60 * 60 * 1000;

        event.setEventName("Lunch IT-Department");


        Date expiryDate = new Date(new Date().getTime() + oneDay*3);
        event.setVotingExpiry(expiryDate);

        Timeslot timeslot;
        Set<Timeslot> selectedTimeslots = new HashSet<>();

        String time1 = "24-Jan-2022 16:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        timeslot = new Timeslot();
        timeslot.setStartTime(parsedTime1);
        timeslot.setEndTime(parsedTime2);

        selectedTimeslots.add(this.timeSlotService.saveTimeslot(timeslot));


        event.setTimeslots(new HashSet<>(selectedTimeslots));

        event.setUser(eventCreator);

        event = this.eventService.saveEvent(event);

        controller.setEvent(event);
        controller.doEndEvent();

        String result = "No results found.";

        Assertions.assertEquals(result,controller.getVotingResults(controller.getEvent()),"No results found if locations are closed, removed or deleted.");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoSaveEvent() {
    	//EmailService mockEmailService = Mockito.mock(EmailService.class);
    	//controller.setEmailService(mockEmailService);

        autoInitLocations.initializeLocations();
        autoInitEvents.initializeEvents();

        Event event = eventService.findEventByEventName("Lunch IT-Department");


        controller.setEvent(event);
        controller.doEndEvent();
        controller.doSaveEvent();

        Assertions.assertEquals("Expired",controller.getEvent().getEventStatus(),"No results found if locations are closed, removed or deleted.");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetWinnerLocation() throws ParseException {

        autoInitLocations.initializeLocations();

        Event event = new Event();

        User eventCreator = userService.loadUser("user1");

        long oneDay = 24 * 60 * 60 * 1000;

        event.setEventName("Lunch IT-Department");

        String time1 = "24-Jan-2022 16:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);



        Timeslot timeslot;
        Set<Timeslot> selectedTimeslots = new HashSet<>();

        timeslot = new Timeslot();
        timeslot.setStartTime(parsedTime1);
        timeslot.setEndTime(parsedTime2);

        selectedTimeslots.add(this.timeSlotService.saveTimeslot(timeslot));


        event.setTimeslots(new HashSet<>(selectedTimeslots));

        Set<Location> locationSet = new HashSet<>();
        Location location1 = locationService.loadLocation(1);

        locationSet.add(location1);
        event.setEventLocations(locationSet);

        Date expiryDate = new Date(new Date().getTime() + oneDay*3);
        event.setVotingExpiry(expiryDate);

        event.setUser(eventCreator);

        event = this.eventService.saveEvent(event);

        controller.setEvent(event);
        controller.doEndEvent();

        Assertions.assertEquals(location1.getLocationName(),controller.getWinnerLocation().getLocationName(),
                "Location 1 is the only one, so this location is the winner.");
        Assertions.assertEquals(location1.getLocationName(),controller.getWinnerLocation(controller.getEvent()).getLocationName(),
                "Testing also the overloaded method for getWinnerLocation with parameter the event");

    }
}
