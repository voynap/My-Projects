package project.models;

import project.DAO.PersonDAO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class Person {

    private int id;

    private List<Book> takenBooks = new ArrayList<>();
    @NotEmpty(message = "Необходимо заполнить имя")
    @Pattern(regexp = "([А-Я][а-я]*)|([A-Z]\\w*)", message = "Имя должно начинаться с большой буквы. Допустимы символы кириллицы или латиницы")
    private String name;
    @Pattern(regexp = "([А-Я][а-я]*)|([A-Z]\\w*)", message = "Фамилия должна начинаться с большой буквы. Допустимы символы кириллицы или латиницы")
    @NotEmpty(message = "Необходимо заполнить фамилию")
    private String lastName;
    @Pattern(regexp = "([А-Я][а-я]*)|([A-Z]\\w*)", message = "Отчество должно начинаться с большой буквы. Допустимы символы кириллицы или латиницы")
    @NotEmpty(message = "Необходимо заполнить отчество")
    private String patronymic;

    private String fullName;
    @Min(value = 1900, message = "Год рождения не может быть меньше 1900")
    @Max(value = 2023, message = "Год рождения не может быть больше текущего")
    private int birthYear;

    public Person() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person(int id, String lastName, String name, String patronymic, int birthYear) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.patronymic = patronymic;
        this.birthYear = birthYear;
        this.fullName = lastName + " " + name + " " + patronymic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullname) {

        this.fullName = fullname;
    }

    public void setFullName(String lastName, String name, String patronymic) {

        this.fullName = lastName + " " + name + " " + patronymic;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }


    public List<Book> getTakenBooks() {
        return takenBooks;
    }

    public void setTakenBooks(List<Book> takenBooks) {
        this.takenBooks = takenBooks;
    }

    public void addBook(Book book) {
        takenBooks.add(book);
    }

    public void removeBook(Book book) {
        takenBooks.remove(book);
    }


}
