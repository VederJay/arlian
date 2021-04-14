package org.arlian.site.tasks;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.arlian.site.start.model.picture.Picture;
import org.arlian.site.start.model.picture.PictureRepository;
import org.arlian.site.start.service.ImageService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class StartupTask {

    private final PictureRepository pictureRepository;
    private final ImageService imageService;

    public StartupTask(PictureRepository pictureRepository, ImageService imageService) {
        this.pictureRepository = pictureRepository;
        this.imageService = imageService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomething() throws ImageWriteException, ImageReadException, IOException {
        List<Picture> pictures = pictureRepository.findAll();

        log.info("Reducing pictures...");
        for (Picture picture : pictures) {
            if(picture.getReducedImage() == null){
                imageService.addImageToPicture(picture, picture.getOriginalImage());
            }
        }

        pictureRepository.saveAll(pictures);
        log.info("Saving all pictures after reducing");
    }
}
