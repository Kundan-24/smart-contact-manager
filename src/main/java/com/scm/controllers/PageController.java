package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    Logger logger = LoggerFactory.getLogger(PageController.class);

    @RequestMapping("/home")
    public String home(Model model) {
        logger.info("homepage handler: ");
        model.addAttribute("name", "Kundan Kumar");
        model.addAttribute("role", "Java Developer");
        model.addAttribute("instaid","https://www.instagram.com/_kunnu.0_0?igsh=cnYwcWN3bmxlMW1v");
        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model){
        logger.info("aboutpage handler: ");
        model.addAttribute("isLogin", false);
        return "about";
    }

    @RequestMapping("/service")
    public String servicePage(Model model){
        logger.info("servicepage handler: ");
        return "service";
    }
}
