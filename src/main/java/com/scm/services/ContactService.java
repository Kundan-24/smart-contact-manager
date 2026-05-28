package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;
import com.scm.entities.Contacts;
import com.scm.entities.User;

public interface ContactService {
    
    Contacts saveContact(Contacts contact);

    Contacts updateContact(Contacts contact);

    List<Contacts> getAll();

    Contacts getById(Long id);

    void delete(Long id);


    List<Contacts> getByUserId(Long id);

    Page<Contacts> getByUser(User user, int page, int size,String sortBy,String dir);

    Page<Contacts> searchContacts(User user, String keyword, int pageNum, int pageSize, String sortBy, String dir);

    long countFavorites(User user);
    
    long countContacts(User user);
}
