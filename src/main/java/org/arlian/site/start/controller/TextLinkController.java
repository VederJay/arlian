package org.arlian.site.start.controller;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.service.CardService;
import org.arlian.site.start.service.LinkService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/start/link")
public class TextLinkController {

    // Autowired services
    private final CardService cardService;
    private final LinkService linkService;

    // Autowired repositories
    private final PageRepository pageRepository;


    public TextLinkController(CardService cardService, LinkService linkService, PageRepository pageRepository) {
        this.cardService = cardService;
        this.linkService = linkService;
        this.pageRepository = pageRepository;
    }


    @PostMapping("/add")
    public String addLink(Authentication authentication,
                          @RequestParam("cardId") long cardId, @RequestParam("linkTitle") String linkTitle,
                          @RequestParam("linkUrl") String linkUrl, @RequestParam("pageId") long pageId)
            throws BadRequestException {

        // Create the link
        Link link = Link.builder()
                .title(linkTitle)
                .url(linkUrl)
                .build();

        // Add it to the card
        cardService.addLinkToCardIfOwnedByUser(cardId, link, authentication);

        // Return
        Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
        return "redirect:/start/edit/" + page.getName();

    }

    @PostMapping("/update")
    public String updateLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId,
                             @RequestParam("linkTitle") String linkTitle,
                             @RequestParam("linkUrl") String linkUrl) throws BadRequestException {

        // Build a new link with updated values
        Link link = Link.builder()
                .title(linkTitle)
                .url(linkUrl)
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
