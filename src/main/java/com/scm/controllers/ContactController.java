package com.scm.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contacts;
import com.scm.entities.User;
import com.scm.forms.ContactsForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {
     
    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping("/add")
    public String addContactView(Model model){
        ContactsForm contactsForm = new ContactsForm();   
        model.addAttribute("contactForm", contactsForm);   
        return "user/add_contact";
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String addContactProcessing(@Valid @ModelAttribute("contactForm") ContactsForm contactsForm, BindingResult bindingResult, Authentication authentication, HttpSession session){
       if (bindingResult.hasErrors()) {
        bindingResult.getAllErrors().forEach(error-> logger.info(error.toString()));
           Message message = Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build();
          session.setAttribute("message",message );
        return "user/add_contact";
       }
       String userName = Helper.getEmailOfLoggedInUser(authentication);
       User user = userService.getUserByEmail(userName);
      
       // image uploading process
       String fileName = UUID.randomUUID().toString();
       String fileURL = null;
       if (contactsForm.getContactImage() !=null && !contactsForm.getContactImage().isEmpty()) {
        fileURL = imageService.uploadImage(contactsForm.getContactImage(), fileName);
       }
         
     Contacts contacts = new Contacts();
         contacts.setName(contactsForm.getName());
         contacts.setEmail(contactsForm.getEmail());
         contacts.setPhoneNumber(contactsForm.getPhoneNumber());
         contacts.setAddress(contactsForm.getAddress());
         contacts.setDescription(contactsForm.getDescription());
         contacts.setFavorite(contactsForm.isFavorite());
         contacts.setWebsiteLink(contactsForm.getWebsiteLink());
         contacts.setLinkedInLink(contactsForm.getLinkedInLink());
         contacts.setPicture(fileURL);
         contacts.setCloudinaryImagePublicId(fileURL != null ? fileName : null);
         contacts.setUser(user);
         contactService.saveContact(contacts);
         Message message = Message.builder()
                                    .content("You have successfully added a new contact")
                                    .type(MessageType.green)
                                    .build();
         session.setAttribute("message", message);
        return "redirect:/user/contacts/add";
    }

    @RequestMapping
    public String viewContacts(
          @RequestParam(value = "pageNum", defaultValue = "0", required = false) int pageNum,
          @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
          @RequestParam(value = "dir", defaultValue = "asc", required = false) String dir, 
          @RequestParam(value = "keyword",required = false) String keyword,
          Model model, Authentication authentication){
        String userName = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(userName);

        Page<Contacts> pageContact;

        if (keyword != null && !keyword.trim().isBlank()) {
           pageContact = contactService.searchContacts(user, keyword, pageNum, pageSize, sortBy, dir);
        }else{
          pageContact = contactService.getByUser(user,pageNum,pageSize,sortBy,dir);
        }

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("keyword", keyword);
        return "user/contacts";
    }

    @RequestMapping("/delete/{id}")
    public String deleteContacts(@PathVariable Long id){
       contactService.delete(id);
       logger.info("contact is deleted");
       return "redirect:/user/contacts";
    }

    @GetMapping("/view/{id}")
    public String updateContactFormView(@PathVariable Long id, Model model){
       Contacts contact = contactService.getById(id);
       ContactsForm contactsForm = new ContactsForm();
       contactsForm.setName(contact.getName());
       contactsForm.setEmail(contact.getEmail());
       contactsForm.setPhoneNumber(contact.getPhoneNumber());
       contactsForm.setAddress(contact.getAddress());
       contactsForm.setDescription(contact.getDescription());
       contactsForm.setLinkedInLink(contact.getLinkedInLink());
       contactsForm.setWebsiteLink(contact.getWebsiteLink());
       contactsForm.setFavorite(contact.isFavorite());
       contactsForm.setPicture(contact.getPicture());
       model.addAttribute("contactsForm", contactsForm);
       model.addAttribute("id", id);
      return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String updateContact(@PathVariable Long id,@Valid  @ModelAttribute ContactsForm contactsForm, BindingResult bindingResult){
       if (bindingResult.hasErrors()) {
         return "user/update_contact_view";
       }
      Contacts contacts = contactService.getById(id);
       
       contacts.setName(contactsForm.getName());
       contacts.setEmail(contactsForm.getEmail());
       contacts.setPhoneNumber(contactsForm.getPhoneNumber());
       contacts.setAddress(contactsForm.getAddress());
       contacts.setDescription(contactsForm.getDescription());
       contacts.setLinkedInLink(contactsForm.getLinkedInLink());
       contacts.setWebsiteLink(contactsForm.getWebsiteLink());
       contacts.setFavorite(contactsForm.isFavorite());
       // image update only if new image selected
       if (contactsForm.getContactImage() != null && !contactsForm.getContactImage().isEmpty()) {
          // Delete Old Image
          if (contacts.getCloudinaryImagePublicId() != null) {
            imageService.deleteImage(contacts.getCloudinaryImagePublicId());
          }
        // Upload New Image  
        String fileName = UUID.randomUUID().toString();
        String imageUrl = imageService.uploadImage(contactsForm.getContactImage(), fileName);
        contacts.setPicture(imageUrl);
        contacts.setCloudinaryImagePublicId(fileName);
       }
       // save update contact
       contactService.updateContact(contacts);
     return "redirect:/user/contacts";
    }

    @GetMapping("/export")
    public void exportContacts(Authentication authentication,HttpServletResponse response) throws IOException {

    String userName = Helper.getEmailOfLoggedInUser(authentication);
    User user = userService.getUserByEmail(userName);

    List<Contacts> contacts = contactService.getByUserId(user.getUserId());

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=contacts.xlsx");

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Contacts");

    // Header Row
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("Name");
    header.createCell(1).setCellValue("Email");
    header.createCell(2).setCellValue("Phone");
    header.createCell(3).setCellValue("Address");
    header.createCell(4).setCellValue("Favorite");
    header.createCell(5).setCellValue("Website");
    header.createCell(6).setCellValue("LinkedIn");

    int rowNum = 1;

    for (Contacts c : contacts) {
        Row row = sheet.createRow(rowNum++);

        row.createCell(0).setCellValue(c.getName());
        row.createCell(1).setCellValue(c.getEmail());
        row.createCell(2).setCellValue(c.getPhoneNumber());
        row.createCell(3).setCellValue(c.getAddress());
        row.createCell(4).setCellValue(c.isFavorite());
        row.createCell(5).setCellValue(c.getWebsiteLink());
        row.createCell(6).setCellValue(c.getLinkedInLink());
    }

    workbook.write(response.getOutputStream());
    workbook.close();
}
}
