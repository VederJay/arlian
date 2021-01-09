package org.arlian.site.tasks;

import org.arlian.site.start.model.picture.Picture;
import org.arlian.site.start.model.picture.PictureRepository;
import org.arlian.site.start.service.ImageService;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConvertPictures {

    private final PictureRepository pictureRepository;
    private final ImageService imageService;

    public ConvertPictures(PictureRepository pictureRepository, ImageService imageService) {
        this.pictureRepository = pictureRepository;
        this.imageService = imageService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {

        List<Picture> pictures = pictureRepository.findAllWithMissingOrientation();

        pictures.forEach(picture -> imageService.addImageToPicture(picture, picture.getImage()));

        pictureRepository.saveAll(pictures);

    }

}
