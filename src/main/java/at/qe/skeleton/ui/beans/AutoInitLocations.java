package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;
import at.qe.skeleton.services.TagService;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import java.time.LocalTime;

@ManagedBean
@Scope("request")
public class AutoInitLocations {

    @Autowired
    private LocationService locationService;

    @Autowired
    private OpeningHoursService openingHoursService;

    @Autowired
    private TagService tagService;

    public void initializeLocations(){
        Location location1;
        Location location2;
        OpeningHours openingHours;
        Tags tags;

        location1 = new Location();
        location1.setLocationName("Mensa Campus Technik");
        location1.setCity("Innsbruck");
        location1.setDescription("Special price for students and staff. Tuesday and thursday offers: Wiener Schnitzel. Accessible to wheelchair users.");
        location1.setMenuUrl("www.mensen.at");
        location1.setStreet("Technikerstrasse");
        location1.setHouseNumber(13);
        location1.setCountry("Austria");
        location1.setZipCode(6020);
        location1 = this.locationService.saveLocation(location1);

        tags = new Tags();
        tags.setTagName("vegan");
        location1.getLocationTags().add(tagService.saveTag(tags));
        
        tags = new Tags();
        tags.setTagName("asian");
        location1.getLocationTags().add(tagService.saveTag(tags));
        
        tags = new Tags();
        tags.setTagName("italian");
        location1.getLocationTags().add(tagService.saveTag(tags));

        for(int i=1; i<=5; i++) {
            openingHours = new OpeningHours();
            openingHours.setDay(i);
            openingHours.setOpeningTime(LocalTime.of(10, 00));
            openingHours.setClosingTime(LocalTime.of(23, 00));
            openingHours.setBreakTimeStart(LocalTime.of(15,00));
            openingHours.setBreakTimeEnd(LocalTime.of(18,00));
            openingHours.setLocation(locationService.loadLocation(location1.getLocationId()));
            openingHoursService.saveOpeningHours(openingHours);
        }
        
        for(int i=6; i<=7; i++){
            openingHours = new OpeningHours();
            openingHours.setDay(i);

            openingHours.setLocation(locationService.loadLocation(location1.getLocationId()));
            openingHoursService.saveOpeningHours(openingHours);
        }

        location2 = new Location();
        location2.setLocationName("Mensa Campus Innrain");
        location2.setCity("Innsbruck");
        location2.setDescription("Special price for students and staff. Tuesday offer: Käsespätzle. Not accessible to wheelchair users.");
        location2.setMenuUrl("www.mensen.at");
        location2.setStreet("Innrain");
        location2.setHouseNumber(62);
        location2.setCountry("Austria");
        location2.setZipCode(6020);
        location2 =  this.locationService.saveLocation(location2);


        tags = new Tags();
        tags.setTagName("vegetarian");
        location2.getLocationTags().add(tagService.saveTag(tags));
        
        tags = new Tags();
        tags.setTagName("austrian");
        location2.getLocationTags().add(tagService.saveTag(tags));


        for(int i=1; i<=6; i++) {
            openingHours = new OpeningHours();
            openingHours.setDay(i);
            openingHours.setOpeningTime(LocalTime.of(16, 00));
            openingHours.setClosingTime(LocalTime.of(23, 00));
            openingHours.setLocation(locationService.loadLocation(location2.getLocationId()));
            openingHoursService.saveOpeningHours(openingHours);
        }
        openingHours = new OpeningHours();
        openingHours.setDay(7);
        openingHours.setLocation(locationService.loadLocation(location2.getLocationId()));
        openingHoursService.saveOpeningHours(openingHours);
        PrimeFaces.current().executeScript("setTimeout(function() { location.reload() },500)");
    }

}
