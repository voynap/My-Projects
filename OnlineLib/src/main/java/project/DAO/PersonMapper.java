package project.DAO;

import org.springframework.jdbc.core.RowMapper;
import project.models.Person;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PersonMapper implements RowMapper<Person> {



    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {

        Person person = new Person();

        person.setId(resultSet.getInt("person_id"));
        person.setFullName(resultSet.getString("fullname"));
        person.setLastName(resultSet.getString("lastname"));
        person.setName(resultSet.getString("firstname"));
        person.setPatronymic(resultSet.getString("patronymic"));
        person.setBirthYear(resultSet.getInt("birth_year"));

        return person;
    }
}
