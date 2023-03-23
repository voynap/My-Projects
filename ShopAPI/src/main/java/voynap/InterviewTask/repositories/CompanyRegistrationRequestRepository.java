package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.requests.CompanyRegistrationRequest;

@Repository
public interface CompanyRegistrationRequestRepository extends JpaRepository<CompanyRegistrationRequest, Integer> {

}