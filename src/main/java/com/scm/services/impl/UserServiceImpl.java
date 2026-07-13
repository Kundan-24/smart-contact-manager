package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private Logger logger=LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        // password encode
       user.setPassword(passwordEncoder.encode(user.getPassword()));
       // set the user role
       user.setRoleList(List.of(AppConstants.ROLE_USER));
       
       
       String emailToken = UUID.randomUUID().toString();
       user.setEmailToken(emailToken);
       User savedUser = userRepo.save(user);
       try {
           String emailLink = Helper.getLinkForEmailVarification(emailToken);
           emailService.sendEmail(savedUser.getEmail(), "Verify Account : Smart Contact Manager", emailLink);
       } catch (Exception e) {
           logger.error("Email sending failed ",e);
       }
        return  savedUser;

       
    }

    @Override
    public Optional<User> getByUserId(Long userId) {
         return userRepo.findById(userId);
    }

    @Override
    public Optional<User> updateUser(User newData) {
        
       User user = userRepo.findById(newData.getUserId()).orElseThrow(()-> new ResourceNotFoundException("user not found"));
        user.setName(newData.getName());
        user.setEmail(newData.getEmail());
        user.setPhoneNumber(newData.getPhoneNumber());
        user.setPassword(newData.getPassword());
        user.setAbout(newData.getAbout());
        user.setProfilePic(newData.getProfilePic());
        user.setEnabled(newData.isEnabled());
        user.setEmailVarified(newData.isEmailVarified());
        user.setPhoneVarified(newData.isPhoneVarified());
        user.setProvider(newData.getProvider());
        user.setProviderId(newData.getProviderId());
        User save = userRepo.save(user);
        return Optional.ofNullable(save);
    }

    @Override
    public void  deleteById(Long userId) {
        User finduser = userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user not found"));
        userRepo.delete(finduser);
    }

    @Override
    public List<User> getAllUsers() {
       return userRepo.findAll();
    }

    @Override
    public boolean isUserExist(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        return user != null ? true : false ;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
       User user = userRepo.findByEmail(email).orElse(null);
       return user !=null ? true : false;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);   
    }

    

}
