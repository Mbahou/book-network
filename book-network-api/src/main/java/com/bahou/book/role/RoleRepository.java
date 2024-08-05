package com.bahou.book.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    public Optional<Role> findByName(String role);
}
