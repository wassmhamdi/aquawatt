package com.siga.camndaapp.repository;

import com.siga.camndaapp.domain.Role;
import com.siga.camndaapp.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author MHAMDI Wassim 03/03/2025
 * SIGA'S Product
 */
@Repository
@EnableJpaRepositories
public interface RoleRepository extends JpaRepository<Role, String> {
}
