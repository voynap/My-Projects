package voynap.InterviewTask.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import voynap.InterviewTask.dto.CompanyDTO;
import voynap.InterviewTask.dto.CustomerDTO;
import voynap.InterviewTask.dto.DiscountDTO;
import voynap.InterviewTask.dto.NotificationDTO;
import voynap.InterviewTask.models.*;
import voynap.InterviewTask.models.requests.CompanyRegistrationRequest;
import voynap.InterviewTask.models.requests.ProductRegistrationRequest;
import voynap.InterviewTask.models.requests.RefundRequest;
import voynap.InterviewTask.repositories.CompanyRepository;
import voynap.InterviewTask.services.*;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final CustomersDetailsService customersDetailsService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationsService notificationsService;
    private final DiscountsService discountsService;
    private final ProductsService productsService;
    private final CompanyRegistrationRequestsService CRRService;
    private final ProductRegistrationRequestService PRRService;
    private final CompanyService companyService;

    private final RefundService refundService;

    @Autowired
    public AdminController(CustomersDetailsService customersDetailsService, ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder, NotificationsService notificationsService,
                           DiscountsService discountsService, ProductsService productsService,
                           CompanyRegistrationRequestsService crrService, ProductRegistrationRequestService prrService, CompanyService companyService,
                           CompanyRepository companyRepository, RefundService refundService) {
        this.customersDetailsService = customersDetailsService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.notificationsService = notificationsService;
        this.discountsService = discountsService;
        this.productsService = productsService;
        this.CRRService = crrService;
        this.PRRService = prrService;
        this.companyService = companyService;
        this.refundService = refundService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/show")
    public ResponseEntity<Customer> showCustomerInfo(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "username", required = false) String username) {
        if (id != null) {
            Optional<Customer> customer = customersDetailsService.findById(id);
            if (customer.isPresent()) {
                return ResponseEntity.ok(customer.get());
            }
        } else if (username != null) {
            Optional<Customer> customer = customersDetailsService.findByUsername(username);
            if (customer.isPresent()) {
                return ResponseEntity.ok(customer.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PutMapping("/update_user")
    public HttpStatus updateUserInfo(@RequestBody CustomerDTO customerDTO) {
        // ИЗМЕНЯЕМ ИНФОРМАЦИЮ, КРОМЕ БАЛАНСА
        Optional<Customer> updatedCustomer = customersDetailsService.findByUsername(customerDTO.getUsername());
        if (updatedCustomer.isPresent()) {
            if (customerDTO.getRole()!=null)
                updatedCustomer.get().setRole(customerDTO.getRole());
            if (customerDTO.getUsername()!= null)
                updatedCustomer.get().setUsername(customerDTO.getUsername());
            if (customerDTO.getPassword()!=null)
                updatedCustomer.get().setPassword(passwordEncoder.encode(customerDTO.getPassword()));
            if (customerDTO.getEmail()!=null)
                updatedCustomer.get().setEmail(customerDTO.getEmail());
            customersDetailsService.update(updatedCustomer.get());
            return HttpStatus.OK;
        } else
            return HttpStatus.BAD_REQUEST;
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PutMapping("/freeze")
    public HttpStatus freezeUser(@RequestBody CustomerDTO customerDTO) {
        // В принципе, метод updateUserInfo способен сделать тоже самое, но на всякий случай :
        if (customerDTO.getId() != null) {
            Optional<Customer> customer = customersDetailsService.findById(customerDTO.getId());
            customer.ifPresent(value -> value.setRole("ROLE_FROZEN"));
        } else if (customerDTO.getUsername() != null) {
            Optional<Customer> customer = customersDetailsService.findByUsername(customerDTO.getUsername());
            customer.ifPresent(value -> value.setRole("ROLE_FROZEN"));
        }

        return HttpStatus.OK;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PutMapping("/deposit")
    public HttpStatus depositToUserAccount(@RequestBody CustomerDTO customerDTO) {
        Optional<Customer> customer = customersDetailsService.findByUsername(customerDTO.getUsername());
        if (customer.isPresent()) {
            if (customerDTO.getBalance() != null) {
                customer.get().setBalance(customerDTO.getBalance());
                return HttpStatus.OK;
            } else {
                return HttpStatus.BAD_REQUEST;
            }
        }
        return HttpStatus.BAD_REQUEST;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @DeleteMapping("/delete")
    public HttpStatus deleteUser(@RequestBody CustomerDTO customerDTO) {
        Optional<Customer> customerToBeDeleted = customersDetailsService.findByUsername(customerDTO.getUsername());
        if (customerToBeDeleted.isPresent()) {
           customersDetailsService.delete(customerToBeDeleted.get());
           return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PutMapping("/update_company")
    public HttpStatus updateCompanyIfo(@RequestBody CompanyDTO companyDTO) {
        // ИЗМЕНЯЕМ ИНФОРМАЦИЮ ОБ ОРГАНИЗАЦИИ
        Optional<Company> updatedCompany = companyService.findByName(companyDTO.getName());

        if (updatedCompany.isPresent()) {
            if (companyDTO.getStatus()!=null)
                // FROZEN, BANNED , или любые другие.
                updatedCompany.get().setStatus(companyDTO.getStatus());
            if (companyDTO.getName()!= null)
                updatedCompany.get().setName(companyDTO.getName());
            if (companyDTO.getDescription()!=null)
                updatedCompany.get().setDescription(companyDTO.getDescription());
            if (companyDTO.getOwnerId()!=null) {
                Optional<Customer> owner = customersDetailsService.findById(companyDTO.getOwnerId());
                if (owner.isPresent()) {
                    updatedCompany.get().setOwner(owner.get());
                }
            }
            companyService.save(updatedCompany.get());
            return HttpStatus.OK;
        } else
            return HttpStatus.BAD_REQUEST;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping("/notification")
    public HttpStatus createNotification(@RequestBody NotificationDTO notificationDTO) {

        Notification notification = new Notification();
        notification.setHead(notificationDTO.getHead());
        notification.setText(notificationDTO.getText());
        notification.setDate(notificationDTO.getDate());

        List<Integer> customersId = notificationDTO.getCustomersId();
        notificationsService.save(notification);

        for (Integer id : customersId) {
           Optional<Customer> customer = customersDetailsService.findById(id);
           if (customer.isPresent()) {
               customer.get().getNotifications().add(notification);
               customersDetailsService.update(customer.get());
           }
        }

        return HttpStatus.OK;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping("/discount")
    //МЕТОД МОЖЕТ ПРИНИМАТЬ НА ВХОД ЛИБО ДАТУ ОКОНЧАНИЯ СКИДКИ, ЛИБО КОЛИЧЕСТВО ЧАСОВ С ТЕКУЩЕГО МОМЕНТА
    public HttpStatus createDiscount(@RequestBody DiscountDTO discountDTO) {
        Discount discount = new Discount();
        int size = discountDTO.getSize();
        if (size > 100 || size < 0) {
            return HttpStatus.BAD_REQUEST;
        }
        discount.setSize(discountDTO.getSize());
        if (discountDTO.getTillDate() != null) {
            discount.setTillDate(discountDTO.getTillDate());
            discountsService.save(discount);
        } else if (discountDTO.getHours() != null) {
            discount.setTillDate(Date.from(ZonedDateTime.now().plusHours(discountDTO.getHours()).toInstant()));
            discountsService.save(discount);
        } else {
            return HttpStatus.BAD_REQUEST;
        }

        List<Integer> productsId = discountDTO.getProductsId();

        List<Product> products = productsService.getProductsByIds(productsId);

        for (Product product : products) {
            product.setDiscount(discount);
            productsService.save(product);
        }
        return HttpStatus.OK;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/applications_company")
    public List<CompanyRegistrationRequest> getCompanyApplications() {
        return CRRService.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/applications_product")
    public List<ProductRegistrationRequest> getProductApplications() {
        return PRRService.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping("/apply_company")
    public HttpStatus applyCompany(@RequestBody CompanyRegistrationRequest application) {
        Company company = new Company();
        Optional<Customer> customer = customersDetailsService.findById(application.getCustomerId());
        if (customer.isPresent()) {
            company.setName(application.getName());
            company.setDescription(application.getDescription());
            company.setLogoURL(application.getLogoURL());
            company.setOwner(customer.get());
            company.setStatus("ACTIVE");
            companyService.save(company);
            CRRService.delete(application.getId());

            if (customer.get().getRole().equals("ROLE_USER")) {
                customer.get().setRole("ROLE_OWNER");
               // Пользователелю необходимо перелогиниться и получить новый JWT Token, если это его первая организация.
                customersDetailsService.update(customer.get());
            }
            /*
            Опционально : отправляем уведомления пользователю
            =============================================================================
            Notification notification = new Notification();
            notification.setDate(new Date());
            notification.setHead("ВАША КОМПАНИЯ ЗАРЕГИСТРИРОВАНА");
            notification.setText("Поздравляем, ваша компания " + application.getName()
                    + "была успешно зарегистрирована");
            customer.get().getNotifications().add(notification);

            =============================================================================
             */
            return HttpStatus.OK;
        }

        return HttpStatus.BAD_REQUEST;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping("/apply_product")
    public HttpStatus applyProduct(@RequestBody ProductRegistrationRequest application) {
        Product product = new Product();
        Optional<Company> company = companyService.findById(application.getCompanyId());

        if (company.isPresent()) {
                product.setName(application.getName());
                product.setDescription(application.getDescription());
                product.setPrice(application.getPrice());
                product.setCompany(company.get());
                productsService.save(product);
                PRRService.delete(application.getId());
                return HttpStatus.OK;
            }
                return HttpStatus.BAD_REQUEST;
        }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping("/apply_refund")

    public ResponseEntity<String> applyRefund(@RequestBody RefundRequest request) {
        Optional<Customer> optionalCustomer = customersDetailsService.findById(request.getCustomerId());

        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь не найден");
        }
        Customer customer = optionalCustomer.get();
        List<Purchase> purchases = customer.getPurchasesHistory();
        Purchase targetPurchase = null;

        for (Purchase purchase : purchases) {
            if (purchase.getId() == request.getPurchaseId()) {
                targetPurchase = purchase;
                break;
            }
        }
        // Проверяем, была ли совершена покупка.
        if (targetPurchase == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Покупка не найдена");
        }

        if (targetPurchase.getPrice() != request.getPrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Сумма возврата не совпадает с суммой покупки");
        }

        // Возвращаем средства на аккаунт пользователя
        customer.setBalance(customer.getBalance() + request.getPrice());

        Customer owner = targetPurchase.getProduct().getCompany().getOwner();

        owner.setBalance(owner.getBalance() - request.getPrice());

        purchases.remove(targetPurchase);

        customersDetailsService.update(customer);
        customersDetailsService.update(owner);
        refundService.delete(request);


        return ResponseEntity.status(HttpStatus.OK).body("Средства были успешно возвращены");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/history")
    //ПРИНИМАЕМ ID ПОЛЬЗОВАТЕЛЯ
    public List<Purchase> getPurchaseHistory(@RequestParam int id) {
        Optional<Customer> customer = customersDetailsService.findById(id);
        return customer.map(Customer::getPurchasesHistory).orElse(null);
    }






    private Customer convertToCustomer(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }
    private CustomerDTO convertToSensorDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

}
