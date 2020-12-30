package org.arlian.site.generic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({"", "/", "index", "/index"})
    public String index(Authentication authentication){

        if(authentication==null)
            return "pages/index";

        else
            return "redirect:/start";
    }

    @GetMapping("privacy")
    public String privacyPolicy(){
        return "pages/privacyAndUsagePolicy";
    }

    @GetMapping("usage")
    public String usagePolicy(){
        return "pages/privacyAndUsagePolicy";
    }

}
