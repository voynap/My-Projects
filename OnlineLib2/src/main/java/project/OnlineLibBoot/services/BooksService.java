package project.OnlineLibBoot.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.OnlineLibBoot.models.Book;
import project.OnlineLibBoot.models.Person;
import project.OnlineLibBoot.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;
    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }


    public List<Book> showAll() {
        return booksRepository.findAll();
    }

    public List<Book> showAll(int page, int size) {
        if (size!=0)
            return booksRepository.findAll(PageRequest.of(page, size)).getContent();
        else return  booksRepository.findAll();
    }

    public List<Book> showAll(boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else return booksRepository.findAll();
    }

    public List<Book> showAll(int page, int size, boolean sortByYear) {
        if (sortByYear) {
            if (size !=0) {
                return booksRepository.findAll(PageRequest.of(page, size, Sort.by("year"))).getContent();}
        } else {
            if (size !=0) { showAll(page, size);}
        }
        return booksRepository.findAll();
    }
    public List<Book> findByNameStartingWith(String startingWith) {

        return booksRepository.findByNameStartingWith(startingWith);
    }


    public Book showDetailed(int id) {
        Optional<Book> book = booksRepository.findById(id);

        return book.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }
    @Transactional
    public void delete(int id) {

        booksRepository.deleteById(id);
    }

    public List<Book> findByOwner(Person owner) {
        return booksRepository.findByOwner(owner);
    }

    @Transactional
    public void release(Book book) {
        book.setOwner(null);
        book.setCreatedAt(new Date());
        booksRepository.save(book);
    }

    @Transactional
    public void apply(Book book, Person person) {
        book.setCreatedAt(new Date());
        book.setOwner(person);
        booksRepository.save(book);
    }


    public boolean checkForOverdue(Book book) {
        long taken = book.getCreatedAt().getTime();
        long now = new Date().getTime();
        long tenDaysInMillis = 1000 * 60 * 60 * 24 * 10;
        if (now - taken > tenDaysInMillis) {
            return true;
        } else {
            return false;
        }
    }

    public void checkBookListForOverdue(List<Book> takenBooks) {
        for (Book book : takenBooks) {
            book.setOverdue(checkForOverdue(book));
        }
    }


    public Book searchFor(Book book) {

        Optional<List<Book>> desiredBook  = Optional.ofNullable(findByNameStartingWith(book.getName()));
        if (!desiredBook.get().isEmpty())
            return desiredBook.get().get(0);
        else
            return null;

    }

}

