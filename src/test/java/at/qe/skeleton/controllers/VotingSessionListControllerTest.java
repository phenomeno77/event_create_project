package at.qe.skeleton.controllers;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.AutoInitEvents2;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import at.qe.skeleton.ui.controllers.VotingSessionController;
import at.qe.skeleton.ui.controllers.VotingSessionList;
import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
public class VotingSessionListControllerTest {

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Autowired
    UserVoteLocationService userVoteLocationService;

    @Autowired
    UserVoteTimeslotService userVoteTimeslotService;

    @Autowired
    TimeSlotService timeSlotService;

    @Autowired
    LocationService locationService;


    @Autowired
    AutoInitLocations autoInitLocations;

    @Autowired
    AutoInitEvents2 autoInitEvents2;




    public JsfMock jsfMock = new JsfMock();

    @Autowired
    ApplicationContext applicationContext;

    VotingSessionList controller = new VotingSessionList();

    VotingSessionController sessionController = new VotingSessionController();

    @BeforeEach
    public void init() {
        jsfMock.init(applicationContext);
        controller.setServices(eventService,userService,userVoteLocationService,userVoteTimeslotService,locationService);
        sessionController.setServices(eventService,timeSlotService,locationService,userService,userVoteLocationService,userVoteTimeslotService);

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetEvent(){

        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Assertions.assertEquals("Farewell Dinner for Peter",controller.getEvent().getEventName(),"One event is in the database with event name Farewell Dinner for Peter");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetTimeslotByEvent(){

        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Assertions.assertEquals(2,controller.getTimeslotsByEvent().size(),"Auto initializer generates 2 timeslots, result should be 2.");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetLocationsByEvent(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Assertions.assertEquals(2,controller.getLocationsByEvent().size(),"Two locations should be in that event");
    }



    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetCurrentUsersRatingForLocation(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Location location = locationService.loadLocation(1);
        Location location2 = locationService.loadLocation(2);

        Assertions.assertEquals(0,controller.getCurrentUsersRatingForLocation(location),"Result should be 0, because no users voted at the moment.");

        sessionController.patchLocationVoting(event,location,3);

        Assertions.assertEquals(3,controller.getCurrentUsersRatingForLocation(location),"Result should be 3 for location with ID 1");

        sessionController.patchLocationVoting(event,location2,5);

        Assertions.assertEquals(5,controller.getCurrentUsersRatingForLocation(location2),"Result should be 5 for location with ID 2");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetCurrentUsersRatingForLocationOnReset(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Location location = locationService.loadLocation(1);

        sessionController.patchLocationVoting(event,location,3);

        Assertions.assertEquals(3,controller.getCurrentUsersRatingForLocation(location),"Result should be 3 for location with ID 1");

        sessionController.patchLocationVotingResetRating(event,location);

        Assertions.assertEquals(0,controller.getCurrentUsersRatingForLocation(location),"After reseting the vote for the location, should be 0");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAverageRatingLocation(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Location location = locationService.loadLocation(1);

        Assertions.assertEquals(0,controller.getAverageRatingLocation(location),"Result should be 0, because no users voted at the moment.");

        sessionController.patchLocationVoting(event,location,3);

        Assertions.assertEquals(1,controller.getAverageRatingLocation(location),"Result should be 1, because 3 users are invited, rating is 3, average is 3 / 3 = 1.");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetCurrentUsersRatingForTimeslot(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);

        sessionController.patchTimeslotVoting(event,timeslot,3);

        Assertions.assertEquals(3,controller.getCurrentUsersRatingForTimeslot(timeslot),"Result should be 3 for that timeslot");

        Timeslot timeslot2 = timeSlotService.findTimeslotByTimeslotId(2);

        sessionController.patchTimeslotVoting(event,timeslot2,5);

        Assertions.assertEquals(5,controller.getCurrentUsersRatingForTimeslot(timeslot2),"Result should be 5 for that timeslot");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetCurrentUsersRatingForTimeslotOnReset(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);

        sessionController.patchTimeslotVoting(event,timeslot,3);

        Assertions.assertEquals(3,controller.getCurrentUsersRatingForTimeslot(timeslot),"First vote should be 3");

        sessionController.patchTimeslotVotingResetRating(event,timeslot);
        Assertions.assertEquals(0,controller.getCurrentUsersRatingForTimeslot(timeslot),"After reseting the vote for the timeslot, should be 0");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAverageRatingTimeslot(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);

        sessionController.patchTimeslotVoting(event,timeslot,3);

        Assertions.assertEquals(1,controller.getAverageRatingTimeslot(timeslot),"Result should be 1, 3 users invited and rating is 3 so 3 / 3 = 1");


    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllUsersVoteForLocation(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        List<String> expectedResults = new ArrayList<>();

        controller.setEvent(event);

        Location location = locationService.loadLocation(1);

        Assertions.assertEquals(expectedResults,controller.getAllUsersVoteForLocation(location),"Result should be empty because until now no Users voted");

        sessionController.patchLocationVoting(event,location,3);

        expectedResults.add(userService.getAuthenticatedUser().getUsername()+" [ " + 3+" ]");

        Assertions.assertEquals(expectedResults,controller.getAllUsersVoteForLocation(location),"Restults after voting should be: username [ rating]");

        sessionController.patchLocationVotingResetRating(event,location);

        expectedResults = new ArrayList<>();

        Assertions.assertEquals(expectedResults,controller.getAllUsersVoteForLocation(location),"After reseting the lists with the results should be empty");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllUsersVoteForTimeslot(){

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        List<String> expectedResults = new ArrayList<>();

        controller.setEvent(event);

        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);

        Assertions.assertEquals(expectedResults,controller.getAllUsersVoteForTimeslot(timeslot),"Result should be empty because until now no Users voted");

        sessionController.patchTimeslotVoting(event,timeslot,3);

        expectedResults.add(userService.getAuthenticatedUser().getUsername()+" [ " + 3+" ]");

        Assertions.assertEquals(expectedResults,controller.getAllUsersVoteForTimeslot(timeslot),"Restults after voting should be: username [ rating]");

        sessionController.patchTimeslotVotingResetRating(event,timeslot);

        expectedResults = new ArrayList<>();

        Assertions.assertEquals(expectedResults,controller.getAllUsersVoteForTimeslot(timeslot),"After reseting the lists with the results should be empty");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPushEventByRedirectSuccessfully() throws IOException {

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        controller.pushEventByRedirect(event);

        Assertions.assertEquals("/events/voting.xhtml",controller.getRedirectUrl(),"If the event is not expired yet, it should redirect the user to voting page");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPushEventByRedirectEventExpired() throws IOException {

        autoInitLocations.initializeLocations();
        autoInitEvents2.initializeEvents();

        Event event = eventService.findEventByEventName("Farewell Dinner for Peter");

        controller.setEvent(event);

        event.setVotingExpiry(new Date());
        event.setEventStatus("Expired");
        event.setActive(false);
        eventService.saveEvent(event);

        controller.pushEventByRedirect(event);

        Assertions.assertEquals("Event Expired",controller.getMessageInfo());
        Assertions.assertEquals("We're sorry, but that Event has been expired at: " + event.getVotingExpiry(),controller.getFacesMessage(),
                "If the voting has been expired, but for some reason the Event homepage is not refreshed, it should not redirect the user" +
                        "to the voting page for that event.");


    }





}
