package com.siga.camndaapp.repository;

import com.siga.camndaapp.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author MHAMDI Wassim 24/02/2025
 * SIGA'S Product
 */
@Repository
@EnableJpaRepositories
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByEmail(String email);
    boolean existsByLogin(String login);
    Optional<Utilisateur> findOneByEmailIgnoreCase(String email);
    Optional<Utilisateur> findOneByLogin(String login); // Added for consistency
    Optional<Utilisateur> findByActivationKey(String activationKey);
    Optional<Utilisateur> findOneByResetKey(String resetKey);
}
