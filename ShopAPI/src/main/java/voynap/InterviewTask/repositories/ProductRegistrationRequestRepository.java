package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.requests.ProductRegistrationRequest;

@Repository
public interface ProductRegistrationRequestRepository extends JpaRepository<ProductRegistrationRequest, Integer> {

}
