package project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.DAO.BookDAO;
import project.DAO.PersonDAO;
import project.models.Person;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final BookDAO bookDAO;
    private final PersonDAO personDAO;
    @Autowired
    public PeopleController(BookDAO bookDAO, PersonDAO personDAO) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
    }

    @GetMapping()
    public String showAll(Model model) {
        model.addAttribute("people", personDAO.showAll());
        return "people/showAll";
    }

    @GetMapping("/{id}")
    public String showDetailed(@PathVariable("id") int id, Model model) {
        Person person =  personDAO.showDetailed(id);
        person.setTakenBooks(bookDAO.showTakenByPerson(person.getId()));
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

        personDAO.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", personDAO.showDetailed(id));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person updatedPerson, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if (bindingResult.hasErrors())
            return "people/edit";

        updatedPerson.setFullName(updatedPerson.getLastName(), updatedPerson.getName(), updatedPerson.getPatronymic());
        personDAO.update(id, updatedPerson);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personDAO.delete(id);
        return "redirect:/people";
    }

}
