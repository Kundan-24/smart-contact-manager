package com.scm.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entities.Contacts;
import com.scm.entities.User;
@Repository
public interface ContactRepo extends JpaRepository<Contacts,Long>{

    // Custom finder method
    Page<Contacts> findByUser(User user, Pageable pageable);

    // Custom Query method
    @Query("SELECT c FROM Contacts c WHERE c.user.id = :userId")
    List<Contacts> findByUserId(@Param("userId") Long userId);

    long countByUserAndFavorite(User user, boolean favorite);
    long countByUser(User user);

    @Query(""" 
        SELECT c FROM Contacts c WHERE c.user = :user
    AND (
        LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR c.phoneNumber LIKE CONCAT('%', :keyword, '%')
    )""")
    Page<Contacts> searchByKeyword(@Param("user") User user, @Param("keyword") String keyword, Pageable pageable);
   

}
