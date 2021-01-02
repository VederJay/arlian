package org.arlian.site.start.controller;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageNameProjection;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.start.service.PageService;
import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/start/page")
public class PageController {

    // Autowired services
    private final UserService userService;
    private final PageService pageService;

    // Autowired repositories
    private final PageRepository pageRepository;

    // Autowired other objects
    private final EntityManager entityManager;


    public PageController(UserService userService, PageService pageService,
                          PageRepository pageRepository,
                          EntityManager entityManager) {
        this.userService = userService;
        this.pageService = pageService;
        this.pageRepository = pageRepository;
        this.entityManager = entityManager;
    }



    @GetMapping("/add")
    public String addPageForm(Model model, Authentication authentication){

        // Get pages based on user ID
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
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

    @PostMapping("/add")
    public String addPage(Authentication authentication,
                          @RequestParam("pageTitle") String pageTitle) {

        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User user = entityManager.getReference(User.class, userIdProjection.getId());
        Page page = pageService.createNewPage(user, pageTitle);
        return "redirect:/start/edit/" + page.getName();

    }

    @PostMapping("/delete")
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

    @PostMapping("/update")
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

}
