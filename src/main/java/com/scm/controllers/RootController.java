package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.UserService;

@ControllerAdvice
public class RootController {


    @Autowired
    private UserService userService;
    Logger logger = LoggerFactory.getLogger(RootController.class);

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication){
        if (authentication == null) {
            return;
        }
        String userName = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("user profile handler principle name: {}",userName);

        // database se data ko fetch : get user from db
        User user = userService.getUserByEmail(userName);
        
            logger.info("get name : {}",user.getName());
            logger.info("get email : {}", user.getEmail());
            model.addAttribute("loggedInUser", user);
       
    }
}
