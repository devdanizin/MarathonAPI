package com.devdaniel.MarathonAPI.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/resultados")
    public String resultados() {
        return "resultados/index.html";
    }

    @GetMapping("/run")
    public String run() {
        return "run/index.html";
    }

    @GetMapping("/inscritos")
    public String inscritos() {
        return "inscritos/index.html";
    }

}
