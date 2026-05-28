package com.scm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public class Helper {
        
    public static String getEmailOfLoggedInUser(Authentication authentication)
    {

       
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2AuthenticatedPrincipal) {
            var aOuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
            String authorizedClientId = aOuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            var oauth2Principle = (OAuth2AuthenticatedPrincipal) principal;

            
            if (authorizedClientId.equalsIgnoreCase("google")) {
             // google login
             System.out.println("Getting emailId from Google");
              return oauth2Principle.getAttribute("email");
            }
            else if (authorizedClientId.equalsIgnoreCase("github")) {    
                // github login
                 System.out.println("Getting emailId from GitHub");
                 Object email = oauth2Principle.getAttribute("email");
                 if (email == null) {
                       Object login = oauth2Principle.getAttribute("login");
                       email = login + "@gmail.com";
                 }
                 return email.toString().toLowerCase();
            }
            
        } else{
            // self login
            System.out.println("Getting emailId from SELF");
           return authentication.getName();
        }
       return "null";
    }

    public static String getLinkForEmailVarification(String emailToken){
      String link ="http://localhost:9090/auth/verify-email?token="+ emailToken;
      return link;
    }
}
