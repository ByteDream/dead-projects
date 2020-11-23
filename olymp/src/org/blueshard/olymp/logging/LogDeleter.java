package org.blueshard.olymp.logging;

import org.blueshard.olymp.user.User;

public class LogDeleter {

    private final User user;
    private final String logLifeTime;

    public LogDeleter(User user, String logLifeTime) {
        this.user = user;
        this.logLifeTime = logLifeTime;
    }

}
