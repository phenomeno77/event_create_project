package at.qe.skeleton.controllers;



import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;


import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;
import at.qe.skeleton.services.TagService;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import at.qe.skeleton.ui.controllers.LocationDetailController;


/**
 * Some very basic tests for {@link LocationDetailController}.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@SpringBootTest
public class LocationDetailControllerTest {


	public JsfMock jsfMock = new JsfMock();

 
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private OpeningHoursService openingHourService;
    
    
    @Autowired
    private AutoInitLocations ailocation;
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    ApplicationContext applicationContext;
    
    @BeforeEach
    public void init() {
    	jsfMock.init(applicationContext);
    }
    
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoCloseLocation() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);
   

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Innrain"));
    	controller.doCloseLocation();

    	Assertions.assertEquals("Location saved.", controller.getMessageErrorHeader());
    	
    }
    
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoOpenLocation() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.doCloseLocation();
    	Assertions.assertEquals("Location saved.", controller.getMessageErrorHeader());
    	controller.doOpenLocation();
    	Assertions.assertEquals("Location saved.", controller.getMessageErrorHeader());
    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoRemoveLocation() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.doRemoveLocation();
    	Assertions.assertEquals("Warning status: inactive", controller.getMessageErrorHeader());
    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoSaveLocationEmptyList() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.getHour(1);
    	controller.getHour(2);
    	
    	for(int i = 0; i< 7; i++) {
    		controller.setListsForTest(controller.new StringWrapperDummy(""), controller.new StringWrapperDummy(""), i);
    	}
    	
    	controller.doSaveLocation();
    	Assertions.assertEquals("OpeningHours are missing.", controller.getMessageErrorHeader());
    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoSaveLocationOHWrongOrder() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.getHour(1);
    	controller.getHour(2);
    	
    	for(int i = 0; i< 6; i++) {
    		controller.setListsForTest(controller.new StringWrapperDummy("08:00-12:00"), controller.new StringWrapperDummy("15:00-21:00"), i);
    	}
		controller.setListsForTest(controller.new StringWrapperDummy("14:00-12:00"), controller.new StringWrapperDummy("15:00-21:00"), 6);


    	controller.doSaveLocation();
    	Assertions.assertEquals("OpeningHours are NOT in order.", controller.getMessageErrorHeader());

    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoSaveLocationMapOpeningHoursSpecialCase() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.getHour(1);
    	controller.getHour(2);
    	
    	for(int i = 0; i< 6; i++) {
    		controller.setListsForTest(controller.new StringWrapperDummy(""), controller.new StringWrapperDummy("15:00-21:00"), i);
    	}
		controller.setListsForTest(controller.new StringWrapperDummy("11:00-14:00"), controller.new StringWrapperDummy("15:00-21:00"), 6);


    	controller.doSaveLocation();
    	Assertions.assertEquals("Location saved.", controller.getMessageErrorHeader());

    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testValideMenuURL() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.getLocation().setMenuUrl("http://www.mensa.at");
    	controller.doSaveLocation();
    	Assertions.assertEquals("MENU URL", controller.getMessageErrorHeader());

    }
    
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoSaveLocation() {
    	LocationDetailController controller = new LocationDetailController();
    	controller.setServices(locationService, openingHourService, tagService);

    	ailocation.initializeLocations();
    	controller.setLocation(locationService.findLocationByName("Mensa Campus Technik"));
    	controller.getHour(1);
    	controller.getHour(2);
    	
    	for(int i = 0; i< 7; i++) {
    		controller.setListsForTest(controller.new StringWrapperDummy("08:00-12:00"), controller.new StringWrapperDummy("15:00-21:00"), i);
    	}

    	controller.doSaveLocation();
    	Assertions.assertEquals("Location saved.", controller.getMessageErrorHeader());

    }
    
    
}
