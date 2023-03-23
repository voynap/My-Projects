package voynap.InterviewTask.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import voynap.InterviewTask.dto.CustomerDTO;
import voynap.InterviewTask.dto.ProductDTO;
import voynap.InterviewTask.models.*;
import voynap.InterviewTask.models.requests.CompanyRegistrationRequest;
import voynap.InterviewTask.models.requests.ProductRegistrationRequest;
import voynap.InterviewTask.models.requests.RefundRequest;
import voynap.InterviewTask.repositories.ProductsRepository;
import voynap.InterviewTask.security.JWTUtil;
import voynap.InterviewTask.services.*;

;import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/customers")
public class CustomersController {
    private final CustomersDetailsService customersDetailsService;
    private final ModelMapper modelMapper;

    private final JWTUtil jwtUtil;

    private final NotificationsService notificationsService;

    private final CompanyRegistrationRequestsService regRequestService;

    private final ProductRegistrationRequestService productRegistrationRequestService;

    private final RefundService refundService;

    private final ProductsService productsService;


    @Autowired
    public CustomersController(CustomersDetailsService customersDetailsService, ModelMapper modelMapper,
                               JWTUtil jwtUtil, NotificationsService notificationsService,
                               CompanyRegistrationRequestsService regRequestService, ProductRegistrationRequestService productRegistrationRequestService, CompanyService companyService,
                               ProductsRepository productsRepository, RefundService refundService, ProductsService productsService) {
        this.customersDetailsService = customersDetailsService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.notificationsService = notificationsService;
        this.regRequestService = regRequestService;
        this.productRegistrationRequestService = productRegistrationRequestService;
        this.refundService = refundService;
        this.productsService = productsService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/notification")
    public List<Notification> showNotification(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        List<Notification> notifications = notificationsService.findByUsername(username);
        return notifications;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/history")
    public List<Purchase> getPurchaseHistory(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer customer = customersDetailsService.findByUsername(username).get();
        return customer.getPurchasesHistory();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OWNER')")
    @PostMapping("/register_company")
    /*
    Убрал администратора из списка доступных ролей поскольку
    администратор владеющий магазином на администрируемой им же торговой площадке
    возможно не совсем этичное явление
     */
    public HttpStatus registerCompany(@RequestBody CompanyRegistrationRequest request,
                                      @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer customer = customersDetailsService.findByUsername(username).get();

        CompanyRegistrationRequest company = new CompanyRegistrationRequest();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setLogoURL(request.getLogoURL());
        company.setCustomerId(customer.getId());

        try {
            regRequestService.save(company);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OWNER')")
    @PostMapping("/register_product")
    public HttpStatus registerProduct(@RequestBody ProductRegistrationRequest request,
                                      @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer owner = customersDetailsService.findByUsername(username).get();

        for (Company company : owner.getCompanies()) {
            if (company.getName().equals(request.getCompanyName())) {
                ProductRegistrationRequest product = new ProductRegistrationRequest();
                product.setName(request.getName());
                product.setDescription(request.getDescription());
                product.setPrice(request.getPrice());
                product.setCompanyId(company.getId());

                productRegistrationRequestService.save(product);
                return HttpStatus.OK;
            }
        }
        return HttpStatus.BAD_REQUEST;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OWNER')")
    @PostMapping("/refund")
    //Получаем ID покупки из истории
    public HttpStatus registerProduct(@RequestBody ProductDTO productDTO,
                                      @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer customer = customersDetailsService.findByUsername(username).get();


        Purchase purchase = customer.getPurchasesHistory().get(productDTO.getId());
        if (purchase == null) {
            return HttpStatus.BAD_REQUEST;
        }

        LocalDateTime purchaseDate = purchase.getPurchaseDate();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        if (purchaseDate.isAfter(twentyFourHoursAgo)) {
            RefundRequest request = new RefundRequest();
            request.setCustomerId(customer.getId());
            request.setPrice(purchase.getPrice());
            request.setPurchaseId(purchase.getId());
            refundService.save(request);
            return HttpStatus.OK;

        } else {
            // Возможная отправка уведомлению пользователю
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OWNER')")
    @PostMapping("/rate")
    // Принимаем ID продукта который хотим оценить и оценку
    public ResponseEntity<String> rateProduct(@RequestBody ProductDTO productDTO, @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer customer = customersDetailsService.findByUsername(username).get();

        if (productDTO.getScore() > 5 || productDTO.getScore() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Оценка должна находиться в диапазоне от 0 до 5 баллов");
        }

        Optional<Product> productOptional = productsService.findById(productDTO.getId());

        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Продукт не найден");
        }

        Product product = productOptional.get();
        boolean productInPurchaseHistory = false;

        for (Purchase purchase : customer.getPurchasesHistory()) {
            if (purchase.getProduct().getId() == product.getId()) {
                productInPurchaseHistory = true;
                break;
            }
        }

        if (!productInPurchaseHistory) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Товара не найдено в истории покупока");
        }

        Rating rating = new Rating();
        rating.setScore(productDTO.getScore());
        rating.setProduct(product);
        product.getRatingList().add(rating);

        productsService.save(product);


        return ResponseEntity.status(HttpStatus.OK).body("Вы успешно оценили продукт");
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OWNER')")
    @PostMapping("/review")
    // Принимаем ID продукта на который пишем отзыв и текст
    // Возможно было объединить с предыдущим методом, но для наглядности решил разделить. Конечно, из-за этого возникает дублирование кода :c
    public ResponseEntity<String> reviewProduct(@RequestBody ProductDTO productDTO, @RequestHeader("Authorization") String token) {

        String actualToken = token.replace("Bearer ", "");
        String username = jwtUtil.validateTokenAndRetrieveClaim(actualToken);
        Customer customer = customersDetailsService.findByUsername(username).get();


        Optional<Product> productOptional = productsService.findById(productDTO.getId());

        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Продукт не найден");
        }

        Product product = productOptional.get();
        boolean productInPurchaseHistory = false;

        for (Purchase purchase : customer.getPurchasesHistory()) {
            if (purchase.getProduct().getId() == product.getId()) {
                productInPurchaseHistory = true;
                break;
            }
        }

        if (!productInPurchaseHistory) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Товара не найдено в истории покупока");
        }

        Review review = new Review();
        review.setText(productDTO.getReview());
        review.setProduct(product);
        product.getReviews().add(review);


        productsService.save(product);


        return ResponseEntity.status(HttpStatus.OK).body("Вы успешно оставили отзыв на продукт");
    }







    private Customer convertToCustomer(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }
    private CustomerDTO convertToSensorDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

}
