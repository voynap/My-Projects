package voynap.springcourse.FirstRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.springcourse.FirstRestApp.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
