package me.muphy.controller;

import me.muphy.servicce.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys")
public class HomeController {

    @Autowired
    private SystemService service;

    @RequestMapping("/urls")
    public List<Map<String, String>> getAllUrl(String s) {
        return service.getAllUrl(s);
    }

}
