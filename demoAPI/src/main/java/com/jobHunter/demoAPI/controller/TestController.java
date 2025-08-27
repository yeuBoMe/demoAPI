package com.jobHunter.demoAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping
    public String helloPage() {
        return "Hello project demoAPI";
    }
}
