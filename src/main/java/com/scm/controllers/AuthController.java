package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.repositories.UserRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepo;
    
    @RequestMapping("/verify-email")
    public String verifyEmail(@RequestParam("token")String token, HttpSession session){
     User user = userRepo.findByEmailToken(token).orElse(null);
     if(user != null){
        user.setEmailVarified(true);
        user.setEnabled(true);
        user.setPhoneVarified(true);
        user.setEmailToken(null);
        userRepo.save(user);
        session.setAttribute("message", Message.builder().content("Your email is verified. Now you can login ").type(MessageType.green).build());
        return "user/success_page";
     }
        Message message = Message.builder()
                .content("Email not verified !! Something went wrong !!")
                .type(MessageType.red)
                .build();
        session.setAttribute("message", message);        
        return "user/error_page";
    }

}
