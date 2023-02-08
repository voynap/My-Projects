package project.OnlineLibBoot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.OnlineLibBoot.models.Book;
import project.OnlineLibBoot.models.Person;


import java.util.List;


@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    List<Book> findByOwner(Person owner);

    List<Book> findByNameStartingWith(String startingWith);
}
