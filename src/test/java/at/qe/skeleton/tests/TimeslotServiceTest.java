package at.qe.skeleton.tests;


import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.services.EventService;
import at.qe.skeleton.services.TimeSlotService;
import at.qe.skeleton.ui.beans.AutoInitEvents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 * Test-Class for {@link at.qe.skeleton.services.TimeSlotService}
 */
@SpringBootTest
@WebAppConfiguration
@WithMockUser(username = "admin", authorities = {"ADMIN"})
public class TimeslotServiceTest {
    @Autowired
    private AutoInitEvents autoInitEvents;
    @Autowired
    private TimeSlotService timeSlotService;
    @Autowired
    private EventService eventService;

    @DirtiesContext
    @Test
    public void testSaveTimeslot() {
        Date startTime = new Date(2022, 1, 27, 12, 0);
        Date endTime = new Date(2022, 1, 27, 13, 0);

        Timeslot timeslotToSave = new Timeslot();

        timeslotToSave.setTimeSlotVotingCount(0);
        timeslotToSave.setStartTime(startTime);
        timeslotToSave.setEndTime(endTime);

        Timeslot savedTimeslot = timeSlotService.saveTimeslot(timeslotToSave);

        Assertions.assertEquals(startTime, timeSlotService.loadTimeslot(1).getStartTime());
        Assertions.assertEquals(endTime, timeSlotService.loadTimeslot(1).getEndTime());
        Assertions.assertEquals(0, timeSlotService.getVotingCount(savedTimeslot));
    }

    @DirtiesContext
    @Test
    public void testGetAllTimeslots() {
        autoInitEvents.initializeEvents();
        Collection<Timeslot> loadedTimeslots = timeSlotService.getAllTimeslots();

        Assertions.assertEquals(2, loadedTimeslots.size());

        Assertions.assertEquals(0, Objects.requireNonNull(loadedTimeslots.stream().findFirst().orElse(null)).getTimeSlotVotingCount());
        Assertions.assertEquals(0, Objects.requireNonNull(loadedTimeslots.stream().findFirst().orElse(null)).getTimeSlotVotingCount());
    }

    @DirtiesContext
    @Test
    public void testLoadTimeslot() {
        autoInitEvents.initializeEvents();

        Timeslot loadedTimeslot1 = timeSlotService.loadTimeslot(1);
        Timeslot loadedTimeslot2 = timeSlotService.loadTimeslot(2);

        Assertions.assertNotNull(loadedTimeslot1);
        Assertions.assertNotNull(loadedTimeslot2);

        Assertions.assertEquals(0, loadedTimeslot1.getTimeSlotVotingCount());
        Assertions.assertEquals(0, loadedTimeslot2.getTimeSlotVotingCount());

        Assertions.assertEquals(1, loadedTimeslot1.getTimeslotId());
        Assertions.assertEquals(2, loadedTimeslot2.getTimeslotId());

        Date expectedStartTime1 = timeSlotService.loadTimeslot(1).getStartTime();
        Date expectedStartTime2 = timeSlotService.loadTimeslot(2).getStartTime();
        Assertions.assertEquals(expectedStartTime1, loadedTimeslot1.getStartTime());
        Assertions.assertEquals(expectedStartTime2, loadedTimeslot2.getStartTime());

        Date expectedEndTime1 = timeSlotService.loadTimeslot(1).getEndTime();
        Date expectedEndTime2 = timeSlotService.loadTimeslot(2).getEndTime();
        Assertions.assertEquals(expectedEndTime1, loadedTimeslot1.getEndTime());
        Assertions.assertEquals(expectedEndTime2, loadedTimeslot2.getEndTime());
    }

