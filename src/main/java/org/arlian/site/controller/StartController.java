package org.arlian.site.controller;

import org.arlian.site.UserService;
import org.arlian.site.model.start.page.Page;
import org.arlian.site.model.start.page.PageNameProjection;
import org.arlian.site.model.start.page.PageRepository;
import org.arlian.site.model.user.User;
import org.arlian.site.model.user.UserIdProjection;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;

@Controller
@RequestMapping("/start")
public class StartController {

    private final UserService userService;
    private final PageRepository pageRepository;
    private final EntityManager entityManager;

    public StartController(UserService userService, PageRepository pageRepository, EntityManager entityManager) {
        this.userService = userService;
        this.pageRepository = pageRepository;
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
            Page page = new Page(userProxy, "home", true);
            pageRepository.saveAndFlush(page);

            pageName = page.getName();
        }

        // Use name of default page
        else{
            pageName = pageNameProjection.getName();
        }

        // Redirect to url with page name
        return new RedirectView("/start/page/"+pageName);
    }

    @GetMapping("/page/{pageName}")
    public String start(Model model, Authentication authentication, @PathVariable String pageName){

        // Get page
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        Page page = pageRepository.findByUserIdAndName(userIdProjection.getId(), pageName);

        // If the page name doesn't exist for the user, redirect to 404
        if(page == null){
            // TODO make 404 page for start
            return "redirect:/404";
        }

        // Add the attributes to the model
        model.addAttribute(page.getName());

        return "pages/start";
    }
}
