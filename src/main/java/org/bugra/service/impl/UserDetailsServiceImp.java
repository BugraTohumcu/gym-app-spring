package org.bugra.service.impl;

import lombok.RequiredArgsConstructor;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            User user = userRepo.findByUsername(username);
            return new UserPrincipal(user);
        }catch(UserNotFoundException ex){
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }
}
