package com.bp.PortFolio.service;

import com.bp.PortFolio.model.Stock;
import com.bp.PortFolio.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    @Autowired
    private StockRepository stockRepository;

    public double calculateTotalPortfolioValue() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .mapToDouble(stock -> stock.getQuantity() * stock.getPurchasePrice())
                .sum();
    }

    public int calculateTotalShares() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();
    }
}