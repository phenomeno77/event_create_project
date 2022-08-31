package at.qe.skeleton.tests;


import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.TagService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import at.qe.skeleton.ui.beans.AutoInitTags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Test-Class for {@link at.qe.skeleton.services.TagService}
 */
@SpringBootTest
@WebAppConfiguration
@WithMockUser(username = "admin", authorities = {"ADMIN"})
public class TagServiceTest {
    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Autowired
    LocationService locationService;

    @Autowired
    AutoInitLocations autoInitLocations;

    @Autowired
    AutoInitTags autoInitTags;

    @DirtiesContext
    @Test
    public void testSaveTag() {
        Tags newTag = new Tags();
        newTag.setTagName("TestTag");
        tagService.saveTag(newTag);
        Assertions.assertEquals("TestTag", tagService.loadTag(1).getTagName());
    }

    @DirtiesContext
    @Test
    public void testAddNewTagToNewLocation() {
        String city = "TestCity";
        String country = "TestCity";
        String description = "TestCity";
        int houseNumber = 1;
        String locationName = "TestCity";
        String menuURL = "TestCity";
        String street = "TestCity";
        int zipCode = 1234;

        Tags newTag = new Tags();
        newTag.setTagName("TestTag");
        
        Tags savedTag = tagService.saveTag(newTag);

        Set<Tags> tagSet = new HashSet<>();
        tagSet.add(savedTag);

        Location newLocation = new Location();

        newLocation.setCity(city);
        newLocation.setCountry(country);
        newLocation.setDescription(description);
        newLocation.setHouseNumber(houseNumber);
        newLocation.setActive(true);
        newLocation.setLocationName(locationName);
        newLocation.setMenuUrl(menuURL);
        newLocation.setStreet(street);
        newLocation.setZipCode(zipCode);
        newLocation.setUser(userService.loadUser("admin"));

        newLocation.setLocationTags(tagSet);

        locationService.saveLocation(newLocation);

        Assertions.assertEquals("TestTag", Objects.requireNonNull(locationService.loadLocation(1).getLocationTags().stream().findFirst().orElse(null)).getTagName());
    }

    @DirtiesContext
    @Test
    public void testAddNewTagsToExistingLocation() {
        Tags newTag = new Tags();
        newTag.setTagName("TestTag");
        Tags savedTag = tagService.saveTag(newTag);

        autoInitLocations.initializeLocations();
        Location locationFromDb = locationService.loadLocation(1);

        Set<Tags> existingTags = locationFromDb.getLocationTags();
        int sizeBefore = existingTags.size();
        existingTags.add(savedTag);

        locationFromDb.setLocationTags(existingTags);
        locationService.saveLocation(locationFromDb);

        Assertions.assertTrue(locationService.loadLocation(1).getLocationTags().contains(savedTag));
        Assertions.assertEquals(sizeBefore + 1, locationService.loadLocation(1).getLocationTags().size());
    }

    @DirtiesContext
    @Test
    public void testGetAllTags() {
        autoInitTags.initializeTags();
        Tags tagFromDb1 = tagService.loadTag(1);
        Tags tagFromDb2 = tagService.loadTag(2);
        Tags tagFromDb3 = tagService.loadTag(3);

        Collection<Tags> tagsCollection = tagService.getAllTags();

        Assertions.assertEquals(3, tagsCollection.size());

        Assertions.assertTrue(tagsCollection.contains(tagFromDb1));
        Assertions.assertTrue(tagsCollection.contains(tagFromDb2));
        Assertions.assertTrue(tagsCollection.contains(tagFromDb3));
    }

    @DirtiesContext
    @Test
    public void testLoadTags() {
        autoInitTags.initializeTags();

        Tags loadedTag1 = tagService.loadTag(1);
        Tags loadedTag2 = tagService.loadTag(2);
        Tags loadedTag3 = tagService.loadTag(3);

        Assertions.assertEquals("testTag1", loadedTag1.getTagName());
        Assertions.assertEquals("testTag2", loadedTag2.getTagName());
        Assertions.assertEquals("testTag3", loadedTag3.getTagName());

        Assertions.assertEquals(1, loadedTag1.getTagId());
        Assertions.assertEquals(2, loadedTag2.getTagId());
        Assertions.assertEquals(3, loadedTag3.getTagId());

    }

}
