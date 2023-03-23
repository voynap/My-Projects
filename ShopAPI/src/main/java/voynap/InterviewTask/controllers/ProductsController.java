package voynap.InterviewTask.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import voynap.InterviewTask.dto.ProductDTO;
import voynap.InterviewTask.models.*;
import voynap.InterviewTask.repositories.ProductsRepository;
import voynap.InterviewTask.security.JWTUtil;
import voynap.InterviewTask.services.CustomersDetailsService;
import voynap.InterviewTask.services.KeywordService;
import voynap.InterviewTask.services.ProductsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final double COMMISSION = 0.05;
    private final ProductsService productsService;
    private final ModelMapper modelMapper;

    private final KeywordService keywordService;

    private final JWTUtil jwtUtil;

    private final CustomersDetailsService customersDetailsService;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsController(ProductsService productsService, ModelMapper modelMapper,
                              KeywordService keywordService, JWTUtil jwtUtil,
                              CustomersDetailsService customersDetailsService,
                              ProductsRepository productsRepository) {
        this.productsService = productsService;
        this.modelMapper = modelMapper;
        this.keywordService = keywordService;
        this.jwtUtil = jwtUtil;
        this.customersDetailsService = customersDetailsService;
        this.productsRepository = productsRepository;
    }
    // Отображаем только продукты активных организаций
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_OWNER')")
    @GetMapping
    public List<Product> getProducts() {
        return productsService.findAllActiveCompanyProducts();
    }


    // Специальный метод для администраторов, чтобы посмотреть товары даже неактивных компаний
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productsService.findAllActiveCompanyProducts();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/edit")
    /*
     После размышлений оставил назначение ключевых слов, и свойств для товаров администраторам,
     поскольку пользователи не могут знать логику категоризации товаров на сайте. Считаю, что делегировать
     эту задачу администраторам выгоднее.
     */
    public HttpStatus editProductInfo(@RequestBody ProductDTO productDTO) {
        Optional<Product> productOptional = productsService.findById(productDTO.getId());
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (productDTO.getName() != null)
                product.setName(productDTO.getName());
            if (productDTO.getDescription()!=null)
                product.setDescription(productDTO.getDescription());
            if (productDTO.getPrice()!=null)
                product.setPrice(productDTO.getPrice());
            if (productDTO.getQuantity()!=null)
                product.setQuantity(productDTO.getQuantity());
            if (!productDTO.getKeywords().isEmpty()) {
                for (Keyword keyword : productDTO.getKeywords()) {
                    product.getKeywords().add(keyword);
                }
            }
            if (!productDTO.getInternals().isEmpty()) {
                for (ProductInternal internal : productDTO.getInternals()) {
                    product.getInternals().add(internal);
                }
            } return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_OWNER')")
    @GetMapping("/keywords")
    public ResponseEntity<List<Product>> getProductsByKeyword(@RequestParam("keywords") List<String> keywordsParam) {
        List<Product> products = new ArrayList<>();
        for (String keyword : keywordsParam) {
            List<Product> productsForKeyword = productsService.findProductsByKeywords(keywordsParam);
            products.addAll(productsForKeyword);
        }
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OWNER')")
    @Transactional
    @PostMapping("/buy")
    // Принимаем ID продукта и количество товаров, которые пользователь хочет приобрести
    public HttpStatus registerProduct(@RequestBody ProductDTO productDTO,
                                      @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer customer = customersDetailsService.findByUsername(username).get();
        Optional<Product> productOptional = productsService.findById(productDTO.getId());
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getQuantity() > 0) {
                Customer owner = product.getCompany().getOwner();
                int quantity = productDTO.getQuantity();
                Double resultPrice = calculateResultPrice(product, quantity);
                if (resultPrice!=null && customer.getBalance() >= resultPrice) {
                    customer.setBalance(customer.getBalance() - resultPrice); // Вычитаем сумму с баланса покупателя
                    owner.setBalance(owner.getBalance() + ( resultPrice - (resultPrice * COMMISSION))); // Добавляем на баланс продавца
                    product.setQuantity(product.getQuantity() - quantity); // Уменьшаем количество товара оставшегося в магазине
                    /*
                    Решил сделать так для корректной реализации механизма возврата товаров.
                    Если куплено два одинаковых товара и один из них не устраивает покупателя, тогда пользователь сможет
                    вернуть лишь этот товар, а не всю покупку целиком. Понимаю, что в этом случае история покупок
                    может быть отображена не совсем корректно.
                     */
                    for (int i = 0; i < quantity; i++) {
                        Purchase purchase = new Purchase();
                        purchase.setProduct(product);
                        purchase.setCustomer(customer);
                        purchase.setPurchaseDate(LocalDateTime.now());
                        purchase.setPrice(resultPrice/quantity);
                        customer.getPurchasesHistory().add(purchase); // Кладем товар в историю покупок пользователя
                    }
                    customersDetailsService.update(owner);
                    customersDetailsService.update(customer);
                    return HttpStatus.OK;
                }
            }
        }

        return HttpStatus.BAD_REQUEST;
    }

    private Double calculateResultPrice(Product product, int quantity) {
        Discount discount = product.getDiscount();
        if (discount != null) {
            if (discount.getTillDate().after(new Date())) {
                return (product.getPrice() * (discount.getSize() / 100.0)) * quantity;
                }
            }
                return product.getPrice() * quantity;
        }

}
