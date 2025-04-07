package com.siga.camndaapp.service;

import com.siga.camndaapp.domain.Role;
import com.siga.camndaapp.domain.Utilisateur;
import com.siga.camndaapp.dto.request.RegisterRequest;
import com.siga.camndaapp.dto.request.ResetPassWord;
import com.siga.camndaapp.exception.EmailAlreadyUsedException;
import com.siga.camndaapp.exception.UsernameAlreadyUsedException;
import com.siga.camndaapp.repository.RoleRepository;
import com.siga.camndaapp.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author MHAMDI Wassim 28/02/2025
 * SIGA'S Product
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;


    public Utilisateur register(RegisterRequest registerRequest) {

            // Check for existing email
            userRepository
                    .findOneByEmailIgnoreCase(registerRequest.getEmail())
                    .ifPresent(existingUser -> {
                        boolean removed = removeNonActivatedUser(existingUser);
                        if (!removed) {
                            throw new EmailAlreadyUsedException();
                        }
                    });

            // Check for existing login
            String login = registerRequest.getLogin() != null ? registerRequest.getLogin().toLowerCase() : null;
            if (login == null || login.isEmpty()) {
                throw new IllegalArgumentException("Login cannot be null or empty");
            }
            userRepository.findOneByLogin(login)
                    .ifPresent(existingUser -> {
                        boolean removed = removeNonActivatedUser(existingUser);
                        if (!removed) {
                            throw new UsernameAlreadyUsedException();
                        }
                    });

            String userActivationKey = UUID.randomUUID().toString();
            Utilisateur user = new Utilisateur();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());

            if (registerRequest.getEmail() != null) {
                user.setEmail(registerRequest.getEmail().toLowerCase());
            }
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setLogin(registerRequest.getLogin());
            //user.setAuthorities(new HashSet<>(registerRequest.getRoles()));
            user.setAuthorities(fetchRoles(registerRequest.getRoles()));
            user.setActivated(false);
            user.setLangKey("fr");
            user.setActivationKey(userActivationKey);
            user.setActivationKeyExpires(Instant.now().plus(1, ChronoUnit.DAYS));

            userRepository.save(user); // This might throw DataIntegrityViolationException

            return user;
    }
    private Set<Role> fetchRoles(Set<Role> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return new HashSet<>(); // Return empty set if no roles provided
        }

        // Fetch roles from repository based on role names
        return requestedRoles.stream()
                .map(Role::getName) // Extract role names
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
    private boolean removeNonActivatedUser(Utilisateur existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }
    public void activateAccount(String activationKey) {
        Utilisateur user = userRepository.findByActivationKey(activationKey)
                .orElseThrow(() -> new RuntimeException("Invalid activation key"));

        if (Instant.now().isAfter(user.getActivationKeyExpires())) {
            userRepository.delete(user);
            throw new RuntimeException("Activation key has expired");
        }

        user.setActivated(true);
        user.setActivationKey(null);
        user.setActivationKeyExpires(null);
        userRepository.save(user);

       /* var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefresh(new HashMap<>(), user);

        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .refreshToken(refreshToken)
                .build();*/
    }

    @Transactional
    public Optional<Utilisateur> requestPasswordReset(String mail) {

        /*Optional<Utilisateur> user = userRepository.findOneByEmailIgnoreCase(mail);

        if (!user.isPresent()) {
            return Optional.empty();
        }

        if (!user.get().isActivated()) {
            return Optional.empty();
        }

        // Update user with reset details
        Utilisateur utilisateur = user.get(); // Get the user entity
        String userResetKey = UUID.randomUUID().toString();
        utilisateur.setResetKey(userResetKey);
        utilisateur.setResetDate(Instant.now());
        utilisateur.setResetKeyExpires(Instant.now().plus(1, ChronoUnit.DAYS));

        // Save the updated user to the database
        userRepository.save(utilisateur);

        // Return the updated user wrapped in Optional
        return Optional.of(utilisateur);*/

        return userRepository.findOneByEmailIgnoreCase(mail)
                .filter(Utilisateur::isActivated) // Check if activated
                .map(user -> {
                    String userResetKey = UUID.randomUUID().toString();
                    user.setResetKey(userResetKey);
                    user.setResetKeyExpires(Instant.now().plus(1, ChronoUnit.DAYS));
                    return userRepository.save(user); // Save and return updated user
                });
    }



    public Optional<Utilisateur> completePasswordReset(ResetPassWord resetPassWord) {
        log.debug("Reset user password for reset key {}", resetPassWord.getKey());

        // Validate passwords
        String newPassword = resetPassWord.getNewPassword();
        String confirmPassword = resetPassWord.getConfirmPassword();

        if (!isPasswordConfirmed(newPassword, confirmPassword)) {
            log.debug("Password confirmation failed for reset key {}", resetPassWord.getKey());
            return Optional.empty();
        }

        if (isPasswordLengthInvalid(newPassword) || isPasswordLengthInvalid(confirmPassword)) {
            log.debug("Password length validation failed for reset key {}", resetPassWord.getKey());
            return Optional.empty();
        }

        // Process reset if validations pass
        return userRepository
                .findOneByResetKey(resetPassWord.getKey())
                .filter(Utilisateur::isActivated)
                .filter(user -> Instant.now().isBefore(user.getResetKeyExpires())) // Check if not expired
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetKeyExpires(null);
                    user.setResetDate(Instant.now()); // Clear reset date after completion
                    userRepository.save(user); // Persist changes
                    return user;
                });
    }

    public boolean isPasswordConfirmed(String newPassword, String confirmPassword) {
        if (newPassword == null || confirmPassword == null) {
            return false; // Handle null case
        }
        return newPassword.equals(confirmPassword);
    }
    public static boolean isPasswordLengthInvalid(String password) {
        return (
                StringUtils.isEmpty(password) ||
                        password.length() < PASSWORD_MIN_LENGTH ||
                        password.length() > PASSWORD_MAX_LENGTH
        );
    }



}
