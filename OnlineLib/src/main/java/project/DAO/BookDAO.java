package project.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import project.models.Book;


import java.util.List;

@Component
public class BookDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Book> showAll() {

        return jdbcTemplate.query("SELECT * FROM Book", new BookMapper());
    }

    public List<Book> showTakenByPerson(int personId) {

        return jdbcTemplate.query("SELECT * FROM Book WHERE person_id=?", new Object[]{personId}, new BookMapper());
    }

    public Book showDetailed(int id) {
        return jdbcTemplate.query("SELECT * FROM Book WHERE book_id=?", new Object[]{id}, new BookMapper())
                .stream().findAny().orElse(null);
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO Book(name, author, year) VALUES (?,?,?)",
                book.getName(),  book.getAuthor(), book.getYear());
    }

    public void update(int id, Book updatedBook) {
        jdbcTemplate.update("UPDATE Book SET name=?, author=?, year=? WHERE book_id=?",
                updatedBook.getName(), updatedBook.getAuthor(), updatedBook.getYear(), id );
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM Book WHERE book_id=?", id);
    }

    public void release(int id) {
        jdbcTemplate.update("UPDATE Book Set person_id=? WHERE book_id=?", null, id);
    }

    public void apply(int bookId, int personId) {
        jdbcTemplate.update("UPDATE Book Set person_id=? WHERE book_id=?", personId, bookId);
    }

}
