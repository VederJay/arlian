package org.arlian.site.controller;

import org.arlian.site.UserService;
import org.arlian.site.model.start.card.Card;
import org.arlian.site.model.start.card.CardRepository;
import org.arlian.site.model.start.page.Page;
import org.arlian.site.model.start.page.PageNameProjection;
import org.arlian.site.model.start.page.PageRepository;
import org.arlian.site.model.user.User;
import org.arlian.site.model.user.UserIdProjection;
import org.arlian.site.service.PageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import java.util.List;

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
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        PageNameProjection pageNameProjection = pageRepository.findDefaultNameByUserId(userIdProjection.getId());
        String pageName = "";

        // Create default page on first login
        if(pageNameProjection == null){

            // Get User proxy
            User userProxy = entityManager.getReference(User.class, userIdProjection.getId());

            // Create default page
            Page page = pageService.createNewPage(userProxy, "home");
            page.setDefault(true);
            pageRepository.save(page);

            // Get the name of the page
            pageName = page.getName();
        }

        // Use name of default page
        else
            pageName = pageNameProjection.getName();

        // Redirect to url with page name
        return new RedirectView("/start/view/"+pageName);
    }

    @GetMapping("/view/{pageName}")
    public String view(Model model, Authentication authentication, @PathVariable String pageName){

        // Get page
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        Page page = pageRepository.findByUserIdAndName(userIdProjection.getId(), pageName);

        // If the page name doesn't exist for the user, redirect to 404
        if(page == null){
            // TODO make 404 page for start
            return "redirect:/404";
        }

        // Get the cards with links
        List<Card> leftColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 0);
        List<Card> middleColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 1);
        List<Card> rightColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 2);

        // Add the attributes to the model
        model.addAttribute("pageName", page.getName());
        model.addAttribute("leftColumnCards", leftColumnCards);
        model.addAttribute("middleColumnCards", middleColumnCards);
        model.addAttribute("rightColumnCards", rightColumnCards);

        return "pages/start/view";
    }

    @GetMapping("/edit/{pageName}")
    public String edit(Model model, Authentication authentication, @PathVariable String pageName){

        // Get page
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        Page page = pageRepository.findByUserIdAndName(userIdProjection.getId(), pageName);

        // If the page name doesn't exist for the user, redirect to 404
        if(page == null){
            // TODO make 404 page for start
            return "redirect:/404";
        }

        // Get the cards with links
        List<Card> leftColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 0);
        List<Card> middleColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 1);
        List<Card> rightColumnCards = cardRepository.findByPageAndPositionOrderByOrderNumber(page, 2);

        // Add the attributes to the model
        model.addAttribute("pageName", page.getName());
        model.addAttribute("leftColumnCards", leftColumnCards);
        model.addAttribute("middleColumnCards", middleColumnCards);
        model.addAttribute("rightColumnCards", rightColumnCards);

        // TODO create an edit page, maybe start using the layout
        return "pages/start/view";
    }
}
