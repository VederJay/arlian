package org.arlian.site.controller;

import org.arlian.site.model.BadRequestException;
import org.arlian.site.model.start.card.Card;
import org.arlian.site.model.start.card.CardRepository;
import org.arlian.site.model.start.card.CardType;
import org.arlian.site.model.start.link.Link;
import org.arlian.site.model.start.link.LinkRepository;
import org.arlian.site.model.start.page.Page;
import org.arlian.site.model.start.page.PageNameProjection;
import org.arlian.site.model.start.page.PageRepository;
import org.arlian.site.model.user.User;
import org.arlian.site.model.user.UserIdProjection;
import org.arlian.site.service.PageService;
import org.arlian.site.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/start")
public class StartController {

    // Autowired services
    private final UserService userService;
    private final PageService pageService;

    // Autowired repositories
    private final PageRepository pageRepository;
    private final CardRepository cardRepository;
    private final LinkRepository linkRepository;

    // Autowired other objects
    private final EntityManager entityManager;


    public StartController(UserService userService, PageService pageService,
                           PageRepository pageRepository, CardRepository cardRepository,
                           LinkRepository linkRepository, EntityManager entityManager) {
        this.userService = userService;
        this.pageService = pageService;
        this.pageRepository = pageRepository;
        this.cardRepository = cardRepository;
        this.linkRepository = linkRepository;
        this.entityManager = entityManager;
    }


    @GetMapping({"", "/"})
    public RedirectView redirectStart(Authentication authentication){

        // Get name of default page
        String pageName = pageService.getDefaultPageName(authentication);

        // Redirect to url with default page name
        return new RedirectView("/start/view/"+pageName);
    }


    @GetMapping("/view/{pageName}")
    public String view(Model model, Authentication authentication, @PathVariable String pageName){

        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageName);

        if(optionalPage.isEmpty())
            return "redirect:/404";

