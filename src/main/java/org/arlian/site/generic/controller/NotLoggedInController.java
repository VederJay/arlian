package org.arlian.site.generic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotLoggedInController {

    @GetMapping({"", "/", "index", "/index"})
    public String index(){
        return "pages/index";
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
