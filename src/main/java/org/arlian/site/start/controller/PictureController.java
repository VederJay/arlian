package org.arlian.site.start.controller;

import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.model.picture.*;
import org.arlian.site.start.service.ImageService;
import org.arlian.site.start.service.PictureService;
import org.arlian.site.user.model.User;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Controller
@RequestMapping("/start/picture")
public class PictureController {

    // Autowired services
    private final UserService userService;
    private final ImageService imageService;
    private final PictureService pictureService;

    // Autowired repositories
    private final PageRepository pageRepository;
    private final PictureRepository pictureRepository;
    private final PictureGroupRepository pictureGroupRepository;
    private final UserPictureGroupLinkRepository userPictureGroupLinkRepository;


    public PictureController(UserService userService, ImageService imageService, PictureService pictureService,
                             PictureRepository pictureRepository, PageRepository pageRepository,
                             PictureGroupRepository pictureGroupRepository,
                             UserPictureGroupLinkRepository userPictureGroupLinkRepository) {
        this.userService = userService;
        this.imageService = imageService;
        this.pictureService = pictureService;
        this.pictureRepository = pictureRepository;
        this.pageRepository = pageRepository;
        this.pictureGroupRepository = pictureGroupRepository;
        this.userPictureGroupLinkRepository = userPictureGroupLinkRepository;
    }


    @PostMapping("/add")
    public String addLink(Authentication authentication,
                          @RequestParam("image") MultipartFile imageFile,
                          @RequestParam("pageId") long pageId)
            throws BadRequestException, IOException {

        // Find owned picture group for user
        User user = userService.getProxyUserFromAuthentication(authentication);
        Optional<PictureGroup> pictureGroupOptional = pictureGroupRepository.findOwnedByUser(user);
        PictureGroup pictureGroup;
        if(pictureGroupOptional.isEmpty()) {
            pictureGroup = PictureGroup.builder().build();
            UserPictureGroupLink userPictureGroupLink = UserPictureGroupLink.builder()
                    .user(user)
                    .pictureGroup(pictureGroup)
                    .role(UserPictureGroupRole.OWNS)
                    .build();
            pictureGroup.addUserPictureGroupLink(userPictureGroupLink);
            pictureGroupRepository.save(pictureGroup);
            userPictureGroupLinkRepository.save(userPictureGroupLink);
        }
        else
            pictureGroup = pictureGroupOptional.get();

        // Create the picture
        Picture picture = Picture.builder()
                .pictureGroup(pictureGroup)
                .build();

        // Add the image
        if(!imageFile.isEmpty())
            imageService.addImageToPicture(picture, imageFile.getBytes());

        // Save it
        pictureRepository.save(picture);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();
    }


    @GetMapping("/getFullSize/{id}")
    public void getImage(Authentication authentication, HttpServletResponse response, @PathVariable("id") long pictureId)
            throws BadRequestException, IOException {

        Picture picture = pictureService.getPictureIfAllowed(pictureId, authentication);

        // Set values for response to send image
        String contentType = new Tika().detect(picture.getImage());
        response.setContentType(contentType);
        InputStream is = new ByteArrayInputStream(picture.getImage());
        IOUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/getThumbnail/{id}")
    public void getThumbnail(Authentication authentication, HttpServletResponse response, @PathVariable("id") long pictureId)
            throws BadRequestException, IOException {

        Picture picture = pictureService.getPictureIfOwned(pictureId, authentication);

        // Set values for response to send image
        String contentType = new Tika().detect(picture.getThumbnail());
        response.setContentType(contentType);
        InputStream is = new ByteArrayInputStream(picture.getThumbnail());
        IOUtils.copy(is, response.getOutputStream());
    }


    @PostMapping("/delete")
    public String deleteLink(Authentication authentication,
                             @RequestParam("pictureId") long pictureId,
                             @RequestParam("pageId") long pageId) throws BadRequestException {

        // Deletes the link if the user owns it
        pictureService.deleteLinkIfAllowed(pictureId, authentication);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();
    }

}
