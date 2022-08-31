package at.qe.skeleton.services;

import at.qe.skeleton.repositories.LocationRepository;
import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Service for accessing and manipulating location data.
 */

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private OpeningHoursService openingHoursService;

    /**
     * Returns a collection of all locations.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER','USER')")
    public Collection<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * Loads a single location identified by its id.
     *
     * @param id the id to search for
     * @return the location with the given id, if no such location null
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Location loadLocation(int id) {
        Optional<Location> loadedLocation = locationRepository.findById(id);
        return loadedLocation.orElse(null);
    }


    /**
     * Saves the location.
     *
     * @param location the location to save
     * @return the updated location
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    /**
     * Deletes the location and all relations corresponding to it.
     *
     * @param location the location to delete
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void delete(Location location) {
        location.getEvents().removeAll(location.getEvents());
        openingHoursService.deleteByLocation(location);
        location.getLocationTags().removeAll(location.getLocationTags());
        saveLocation(location);
        locationRepository.delete(location);
    }


    /**
     * Adds a new Location to H2
     **/

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Location addNewLocation(Location request) {
        Location location = new Location();
        location.setLocationId(request.getLocationId());
        location.setEvents(request.getEvents());
        location.setLocationTags(request.getLocationTags());
        location.setLocationName((request.getLocationName()));
        location.setActive(request.isActive());
        location.setDescription(request.getDescription());
        location.setMenuUrl(request.getMenuUrl());
        location.setUser(userService.getAuthenticatedUser());
        location.setZipCode(request.getZipCode());
        location.setStreet(request.getStreet());
        location.setHouseNumber(request.getHouseNumber());
        location.setCity(request.getCity());
        location.setCountry(request.getCountry());
        return saveLocation(location);
    }

    /**
     * Loads locations identified by the assigned event.
     *
     * @param event the event where search for the assigned location
     * @return a collection of all assigned locations to a given event
     */
    public Collection<Location> findLocationsByEvents(Event event) {
        return locationRepository.findLocationsByEvents(event);
    }

    /**
     * Loads a single location identified by it's name.
     *
     * @param name the name of the location to search for
     * @return the location with the given name
     */
    public Location findLocationByName(String name) {
        return locationRepository.findLocationByLocationName(name);
    }
}
