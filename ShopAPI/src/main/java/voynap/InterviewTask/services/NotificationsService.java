package voynap.InterviewTask.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.InterviewTask.models.Customer;
import voynap.InterviewTask.models.Notification;
import voynap.InterviewTask.repositories.NotificationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsService {

    private final NotificationRepository notificationRepository;
    private final CustomersDetailsService customersDetailsService;
    @Autowired

    public NotificationsService(NotificationRepository notificationRepository, CustomersDetailsService customersDetailsService) {
        this.notificationRepository = notificationRepository;
        this.customersDetailsService = customersDetailsService;
    }

    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    public List<Notification> findByUsername(String username) {
        Optional<Customer> customer = customersDetailsService.findByUsername(username);
        if (customer.isPresent()) {
            return new ArrayList<>(customer.get().getNotifications());
        } else {
            return Collections.emptyList();
        }
    }

}
