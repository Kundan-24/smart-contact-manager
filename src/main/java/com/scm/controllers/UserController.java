package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.ContactService;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;


    // user dashboard page
    @RequestMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

      String email = Helper.getEmailOfLoggedInUser(authentication);
      User user = userService.getUserByEmail(email);

      long totalContacts = contactService.countContacts(user);
      long favoriteContacts = contactService.countFavorites(user);

      model.addAttribute("totalContacts", totalContacts);
      model.addAttribute("favoriteContacts", favoriteContacts);
      model.addAttribute("user", user);

      return "user/dashboard";
    }

    // user profile page
    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication){
        System.out.println("user profile handler");
        return "user/profile";
    }
}
