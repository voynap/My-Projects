package project.OnlineLibBoot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.OnlineLibBoot.models.Person;
import project.OnlineLibBoot.services.PeopleService;


@Controller
@RequestMapping("/people")
public class PeopleController {


    private final PeopleService peopleService;

    private final BooksService booksService;


    @Autowired
    public PeopleController(PeopleService peopleService, BooksService booksService) {

        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String showAll(Model model) {
        model.addAttribute("people", peopleService.showAll());
        return "people/showAll";
    }

    @GetMapping("/{id}")
    public String showDetailed(@PathVariable("id") int id, Model model) {
        Person person =  peopleService.showDetailed(id);
        booksService.checkBookListForOverdue(person.getTakenBooks());
        model.addAttribute("person", person);
        return "people/showDetailed";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        person.setFullName(person.getLastName(), person.getName(), person.getPatronymic());
        if (bindingResult.hasErrors())
            return "people/new";

        peopleService.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.showDetailed(id));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person updatedPerson, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if (bindingResult.hasErrors())
            return "people/edit";

        updatedPerson.setFullName(updatedPerson.getLastName(), updatedPerson.getName(), updatedPerson.getPatronymic());
        peopleService.update(id, updatedPerson);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }





}
