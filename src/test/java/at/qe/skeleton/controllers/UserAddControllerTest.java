package at.qe.skeleton.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.UserAddController;
import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Some very basic tests for {@link UserAddController}.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@SpringBootTest
@WebAppConfiguration
public class UserAddControllerTest {


	public JsfMock jsfMock = new JsfMock();

    @Autowired
    UserService userService;
    
    @Autowired
    ApplicationContext applicationContext;


	@BeforeEach
    public void init() {
    	jsfMock.init(applicationContext);
    	user1 = new User();
    	user1.setFirstName("Pipi");
    	user1.setLastName("Langstrumpf");
    	user1.setUsername("pip4live");
    	user1.setEmail("123pip@gmx.at");
    	user1.setRoles(Sets.newSet(UserRole.USER, UserRole.LOCATION_MANAGER));

    }
    private User user1;
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCheckValidateUsernameFails() {
    	UserAddController controller = new UserAddController();
    	controller.setService(userService);
    	
    	controller.checkValidUsername();
    	Assertions.assertTrue(controller.isValidUsername());
    	userService.addNewUser(user1);
    	controller.checkValidUsername();
    	Assertions.assertFalse(controller.isValidUsername());
    	Assertions.assertEquals("Username Exists", controller.getMessageInfo());


    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCheckValidateUsernameSuccess() {
    	UserAddController controller = new UserAddController();
    	controller.setService(userService);
    	
    	controller.checkValidUsername();
    	Assertions.assertTrue(controller.isValidUsername());
    	controller.checkValidUsername();
    	Assertions.assertTrue(controller.isValidUsername());


    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCheckValidateEmailFails() {
    	UserAddController controller = new UserAddController();
    	controller.setService(userService);
    	
    	controller.checkValidEmail();
    	Assertions.assertTrue(controller.isValidEmail());
    	userService.addNewUser(user1);
    	controller.checkValidEmail();
    	Assertions.assertFalse(controller.isValidEmail());
    	Assertions.assertEquals("Email Exists", controller.getMessageInfo());

    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCheckValidateEmailSuccess() {
    	UserAddController controller = new UserAddController();
    	controller.setService(userService);
    	controller.checkValidEmail();
    	Assertions.assertTrue(controller.isValidEmail());

    }
}