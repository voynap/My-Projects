package voynap.WeatherDataCollector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.WeatherDataCollector.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
