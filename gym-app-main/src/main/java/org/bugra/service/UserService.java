package org.bugra.service;

import org.bugra.dto.request.UpdateUserStatus;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;

public interface UserService {

    /**
     * Updates active status of a user by given username
     * @param username a unique username
     * */
    void toggleActiveStatus(String username, UpdateUserStatus userStatus);


    /**
     * Retrieves a user with a provided username
     * <p>Calls {@link UserRepo#findByUsername(String)}</p>
     * @param username a unique, lowercased string to be used as the profile username
     * @return Retrieved {@link User} from database
     * */
    User findByUsername(String username);

    /**
     * Deletes a user with provided username
     * @param username a unique, lowercased string to be used as the profile username
     * @throws org.bugra.exception.UserNotFoundException if there isn't any record deleted
     * @return true if deleted, false otherwise
     * */
    boolean deleteByUsername(String username);
}
