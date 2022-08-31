package at.qe.skeleton.controllers;


import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.AutoInitEvents;
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

@WebAppConfiguration
@SpringBootTest
public class VotingSessionControllerTest {

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

    @Autowired
    AutoInitEvents autoInitEvents;

    public JsfMock jsfMock = new JsfMock();

    @Autowired
    ApplicationContext applicationContext;

    VotingSessionList listController = new VotingSessionList();

    VotingSessionController controller = new VotingSessionController();

    @BeforeEach
    public void init() {
        jsfMock.init(applicationContext);
        listController.setServices(eventService,userService,userVoteLocationService,userVoteTimeslotService,locationService);
        controller.setServices(eventService,timeSlotService,locationService,userService,userVoteLocationService,userVoteTimeslotService);
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetLocationRating() {

        controller.setLocationRating(3);

        Assertions.assertEquals(3, controller.getLocationRating());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetTimeslotRating() {

        controller.setTimeslotRating(3);

        Assertions.assertEquals(3, controller.getTimeslotRating());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPatchLocationVoting() throws IOException {

        autoInitLocations.initializeLocations();
        autoInitEvents.initializeEvents();

        Event event = eventService.findEventByEventName("Lunch IT-Department");
        listController.pushEventByRedirect(event);

        Location location1 = locationService.loadLocation(1);
        Location location2 = locationService.loadLocation(2);

        controller.patchLocationVoting(event,location1,3);

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForLocation(location1),"Voting controller will set the rating for the Location with ID 1 to 3." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchLocationVoting(event,location2,4);

        Assertions.assertEquals(4, listController.getCurrentUsersRatingForLocation(location2),"Voting controller will set the rating for the Location with ID 2 to 4." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchLocationVoting(event,location2,5);

        Assertions.assertEquals(5, listController.getCurrentUsersRatingForLocation(location2),"Users can always change their rating");

        controller.patchLocationVoting(event,location2,1);

        Assertions.assertEquals(1, listController.getCurrentUsersRatingForLocation(location2),"Users can always change their rating");
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPatchLocationVotingResetRating() throws IOException {

        autoInitLocations.initializeLocations();
        autoInitEvents.initializeEvents();

        Event event = eventService.findEventByEventName("Lunch IT-Department");
        listController.pushEventByRedirect(event);

        Location location1 = locationService.loadLocation(1);
        Location location2 = locationService.loadLocation(2);

        controller.patchLocationVoting(event,location1,3);

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForLocation(location1),"Voting controller will set the rating for the Location with ID 1 to 3." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchLocationVoting(event,location2,4);

        Assertions.assertEquals(4, listController.getCurrentUsersRatingForLocation(location2),"Voting controller will set the rating for the Location with ID 2 to 4." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchLocationVoting(event,location1,3);

        controller.patchLocationVotingResetRating(event,location2);

        Assertions.assertEquals(0, listController.getCurrentUsersRatingForLocation(location2),"After reseting the rating from Location with ID 2, value should be 0");

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForLocation(location1),"Rating for Location with ID 1 should remain the same as before");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPatchTimeslotVoting() throws IOException {

        autoInitLocations.initializeLocations();
        autoInitEvents.initializeEvents();

        Event event = eventService.findEventByEventName("Lunch IT-Department");
        listController.pushEventByRedirect(event);

       Timeslot timeslot1 = timeSlotService.findTimeslotByTimeslotId(1);
        Timeslot timeslot2 = timeSlotService.findTimeslotByTimeslotId(2);

        controller.patchTimeslotVoting(event,timeslot1,3);

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForTimeslot(timeslot1),"Voting controller will set the rating for the Timeslot with ID 1 to 3." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchTimeslotVoting(event,timeslot2,4);

        Assertions.assertEquals(4, listController.getCurrentUsersRatingForTimeslot(timeslot2),"Voting controller will set the rating for the Timeslot with ID 2 to 4." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchTimeslotVoting(event,timeslot2,3);

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForTimeslot(timeslot2),"Users can always change their rating");

        controller.patchTimeslotVoting(event,timeslot2,1);

        Assertions.assertEquals(1, listController.getCurrentUsersRatingForTimeslot(timeslot2),"Users can always change their rating");
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPatchTimeslotVotingResetRating() throws IOException {

        autoInitLocations.initializeLocations();
        autoInitEvents.initializeEvents();

        Event event = eventService.findEventByEventName("Lunch IT-Department");
        listController.pushEventByRedirect(event);

        Timeslot timeslot1 = timeSlotService.findTimeslotByTimeslotId(1);
        Timeslot timeslot2 = timeSlotService.findTimeslotByTimeslotId(2);

        controller.patchTimeslotVoting(event,timeslot1,3);

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForTimeslot(timeslot1),"Voting controller will set the rating for the Timeslot with ID 1 to 3." +
                "Checking with sessionListController if the results are pushed in DB.");

        controller.patchTimeslotVoting(event,timeslot2,4);

        Assertions.assertEquals(4, listController.getCurrentUsersRatingForTimeslot(timeslot2),"Voting controller will set the rating for the Location with ID 2 to 4." +
                "Checking with sessionListController if the results are pushed in DB.");


        controller.patchTimeslotVotingResetRating(event,timeslot2);

        Assertions.assertEquals(0, listController.getCurrentUsersRatingForTimeslot(timeslot2),"After reseting the rating from Timeslot with ID 2, value should be 0");

        Assertions.assertEquals(3, listController.getCurrentUsersRatingForTimeslot(timeslot1),"Rating for Timeslot with ID 1 should remain the same as before");

    }




}
