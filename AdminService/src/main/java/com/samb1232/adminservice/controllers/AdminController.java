package com.samb1232.adminservice.controllers;

import com.samb1232.adminservice.database.repos.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final CatRepository catRepository;

    @Autowired
    public AdminController(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("cats", catRepository.findAll());
        return "cats";
    }

    @PostMapping("/delete/{id}")
    public String deleteCat(@PathVariable(name = "id") Long id) {
        catRepository.deleteById(id);
        return "redirect:/";
    }
}
