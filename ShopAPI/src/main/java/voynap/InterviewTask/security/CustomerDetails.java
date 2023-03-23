package voynap.InterviewTask.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import voynap.InterviewTask.models.Customer;

import java.util.Collection;
import java.util.Collections;

public class CustomerDetails implements UserDetails {

    private final Customer customer;

    public CustomerDetails(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // ROLE_ADMIN
        // ROLE_USER
        // ROLE_OWNER
        // ROLE_FROZEN
        // ROLE_BANNED


        return Collections.singletonList(new SimpleGrantedAuthority(customer.getRole()));
    }

    @Override
    public String getPassword() {
        return this.customer.getPassword();
    }

    @Override
    public String getUsername() {
        return this.customer.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public Customer getCustomer() {
        return this.customer;
    }
}
