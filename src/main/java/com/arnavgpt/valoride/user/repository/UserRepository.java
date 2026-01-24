package com.arnavgpt.valoride.user.repository;

import com.arnavgpt.valoride.user.entity.Role;
import com.arnavgpt.valoride.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndActiveTrue(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    Page<User> findByActiveTrue(Pageable pageable);
}