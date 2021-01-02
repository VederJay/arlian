package org.arlian.site.start.service;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.picture.Picture;
import org.arlian.site.start.model.picture.PictureIdProjection;
import org.arlian.site.start.model.picture.PictureRepository;
import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;

@Service
public class PictureService {

    private final UserService userService;
    private final EntityManager entityManager;
    private final PictureRepository pictureRepository;

    public PictureService(UserService userService, EntityManager entityManager, PictureRepository pictureRepository) {
        this.userService = userService;
        this.entityManager = entityManager;
        this.pictureRepository = pictureRepository;
    }

    public void addPictureIds(Model model, Authentication authentication) {
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());

        List<PictureIdProjection> pictureIds = pictureRepository.findByUser(proxyUser);
        model.addAttribute("pictureIds", pictureIds);


        Random randomizer = new Random();
        long selectedPictureId = pictureIds
                .get(randomizer.nextInt(pictureIds.size()))
                .getId();

        model.addAttribute("selectedPictureId", selectedPictureId);
    }

    public Picture getPictureIfAllowed(long pictureId, Authentication authentication)
            throws BadRequestException {

        Picture picture = pictureRepository.findById(pictureId).orElseThrow(BadRequestException::new);

        if(pictureBelongsToUser(picture, authentication))
            return picture;
        else
            throw new BadRequestException();
    }

    private boolean pictureBelongsToUser(Picture picture, Authentication authentication) {
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        return picture.getUser().getId() == userIdProjection.getId();
    }

    public void deleteLinkIfAllowed(long pictureId, Authentication authentication) throws BadRequestException {

        Picture picture = getPictureIfAllowed(pictureId, authentication);
        pictureRepository.delete(picture);
    }
}
