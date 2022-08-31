package at.qe.skeleton.ui.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.qe.skeleton.model.UserInvited;
import at.qe.skeleton.services.UserInvitedService;

@RestController
@RequestMapping("/confirmation")
public class AcceptInvitationController {

    @Autowired
    UserInvitedService userInvitedService;

    @GetMapping
    public void confirmInvite(@RequestParam String token)

    {
        UserInvited userInvited = userInvitedService.findByToken(token);
        
        userInvited.setInvitationAccepted(true);
        userInvitedService.save(userInvited);
    }
}
