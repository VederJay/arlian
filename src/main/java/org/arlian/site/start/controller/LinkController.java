package org.arlian.site.start.controller;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.card.Card;
import org.arlian.site.start.model.card.CardRepository;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.link.LinkRepository;
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
public class LinkController {

    // Autowired services
    private final CardService cardService;
    private final LinkService linkService;

    // Autowired repositories
    private final PageRepository pageRepository;
    private final CardRepository cardRepository;
    private final LinkRepository linkRepository;


    public LinkController(CardService cardService, LinkService linkService, PageRepository pageRepository,
                          CardRepository cardRepository,
                          LinkRepository linkRepository) {
        this.cardService = cardService;
        this.linkService = linkService;
        this.pageRepository = pageRepository;
        this.cardRepository = cardRepository;
        this.linkRepository = linkRepository;
    }


    @PostMapping("/add")
    public String addLink(Authentication authentication,
                          @RequestParam("cardId") long cardId, @RequestParam("linkTitle") String linkTitle,
                          @RequestParam("linkUrl") String linkUrl) throws BadRequestException {

        // Find card
        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardService.cardBelongsToUser(card, authentication)){

            Link link = Link.builder()
                    .title(linkTitle)
                    .url(linkUrl)
                    .build();
            card.addLink(link);

            // Remove card
            cardRepository.save(card);
            linkRepository.save(link);

            // Return
            Page page = pageRepository.findById(card.getPage().getId()).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "redirect:/403";
    }

    @PostMapping("/update")
    public String updateLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId,
                             @RequestParam("linkTitle") String linkTitle,
                             @RequestParam("linkUrl") String linkUrl) throws BadRequestException {

        Link link = linkRepository.findById(linkId).orElseThrow(BadRequestException::new);

        if(linkService.linkBelongsToUser(link, authentication)){

            // Update and save
            link.setTitle(linkTitle);
            link.setUrl(linkUrl);
            linkRepository.save(link);

            // Return
            Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "pages/start/edit";
    }

    @PostMapping("/delete")
    public String deleteLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId) throws BadRequestException {

        Link link = linkRepository.findById(linkId).orElseThrow(BadRequestException::new);

        if(linkService.linkBelongsToUser(link, authentication)){

            // Delete link
            linkRepository.delete(link);

            // Return
            Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "pages/start/edit";
    }

}
