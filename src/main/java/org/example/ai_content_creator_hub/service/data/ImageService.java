package org.example.ai_content_creator_hub.service.data;

import org.example.ai_content_creator_hub.entity.Image;
import org.example.ai_content_creator_hub.repository.ImageRepository;
import org.example.ai_content_creator_hub.service.data.ContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image createImage(Image image) {
        return imageRepository.save(image);
    }
}
