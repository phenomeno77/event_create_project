package at.qe.skeleton.tests;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalTime;
import java.util.Collection;

/**
 * Test-Class for {@link at.qe.skeleton.services.OpeningHoursService}
 */
@SpringBootTest
@WebAppConfiguration
@WithMockUser(username = "admin", authorities = {"ADMIN"})
public class OpeningHoursServiceTest {
    @Autowired
    private AutoInitLocations autoInitLocations;
    @Autowired
    private OpeningHoursService openingHoursService;
    @Autowired
    private LocationService locationService;

    @DirtiesContext
    @Test
    public void testGetAllOpeningHours() {
        autoInitLocations.initializeLocations();
        Collection<OpeningHours> loadedOpeningHours = openingHoursService.getAllOpeningHours();

        Assertions.assertEquals(14, loadedOpeningHours.size());
    }

    @DirtiesContext
    @Test
    public void testLoadOpeningHours() {
        autoInitLocations.initializeLocations();
        OpeningHours loadedOpeningHour1 = openingHoursService.loadOpeningHours(1);
        OpeningHours loadedOpeningHour2 = openingHoursService.loadOpeningHours(2);
        OpeningHours loadedOpeningHour3 = openingHoursService.loadOpeningHours(3);

        Assertions.assertEquals("10:00", loadedOpeningHour1.getOpeningTime().toString());
        Assertions.assertEquals("10:00", loadedOpeningHour2.getOpeningTime().toString());
        Assertions.assertEquals("10:00", loadedOpeningHour3.getOpeningTime().toString());

        Assertions.assertEquals("15:00", loadedOpeningHour1.getBreakTimeStart().toString());
        Assertions.assertEquals("15:00", loadedOpeningHour1.getBreakTimeStart().toString());
        Assertions.assertEquals("15:00", loadedOpeningHour1.getBreakTimeStart().toString());

        Assertions.assertEquals("18:00", loadedOpeningHour1.getBreakTimeEnd().toString());
        Assertions.assertEquals("18:00", loadedOpeningHour1.getBreakTimeEnd().toString());
        Assertions.assertEquals("18:00", loadedOpeningHour1.getBreakTimeEnd().toString());

        Assertions.assertEquals("23:00", loadedOpeningHour1.getClosingTime().toString());
        Assertions.assertEquals("23:00", loadedOpeningHour2.getClosingTime().toString());
        Assertions.assertEquals("23:00", loadedOpeningHour3.getClosingTime().toString());
    }

    @DirtiesContext
    @Test
    public void testSaveOpeningHours() {
        OpeningHours openingHourToSave = new OpeningHours();
        openingHourToSave.setDay(1);
        openingHourToSave.setOpeningTime(LocalTime.of(8, 0));
        openingHourToSave.setBreakTimeStart(LocalTime.of(12, 0));
        openingHourToSave.setBreakTimeEnd(LocalTime.of(13, 0));
        openingHourToSave.setClosingTime(LocalTime.of(18, 0));

        openingHoursService.saveOpeningHours(openingHourToSave);

        Assertions.assertEquals(1, openingHoursService.getAllOpeningHours().size());
        Assertions.assertEquals("08:00", openingHoursService.loadOpeningHours(1).getOpeningTime().toString());
        Assertions.assertEquals("12:00", openingHoursService.loadOpeningHours(1).getBreakTimeStart().toString());
        Assertions.assertEquals("13:00", openingHoursService.loadOpeningHours(1).getBreakTimeEnd().toString());
        Assertions.assertEquals("18:00", openingHoursService.loadOpeningHours(1).getClosingTime().toString());
    }

    @DirtiesContext
    @Test
    public void testAddNewOpeningHourToLocation() {
        Location locationToAdd = new Location();
        locationToAdd.setLocationName("TestLocation");

        Location savedLocation = locationService.saveLocation(locationToAdd);

        OpeningHours openingHourToAdd = new OpeningHours();
        openingHourToAdd.setDay(1);
        openingHourToAdd.setOpeningTime(LocalTime.of(8, 0));
        openingHourToAdd.setBreakTimeStart(LocalTime.of(12, 0));
        openingHourToAdd.setBreakTimeEnd(LocalTime.of(13, 0));
        openingHourToAdd.setClosingTime(LocalTime.of(18, 0));
        openingHourToAdd.setLocation(savedLocation);

        openingHoursService.saveOpeningHours(openingHourToAdd);

        Assertions.assertEquals("TestLocation", openingHoursService.loadOpeningHours(1).getLocation().getLocationName());
        Assertions.assertEquals("08:00", openingHoursService.loadOpeningHours(1).getOpeningTime().toString());
        Assertions.assertEquals("12:00", openingHoursService.loadOpeningHours(1).getBreakTimeStart().toString());
        Assertions.assertEquals("13:00", openingHoursService.loadOpeningHours(1).getBreakTimeEnd().toString());
        Assertions.assertEquals("18:00", openingHoursService.loadOpeningHours(1).getClosingTime().toString());
    }

    @DirtiesContext
    @Test
    public void testDeleteOpeningHourByLocation() {
        autoInitLocations.initializeLocations();
        Assertions.assertEquals(14, openingHoursService.getAllOpeningHours().size());

        openingHoursService.deleteByLocation(locationService.loadLocation(1));
        Assertions.assertEquals(7, openingHoursService.getAllOpeningHours().size());

    }
}
