package project.OnlineLibBoot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.OnlineLibBoot.models.Book;
import project.OnlineLibBoot.models.Person;
import project.OnlineLibBoot.repositories.BooksRepository;

import project.OnlineLibBoot.services.PeopleService;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;

    private final PeopleService peopleService;
    private final BooksRepository booksRepository;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService,
                           BooksRepository booksRepository) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.booksRepository = booksRepository;
    }

    @GetMapping()
    public String showAll(@RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "size", required = false) Integer size,
                          @RequestParam(value = "sort_by_year", required = false) Boolean sortByYear, Model model) {

        if (sortByYear != null) {
            if (page != null && size != null) {
                model.addAttribute("books", booksService.showAll(page, size, sortByYear));
            } else {
                model.addAttribute("books", booksService.showAll(sortByYear));
            }
        } else {
            if (page != null && size != null) {
                model.addAttribute("books", booksService.showAll(page, size));
            } else {
                model.addAttribute("books", booksService.showAll());
            }
        }

        return "books/showAll";
    }




    @GetMapping("/{id}")
    public String showDetailed(@PathVariable("id") int id, Model model) {

        Book book = booksService.showDetailed(id);
        book.setOverdue(booksService.checkForOverdue(book));
        Person person;
        if (book.getOwner() != null) {
           person = peopleService.showDetailed(book.getOwner().getId());
        } else {
            person = new Person();
        }
        model.addAttribute("book", book);
        model.addAttribute("people", peopleService.showAll());
        model.addAttribute("person", person);

        return "books/showDetailed";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", booksService.showDetailed(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book updatedBook, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(id, updatedBook);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        Book book = booksService.showDetailed(id);
        booksService.release(book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/apply")
    public String apply(@ModelAttribute("person") Person personFromForm, @PathVariable("id") int bookId) {
        Person person = peopleService.showDetailed(personFromForm.getId());
        Book book = booksService.showDetailed(bookId);
        booksService.apply(book, person);
        return "redirect:/books";

    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String search(@ModelAttribute("book") Book book, Model model) {
        model.addAttribute("booksList", booksService.showAll());
        return "books/search";
    }

    @GetMapping("/search/result")
    public String searchProcessing(@ModelAttribute("book") Book book, Model model) {
        Book desiredBook = booksService.searchFor(book);
        Boolean isFound = true;
         if (desiredBook == null) {
             isFound = false;
         }
         model.addAttribute("desiredBook", desiredBook);
         model.addAttribute("isFound", isFound);

        return "books/search";
    }


}
