package voynap.InterviewTask.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.Discount;
import voynap.InterviewTask.repositories.DiscountsRepository;

@Service
public class DiscountsService {

    private final DiscountsRepository discountsRepository;
    @Autowired
    public DiscountsService(DiscountsRepository discountsRepository) {
        this.discountsRepository = discountsRepository;
    }

    @Transactional
    public void save(Discount discount) { discountsRepository.save(discount);
    }
}
