package org.arlian.site.start.service;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.picture.*;
import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PictureService {

    private final UserService userService;
    private final EntityManager entityManager;
    private final PictureRepository pictureRepository;
    private final UserPictureGroupLinkRepository userPictureGroupLinkRepository;

    public PictureService(UserService userService, EntityManager entityManager, PictureRepository pictureRepository,
                          UserPictureGroupLinkRepository userPictureGroupLinkRepository) {
        this.userService = userService;
        this.entityManager = entityManager;
        this.pictureRepository = pictureRepository;

        this.userPictureGroupLinkRepository = userPictureGroupLinkRepository;
    }

    public void addPictureIds(Model model, Authentication authentication) {

        // Get a proxy of the user
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());

        // Get the pictures for the user
        List<PictureIdAndOrientationProjection> horizontalPictureProjections = new ArrayList<>();
        List<PictureIdAndOrientationProjection> ownedHorizontalPictureProjections = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.HORIZONTAL, UserPictureGroupRole.OWNS);
        List<PictureIdAndOrientationProjection> sharedHorizontalPictureProjections = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.HORIZONTAL, UserPictureGroupRole.SHARES);
        horizontalPictureProjections.addAll(ownedHorizontalPictureProjections);
        horizontalPictureProjections.addAll(sharedHorizontalPictureProjections);

        List<PictureIdAndOrientationProjection> verticalPictureProjections = new ArrayList<>();
        List<PictureIdAndOrientationProjection> ownedVerticalPictureProjections = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.VERTICAL, UserPictureGroupRole.OWNS);
        List<PictureIdAndOrientationProjection> sharedVerticalPictureProjections = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.VERTICAL, UserPictureGroupRole.SHARES);
        verticalPictureProjections.addAll(ownedVerticalPictureProjections);
        verticalPictureProjections.addAll(sharedVerticalPictureProjections);


        // Prepare a collection of all pictures
        List<PictureIdAndOrientationProjection> allPictureProjections = new ArrayList<>();
        allPictureProjections.addAll(horizontalPictureProjections);
        allPictureProjections.addAll(verticalPictureProjections);

        // Prepare a collection of all owned pictures
        List<PictureIdAndOrientationProjection> ownedPictureProjections = new ArrayList<>();
        ownedPictureProjections.addAll(ownedHorizontalPictureProjections);
        ownedPictureProjections.addAll(ownedVerticalPictureProjections);

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
        model.addAttribute("pictureIds", ownedPictureProjections);
        model.addAttribute("selectedPictureId1", selectedPictureId1);
        model.addAttribute("selectedPictureId2", selectedPictureId2);
    }

    public ReducedImagePicture getReducedPictureIfAllowed(long pictureId, Authentication authentication)
            throws BadRequestException {

        ReducedImagePicture picture = pictureRepository.findById(pictureId, ReducedImagePicture.class)
                .orElseThrow(BadRequestException::new);

        if(pictureCanBeSeenByUser(picture, authentication))
            return picture;
        else
            throw new BadRequestException();
    }

    private boolean pictureCanBeSeenByUser(ReducedImagePicture picture, Authentication authentication) {

        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);

        long userId = userIdProjection.getId();
        long pictureGroupId = picture.getPictureGroup().getId();

        Optional<UserPictureGroupLink> userPictureGroupLinkOptional = userPictureGroupLinkRepository
                .pictureGroupIdAndUserIdViewableMatch(pictureGroupId, userId);

        return userPictureGroupLinkOptional.isPresent();
    }

    private boolean pictureBelongsToUser(Picture picture, Authentication authentication) {
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);

        for (UserPictureGroupLink userPictureGroupLink : picture.getPictureGroup().getUserPictureGroupLinks()) {
            if (userPictureGroupLink.getRole().equals(UserPictureGroupRole.OWNS))
                if (userPictureGroupLink.getUser().getId() == userIdProjection.getId())
                    return true;
        }
        return false;
    }

    public void deleteLinkIfAllowed(long pictureId, Authentication authentication) throws BadRequestException {

        Picture picture = getPictureIfOwned(pictureId, authentication);
        pictureRepository.delete(picture);
    }

    public Picture getPictureIfOwned(long pictureId, Authentication authentication) throws BadRequestException {
        Picture picture = pictureRepository.findByIdWithGroups(pictureId).orElseThrow(BadRequestException::new);

        if(pictureBelongsToUser(picture, authentication))
            return picture;
        else
            throw new BadRequestException();
    }
}
