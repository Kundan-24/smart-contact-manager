package com.scm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.entities.Contacts;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.ContactRepo;
import com.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService{

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contacts saveContact(Contacts contact) {
      return contactRepo.save(contact);
    }

    @Override
    public Contacts updateContact(Contacts contact) {
      Contacts contacts = contactRepo.findById(contact.getId()).orElseThrow(()-> new ResourceNotFoundException("Contact not found with given Id"));
      contacts.setName(contact.getName());
      contacts.setEmail(contact.getEmail());
      contacts.setPhoneNumber(contact.getPhoneNumber());
      contacts.setAddress(contact.getAddress());
      contacts.setDescription(contact.getDescription());
      contacts.setFavorite(contact.isFavorite());
      contacts.setWebsiteLink(contact.getWebsiteLink());
      contacts.setLinkedInLink(contact.getLinkedInLink());
      contacts.setPicture(contact.getPicture());
      contacts.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
      
      return contactRepo.save(contacts);
    }

    @Override
    public List<Contacts> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contacts getById(Long id) {
        return contactRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with given Id"));
    }

    @Override
    public void delete(Long id) {
        Contacts contact = contactRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("contact not found with given Id"));
        contactRepo.delete(contact);
    }

   

    @Override
    public List<Contacts> getByUserId(Long id) {
       return contactRepo.findByUserId(id);
    }

    

    @Override
    public Page<Contacts> getByUser(User user, int page, int size, String sortBy, String dir) {
         Sort sort = dir.equals("desc")? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
         PageRequest pagerequest = PageRequest.of(page, size, sort);
        return contactRepo.findByUser(user,pagerequest);
    }

    @Override
    public Page<Contacts> searchContacts(User user, String keyword, int pageNum, int pageSize, String sortBy,String dir) {
    Sort sort = dir.equalsIgnoreCase("desc")? Sort.by(sortBy).descending(): Sort.by(sortBy).ascending();

     PageRequest pagerequest = PageRequest.of(pageNum, pageSize, sort);

    return contactRepo.searchByKeyword(user, keyword, pagerequest);
    }

    @Override
    public long countFavorites(User user) {
        return contactRepo.countByUserAndFavorite(user, true);
    }

    @Override
    public long countContacts(User user) {
        return contactRepo.countByUser(user);
    }

    

}
