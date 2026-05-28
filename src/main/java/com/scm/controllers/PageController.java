package com.scm.controllers;

import com.scm.repositories.ContactMessageRepo;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.ContactMessage;
import com.scm.entities.User;
import com.scm.forms.ContactMessageForm;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    private final ContactMessageRepo contactMessageRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    PageController(ContactMessageRepo contactMessageRepo) {
        this.contactMessageRepo = contactMessageRepo;
    }

    @GetMapping("/")
    public String index(){
        
        return "redirect:/home";
    }


    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("homepage handler: ");
        model.addAttribute("name", "Kundan Kumar");
        model.addAttribute("role", "Java Developer");
        model.addAttribute("instaid","https://www.instagram.com/_kunnu.0_0?igsh=cnYwcWN3bmxlMW1v");
        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model){
        System.out.println("aboutpage handler: ");
        model.addAttribute("isLogin", false);
        return "about";
    }

    @RequestMapping("/services")
    public String servicePage(Model model){
        System.out.println("servicepage handler: ");
        return "services";
    }

    @RequestMapping("/contact")
    public String contactPage(Model model){
        System.out.println("contactpage handler: ");
        model.addAttribute("contactMessage", new ContactMessageForm());
        return "contact";
    }

    @RequestMapping(value ="/contact", method =  RequestMethod.POST)
    public String submitContactMessage(@Valid @ModelAttribute("contactMessage") ContactMessageForm form, BindingResult result, Model model, HttpSession session){
      if(result.hasErrors()){
        model.addAttribute("contactMessage", form);
        return "contact";
      }

      ContactMessage msg = new ContactMessage();
      msg.setName(form.getName());
      msg.setEmail(form.getEmail());
      msg.setMessage(form.getMessage());

      contactMessageRepo.save(msg);
      session.setAttribute("message", Message.builder().content("Thanks! Your feedback is submitted.").type(MessageType.green).build());
      return "redirect:/contact";
    }

    // this is showing login page
    @RequestMapping("/login")
    public String loginPage(Model model){
        System.out.println("login handler: ");
        return "login";
    }

    // this is showing registration page
    @RequestMapping("/register")
    public String registerPage(Model model){
        UserForm userForm = new UserForm();
        
        model.addAttribute("userForm", userForm);
        return "register";
    }

    //processing registration
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String  processRegister(@Valid  @ModelAttribute UserForm userForm,BindingResult result, HttpSession session){
        System.out.println("processing registration");
        // validate form data
        if(result.hasErrors()){
            return "register";
        }

        if (userForm.getImage() == null || userForm.getImage().isEmpty()) {
            result.rejectValue("image", "error.image","Image is required");
            return "register";
        }

        String fileName = UUID.randomUUID().toString();
        String imageUrl = imageService.uploadImage(userForm.getImage(), fileName);

           User user = new User();
           user.setName(userForm.getName());
           user.setEmail(userForm.getEmail());
           user.setPhoneNumber(userForm.getPhoneNumber());
           user.setPassword(userForm.getPassword());
           user.setAbout(userForm.getAbout());
           user.setProfilePic(imageUrl);
           userService.saveUser(user);
           System.out.println("user saved :");
        // add the message:
           Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
           session.setAttribute("message", message);
        // redirect page
        return "redirect:/register";
    }
}
