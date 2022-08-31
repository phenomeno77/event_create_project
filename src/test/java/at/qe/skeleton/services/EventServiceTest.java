package at.qe.skeleton.services;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.services.EventService;
import at.qe.skeleton.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
@WithMockUser(username = "admin", authorities = {"ADMIN"})
class EventServiceTest {
    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @DirtiesContext
    @Test
    void getAllEvents() {
        Event testEvent1 = new Event();
        Event testEvent2 = new Event();


        testEvent1.setEventName("Dinner Event 1");
        testEvent2.setEventName("Dinner Event 2");

        eventService.saveEvent(testEvent1);
        eventService.saveEvent(testEvent2);

        Assertions.assertEquals(2, eventService.getAllEvents().size());
    }

    @DirtiesContext
    @Test
    void loadEvent() {
        Event testEvent = new Event();
        testEvent.setEventName("Dinner 1");
        testEvent.setEventId(1);
        eventService.saveEvent(testEvent);
        Assertions.assertEquals(testEvent.getEventId(), eventService.loadEvent(1).getEventId());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void saveEvent() {
        Event testEvent = new Event();
        testEvent.setEventName("Dinner 1");
        testEvent.setActive(true);
        testEvent.setUser(userService.loadUser("admin"));
        eventService.saveEvent(testEvent);
        Assertions.assertEquals("Dinner 1", eventService.findEventByEventId(1).getEventName());
    }



    @DirtiesContext
    @Test
    void cancelEvent() {
        Event testEvent = new Event();
        testEvent.setEventName("Work Dinner");
        testEvent.setActive(true);
        testEvent.setEventStatus("Active");
        eventService.cancelEvent(testEvent);
        eventService.saveEvent(testEvent);

        Assertions.assertEquals("Canceled", eventService.findEventByEventName("Work Dinner").getEventStatus());

    }
}