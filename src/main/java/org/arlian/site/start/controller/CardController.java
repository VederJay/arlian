package org.arlian.site.start.controller;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.card.Card;
import org.arlian.site.start.model.card.CardRepository;
import org.arlian.site.start.model.card.CardType;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.service.CardService;
import org.arlian.site.start.service.PageService;
import org.arlian.site.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import java.util.Optional;

@Controller
@RequestMapping("/start/card")
public class CardController {

    // Autowired services
    private final PageService pageService;
    private final CardService cardService;

    // Autowired repositories
    private final PageRepository pageRepository;
    private final CardRepository cardRepository;

    // Autowired other objects
    private final EntityManager entityManager;


    public CardController(PageService pageService,
                          CardService cardService, PageRepository pageRepository, CardRepository cardRepository,
                          EntityManager entityManager) {
        this.pageService = pageService;
        this.cardService = cardService;
        this.pageRepository = pageRepository;
        this.cardRepository = cardRepository;
        this.entityManager = entityManager;
    }




    @PostMapping("/add")
    public String addCard(Authentication authentication, @RequestParam("pageId") Long pageId,
                          @RequestParam("cardTitle") String cardTitle, @RequestParam("position") int position,
                          @RequestParam("orderNumber") int orderNumber,
                          @RequestParam("cardType") CardType cardType){

        // Get the page
        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageId);
        if(optionalPage.isEmpty())
            return "redirect:/404";
        Page page = optionalPage.get();

        // Get the user
        User user = entityManager.getReference(User.class, page.getUser().getId());

        // Build the card
        Card card = Card.builder()
                .title(cardTitle)
                .type(cardType)
                .page(page)
                .position(position)
                .orderNumber(orderNumber)
                .user(user)
                .build();

        // save
        cardRepository.save(card);

        // return to page
        return "redirect:/start/edit/" + page.getName();
    }

    @PostMapping("/update")
    public String updateCard(Authentication authentication,
                             @RequestParam("cardId") long cardId,
                             @RequestParam("cardTitle") String cardTitle) throws BadRequestException {

        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardService.cardBelongsToUser(card, authentication)){

            // update title
            card.setTitle(cardTitle);
            cardRepository.save(card);

            // return to page
            Page page = pageRepository.findById(card.getPage().getId()).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "redirect:/403";
    }

    @PostMapping("/delete")
    public String deleteCard(Authentication authentication,
                             @RequestParam("cardId") long cardId) throws BadRequestException {

        // Find card
        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardService.cardBelongsToUser(card, authentication)){

            // Remove card
            cardRepository.delete(card);

            // Return
            Page page = pageRepository.findById(card.getPage().getId()).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "redirect:/403";
    }


}
