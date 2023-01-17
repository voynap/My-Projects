package project.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.DAO.BookDAO;
import project.DAO.PersonDAO;
import project.models.Book;
import project.models.Person;


import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookDAO bookDAO;
    private final PersonDAO personDAO;

    public BooksController(BookDAO bookDAO, PersonDAO personDAO) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
    }


    @GetMapping()
    public String showAll(Model model) {
        model.addAttribute("books", bookDAO.showAll());
        return "books/showAll";
    }

    @GetMapping("/{id}")
    public String showDetailed(@ModelAttribute("person") Person person, @PathVariable("id") int id, Model model) {
        int personId = bookDAO.showDetailed(id).getPersonId();
        model.addAttribute("book", bookDAO.showDetailed(id));
        model.addAttribute("visitor", personDAO.showDetailed(personId));
        model.addAttribute("people", personDAO.showAll());
        return "books/showDetailed";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        bookDAO.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.showDetailed(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book updatedBook, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if (bindingResult.hasErrors())
            return "books/edit";

        bookDAO.update(id, updatedBook);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        Book book = bookDAO.showDetailed(id);
        Person person = personDAO.showDetailed(book.getPersonId());
        person.removeBook(bookDAO.showDetailed(id));
        bookDAO.release(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/apply")
    public String apply(@ModelAttribute("person") Person person, @PathVariable("id") int bookId) {
        bookDAO.apply(bookId, person.getId());
        person = personDAO.showDetailed(person.getId());
        person.addBook(bookDAO.showDetailed(bookId));
        return "redirect:/books";

    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookDAO.delete(id);
        return "redirect:/books";
    }

}
