package at.qe.skeleton.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.EmailService;
import at.qe.skeleton.services.EventService;
import at.qe.skeleton.services.UserInvitedService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.UserDetailController;
import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@WebAppConfiguration
public class UserDetailControllerTest {

    public JsfMock jsfMock = new JsfMock();

    @Autowired
    private UserService userService;

    @Autowired
    private UserInvitedService userInvitedService;

    @Autowired
    private EventService eventService;

    @Autowired
    EmailService emailService;

    @Autowired
    ApplicationContext applicationContext;

    UserDetailController controller = new UserDetailController();


    @BeforeEach
    public void init() {
        jsfMock.init(applicationContext);
        controller.setServices(userService,userInvitedService,eventService,emailService);

        EmailService emailServiceMocked = Mockito.mock(EmailService.class);
        controller.setEmailService(emailServiceMocked);
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSettersGettersUser() {
        User user1 = userService.loadUser("user1");
        controller.setUser(user1);

        Assertions.assertEquals("user1",controller.getUser().getUsername());

        User user2 = userService.loadUser("user2");
        controller.setUser(user2);

        Assertions.assertEquals("user2",controller.getUser().getUsername());

        controller.getUser().setUsername("TestUsername");
        controller.doSaveUser();

        Assertions.assertEquals("TestUsername",controller.getUser().getUsername());
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testUserSelectedRoles() {
        User user1 = userService.loadUser("user1");
        controller.setUser(user1);

        Assertions.assertEquals(2,controller.getSelectedRoles().size(),"User1 has 2 roles, LOCATION_MANAGER and USER");

        User user2 = userService.loadUser("user2");
        controller.setUser(user2);

        Assertions.assertEquals(1,controller.getSelectedRoles().size(),"User2 has 1 role, USER");

        controller.setUser(user1);
        Set<String> newRoles = new HashSet<>();
        newRoles.add("ADMIN");
        newRoles.addAll(controller.getSelectedRoles());
        controller.setSelectedRoles(newRoles);

        Assertions.assertEquals(3,controller.getSelectedRoles().size(),"User1 should now have all 3 roles, USER, LOCATION_MANAGER and ADMIN");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user2", authorities = {"USER"})
    public void testGetCurrentUser() {
        Assertions.assertEquals("user2",controller.getCurrentUser().getUsername());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testGetCurrentUser2() {
        Assertions.assertEquals("user1",controller.getCurrentUser().getUsername());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"LOCATION_MANAGER"})
    public void testDoReloadUser() {
        Assertions.assertEquals("user1",controller.getCurrentUser().getUsername());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoBanUser() {
        User user1 = userService.loadUser("user1");
        controller.setUser(user1);
        Assertions.assertTrue(controller.getUser().isEnabled(),"User1 is yet enabled");

        controller.doBanUser();

        Assertions.assertFalse(controller.getUser().isEnabled(),"User1 got banned and now is disabled");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoBanUserCurrentLoggedInUser() {
        User admin = userService.loadUser("admin");
        controller.setUser(admin);
        controller.doBanUser();

        String  messageInfo="Warning";
        String facesMessage="Dear " + admin.getUsername() + " you cannot ban yourself.";


        Assertions.assertTrue(controller.getUser().isEnabled(),"Current logged in admin should not be able to ban himself/herself");
        Assertions.assertEquals(messageInfo,controller.getMessageInfo());
        Assertions.assertEquals(facesMessage,controller.getFacesMessage());
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoUnBanUser() {
        User user1 = userService.loadUser("user1");

        controller.setUser(user1);

        Assertions.assertTrue(controller.getUser().isEnabled(),"User1 is yet enabled");
        Assertions.assertEquals("Active",controller.getUser().getUserStatus(),"User status should be Active");

        controller.doBanUser();

        Assertions.assertFalse(controller.getUser().isEnabled(),"User1 got banned and now is disabled");
        Assertions.assertEquals("Banned",controller.getUser().getUserStatus(),"User status should be banned");

        controller.doUnBanUser();

        Assertions.assertTrue(controller.getUser().isEnabled(),"User1 is now unbanned and enabled set to true again");
        Assertions.assertEquals("Active",controller.getUser().getUserStatus(),"User status is again Active");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoRemoveUser() {
        User user1 = userService.loadUser("user1");
        controller.setUser(user1);

        Assertions.assertTrue(controller.getUser().isEnabled(),"User1 is yet enabled");
        Assertions.assertEquals("Active",controller.getUser().getUserStatus(),"User status should be Active");


        controller.doRemoveUser();

        Assertions.assertFalse(controller.getUser().isEnabled(),"User is now removed and disabled");
        Assertions.assertEquals("Removed",controller.getUser().getUserStatus(),"User status should be Removed");

        controller.doUnBanUser();

        Assertions.assertFalse(controller.getUser().isEnabled(),"Trying to unban user to set him to Active while that user is removed should fail");
        Assertions.assertEquals("Removed",controller.getUser().getUserStatus(),"User status should be Removed");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoRemovedUserCurrentLoggedInUser() {
        User admin = userService.loadUser("admin");
        controller.setUser(admin);
        controller.doRemoveUser();

        String  messageInfo="Warning";
        String  facesMessage="Dear " + admin.getUsername() + " you cannot remove yourself.";


        Assertions.assertTrue(controller.getUser().isEnabled(),"Current logged in admin should not be able to remove himself/herself");
        Assertions.assertEquals(messageInfo,controller.getMessageInfo());
        Assertions.assertEquals(facesMessage,controller.getFacesMessage());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoDeleteUser() {
        User user1 = userService.loadUser("user1");
        controller.setUser(user1);
        controller.doRemoveUser();
        controller.doDeleteUser();

        Assertions.assertNull(controller.getUser());
        Assertions.assertEquals(3,userService.getAllUsers().size());

        User user2 = userService.loadUser("user2");
        controller.setUser(user2);
        controller.doRemoveUser();
        controller.doDeleteUser();

        Assertions.assertNull(controller.getUser());
        Assertions.assertEquals(2,userService.getAllUsers().size());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoDeleteUserFail() {
        User user1 = userService.loadUser("user1");
        controller.setUser(user1);
        controller.doDeleteUser();

        Assertions.assertNotNull(controller.getUser(),"User should not be deleted before user is removed");
        Assertions.assertEquals(4,userService.getAllUsers().size());

        controller.doBanUser();

        Assertions.assertNotNull(controller.getUser(),"User should not be deleted before user is removed, also if banned");
        Assertions.assertEquals(4,userService.getAllUsers().size());
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testIsValidEmail() {
        User user1 = userService.loadUser("user1");
        String alreadyExistingMail = user1.getEmail();

        User user2 = userService.loadUser("user2");
        controller.setUser(user2);
        controller.getUser().setEmail(alreadyExistingMail);
        controller.checkValidEmail();

       String messageInfo="Email Exists";
        String facesMessage="The E-mail: " + controller.getUser().getEmail() + " is already in use! Please choose another one.";

        Assertions.assertEquals(messageInfo,controller.getMessageInfo());
        Assertions.assertEquals(facesMessage,controller.getFacesMessage());

        String validEMail = "testValidEMail@mail.com";
        controller.getUser().setEmail(validEMail);
        controller.checkValidEmail();

        Assertions.assertEquals(validEMail,controller.getUser().getEmail());


    }



}
