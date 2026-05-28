package com.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scm.entities.ContactMessage;

public interface ContactMessageRepo extends JpaRepository<ContactMessage,Long>{

}
