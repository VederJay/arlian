package org.arlian.site.tasks;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.link.LinkRepository;
import org.arlian.site.start.service.ImageService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class StartupTask {

    private final LinkRepository linkRepository;
    private final ImageService imageService;

    public StartupTask(LinkRepository linkRepository, ImageService imageService) {
        this.linkRepository = linkRepository;
        this.imageService = imageService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fixMissingThumbnails() throws ImageWriteException, ImageReadException, IOException {
        List<Link> links = linkRepository.findAll();

        for (Link link : links) {
            if(link.getImage() != null && link.getThumbnail() == null){
                byte[] imageBytes = link.getImage();
                imageService.addImageToLink(link, imageBytes);
            }
        }

        linkRepository.saveAll(links);
    }
}