        // Enrich model with page related attributes but return view page
        Page page = optionalPage.get();
        enrichModelForPage(model, authentication, page);
        return "pages/start/view";
    }


    @GetMapping("/edit/{pageName}")
    public String edit(Model model, Authentication authentication, @PathVariable String pageName){

        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageName);

        if(optionalPage.isEmpty())
            return "redirect:/404";

        // Enrich model with page related attributes and return
        Page page = optionalPage.get();
        enrichModelForPage(model, authentication, page);
        return "pages/start/edit";
    }

    @PostMapping("/card/add")
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

    @PostMapping("/card/update")
    public String updateCard(Authentication authentication,
                             @RequestParam("cardId") long cardId,
                             @RequestParam("cardTitle") String cardTitle) throws BadRequestException {

        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardBelongsToUser(card, authentication)){

            // update title
            card.setTitle(cardTitle);
            cardRepository.save(card);

            // return to page
            Page page = pageRepository.findById(card.getPage().getId()).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "redirect:/403";
    }

    @PostMapping("/card/delete")
    public String deleteCard(Authentication authentication,
                             @RequestParam("cardId") long cardId) throws BadRequestException {

        // Find card
        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardBelongsToUser(card, authentication)){

            // Remove card
            cardRepository.delete(card);

            // Return
            Page page = pageRepository.findById(card.getPage().getId()).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "redirect:/403";
    }


    @PostMapping("/link/add")
    public String addLink(Authentication authentication,
                          @RequestParam("cardId") long cardId, @RequestParam("linkTitle") String linkTitle,
                          @RequestParam("linkUrl") String linkUrl) throws BadRequestException {

        // Find card
        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardBelongsToUser(card, authentication)){

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

    @PostMapping("/link/update")
    public String updateLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId,
                             @RequestParam("linkTitle") String linkTitle,
                             @RequestParam("linkUrl") String linkUrl) throws BadRequestException {

        Link link = linkRepository.findById(linkId).orElseThrow(BadRequestException::new);

        if(linkBelongsToUser(link, authentication)){

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

    @PostMapping("/link/delete")
    public String deleteLink(Authentication authentication,
                             @RequestParam("linkId") long linkId,
                             @RequestParam("pageId") long pageId) throws BadRequestException {

        Link link = linkRepository.findById(linkId).orElseThrow(BadRequestException::new);

        if(linkBelongsToUser(link, authentication)){

            // Delete link
            linkRepository.delete(link);

            // Return
            Page page = pageRepository.findById(pageId).orElseThrow(BadRequestException::new);
            return "redirect:/start/edit/" + page.getName();
        }

        return "pages/start/edit";
    }

    @GetMapping("/page/add")
    public String addPageForm(Model model, Authentication authentication){

        // Get pages based on user ID
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        User user = entityManager.getReference(User.class, userIdProjection.getId());
        List<PageNameProjection> pageNameProjections = pageRepository.findByUser(user);

        // Translate to list of strings
        List<String> pageNames = new ArrayList<>();
        pageNameProjections.forEach(projection -> pageNames.add(projection.getName()));

        // Add to model
        model.addAttribute("pageNames", pageNames);

        // Return page
        return "pages/start/add";
    }

    @PostMapping("/page/add")
    public String addPage(Authentication authentication,
                          @RequestParam("pageTitle") String pageTitle) {

        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        User user = entityManager.getReference(User.class, userIdProjection.getId());
        Page page = pageService.createNewPage(user, pageTitle);
        return "redirect:/start/edit/" + page.getName();

    }

    @PostMapping("/page/delete")
    public String deletePage(Authentication authentication,
                             @RequestParam("pageId") long pageId) throws BadRequestException {

        // Check if ID exists and belongs to user
        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageId);
        if(optionalPage.isEmpty())
            return "redirect:/404";

        // Get page
        Page page = optionalPage.get();
        pageRepository.delete(page);

        return "redirect:/start";
    }

    @PostMapping("/page/update")
    public String updatePage(Authentication authentication,
                             @RequestParam("pageId") long pageId,
                             @RequestParam("pageTitle") String pageTitle) throws BadRequestException {

        // Check if ID exists and belongs to user
        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageId);
        if(optionalPage.isEmpty())
            return "redirect:/404";

        // Get page and update
        Page page = optionalPage.get();
        page.setName(pageTitle);
        pageRepository.save(page);

        return "redirect:/start/edit/" + page.getName();
    }



    private boolean cardBelongsToUser(Card card, Authentication authentication) {
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return card.getUser().getId() == userIdProjection.getId();
    }

    private boolean linkBelongsToUser(Link link, Authentication authentication) {

        // Check if the link is in a card
        Optional<Card> optionalCard = cardRepository.findByLink(link);
        if(optionalCard.isEmpty())
            return false;

        // Check if the card belongs to the user
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return optionalCard.get().getUser().getId() == userIdProjection.getId();
    }

    private void enrichModelForPage(Model model, Authentication authentication, Page page){

        // Enrich model with page related attributes
        addCardsToModel(model, authentication, page);
        addOtherPageNamesToModel(model, page);

    }

    private void addOtherPageNamesToModel(Model model, Page page) {

        // Get pages based on user ID
        User user = entityManager.getReference(User.class, page.getUser().getId());
        List<PageNameProjection> pageNameProjections = pageRepository.findByUser(user);

        // Translate to list of strings
        List<String> pageNames = new ArrayList<>();
        pageNameProjections.forEach(projection -> pageNames.add(projection.getName()));

        // Add to model
        model.addAttribute("pageNames", pageNames);
    }

    private void addCardsToModel(Model model, Authentication authentication, Page page){

        // Get the cards with links
        List<Card> leftColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 0);
        List<Card> middleColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 1);
        List<Card> rightColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 2);

        // Add the attributes to the model
        model.addAttribute("pageName", page.getName());
        model.addAttribute("pageId", page.getId());
        model.addAttribute("leftColumnCards", leftColumnCards);
        model.addAttribute("middleColumnCards", middleColumnCards);
        model.addAttribute("rightColumnCards", rightColumnCards);
    }
}
