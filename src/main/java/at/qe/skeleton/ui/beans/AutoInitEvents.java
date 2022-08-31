package at.qe.skeleton.ui.beans;


import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import at.qe.skeleton.tasks.VotingExpiryTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.ManagedBean;
import java.util.*;

@ManagedBean
@Scope("request")
public class AutoInitEvents {

    @Autowired
    private EventService eventService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    TaskScheduler taskScheduler;

    private final Event event = new Event();



    public void initializeEvents(){

        User eventCreator = userService.loadUser("user1");

        long oneDay = 24 * 60 * 60 * (long)1000;
        long fourDays = 4*oneDay;
        long fiveDays = 5*oneDay;

        event.setEventName("Lunch IT-Department");

        Set<Date> dateSet = new HashSet<>();
        Date date1 = new Date(new Date().getTime() + fourDays);
        Date date2 = new Date(new Date().getTime() + fiveDays);
        dateSet.add(date1);
        dateSet.add(date2);


        Calendar calendar = Calendar.getInstance();

        Timeslot timeslot;
        Set<Timeslot> selectedTimeslots = new HashSet<>();
        for (Date date : dateSet) {
            timeslot = new Timeslot();
            timeslot.setStartTime(date);

            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 2);
            timeslot.setEndTime(calendar.getTime());

            selectedTimeslots.add(this.timeSlotService.saveTimeslot(timeslot));
        }

        event.setTimeslots(new HashSet<>(selectedTimeslots));

        Set<Location> locationSet = new HashSet<>();
        Location location1 = locationService.loadLocation(1);
        Location location2 = locationService.loadLocation(2);

        locationSet.add(location1);
        locationSet.add(location2);
        event.setEventLocations(locationSet);

        Set<UserInvited> usersInvited = new HashSet<>();
        UserInvited userInvited;
        Set<User> selectedUsers = new HashSet<>();

        for(User user : userService.getAllUsers()){
            if(user.getUsername().equals("elvis")){
                continue;
            }
            if(user.isEnabled()) {
                userInvited = new UserInvited();
                userInvited.setUser(user);
                userInvited.setEvent(event);
                userInvited.setInvitationAccepted(true);
                usersInvited.add(userInvited);
                selectedUsers.add(user);
            }
        }


        userInvited = new UserInvited();
        userInvited.setUser(eventCreator);
        userInvited.setEvent(event);
        userInvited.setInvitationAccepted(true);
        usersInvited.add(userInvited);

        event.setUserInvited(usersInvited);

        Date expiryDate = new Date(new Date().getTime() + oneDay*3);
        event.setVotingExpiry(expiryDate);

        event.setUser(eventCreator);

        this.eventService.saveEvent(event);

        scheduleVotingExpiry();

        for(User user : selectedUsers) {
            invitedUsers.add(userService.loadUser(user.getUsername()));
        }
    }

    private final List<User> invitedUsers = new ArrayList<>();


    private void scheduleVotingExpiry(){
        VotingExpiryTask task = new VotingExpiryTask(event, this.eventService, invitedUsers, this.emailService);
        taskScheduler.schedule(task, event.getVotingExpiry());
    }
}
