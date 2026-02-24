package com.samb1232.adminservice.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.samb1232.adminservice.database.entities.Cat;
import com.samb1232.adminservice.database.repos.CatRepository;

@Controller
public class AdminController {

    private final CatRepository catRepository;

    @Autowired
    public AdminController(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    @GetMapping("/")
    public String viewDashboard() {
        return "dashboard";
    }

    @GetMapping("/cats")
    public String manageCats(Model model, @RequestParam(name = "query", required = false) String query) {
        List<Cat> cats = catRepository.findAll();

        // Simple search logic
        if (query != null && !query.trim().isEmpty()) {
            String lowerQuery = query.toLowerCase();
            cats = cats.stream()
                    .filter(cat -> cat.getName() != null && cat.getName().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }

        model.addAttribute("cats", cats);
        model.addAttribute("query", query);
        return "cats-list";
    }

    @GetMapping("/cats/edit/{id}")
    public String showEditCatForm(@PathVariable("id") Long id, Model model) {
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cat Id:" + id));
        model.addAttribute("cat", cat);
        return "cat-form";
    }

    @PostMapping("/cats/save")
    public String saveCat(@ModelAttribute("cat") Cat cat) {
        catRepository.save(cat);
        return "redirect:/cats";
    }

    @PostMapping("/cats/delete/{id}")
    public String deleteCat(@PathVariable("id") Long id) {
        catRepository.deleteById(id);
        return "redirect:/cats";
    }
}
