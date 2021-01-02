package org.arlian.site.start.controller;

import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.model.picture.Picture;
import org.arlian.site.start.model.picture.PictureRepository;
import org.arlian.site.start.service.LinkService;
import org.arlian.site.start.service.PictureService;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/start/picture")
public class PictureController {

    // Autowired services
    private final UserService userService;
    private final LinkService linkService;
    private final PictureService pictureService;

    // Autowired repositories
    private final PageRepository pageRepository;
    private final PictureRepository pictureRepository;


    public PictureController(UserService userService, LinkService linkService, PictureService pictureService,
                             PictureRepository pictureRepository, PageRepository pageRepository) {
        this.userService = userService;
        this.linkService = linkService;
        this.pictureService = pictureService;
        this.pictureRepository = pictureRepository;
        this.pageRepository = pageRepository;
    }


    @PostMapping("/add")
    public String addLink(Authentication authentication,
                          @RequestParam("image") MultipartFile imageFile,
                          @RequestParam("pageId") long pageId)
            throws BadRequestException, IOException {


        // Create the picture
        Picture picture = Picture.builder()
                .image((imageFile.isEmpty()) ? null : imageFile.getBytes())
                .user(userService.getProxyUserFromAuthentication(authentication))
                .build();

        // Save it
        pictureRepository.save(picture);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();
    }

    @GetMapping("/get/{id}")
    public void getImage(Authentication authentication, HttpServletResponse response, @PathVariable("id") long pictureId)
            throws BadRequestException, IOException {

        Picture picture = pictureService.getPictureIfAllowed(pictureId, authentication);

        // Set values for response to send image
        String contentType = new Tika().detect(picture.getImage());
        response.setContentType(contentType);
        InputStream is = new ByteArrayInputStream(picture.getImage());
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
