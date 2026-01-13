package com.uniflow.repository;

import com.uniflow.model.User;
import com.uniflow.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(RoleEnum role);
    boolean existsByEmail(String email);
}