    @DirtiesContext
    @Test
    public void testAddNewTimeslot() {
        autoInitEvents.initializeEvents();

        Date startTime = new Date(2022, 1, 27, 12, 0);
        Date endTime = new Date(2022, 1, 27, 13, 0);

        Timeslot timeslotToAdd = new Timeslot();
        timeslotToAdd.setTimeSlotVotingCount(0);
        timeslotToAdd.setStartTime(startTime);
        timeslotToAdd.setEndTime(endTime);

        Timeslot savedTimeslot = timeSlotService.saveTimeslot(timeslotToAdd);

        Event eventToAddNewTimeslot = eventService.loadEvent(1);

        Assertions.assertEquals(2, eventToAddNewTimeslot.getTimeslots().size());

        eventToAddNewTimeslot.addTimeslotToEvent(savedTimeslot);
        eventService.saveEvent(eventToAddNewTimeslot);

        Assertions.assertEquals(3, eventToAddNewTimeslot.getTimeslots().size());
        Assertions.assertTrue(eventService.findEventByEventId(1).getTimeslots().contains(savedTimeslot));
    }

    @DirtiesContext
    @Test
    public void testGetVotingCount() {
        Timeslot timeslot1 = new Timeslot();
        Timeslot timeslot2 = new Timeslot();

        timeslot1.setTimeSlotVotingCount(1);
        timeslot2.setTimeSlotVotingCount(10);

        Timeslot savedTimeslot1 = timeSlotService.saveTimeslot(timeslot1);
        Timeslot savedTimeslot2 = timeSlotService.saveTimeslot(timeslot2);

        Assertions.assertEquals(1, timeSlotService.getVotingCount(savedTimeslot1));
        Assertions.assertEquals(10, timeSlotService.getVotingCount(savedTimeslot2));
    }

    @DirtiesContext
    @Test
    public void testIncrementVotingCount() {
        autoInitEvents.initializeEvents();

        Timeslot timeslotFromDb1 = timeSlotService.loadTimeslot(1);
        Timeslot timeslotFromDb2 = timeSlotService.loadTimeslot(2);

        Assertions.assertEquals(0, timeslotFromDb1.getTimeSlotVotingCount());
        Assertions.assertEquals(0, timeslotFromDb2.getTimeSlotVotingCount());
        timeSlotService.incrementVotingCount(timeslotFromDb1);
        timeSlotService.incrementVotingCount(timeslotFromDb2);

        Assertions.assertEquals(1, timeSlotService.loadTimeslot(1).getTimeSlotVotingCount());
        Assertions.assertEquals(1, timeSlotService.loadTimeslot(2).getTimeSlotVotingCount());

        Timeslot incrementedTimeslot1 = timeSlotService.loadTimeslot(1);
        timeSlotService.incrementVotingCount(incrementedTimeslot1);

        Assertions.assertEquals(2, timeSlotService.loadTimeslot(1).getTimeSlotVotingCount());
        Assertions.assertEquals(1, timeSlotService.loadTimeslot(2).getTimeSlotVotingCount());
    }

    @DirtiesContext
    @Test
    public void testDecrementVotingCount() {
        autoInitEvents.initializeEvents();
        Timeslot timeslotFromDb1 = timeSlotService.loadTimeslot(1);
        Timeslot timeslotFromDb2 = timeSlotService.loadTimeslot(2);
        Assertions.assertEquals(0, timeslotFromDb1.getTimeSlotVotingCount());
        Assertions.assertEquals(0, timeslotFromDb2.getTimeSlotVotingCount());

        timeSlotService.incrementVotingCount(timeslotFromDb1);
        timeSlotService.incrementVotingCount(timeslotFromDb2);
        Assertions.assertEquals(1, timeSlotService.loadTimeslot(1).getTimeSlotVotingCount());
        Assertions.assertEquals(1, timeSlotService.loadTimeslot(2).getTimeSlotVotingCount());

        Timeslot incrementedTimeslot1 = timeSlotService.loadTimeslot(1);
        Timeslot incrementedTimeslot2 = timeSlotService.loadTimeslot(2);
        timeSlotService.decrementVotingCount(incrementedTimeslot1);
        timeSlotService.decrementVotingCount(incrementedTimeslot2);
        Assertions.assertEquals(0, timeSlotService.loadTimeslot(1).getTimeSlotVotingCount());
        Assertions.assertEquals(0, timeSlotService.loadTimeslot(2).getTimeSlotVotingCount());
    }
}
