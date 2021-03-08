package org.blueshard.theosUI.utils;

import java.util.Map;

public class StringUtils {

    public static String format(String string, Map<String, String> formatMap){

        for(Map.Entry<String, String> entry: formatMap.entrySet()) {
            string = string.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return string;

    }

}
