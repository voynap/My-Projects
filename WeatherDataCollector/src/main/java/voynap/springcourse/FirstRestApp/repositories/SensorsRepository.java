package voynap.springcourse.FirstRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.springcourse.FirstRestApp.models.Sensor;

@Repository
public interface SensorsRepository extends JpaRepository<Sensor, Integer> {
        Sensor findByName(String name);
}
