package org.arlian.site.start.controller;

import org.arlian.site.start.model.card.Card;
import org.arlian.site.start.model.card.CardRepository;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageNameProjection;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.service.PageService;
import org.arlian.site.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/start")
public class IndexViewEditController {

    // Autowired services
    private final PageService pageService;

    // Autowired repositories
    private final PageRepository pageRepository;
    private final CardRepository cardRepository;

    // Autowired other objects
    private final EntityManager entityManager;


    public IndexViewEditController(PageService pageService,
                                   PageRepository pageRepository, CardRepository cardRepository,
                                   EntityManager entityManager) {
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

        return returnTemplateIfOk("pages/start/view",
                model, authentication, pageName);
    }


    @GetMapping("/edit/{pageName}")
    public String edit(Model model, Authentication authentication, @PathVariable String pageName){

        return returnTemplateIfOk("pages/start/edit",
                model, authentication, pageName);
    }


    private String returnTemplateIfOk(String template, Model model, Authentication authentication, String pageName){

        // Try to find the page for the user
        Optional<Page> optionalPage = pageService.getOptionalForPage(authentication, pageName);

        // If it doesn't exist for the user, 404
        if(optionalPage.isEmpty())
            return "redirect:/404";

        // Get the page
        Page page = optionalPage.get();

        // Enrich model with page related attributes
        addCardsToModel(model, page);
        addOtherPageNamesToModel(model, page);

        // Return the given template
        return template;
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

    private void addCardsToModel(Model model, Page page){

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
