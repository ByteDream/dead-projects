package org.blueshard.sekaijuclt.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Data {

    private String dataAsString;
    private Map<String, String> data = new HashMap<>();

    public Data(String data) {
        this.dataAsString = data;

        String key = "";
        String dataString = dataAsString;

        String separator = getSeparator();
        try {
            if (separator.length() == 1) {
                dataString = dataString.substring(dataString.indexOf(",")).strip();
            } else {
                dataString = dataString.substring(dataString.indexOf(",") + 1).strip();
            }
        } catch (StringIndexOutOfBoundsException e) {
            dataString = "";
        }
        for (String string: dataString.split(separator)) {
            string = string.strip();
            if (string.equals(":") || string.equals(",") || string.equals("{") || string.equals("}")) {
                continue;
            } else if (key.isEmpty()) {
                key = string;
            } else {
                this.data.put(key, string);
                key = "";
            }
        }
    }

    public int getCode() {
        int lenBeforeCode = 1 + getSeparator().length() + 1;
        return Integer.parseInt(dataAsString.substring(lenBeforeCode, lenBeforeCode + DataCodes.DATACODESLENGHT));
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getDataAsString() {
        return dataAsString;
    }

    public String getFromData(String key) {
        return data.get(key);
    }

    private String getSeparator() {
        return dataAsString.substring(1).split(":")[0];
    }

    public static class Builder {

        private Map<String, String> data = new HashMap<>();
        private int code;

        public Builder(int code) {
            this.code = code;
        }

        public String createData() {
            String separator = createSeparator();

            StringBuilder dataAsString = new StringBuilder("{" + separator + ":" + code);
            data.forEach((key, value) -> dataAsString.append(",")
                    .append(separator)
                    .append(key)
                    .append(separator)
                    .append(":")
                    .append(separator)
                    .append(value)
                    .append(separator));
            dataAsString.append("}");

            return dataAsString.toString();
        }

        public String createSeparator() {
            char choice;

            StringBuilder stringBuilder = new StringBuilder();
            String indicator = "'";
            char[] choices = {'\'', '"', '^'};

            data.forEach((key, value) -> stringBuilder.append(key).append(value));

            String string = stringBuilder.toString();

            while (true) {
                if (string.contains(indicator)) {
                    switch (indicator) {
                        case "'" -> indicator = "\"";
                        case "\"" -> indicator = "^";
                        default -> {
                            choice = choices[new Random().nextInt(choices.length) - 1];
                            if (indicator.contains("|")) {
                                String[] splitted_indicator = indicator.split("\\|");
                                indicator = splitted_indicator[0] + choice + '|' + choice + splitted_indicator[1];
                            } else {
                                indicator = indicator + choice + '|' + choice + indicator;
                            }
                        }
                    }
                } else {
                    return indicator;
                }
            }
        }

        public void addData(String key, String value) {
            this.data.put(key, value);
        }

        public void addAllData(Map<String, String> allData) {
            this.data.putAll(allData);
        }

        public int getCode() {
            return code;
        }

        public Data getData() {
            return new Data(getDataAsString());
        }

        public Map<String, String> getDataMap() {
            return data;
        }

        public String getDataAsString() {
            return createData();
        }
    }

    public static boolean isDataString(String string) {
        try {
            string = string.strip();
            if (string.startsWith("{") && string.endsWith("}") && string.contains(":")) {
                String separator = string.substring(1).split(":")[0];
                int lenBeforeCode = 1 + separator.length() + 1;
                Integer.parseInt(string.substring(lenBeforeCode, lenBeforeCode + DataCodes.DATACODESLENGHT));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
