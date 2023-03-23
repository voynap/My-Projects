package voynap.InterviewTask.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.requests.CompanyRegistrationRequest;
import voynap.InterviewTask.repositories.CompanyRegistrationRequestRepository;

import java.util.List;


@Service
public class CompanyRegistrationRequestsService {

   private final CompanyRegistrationRequestRepository repository;

    public CompanyRegistrationRequestsService(CompanyRegistrationRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(CompanyRegistrationRequest company) {
        repository.save(company);
    }

    public List<CompanyRegistrationRequest> findAll() {
        return repository.findAll();
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
