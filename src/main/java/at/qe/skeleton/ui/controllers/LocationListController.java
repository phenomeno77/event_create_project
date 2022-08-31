package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Controller for the location list view.
 *
 */
@Component
@Scope("view")
public class LocationListController {

    
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private OpeningHoursService openingHourService;
    
 
    private Location location;
    
    Collection<Location> locations; 
    
    
    @PostConstruct
    public void init() {
    	locations = locationService.getAllLocations();
    }


    /**
     * Returns a list of all Location for Overview.
     *
     * @return Collection
     */
    
    public Collection<Location> getLocations() {
        return locations;
    }

    public Collection<Location> getLocationsForEvent() {
        return locationService.getAllLocations().stream()
                .filter(location1 -> Objects.equals(location1.getLocationStatus(), "Active"))
                .collect(Collectors.toList());
    }

    
    
	// getter and setter for location

    
    public Location getLocation() {
    	return this.location;
    }
    
    public void setLocation(Location l) {
    	this.location = l;
    }
    
    
    /**
     * Returns a list of string for OpeningHours for Location Detail View.
     *
     * @return List
     */

	public List<String> getOpeningHours(Location l) {

		List<OpeningHours> ohc = openingHourService.getAllOpeningHours().stream().filter(oh -> oh.getLocation().equals(l)).collect(Collectors.toList());
		List<String> openingHoursMapped = new ArrayList<>();

		for(OpeningHours o : ohc) {
			if(o.getBreakTimeStart() == null && o.getOpeningTime() != null){
					openingHoursMapped.add(DayOfWeek.of(o.getDay()).toString() + "  " + o.getOpeningTime() + " - " + o.getClosingTime());
			}
			if(o.getBreakTimeStart() != null && o.getOpeningTime() != null){
				openingHoursMapped.add(DayOfWeek.of(o.getDay()).toString() + "  " + o.getOpeningTime() + " - " + o.getBreakTimeStart() + ", "
						+ o.getBreakTimeEnd() + " - " + o.getClosingTime() );
			}
		}
		return openingHoursMapped;
	}
	
	


    /**
     * Returns a list of tags for location overview .
     *
     * @return List
     */

    public List<Tags> getAllTags(Location l){

		return new ArrayList<>(l.getLocationTags());
    }
    
   


}
