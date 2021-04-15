package org.arlian.site.start.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    public void addPictureIdsToModel(Model model, Authentication authentication) {

        // Get a proxy of the user
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());

        // Get the pictures for the user
        List<Long> horizontalPictureIds = new ArrayList<>();
        List<Long> ownedHorizontalPictureIds = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.HORIZONTAL, UserPictureGroupRole.OWNS);
        List<Long> sharedHorizontalPictureIds = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.HORIZONTAL, UserPictureGroupRole.SHARES);
        horizontalPictureIds.addAll(ownedHorizontalPictureIds);
        horizontalPictureIds.addAll(sharedHorizontalPictureIds);

        List<Long> verticalPictureIds = new ArrayList<>();
        List<Long> ownedVerticalPictureIds = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.VERTICAL, UserPictureGroupRole.OWNS);
        List<Long> sharedVerticalPictureIds = pictureRepository
                .findByUserAndOrientationAndRole(proxyUser, Orientation.VERTICAL, UserPictureGroupRole.SHARES);
        verticalPictureIds.addAll(ownedVerticalPictureIds);
        verticalPictureIds.addAll(sharedVerticalPictureIds);


        // Prepare a collection of all pictures
        List<Long> allViewablePictureIds = new ArrayList<>();
        allViewablePictureIds.addAll(horizontalPictureIds);
        allViewablePictureIds.addAll(verticalPictureIds);

        // Prepare a collection of all owned pictures
        List<Long> allOwnedPictureIds = new ArrayList<>();
        allOwnedPictureIds.addAll(ownedHorizontalPictureIds);
        allOwnedPictureIds.addAll(ownedVerticalPictureIds);

        // Select a picture or set thereof
        long selectedPictureId1 = 0;
        long selectedPictureId2 = 0;
        // if there's 2 or more vertical pictures, we can pick any random picture and if it's vertical, we'll have
        // a second one to set next to it
        if(verticalPictureIds.size() >= 2){

            // pick a first picture
            Random randomizer = new Random();
            selectedPictureId1 = allViewablePictureIds
                    .get(randomizer.nextInt(allViewablePictureIds.size()));

            // if vertical, pick a second vertical one
            if(verticalPictureIds.contains(selectedPictureId1)){
                verticalPictureIds.remove(selectedPictureId1);
                selectedPictureId2 = verticalPictureIds
                        .get(randomizer.nextInt(verticalPictureIds.size()));
            }
        }
        // if there's insufficient vertical pictures, pick a horizontal one (if there's one)
        else if(horizontalPictureIds.size() > 0){
            Random randomizer = new Random();
            selectedPictureId1 = horizontalPictureIds
                    .get(randomizer.nextInt(horizontalPictureIds.size()));
        }

        // Add all data to the model
        model.addAttribute("pictureIds", allOwnedPictureIds);
        model.addAttribute("selectedPictureId1", selectedPictureId1);
        model.addAttribute("selectedPictureId2", selectedPictureId2);

    }

    public ReducedImagePicture getReducedPictureIfAllowed(long pictureId, Authentication authentication)
            throws BadRequestException {

        // Get picture projection
        ReducedImagePicture picture = pictureRepository.findById(pictureId, ReducedImagePicture.class)
                .orElseThrow(BadRequestException::new);

        // Match with user
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        long userId = userIdProjection.getId();
        long pictureGroupId = picture.getPictureGroup().getId();
        Optional<UserPictureGroupLink> userPictureGroupLinkOptional = userPictureGroupLinkRepository
                .pictureGroupIdAndUserIdViewableMatch(pictureGroupId, userId);

        // Return if viewable
        if(userPictureGroupLinkOptional.isPresent())
            return picture;
        else
            throw new BadRequestException();
    }

    public ThumbnailPicture getThumbnailPictureIfOwned(long pictureId, Authentication authentication) throws BadRequestException {

        // Get picture projection
        ThumbnailPicture picture = pictureRepository.findById(pictureId, ThumbnailPicture.class)
                .orElseThrow(BadRequestException::new);

        // Match with owner
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        long userId = userIdProjection.getId();
        long pictureGroupId = picture.getPictureGroup().getId();
        Optional<UserPictureGroupLink> userPictureGroupLinkOptional = userPictureGroupLinkRepository
                .pictureGroupIdAndUserIdOwnerMatch(pictureGroupId, userId);

        // Return if owned
        if(userPictureGroupLinkOptional.isPresent())
            return picture;
        else
            throw new BadRequestException();
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

    public void deletePictureIfAllowed(long pictureId, Authentication authentication) throws BadRequestException {

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
