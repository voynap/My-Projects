package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.Discount;

@Repository
public interface DiscountsRepository extends JpaRepository<Discount, Integer> {
}
