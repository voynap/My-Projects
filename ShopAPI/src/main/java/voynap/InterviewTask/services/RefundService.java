package voynap.InterviewTask.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.requests.RefundRequest;
import voynap.InterviewTask.repositories.RefundRepository;

@Service
public class RefundService {
    private final RefundRepository repository;

    public RefundService(RefundRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public void save(RefundRequest request){
        repository.save(request);
    }

    public void delete(RefundRequest request) {
        repository.delete(request);
    }
}
