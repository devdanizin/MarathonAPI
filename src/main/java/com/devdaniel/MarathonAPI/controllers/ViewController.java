package com.devdaniel.MarathonAPI.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {


    @GetMapping("/inscritos")
    public String inscritos() {
        return "inscritos/index.html";
    }

}
