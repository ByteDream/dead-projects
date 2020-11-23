package org.blueshard.olymp.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class EmptyLogger {

    public Logger getLogger() {
        Logger logger = Logger.getLogger(EmptyLogger.class.getName());
        logger.setLevel(Level.OFF);
        return logger;
    }

}
