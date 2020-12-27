package org.arlian.site.controller;

import org.arlian.site.model.BadRequestException;
import org.arlian.site.service.UserService;
import org.arlian.site.model.start.card.Card;
import org.arlian.site.model.start.card.CardRepository;
import org.arlian.site.model.start.card.CardType;
import org.arlian.site.model.start.page.Page;
import org.arlian.site.model.start.page.PageNameProjection;
import org.arlian.site.model.start.page.PageRepository;
import org.arlian.site.model.user.User;
import org.arlian.site.model.user.UserIdProjection;
import org.arlian.site.service.PageService;
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

    // Autowired other objects
    private final EntityManager entityManager;


    public StartController(UserService userService, PageService pageService,
                           PageRepository pageRepository, CardRepository cardRepository,
                           EntityManager entityManager) {
        this.userService = userService;
        this.pageService = pageService;
        this.pageRepository = pageRepository;
        this.cardRepository = cardRepository;
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

        Page page = optionalPage.get();

        addCardsToModel(model, authentication, page);
        addOtherPageNamesToModel(model, authentication, page);

        return "pages/start/view";
    }


    @GetMapping("/edit/{pageName}")
    public String edit(Model model, Authentication authentication, @PathVariable String pageName){

        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageName);

        if(optionalPage.isEmpty())
            return "redirect:/404";

        Page page = optionalPage.get();

        addCardsToModel(model, authentication, page);
        addOtherPageNamesToModel(model, authentication, page);

        return "pages/start/edit";
    }

    @PostMapping("/card/add")
    public String addCard(Model model, Authentication authentication, @RequestParam String pageName,
                         @RequestParam("cardId") long cardId, @RequestParam("cardTitle") String cardTitle,
                         @RequestParam("cardType") CardType cardType){


        return "pages/start/edit";
    }

    @PostMapping("/card/update")
    public String updateCard(Model model, Authentication authentication,
                         @RequestParam("cardId") long cardId, @RequestParam("cardTitle") String cardTitle) throws BadRequestException {

        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardBelongsToUser(card, authentication)){

            // update title
            card.setTitle(cardTitle);
            cardRepository.save(card);

            // Enrich model with page related attributes
            Page page = pageRepository.findById(card.getPage().getId()).orElseThrow(BadRequestException::new);
            addCardsToModel(model, authentication, page);
            addOtherPageNamesToModel(model, authentication, page);

            // Return page being edited
            return "pages/start/edit";
        }

        return "redirect:/403";
    }

    private boolean cardBelongsToUser(Card card, Authentication authentication) {

        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);

        return card.getUser().getId() == userIdProjection.getId();
    }

    @PostMapping("/card/delete")
    public String deleteCard(Model model, Authentication authentication,
                             @RequestParam("cardId") long cardId){


        return "pages/start/edit";
    }

    @PostMapping("/link/add")
    public String addLink(Model model, Authentication authentication,
                          @RequestParam("cardId") long cardId){


        return "pages/start/edit";
    }

    @PostMapping("/link/update")
    public String updateLink(Model model, Authentication authentication,
                             @RequestParam("linkId") long linkId){

        return "pages/start/edit";
    }

    @PostMapping("/link/delete")
    public String deleteLink(Model model, Authentication authentication,
                             @RequestParam("linkId") long linkId){

        return "pages/start/edit";
    }


    private void addOtherPageNamesToModel(Model model, Authentication authentication, Page page) {

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
        model.addAttribute("leftColumnCards", leftColumnCards);
        model.addAttribute("middleColumnCards", middleColumnCards);
        model.addAttribute("rightColumnCards", rightColumnCards);
    }
}
