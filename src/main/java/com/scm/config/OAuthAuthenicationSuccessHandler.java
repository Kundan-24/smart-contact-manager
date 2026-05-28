package com.scm.config;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepo;

    private Logger logger = LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, 
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
                logger.info("OAuthAuthenticationSuccessHandler");


                //  identify the provider
             var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
             String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
             logger.info("authorizedClientRegistrationId: {}",authorizedClientRegistrationId);

             var outhUser = (DefaultOAuth2User) authentication.getPrincipal();

             outhUser.getAttributes().forEach((key,value)->{
                logger.info(key + " : " +value);
             });

             User user = new User();
             user.setRoleList(List.of(AppConstants.ROLE_USER));
             user.setEmailVarified(true);
             user.setEnabled(true);
             user.setPassword("dummy");

             if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
                // google
                // google attributes
                user.setEmail(outhUser.getAttribute("email").toString());
                user.setProfilePic(outhUser.getAttribute("picture").toString());
                user.setName(outhUser.getAttribute("name").toString());
                user.setProviderId(outhUser.getName());
                user.setProvider(Providers.GOOGLE);
                user.setAbout("This account is created using google..");
             }else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
                // github
                // github attributes
                String email = outhUser.getAttribute("email") !=null ? outhUser.getAttribute("email").toString() : outhUser.getAttribute("login").toString()+"@gmail.com";
                String picture = outhUser.getAttribute("avatar_url").toString();
                String name = outhUser.getAttribute("login").toString();
                String providerid = outhUser.getName();

                user.setEmail(email);
                user.setProfilePic(picture);
                user.setName(name);
                user.setProviderId(providerid);
                user.setProvider(Providers.GITHUB);
                user.setAbout("This account is created using GitHub..");
             }else{
                logger.info("OAuthAuthenticationSuccessHandler: Unknown provide");
             }
                
                     
            // that means user hai to user ko save nahi karege detabase me agar nahi hai to user ko save kar dege.
            User userByEmail = userRepo.findByEmail(user.getEmail()).orElse(null);

            if (userByEmail == null) {
                userRepo.save(user);
                logger.info("user saved: "+user.getEmail());
            }

               new DefaultRedirectStrategy().sendRedirect(request, response, "/user/dashboard");
    }


}
