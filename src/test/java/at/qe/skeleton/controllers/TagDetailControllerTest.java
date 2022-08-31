package at.qe.skeleton.controllers;



import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.TagService;
import at.qe.skeleton.ui.beans.AutoInitLocations;

import at.qe.skeleton.ui.controllers.TagsDetailController;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;





@WebAppConfiguration
@SpringBootTest
public class TagDetailControllerTest {


    @Autowired
    private TagService tagService;
    
    @Autowired
    private AutoInitLocations autoInitLocations;

   
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoRemoveTag() {
    	TagsDetailController controller = new TagsDetailController();
    	controller.setService(tagService);

    	autoInitLocations.initializeLocations();
    	
    	Assertions.assertEquals(5, tagService.getAllTags().size());
    	Tags tag = new Tags();
    	tag.setTagName("vegan");
    	controller.setTags(tag);
    	controller.removeTag();
        Assertions.assertNull(controller.getTags());


    }
    
    
    
    




    
    

}