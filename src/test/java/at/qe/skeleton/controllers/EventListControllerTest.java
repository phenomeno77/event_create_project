package at.qe.skeleton.controllers;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.AutoInitEvents;
import at.qe.skeleton.ui.beans.AutoInitEvents2;
import at.qe.skeleton.ui.controllers.EventListController;
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

import java.util.ArrayList;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
public class EventListControllerTest {
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

    public JsfMock jsfMock = new JsfMock();

    @Autowired
    ApplicationContext applicationContext;

    EventListController controller = new EventListController();

    @BeforeEach
    public void init() {
        jsfMock.init(applicationContext);
        controller.setServices(eventService, userService, locationService, userVoteLocationService,
                userVoteTimeslotService);
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetUsersExceptCurrent() {

        //method which shows the list to invite users except the User who creates the event at the moment
        List<User> usersExceptCurrent = new ArrayList<>(controller.getUsersExceptCurrent());
        Assertions.assertEquals(3, usersExceptCurrent.size(), "The return value should be 3, since 4 users are in the DB except the current");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllUsersEventsNoEvents() {

        Assertions.assertEquals(0, controller.getAllEvents().size(),
                "The return value should be 0, since there are no event at the moment where that user has been invited");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllUsersEventsWithAdminInvitedToEvent() {

        autoInitEvents.initializeEvents();

        Assertions.assertEquals(1, controller.getEventInvitedUsers().size(),
                "The return value should be 1, since the admin user has been invited to the event");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllUsersEventsAdminAsCreatorAndInvited() {

        autoInitEvents.initializeEvents();
        autoInitEvents2.initializeEvents();

        Assertions.assertEquals(2, controller.getEventInvitedUsers().size(),
                "The return value should be 2, since the admin user has been invited to an event and he is the creator of the second event.");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetNotActiveEventsHistoryView() {

        autoInitEvents.initializeEvents();
        Event event = eventService.loadEvent(1);
        event.setActive(false);
        event.setEventStatus("Expired");
        eventService.saveEvent(event);

        Assertions.assertEquals(1, controller.getEventInvitedUsersHistory().size(),
                "The return value should be 1, since the admin user has been invited to an event and that event has been set to not active.");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllEventsAuthenticatedUser() {

        autoInitEvents.initializeEvents();

        Assertions.assertEquals(1, controller.getAllEvents().size(),
                "The return value should be 1, only one event at the moment");

        for (int i = 0; i < 2; i++) {
            autoInitEvents.initializeEvents();
        }

        Assertions.assertEquals(3, controller.getAllEvents().size(),
                "The return value should be 3, 1 plus 2 events created");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testGetAllEventsUnAuthenticatedUser() {

        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            for (Event ignored : controller.getAllEvents()) {
                Assertions.fail("Call to get all events with no authority should not work");
            }
        });
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testGetAllEventsUnAuthenticatedLocationManager() {

        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            for (Event ignored : controller.getAllEvents()) {
                Assertions.fail("Call to get all events with no authority should not work");
            }
        });
    }


    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void testUnauthorizedEventLoad() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            for (Event ignored : controller.getAllEvents()) {
                Assertions.fail("Call to get all events with no authority should not work");
            }
        });
    }
}