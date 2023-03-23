package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.Product;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIdIn(List<Integer> ids);

    List<Product> findDistinctByKeywords_KeywordIn(List<String> keywords);

    List<Product> findByCompanyStatus(String status);
}
