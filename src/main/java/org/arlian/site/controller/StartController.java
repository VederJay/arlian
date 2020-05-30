package org.arlian.site.controller;

import org.arlian.site.UserService;
import org.arlian.site.model.user.UserIdProjection;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartController {

    private UserService userService;

    public StartController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/start")
    public String start(Model model, Authentication authentication){
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return "start";
    }
}
