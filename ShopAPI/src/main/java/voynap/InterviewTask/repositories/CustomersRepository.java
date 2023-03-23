package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.Customer;

import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByUsername(String username);
}
