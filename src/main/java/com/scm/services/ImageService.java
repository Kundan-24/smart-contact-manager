package com.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String uploadImage(MultipartFile multipartFile, String fileName);

    String getUrlFromPublicId(String publicId);

    void deleteImage(String publicId);
}
