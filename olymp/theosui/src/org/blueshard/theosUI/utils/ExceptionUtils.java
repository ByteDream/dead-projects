package org.blueshard.theosUI.utils;

import java.util.Arrays;

public class ExceptionUtils {

    public static String extractExceptionMessage(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.asList(throwable.getStackTrace()).forEach(stackTraceElement -> stringBuilder.append(stackTraceElement).append("\n"));

        return stringBuilder.toString();
    }

}
