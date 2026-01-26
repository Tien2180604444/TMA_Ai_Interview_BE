//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.interview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/"})
    public String loginEntry() {
        return "redirect:/oauth2/authorization/cognito";
    }

    @GetMapping({"/home"})
    public String home() {
        return "index.html";
    }
}
