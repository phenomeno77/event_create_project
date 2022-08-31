package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.ui.beans.AutoInitEvents;
import at.qe.skeleton.ui.beans.AutoInitLocations;
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
public class UserVoteLocationTest {


    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;


    @Autowired
    private UserVoteLocationService userVoteLocationService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AutoInitEvents autoInitEvent;

    @Autowired
    private AutoInitLocations autoInitLocations;

    @Test
    @DirtiesContext
    void voteLocationTest(){
        autoInitEvent.initializeEvents();
        autoInitLocations.initializeLocations();
        Event eve = eventService.findEventByEventName("Lunch IT-Department");
        Location location = locationService.loadLocation(1);
        UserVoteLocation votingLocation = new UserVoteLocation();
        User admin = userService.loadUser("admin");
        votingLocation.setVotingCount(3);
        votingLocation.setLocation(location);
        votingLocation.setEvent(eve);
        votingLocation.setUser(admin);
        locationService.saveLocation(location);
        userVoteLocationService.voteLocation(votingLocation);
        UserVoteLocation userVote = userVoteLocationService.findUserVoteLocationByUserAndEventAndLocation(admin, eve, location);
        Assertions.assertEquals(3, userVote.getVotingCount());

    }

    @DirtiesContext
    @Test
    void getTotalVotingByLocationTest() {
        autoInitEvent.initializeEvents();
        autoInitLocations.initializeLocations();

        Event eve = eventService.findEventByEventName("Lunch IT-Department");
        Location location = locationService.loadLocation(1);
        UserVoteLocation votingLocation1 = new UserVoteLocation();
        UserVoteLocation votingLocation2 = new UserVoteLocation();
        User admin = userService.loadUser("admin");
        User user1 = userService.loadUser("user1");

        votingLocation1.setVotingCount(3);
        votingLocation1.setLocation(location);
        votingLocation1.setEvent(eve);
        votingLocation1.setUser(admin);

        votingLocation2.setVotingCount(5);
        votingLocation2.setLocation(location);
        votingLocation2.setEvent(eve);
        votingLocation2.setUser(user1);

        locationService.saveLocation(location);
        userVoteLocationService.voteLocation(votingLocation1);
        userVoteLocationService.voteLocation(votingLocation2);
        Assertions.assertEquals(8, userVoteLocationService.getTotalVoting(eve, location));


    }

    @DirtiesContext
    @Test
    void getAverageVotingByLocationTest() {
        autoInitEvent.initializeEvents();
        autoInitLocations.initializeLocations();

        Event eve = eventService.findEventByEventName("Lunch IT-Department");
        Location location = locationService.loadLocation(1);
        UserVoteLocation votingLocation1 = new UserVoteLocation();
        UserVoteLocation votingLocation2 = new UserVoteLocation();
        User admin = userService.loadUser("admin");
        User user1 = userService.loadUser("user1");

        votingLocation1.setVotingCount(3);
        votingLocation1.setLocation(location);
        votingLocation1.setEvent(eve);
        votingLocation1.setUser(admin);

        votingLocation2.setVotingCount(6);
        votingLocation2.setLocation(location);
        votingLocation2.setEvent(eve);
        votingLocation2.setUser(user1);

        locationService.saveLocation(location);
        userVoteLocationService.voteLocation(votingLocation1);
        userVoteLocationService.voteLocation(votingLocation2);
        Assertions.assertEquals("[admin [ 3 ], user1 [ 6 ]]", userVoteLocationService.getUserLocationVoting(eve, location).toString());


    }


    @DirtiesContext
    @Test
    void findUserVoteLocationTest() {
        autoInitEvent.initializeEvents();
        autoInitLocations.initializeLocations();

        Event eve = eventService.findEventByEventName("Lunch IT-Department");
        Location location = locationService.loadLocation(1);
        UserVoteLocation votingLocation1 = new UserVoteLocation();
        UserVoteLocation votingLocation2 = new UserVoteLocation();
        User admin = userService.loadUser("admin");
        User user1 = userService.loadUser("user1");

        votingLocation1.setVotingCount(3);
        votingLocation1.setLocation(location);
        votingLocation1.setEvent(eve);
        votingLocation1.setUser(admin);

        votingLocation2.setVotingCount(6);
        votingLocation2.setLocation(location);
        votingLocation2.setEvent(eve);
        votingLocation2.setUser(user1);

        locationService.saveLocation(location);
        userVoteLocationService.voteLocation(votingLocation1);
        userVoteLocationService.voteLocation(votingLocation2);
        Assertions.assertEquals(votingLocation1, userVoteLocationService.findUserVoteLocationByUserAndEventAndLocation(admin, eve, location));


    }
}
