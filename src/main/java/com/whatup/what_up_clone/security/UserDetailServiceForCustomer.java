package com.whatup.what_up_clone.security;



import com.whatup.what_up_clone.entity.Customer;
import com.whatup.what_up_clone.entity.Role;
import com.whatup.what_up_clone.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@Service
public class UserDetailServiceForCustomer implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.matches(".*@.*")){
            return customerRepository.findByEmail(username)

                    .map(customer ->
                            User.withUsername(username)
                                    .authorities(Role.USER.name())
                                    .password(customer.getPassword())
                                    .accountExpired(isExpired(customer))
                                    .accountLocked(customer.isLocked())
                                    .credentialsExpired(isCredentialExpired(customer))
                                    .build())
                    .orElseThrow(()-> new UsernameNotFoundException(username));
            }else{
            return customerRepository.findByPhone(username)

                    .map(customer ->
                            User.withUsername(username)
                                    .authorities(Role.USER.name())
                                    .password(customer.getPassword())
                                    .accountExpired(isExpired(customer))
                                    .accountLocked(customer.isLocked())
                                    .credentialsExpired(isCredentialExpired(customer))
                                    .build())
                    .orElseThrow(()-> new UsernameNotFoundException(username));
        }
    }
    private boolean isCredentialExpired(Customer customer){
        if (null !=customer.getValidPassDate()){
            LocalDateTime validPassDate = customer.getValidPassDate();
            if (validPassDate.isBefore(LocalDateTime.now())){
                return true;
            }
        }
        return false;
    }

    private boolean  isExpired(Customer customer){
        if (null != customer.getRetiredDate()){
            LocalDateTime retiredDate = customer.getRetiredDate();
            if (retiredDate.isBefore(LocalDateTime.now())){
                return true;
            }
        }
        return false;
    }
}
