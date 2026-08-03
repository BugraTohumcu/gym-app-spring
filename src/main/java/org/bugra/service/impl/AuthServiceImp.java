package org.bugra.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.InvalidPasswordException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.security.UserPrincipal;
import org.bugra.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final static Logger logger = LoggerFactory.getLogger(AuthServiceImp.class);
    private final UserRepo userRepo;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponse login(LoginUser loginUser) {
        logger.info("User with username: {} is trying to login" , loginUser.username());

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.username(),
                        loginUser.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        UserPrincipal user = (UserPrincipal)  auth.getPrincipal();

        logger.info("User with username: {} is successfully logged in" , user.getUsername());
        return new UserResponse(
                user.getUsername(),
                user.getPassword()
        );
    }

    @Transactional
    @Override
    public boolean changePassword(ChangePassword changePassword) {
        logger.info("The user with username: {} is changing password",
                changePassword.username()
                );

        User currentUser = userRepo.findByUsername(changePassword.username());
        // Password check
        if(!passwordEncoder.matches(changePassword.currentPassword(), currentUser.getPassword())){
            logger.warn("The provided password for user with username: {} is invalid",
                    currentUser.getUsername());
            throw new InvalidPasswordException();
        }

        // set new password and update db
        currentUser.setPassword(passwordEncoder.encode(changePassword.newPassword()));
        userRepo.update(currentUser);

        logger.info("User with username: {} successfully updated password" , currentUser.getUsername());
        return true;
    }

    @Override
    public boolean isAuthenticated(String username, String password) {
        User user = userRepo.findByUsername(username);
        return user.getPassword().equals(password);
    }
}
