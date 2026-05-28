package com.scm.services.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helpers.AppConstants;
import com.scm.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService{

    @Autowired
    private Cloudinary cloudinary;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String uploadImage(MultipartFile contactImage, String filename) {
        try {
            byte[] data = new byte[contactImage.getInputStream().available()];
            contactImage.getInputStream().read(data);
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                                        "public_id", filename
            ));  
        return this.getUrlFromPublicId(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary.url()
                         .transformation(new Transformation<>()
                                    .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                    .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                    .crop(AppConstants.CONTACT_IMAGE_CROP))
                         .generate(publicId);
    }

    @Override
    public void deleteImage(String publicId) {
      try {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        logger.info("Old image deleted.....");
      } catch (Exception e) {
        e.printStackTrace();
      }    
    }

}
