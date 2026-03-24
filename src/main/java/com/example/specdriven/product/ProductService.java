package com.example.specdriven.product;

import com.example.specdriven.stock.StockEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockEventRepository stockEventRepository;

    public ProductService(ProductRepository productRepository,
                          StockEventRepository stockEventRepository) {
        this.productRepository = productRepository;
        this.stockEventRepository = stockEventRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Product product) {
        productRepository.delete(product);
    }

    public boolean hasStockEvents(Product product) {
        return stockEventRepository.existsByProductId(product.getId());
    }

    public boolean isSkuTaken(String sku, Long excludeProductId) {
        Optional<Product> existing = productRepository.findBySku(sku);
        return existing.isPresent() &&
               !existing.get().getId().equals(excludeProductId);
    }

    public List<String> findAllCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
