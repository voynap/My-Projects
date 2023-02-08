package voynap.WeatherDataCollector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.WeatherDataCollector.models.Sensor;

@Repository
public interface SensorsRepository extends JpaRepository<Sensor, Integer> {
        Sensor findByName(String name);
}
