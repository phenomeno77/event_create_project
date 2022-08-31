package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;

public interface LocationRepository extends AbstractRepository<Location, Integer> {

    Collection<Location> findLocationsByEvents(Event event);

    Location findLocationByLocationName(String name);
    Collection<Location> findAllByEvents(Event event);
    Location findLocationByLocationId(int id);
    @Transactional
    public void deleteLocationByLocationId(int id);


}
