package org.blueshard.olymp.utils;

import org.blueshard.olymp.exception.ErrorCodes;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ExceptionUtils {

    public static String errorNumberToString(int errorNumber) {
        String name;
        int errno;
        ErrorCodes errorCodes = new ErrorCodes();
        Field[] fields = ErrorCodes.class.getFields();

        for (Field field : fields) {
            try {
                name = field.getName();
                errno = (int) field.get(errorCodes);
                if (errno == errorNumber) {
                    return name;
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        return null;
    }

    public static String extractExceptionMessage(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.asList(throwable.getStackTrace()).forEach(stackTraceElement -> stringBuilder.append(stackTraceElement).append("\n"));

        return stringBuilder.toString();
    }

}
