package at.qe.skeleton.tasks;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.EmailService;
import at.qe.skeleton.services.EventService;

import java.util.List;

public class VotingExpiryTask implements Runnable {
    private Event event;
    private EventService eventService;

    EmailService emailService;
    private List<User> userInvited;

    public VotingExpiryTask(Event event, EventService eventService, List<User> userInvited, EmailService emailService) {
        this.event = event;
        this.eventService = eventService;
        this.emailService = emailService;
        this.userInvited = userInvited;
    }

    public VotingExpiryTask() {
    }

    @Override
    public void run() {

        Event currentEvent = eventService.findEventByEventId(event.getEventId());

        if (!currentEvent.getEventStatus().equals("Expired")) {
            event = this.eventService.patchVotingExpiry(event, false);
            event = this.eventService.patchEventStatus(event, "Expired");
            this.eventService.getVotingResults(event);

            String locationWinner = event.getEventLocationWinner();
            String timeslotWinner = event.getEventTimeslotWinner();
            String result = this.eventService.getVotingResults(event);

            if(!result.equals("No results found.")) {
                for (User user : userInvited) {
                    emailService.sendEventResult(event, locationWinner, timeslotWinner, user);
                }
            }
        }

    }
}
