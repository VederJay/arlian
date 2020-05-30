package org.arlian.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotLoggedInController {

    @GetMapping({"", "/", "index", "/index"})
    public String index(){
        return "index";
    }


}
