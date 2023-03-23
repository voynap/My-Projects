package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.requests.RefundRequest;

@Repository
public interface RefundRepository extends JpaRepository<RefundRequest, Integer> {

}
