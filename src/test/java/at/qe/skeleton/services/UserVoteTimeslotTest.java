package at.qe.skeleton.services;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserVoteTimeslot;
import at.qe.skeleton.ui.beans.AutoInitEvents;
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
public class UserVoteTimeslotTest {






    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;


    @Autowired
    private UserVoteTimeslotService userVoteTimeslotService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private AutoInitEvents autoInitEvent;

    @DirtiesContext
    @Test
    void voteTimeslotTest() {
        autoInitEvent.initializeEvents();

        Event eve = eventService.findEventByEventName("TEST EVENT 1");
        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);
        UserVoteTimeslot votingTimeslot = new UserVoteTimeslot();
        User admin = userService.loadUser("admin");
        votingTimeslot.setVotingCount(3);
        votingTimeslot.setTimeslot(timeslot);
        votingTimeslot.setEvent(eve);
        votingTimeslot.setUser(admin);
        timeSlotService.saveTimeslot(timeslot);
        userVoteTimeslotService.voteTimeslot(votingTimeslot);
        UserVoteTimeslot userVoteTimeslot1 = userVoteTimeslotService.findUserVoteTimeslotByUserAndEventAndTimeslot(admin, eve, timeslot);
        Assertions.assertEquals(3, userVoteTimeslot1.getVotingCount());

    }



    @DirtiesContext
    @Test
    void getTotalVotingByTimeslotTest() {
        autoInitEvent.initializeEvents();

        Event eve = eventService.findEventByEventName("TEST EVENT 1");
        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);
        UserVoteTimeslot votingTimeslot1 = new UserVoteTimeslot();
        UserVoteTimeslot votingTimeslot2 = new UserVoteTimeslot();
        User admin = userService.loadUser("admin");
        User user1 = userService.loadUser("user1");

        votingTimeslot1.setVotingCount(3);
        votingTimeslot1.setTimeslot(timeslot);
        votingTimeslot1.setEvent(eve);
        votingTimeslot1.setUser(admin);

        votingTimeslot2.setVotingCount(5);
        votingTimeslot2.setTimeslot(timeslot);
        votingTimeslot2.setEvent(eve);
        votingTimeslot2.setUser(user1);

        timeSlotService.saveTimeslot(timeslot);
        userVoteTimeslotService.voteTimeslot(votingTimeslot1);
        userVoteTimeslotService.voteTimeslot(votingTimeslot2);
        Assertions.assertEquals(8, userVoteTimeslotService.getTotalVoting(eve, timeslot));


    }
    @DirtiesContext
    @Test
    void getAverageVotingByTimeslotTest() {
        autoInitEvent.initializeEvents();

        Event eve = eventService.findEventByEventName("Lunch IT-Department");
        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);
        UserVoteTimeslot votingTimeslot1 = new UserVoteTimeslot();
        UserVoteTimeslot votingTimeslot2 = new UserVoteTimeslot();
        User admin = userService.loadUser("admin");
        User user1 = userService.loadUser("user1");

        votingTimeslot1.setVotingCount(4);
        votingTimeslot1.setTimeslot(timeslot);
        votingTimeslot1.setEvent(eve);
        votingTimeslot1.setUser(admin);

        votingTimeslot2.setVotingCount(5);
        votingTimeslot2.setTimeslot(timeslot);
        votingTimeslot2.setEvent(eve);
        votingTimeslot2.setUser(user1);

        timeSlotService.saveTimeslot(timeslot);
        userVoteTimeslotService.voteTimeslot(votingTimeslot1);
        userVoteTimeslotService.voteTimeslot(votingTimeslot2);

        double userVoting = userVoteTimeslotService.getAverageVoting(eve, timeslot);
        Assertions.assertEquals(3, userVoting);

    }
    @DirtiesContext
    @Test
    void votingPerUserTest() {
        autoInitEvent.initializeEvents();

        Event eve = eventService.findEventByEventName("Lunch IT-Department");
        Timeslot timeslot = timeSlotService.findTimeslotByTimeslotId(1);
        UserVoteTimeslot votingTimeslot = new UserVoteTimeslot();
        UserVoteTimeslot votingTimeslot2 = new UserVoteTimeslot();

        User admin = userService.loadUser("admin");
        User user1 = userService.loadUser("user1");

        votingTimeslot.setVotingCount(3);
        votingTimeslot.setTimeslot(timeslot);
        votingTimeslot.setEvent(eve);
        votingTimeslot.setUser(admin);

        votingTimeslot2.setVotingCount(5);
        votingTimeslot2.setTimeslot(timeslot);
        votingTimeslot2.setEvent(eve);
        votingTimeslot2.setUser(user1);

        timeSlotService.saveTimeslot(timeslot);
        userVoteTimeslotService.voteTimeslot(votingTimeslot);
        userVoteTimeslotService.voteTimeslot(votingTimeslot2);


        Assertions.assertEquals("[admin [ 3 ], user1 [ 5 ]]", userVoteTimeslotService.getUserTimeslotVoting(eve,timeslot).toString());
    }

}
