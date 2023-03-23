package voynap.InterviewTask.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.requests.ProductRegistrationRequest;
import voynap.InterviewTask.repositories.ProductRegistrationRequestRepository;

import java.util.List;

@Service
public class ProductRegistrationRequestService {

    private final ProductRegistrationRequestRepository repository;

    public ProductRegistrationRequestService(ProductRegistrationRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(ProductRegistrationRequest request) {
        repository.save(request);
    }

    public List<ProductRegistrationRequest> findAll() {
        return repository.findAll();
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
