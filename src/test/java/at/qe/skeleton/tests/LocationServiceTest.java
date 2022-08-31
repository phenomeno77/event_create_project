package at.qe.skeleton.tests;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import at.qe.skeleton.services.EventService;
import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import at.qe.skeleton.ui.beans.AutoInitEvents;

import java.util.*;

/**
 * Test-Class for {@link at.qe.skeleton.services.LocationService}
 */
@SpringBootTest
@WebAppConfiguration
@WithMockUser(username = "admin", authorities = {"ADMIN"})
public class LocationServiceTest {
    @Autowired
    private LocationService locationService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private AutoInitLocations autoInitLocations;
    @Autowired
    private AutoInitEvents autoInitEvents;

    @DirtiesContext
    @Test
    public void testGetAllLocations() {
        autoInitLocations.initializeLocations();
        Collection<Location> loadedLocations = locationService.getAllLocations();

        Assertions.assertEquals(2, loadedLocations.size());
        Assertions.assertEquals("Mensa Campus Technik", Objects.requireNonNull(loadedLocations.stream().findFirst().orElse(null)).getLocationName());

        loadedLocations.remove(loadedLocations.stream().findFirst().orElse(null));

        Assertions.assertEquals("Mensa Campus Innrain", Objects.requireNonNull(loadedLocations.stream().findFirst().orElse(null)).getLocationName());
        loadedLocations.remove(loadedLocations.stream().findFirst().orElse(null));

        Assertions.assertEquals(0, loadedLocations.size());
    }

    @DirtiesContext
    @Test
    public void testSaveLocation() {

        String city = "TestCity";
        String country = "TestCity";
        String description = "TestCity";
        int houseNumber = 1;
        String locationName = "TestCity";
        String menuURL = "TestCity";
        String street = "TestCity";
        int zipCode = 1234;

        Location testLocation = new Location();

        testLocation.setCity(city);
        testLocation.setCountry(country);
        testLocation.setDescription(description);
        testLocation.setHouseNumber(houseNumber);
        testLocation.setActive(true);
        testLocation.setLocationName(locationName);
        testLocation.setMenuUrl(menuURL);
        testLocation.setStreet(street);
        testLocation.setZipCode(zipCode);
        testLocation.setUser(userService.loadUser("admin"));

        locationService.saveLocation(testLocation);

        Assertions.assertEquals(1, locationService.getAllLocations().size());

        Assertions.assertEquals(city, locationService.loadLocation(1).getCity());
        Assertions.assertEquals(country, locationService.loadLocation(1).getCountry());
        Assertions.assertEquals(description, locationService.loadLocation(1).getDescription());
        Assertions.assertEquals(houseNumber, locationService.loadLocation(1).getHouseNumber());
        Assertions.assertTrue(locationService.loadLocation(1).isActive());
        Assertions.assertEquals(locationName, locationService.loadLocation(1).getLocationName());
        Assertions.assertEquals(menuURL, locationService.loadLocation(1).getMenuUrl());
        Assertions.assertEquals(street, locationService.loadLocation(1).getStreet());
        Assertions.assertEquals(zipCode, locationService.loadLocation(1).getZipCode());
    }

    @DirtiesContext
    @Test
    public void testLoadLocation() {
        autoInitLocations.initializeLocations();
        Assertions.assertEquals("Mensa Campus Technik", locationService.loadLocation(1).getLocationName());
        Assertions.assertEquals("Mensa Campus Innrain", locationService.loadLocation(2).getLocationName());

        Assertions.assertEquals("Technikerstrasse", locationService.loadLocation(1).getStreet());
        Assertions.assertEquals("Innrain", locationService.loadLocation(2).getStreet());

        Assertions.assertEquals("Austria", locationService.loadLocation(1).getCountry());
        Assertions.assertEquals("Austria", locationService.loadLocation(2).getCountry());

        Assertions.assertEquals(13, locationService.loadLocation(1).getHouseNumber());
        Assertions.assertEquals(62, locationService.loadLocation(2).getHouseNumber());

        Assertions.assertEquals("Innsbruck", locationService.loadLocation(1).getCity());
        Assertions.assertEquals("Innsbruck", locationService.loadLocation(2).getCity());

        Assertions.assertEquals("Special price for students and staff. Tuesday and thursday offers: Wiener Schnitzel. Accessible to wheelchair users.", locationService.loadLocation(1).getDescription());
        Assertions.assertEquals("Special price for students and staff. Tuesday offer: Käsespätzle. Not accessible to wheelchair users.", locationService.loadLocation(2).getDescription());

        Assertions.assertEquals("www.mensen.at", locationService.loadLocation(1).getMenuUrl());
        Assertions.assertEquals("www.mensen.at", locationService.loadLocation(2).getMenuUrl());

        Assertions.assertEquals(6020, locationService.loadLocation(1).getZipCode());
        Assertions.assertEquals(6020, locationService.loadLocation(2).getZipCode());
    }

    @DirtiesContext
    @Test
    public void testFindLocationByEvents() {
        autoInitLocations.initializeLocations();
        autoInitEvents.initializeEvents();

        Event eventFromDB = eventService.loadEvent(1);
        Location location1 = locationService.loadLocation(1);
        Location location2 = locationService.loadLocation(2);

        Location[] loadedLocationArray = locationService.findLocationsByEvents(eventFromDB).toArray(new Location[0]);

        Assertions.assertEquals(2, locationService.findLocationsByEvents(eventFromDB).size());
        Assertions.assertEquals(location1.getLocationName(), loadedLocationArray[0].getLocationName());
        Assertions.assertEquals(location2.getLocationName(), loadedLocationArray[1].getLocationName());
    }

    @DirtiesContext
    @Test
    public void testFindLocationByName() {
        autoInitLocations.initializeLocations();

        String actualDescriptionLocation1 = "Special price for students and staff. Tuesday and thursday offers: Wiener Schnitzel. Accessible to wheelchair users.";
        String actualDescriptionLocation2 = "Special price for students and staff. Tuesday offer: Käsespätzle. Not accessible to wheelchair users.";

        String loadedDescriptionLocation1 = locationService.findLocationByName("Mensa Campus Technik").getDescription();
        String loadedDescriptionLocation2 = locationService.findLocationByName("Mensa Campus Innrain").getDescription();

        Assertions.assertEquals(actualDescriptionLocation1, loadedDescriptionLocation1);
        Assertions.assertEquals(actualDescriptionLocation2, loadedDescriptionLocation2);
    }

    @DirtiesContext
    @Test
    public void testAddNewLocation() {
        String city = "TestCity";
        String country = "TestCity";
        String description = "TestCity";
        int houseNumber = 1;
        String locationName = "TestCity";
        String menuURL = "TestCity";
        String street = "TestCity";
        int zipCode = 1234;

        Location locationToAdd = new Location();
        locationToAdd.setCity(city);
        locationToAdd.setCountry(country);
        locationToAdd.setDescription(description);
        locationToAdd.setHouseNumber(houseNumber);
        locationToAdd.setActive(true);
        locationToAdd.setLocationName(locationName);
        locationToAdd.setMenuUrl(menuURL);
        locationToAdd.setStreet(street);
        locationToAdd.setZipCode(zipCode);
        locationToAdd.setUser(userService.loadUser("admin"));

        locationService.addNewLocation(locationToAdd);
        Assertions.assertEquals(locationName, locationService.loadLocation(1).getLocationName());
    }
}