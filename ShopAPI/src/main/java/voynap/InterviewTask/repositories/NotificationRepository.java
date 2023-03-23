package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.Customer;
import voynap.InterviewTask.models.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByCustomers(Customer customer);
}
