package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;

import java.util.Collection;

public interface EventRepository extends AbstractRepository<Event, Integer> {

    Event findEventByEventName(String eventName);

    Collection<Event> findEventsByUser(User user);
    Event findEventByEventId(int id);
}
