package voynap.InterviewTask.dto;

import voynap.InterviewTask.models.Customer;

public class CustomerDTO {
    private Integer id;
    private String username;
    private String email;

    private String password;

    private String role;

    private Double balance;


    public CustomerDTO() {

    }
    public CustomerDTO(Customer customer) {
        this.username = customer.getUsername();
        this.email = customer.getEmail();
        this.password = customer.getPassword();
        this.role = customer.getRole();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }


}
