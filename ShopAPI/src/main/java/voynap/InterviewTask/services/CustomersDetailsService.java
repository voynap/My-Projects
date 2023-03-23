package voynap.InterviewTask.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.Customer;
import voynap.InterviewTask.repositories.CustomersRepository;
import voynap.InterviewTask.security.CustomerDetails;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomersDetailsService implements UserDetailsService {

    private final CustomersRepository customersRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomersDetailsService(CustomersRepository customersRepository, PasswordEncoder passwordEncoder) {
        this.customersRepository = customersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Customer customer) {
        //TODO - ПОЛЬЗОВАТЕЛЬ С ТАКИМ ИМЕНЕМ УЖЕ СУЩЕСТВУЕТ
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole("ROLE_USER");
        customersRepository.save(customer);
    }

    public List<Customer> findAll() {
        return customersRepository.findAll();
    }

    public Optional<Customer> findByUsername(String username) {

        return customersRepository.findByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customersRepository.findByUsername(username);
        if (customer.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomerDetails(customer.get());
    }

    public void update(Customer customer) {
        customersRepository.save(customer);
    }

    public void delete(Customer customerToBeDeleted) {
        customersRepository.delete(customerToBeDeleted);
    }

    public Optional<Customer> findById(Integer id) {
        Optional<Customer> customer = customersRepository.findById(id);
        return customer;
    }
}