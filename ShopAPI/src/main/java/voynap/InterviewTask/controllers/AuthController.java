package voynap.InterviewTask.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import voynap.InterviewTask.dto.AuthenticationRequest;
import voynap.InterviewTask.dto.CustomerDTO;
import voynap.InterviewTask.models.Customer;
import voynap.InterviewTask.security.JWTUtil;
import voynap.InterviewTask.services.CustomersDetailsService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final CustomersDetailsService customersDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final JWTUtil jwtUtil;
    @Autowired
    public AuthController(CustomersDetailsService customersDetailsService, PasswordEncoder passwordEncoder, ModelMapper modelMapper, JWTUtil jwtUtil) {
        this.customersDetailsService = customersDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/registration")
    public Map<String, String> create(@RequestBody @Valid CustomerDTO customerDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //TODO
        }
        customersDetailsService.register(convertToCustomer(customerDTO));

        String token = jwtUtil.generateToken(customerDTO.getUsername(), customerDTO.getRole());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationRequest request) {

        Optional<Customer> customer = customersDetailsService.findByUsername(request.getUsername());
        String token = "Invalid Token";
        System.out.println(customer.get().getPassword());
        System.out.println(passwordEncoder.encode(request.getPassword()));
        if (customer.isPresent()) {
            if (passwordEncoder.matches(request.getPassword(), customer.get().getPassword())) {
                token = jwtUtil.generateToken(customer.get().getUsername(), customer.get().getRole());
                return Map.of("jwt-token", token);
            }
        }
        return Map.of("jwt-token", token);
    }

    private Customer convertToCustomer(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }
    private CustomerDTO convertToSensorDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }
}
