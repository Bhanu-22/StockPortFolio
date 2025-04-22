// controller/StockController.java
package com.bp.PortFolio.controller;

import com.bp.PortFolio.model.Stock;
import com.bp.PortFolio.repository.StockRepository;
import com.bp.PortFolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/")
public class StockController {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioService portfolioService;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String index(Model model) {
        Page<Stock> stocksPage = stockRepository
                .findAll(PageRequest.of(0, PAGE_SIZE, Sort.by("purchaseDate").descending()));
        model.addAttribute("stocks", stocksPage.getContent());
        model.addAttribute("totalValue", portfolioService.calculateTotalPortfolioValue());
        model.addAttribute("totalShares", portfolioService.calculateTotalShares());
        return "index";
    }

    @GetMapping("/api/stocks")
    @ResponseBody
    public ResponseEntity<Page<Stock>> getStocks(@RequestParam(defaultValue = "0") int page) {
        Page<Stock> stocksPage = stockRepository
                .findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("purchaseDate").descending()));
        return ResponseEntity.ok(stocksPage);
    }

    @PostMapping("/api/stocks")
    public String createStock(@ModelAttribute Stock stock) {
        if (stock.getPurchaseDate() == null) {
            stock.setPurchaseDate(LocalDate.now());
        }
        stockRepository.save(stock);
        return "redirect:/";
    }

    @GetMapping("/api/stocks/{id}")
    @ResponseBody
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        return stockRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/stocks/{id}")
    @ResponseBody
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stockDetails) {
        return stockRepository.findById(id)
                .map(stock -> {
                    stock.setTicker(stockDetails.getTicker());
                    stock.setQuantity(stockDetails.getQuantity());
                    stock.setPurchasePrice(stockDetails.getPurchasePrice());
                    stock.setPurchaseDate(stockDetails.getPurchaseDate());
                    stock.setNotes(stockDetails.getNotes());
                    return ResponseEntity.ok(stockRepository.save(stock));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/stocks/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStock(@PathVariable Long id) {
        return stockRepository.findById(id)
                .map(stock -> {
                    stockRepository.delete(stock);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/stocks/portfolio/stats")
    @ResponseBody
    public Map<String, Object> getPortfolioStats() {
        return Map.of(
                "totalValue", portfolioService.calculateTotalPortfolioValue(),
                "totalShares", portfolioService.calculateTotalShares());
    }
}
