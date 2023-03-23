package voynap.InterviewTask.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.Company;
import voynap.InterviewTask.repositories.CompanyRepository;

import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public void save(Company company) {
        companyRepository.save(company);
    }

    public Optional<Company> findByName(String companyName) {
        return companyRepository.findByName(companyName);
    }

    public Optional<Company> findById(int companyId) {
        return companyRepository.findById(companyId);
    }
}
