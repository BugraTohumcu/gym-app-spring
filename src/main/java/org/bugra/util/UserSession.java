package org.bugra.util;

import lombok.Getter;
import org.bugra.model.User;

public class UserSession {
    @Getter
    private static User currentUser;


    public static void setCurrentUser(User currentUser) {
        UserSession.currentUser = currentUser;
    }

}
