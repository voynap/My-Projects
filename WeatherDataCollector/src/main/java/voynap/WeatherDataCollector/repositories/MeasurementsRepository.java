package voynap.WeatherDataCollector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.WeatherDataCollector.models.Measurement;

import java.util.List;

@Repository
public interface MeasurementsRepository extends JpaRepository<Measurement, Integer> {

    List<Measurement> findByRainingTrue();
}
