package com.whatup.what_up_clone.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.whatup.what_up_clone.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByEmail(String username);

    Optional<Customer> findByPhone(String username);
}
