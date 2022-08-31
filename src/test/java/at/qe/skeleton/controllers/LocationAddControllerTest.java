package at.qe.skeleton.controllers;


import at.qe.skeleton.model.Location;
import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;
import at.qe.skeleton.services.TagService;
import at.qe.skeleton.ui.controllers.LocationAddController;
import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;



/**
 * Some very basic tests for {@link LocationAddController}.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@WebAppConfiguration
@SpringBootTest
public class LocationAddControllerTest {


	public JsfMock jsfMock = new JsfMock();

	 	
    @Autowired
    private LocationService locationService;

    @Autowired
    private OpeningHoursService openingHourService;

    @Autowired
    private TagService tagService;
    
    
    private Location location;
    
    @Autowired
    ApplicationContext applicationContext;
    
	@BeforeEach
    public void init() {
    	jsfMock.init(applicationContext);
    }

    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testAddNewLocationDublicate() throws IOException {
    	LocationAddController controller = new LocationAddController();
    	controller.setServices(locationService, openingHourService, tagService);
    	
    	for(int i = 0; i <7 ; i++) {
    		controller.setHour("08:00 - 13:00");
        	controller.setHour("14:00 - 15:00");
    	}
    	
    	location = controller.getLocation();
    	location.setLocationName("Victorias");
    	location.setDescription("blabla");
    	location.setStreet("Innrain"); 
    	location.setHouseNumber(50);
    	location.setCity("Innsbruck");
    	location.setZipCode(6020);
    	location.setCountry("Austria");
    	location.setMenuUrl("");

    	controller.setLocation(location);
    	
       	controller.addNewLocation();

    	Assertions.assertEquals(1,locationService.getAllLocations().size());
    	
    	
    	
    	for(int i = 0; i <7 ; i++) {
    		controller.setHour("08:00 - 13:00");
        	controller.setHour("14:00 - 15:00");
    	}
    	
    	location = controller.getLocation();
    	location.setLocationName("Victorias");
    	location.setDescription("blabla");
    	location.setStreet("Innrain"); 
    	location.setHouseNumber(50);
    	location.setCity("Innsbruck");
    	location.setZipCode(6020);
    	location.setCountry("Austria");

    	controller.setLocation(location);
       	controller.addNewLocation();


    	Assertions.assertEquals("Creating Location not successful", controller.getMessageErrorHeader());

    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testAddNewLocationOHEmptyValidation() throws IOException {
    	LocationAddController controller = new LocationAddController();
    	controller.setServices(locationService, openingHourService, tagService);
    	
    	for(int i = 0; i < 7; i++) {
    		controller.setHour("");
    	}
    	
    	
    	location = controller.getLocation();
    	
    	location.setLocationName("Katzung");
    	location.setDescription("blabla");
    	location.setStreet("Museumsstraße"); 
    	location.setHouseNumber(23);
    	location.setCity("Innsbruck");
    	location.setZipCode(6020);
    	location.setCountry("Austria");

    	controller.setLocation(location);

    	controller.addNewLocation();

    	Assertions.assertEquals("Opening Hours missing!", controller.getMessageErrorHeader());
    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testAddNewLocationOHNotInRightOrder() throws IOException {
    	LocationAddController controller = new LocationAddController();
    	controller.setServices(locationService, openingHourService, tagService);

    	
    	for(int i = 0; i < 6; i++) {
    		controller.setHour("");
    	}
    	controller.setHour("14:00 - 10:00");
    	controller.setHour("15:00 - 21:00");
    	
    	
    	location = controller.getLocation();
    	
    	location.setLocationName("Wiener Kaffee");
    	location.setDescription("blabla");
    	location.setStreet("Josef-Geisen-Gasse"); 
    	location.setHouseNumber(2);
    	location.setCity("Brixen");
    	location.setZipCode(39042);
    	location.setCountry("Austria");

    	controller.setLocation(location);

    	controller.addNewLocation();

    	Assertions.assertEquals("Opening Hours out of order!", controller.getMessageErrorHeader());

    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testValidateOpeningHoursOrder() {
    	LocationAddController controller = new LocationAddController();
    	controller.setServices(locationService, openingHourService, tagService);

    	LocalTime open = LocalTime.parse("09:00", DateTimeFormatter.ofPattern("HH:mm"));
    	LocalTime breakS = LocalTime.parse("12:00", DateTimeFormatter.ofPattern("HH:mm"));
    	LocalTime breakE = LocalTime.parse("14:00", DateTimeFormatter.ofPattern("HH:mm"));
    	LocalTime close = LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm"));

    	
    	Assertions.assertTrue(controller.validateOpeningHoursOrder(open, breakS, breakE, close));
    	
    	open = LocalTime.parse("13:00", DateTimeFormatter.ofPattern("HH:mm"));
    	breakS = LocalTime.parse("12:00", DateTimeFormatter.ofPattern("HH:mm"));
    	breakE = LocalTime.parse("14:00", DateTimeFormatter.ofPattern("HH:mm"));
    	close = LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm"));

    	
    	Assertions.assertFalse(controller.validateOpeningHoursOrder(open, breakS, breakS, close));
    	
    	open = LocalTime.parse("09:00", DateTimeFormatter.ofPattern("HH:mm"));
    	close = LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm"));

    	
    	Assertions.assertTrue(controller.validateOpeningHoursOrder(open, null, null, close));
    	
    	

    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testValidateMenuURL() throws IOException {
    	LocationAddController controller = new LocationAddController();
    	controller.setServices(locationService, openingHourService, tagService);
    	
    	for(int i = 0; i <7 ; i++) {
    		controller.setHour("08:00 - 13:00");
        	controller.setHour("14:00 - 15:00");
    	}
    	
    	location = controller.getLocation();
    	location.setLocationName("Mensa");
    	location.setStreet("Innrain"); 
    	location.setHouseNumber(52);
    	location.setCity("Innsbruck");
    	location.setZipCode(6020);
    	location.setCountry("Austria");
    	location.setMenuUrl("http://www.mensen.at");

    	controller.setLocation(location);
    	
       	controller.addNewLocation();


    	
    	Assertions.assertEquals("Menu URL", controller.getMessageErrorHeader());

    }
    	
    	
    
    
    
    




    
    

}