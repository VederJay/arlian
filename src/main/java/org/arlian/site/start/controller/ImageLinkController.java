package org.arlian.site.start.controller;

import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.service.CardService;
import org.arlian.site.start.service.LinkService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/start/image-link")
public class ImageLinkController {

    // Autowired services
    private final CardService cardService;
    private final LinkService linkService;

    // Autowired repositories
    private final PageRepository pageRepository;


    public ImageLinkController(CardService cardService, LinkService linkService, PageRepository pageRepository) {
        this.cardService = cardService;
        this.linkService = linkService;
        this.pageRepository = pageRepository;
    }


    @PostMapping("/add")
    public String addLink(Authentication authentication,
                          @RequestParam("cardId") long cardId, @RequestParam("linkTitle") String linkTitle,
                          @RequestParam("linkUrl") String linkUrl, @RequestParam("image") MultipartFile imageFile,
                          @RequestParam("pageId") long pageId)
            throws BadRequestException, IOException {

        // Create the link
        Link link = Link.builder()
                .title(linkTitle)
                .url(linkUrl)
                .image(imageFile.getBytes())
                .build();

        // Add it to the card
        cardService.addLinkToCardIfOwnedByUser(cardId, link, authentication);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();
    }

    @GetMapping("/get/{id}")
    public void getImage(Authentication authentication, HttpServletResponse response, @PathVariable("id") long linkId)
            throws BadRequestException, IOException {

        Link link = linkService.getLinkIfAllowed(linkId, authentication);

        // Set values for response to send image
        String contentType = new Tika().detect(link.getImage());
        response.setContentType(contentType);
        InputStream is = new ByteArrayInputStream(link.getImage());
        IOUtils.copy(is, response.getOutputStream());

    }

    @PostMapping("/update")
    public String updateLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId,
                             @RequestParam("linkTitle") String linkTitle,
                             @RequestParam("linkUrl") String linkUrl,
                             @RequestParam("image") MultipartFile imageFile) throws BadRequestException, IOException {


        // Build a new link with updated values
        Link link = Link.builder()
                .title(linkTitle)
                .url(linkUrl)
                .image( (imageFile != null) ? imageFile.getBytes() : null)
                .build();

        // Update the existing link from the new link, if authorized
        linkService.updateLinkWithLinkIfAllowed(linkId, link, authentication);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();
    }

    @PostMapping("/delete")
    public String deleteLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId) throws BadRequestException {

        // Deletes the link if the user owns it
        linkService.deleteLinkIfAllowed(linkId, authentication);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();
    }

}
