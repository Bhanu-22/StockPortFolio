// controller/StockController.java
package com.bp.PortFolio.controller;

import com.bp.PortFolio.model.Stock;
import com.bp.PortFolio.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StockController {

    @Autowired
    private StockRepository stockRepo;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("stocks", stockRepo.findAll());
        return "index";
    }

    @PostMapping("/add")
    public String addStock(@ModelAttribute Stock stock) {
        stockRepo.save(stock);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteStock(@PathVariable Long id) {
        stockRepo.deleteById(id);
        return "redirect:/";
    }
}
