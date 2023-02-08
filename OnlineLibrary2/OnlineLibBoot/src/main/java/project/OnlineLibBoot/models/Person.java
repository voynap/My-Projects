package project.OnlineLibBoot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;


import java.util.List;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Book> takenBooks;

    @Column(name = "firstname")
    @NotEmpty(message = "Необходимо заполнить имя")
    @Pattern(regexp = "([А-Я][а-я]*)|([A-Z]\\w*)", message = "Имя должно начинаться с большой буквы. Допустимы символы кириллицы или латиницы")
    private String name;

    @Column(name = "lastname")
    @Pattern(regexp = "([А-Я][а-я]*)|([A-Z]\\w*)", message = "Фамилия должна начинаться с большой буквы. Допустимы символы кириллицы или латиницы")
    @NotEmpty(message = "Необходимо заполнить фамилию")
    private String lastName;

    @Column(name = "patronymic")
    @Pattern(regexp = "([А-Я][а-я]*)|([A-Z]\\w*)", message = "Отчество должно начинаться с большой буквы. Допустимы символы кириллицы или латиницы")
    @NotEmpty(message = "Необходимо заполнить отчество")
    private String patronymic;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "birth_year")
    @Min(value = 1900, message = "Год рождения не может быть меньше 1900")
    @Max(value = 2023, message = "Год рождения не может быть больше текущего")
    private int birthYear;

    public Person() {

    }

    public Person(int id, String lastName, String name, String patronymic, int birthYear) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.patronymic = patronymic;
        this.birthYear = birthYear;
        this.fullName = lastName + " " + name + " " + patronymic;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


}
