package voynap.InterviewTask.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.Product;
import voynap.InterviewTask.repositories.ProductsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductsService {

    private final ProductsRepository productsRepository;
    @Autowired
    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }


    @Transactional
    public void save(Product product) {
        productsRepository.save(product);
    }


    public List<Product> findAll() {
        return productsRepository.findAll();
    }

    public List<Product> getProductsByIds(List<Integer> ids) {
        return productsRepository.findByIdIn(ids);
    }


    public Optional<Product> findById(int id) {
       return productsRepository.findById(id);
    }

    public List<Product> findProductsByKeywords(List<String> keywords) {
        return productsRepository.findDistinctByKeywords_KeywordIn(keywords);
    }
    public List<Product> findAllActiveCompanyProducts() {
        return productsRepository.findByCompanyStatus("ACTIVE");
    }
}
