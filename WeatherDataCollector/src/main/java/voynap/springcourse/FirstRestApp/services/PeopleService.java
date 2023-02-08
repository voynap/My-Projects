package voynap.springcourse.FirstRestApp.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.springcourse.FirstRestApp.models.Person;
import voynap.springcourse.FirstRestApp.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> showAll() {

        return peopleRepository.findAll();
    }

    public Person showDetailed(int id) {
        Optional<Person> foundPerson =  peopleRepository.findById(id);
        if (foundPerson.isPresent()) {
            Hibernate.initialize(foundPerson);
        }
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person) {
        enrichPerson(person);
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id, Person updatedPerson) {

        peopleRepository.save(updatedPerson);
    }
    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }



    private void enrichPerson(Person person) {


    }


}
