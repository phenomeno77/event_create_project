
package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface OpeningHoursRepository extends AbstractRepository<OpeningHours, Integer> {
    Collection<OpeningHours> findOpeningHoursByLocation(Location location);
    @Transactional
    void deleteByLocation(Location location);
}

