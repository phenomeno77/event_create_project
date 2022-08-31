package at.qe.skeleton.controllers;



import at.qe.skeleton.services.TagService;
import at.qe.skeleton.ui.beans.AutoInitLocations;

import at.qe.skeleton.ui.controllers.TagsAddController;

import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;


import java.util.ArrayList;
import java.util.List;



@WebAppConfiguration
@SpringBootTest
public class TagAddControllerTest {


    @Autowired
    private TagService tagService;
    
    @Autowired
    private AutoInitLocations autoInitLocations;

   
    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoSaveTagsWithDuplicates() {
    	TagsAddController controller = new TagsAddController();
    	controller.setService(tagService);

    	autoInitLocations.initializeLocations();
    	
    	Assertions.assertEquals(5, tagService.getAllTags().size());
    	List<String> tagList = new ArrayList<>();
    	tagList.add("tag 1");
    	tagList.add("vegan");
    	controller.setTags(tagList);
    	controller.doSaveTags();
    	Assertions.assertEquals(6, tagService.getAllTags().size());

    }
    
    
    
    




    
    

}