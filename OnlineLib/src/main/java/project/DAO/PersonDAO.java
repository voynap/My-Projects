package project.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import project.models.Person;

import java.util.List;

@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Person> showAll() {
        return jdbcTemplate.query("SELECT * FROM Person", new PersonMapper());
    }

    public Person showDetailed(int id) {
        return jdbcTemplate.query("SELECT * FROM Person WHERE person_id=?", new Object[]{id}, new PersonMapper())
                .stream().findAny().orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO Person(fullname, lastname, firstname, patronymic, birth_year) VALUES (?,?,?,?,?)",
                person.getFullName(), person.getLastName(), person.getName(), person.getPatronymic(), person.getBirthYear());
    }

    public void update(int id, Person updatedPerson) {
        jdbcTemplate.update("UPDATE Person SET fullname=?, lastname=?, firstname=?, patronymic=?, birth_year=? WHERE person_id=?",
                updatedPerson.getFullName(), updatedPerson.getLastName(), updatedPerson.getName(),
                updatedPerson.getPatronymic(), updatedPerson.getBirthYear(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM Person WHERE person_id=?", id);
    }
}
