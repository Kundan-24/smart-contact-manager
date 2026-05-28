package com.scm.forms;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContactsForm {

    @NotBlank(message = "name is required")
    @Size(min = 3, message = "min 3 characters is required !!")
    private String name;

    @NotBlank(message = "email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",message = "invalid email !!")
    private String email;

    @NotBlank(message = "contact number is required")
    @Size(min = 10, max = 10, message = "invalid contact number !!")
    private String phoneNumber;

    @NotBlank(message = "address is required")
    private String address;
    private String description;
      
    private boolean favorite;
    private String websiteLink;
    private String linkedInLink;
    private MultipartFile contactImage;
    private String picture;
}
