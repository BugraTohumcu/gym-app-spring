package org.bugra.service.impl;

import lombok.RequiredArgsConstructor;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.security.UserPrincipal;
import org.bugra.service.LoginAttemptService;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepo userRepo;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            if(loginAttemptService.isBlocked(username)){
                throw new LockedException("Too many attempts! Please wait for five minutes");
            }
                User user = userRepo.findByUsername(username);
                return new UserPrincipal(user);
        }catch(UserNotFoundException ex){
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }
}
