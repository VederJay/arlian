package org.arlian.site.start.service;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.picture.Orientation;
import org.arlian.site.start.model.picture.Picture;
import org.arlian.site.start.model.picture.PictureIdAndOrientationProjection;
import org.arlian.site.start.model.picture.PictureRepository;
import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import java.util.ArrayList;
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

        // Get a proxy of the user
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());

        // Get the pictures for the user
        List<PictureIdAndOrientationProjection> horizontalPictureProjections = pictureRepository
                .findByUserAndOrientation(proxyUser, Orientation.HORIZONTAL);
        List<PictureIdAndOrientationProjection> verticalPictureProjections = pictureRepository
                .findByUserAndOrientation(proxyUser, Orientation.VERTICAL);

        // Prepare a collection of all pictures
        List<PictureIdAndOrientationProjection> allPictureProjections = new ArrayList<>();
        allPictureProjections.addAll(horizontalPictureProjections);
        allPictureProjections.addAll(verticalPictureProjections);

        // Select a picture or set thereof
        long selectedPictureId1 = 0;
        long selectedPictureId2 = 0;
        // if there's 2 or more vertical pictures, we can pick any random picture and if it's vertical, we'll have
        // a second one to set next to it
        if(verticalPictureProjections.size() >= 2){

            // pick a first picture
            Random randomizer = new Random();
            PictureIdAndOrientationProjection selectedProjection1 = allPictureProjections
                    .get(randomizer.nextInt(allPictureProjections.size()));
            selectedPictureId1 = selectedProjection1.getId();

            // if vertical, pick a second vertical one
            if(selectedProjection1.getOrientation() == Orientation.VERTICAL){
                verticalPictureProjections.remove(selectedProjection1);
                PictureIdAndOrientationProjection selectedProjection2 = verticalPictureProjections
                        .get(randomizer.nextInt(verticalPictureProjections.size()));
                selectedPictureId2 = selectedProjection2.getId();
            }
        }
        // if there's insufficient vertical pictures, pick a horizontal one (if there's one)
        else if(horizontalPictureProjections.size() > 0){
            Random randomizer = new Random();
            PictureIdAndOrientationProjection selectedProjection1 = horizontalPictureProjections
                    .get(randomizer.nextInt(horizontalPictureProjections.size()));
            selectedPictureId1 = selectedProjection1.getId();
        }

        // Add all data to the model
        model.addAttribute("pictureIds", allPictureProjections);
        model.addAttribute("selectedPictureId1", selectedPictureId1);
        model.addAttribute("selectedPictureId2", selectedPictureId2);
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
