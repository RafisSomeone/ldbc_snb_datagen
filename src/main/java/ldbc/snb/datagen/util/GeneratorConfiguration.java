package ldbc.snb.datagen.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GeneratorConfiguration implements Iterable<Map.Entry<String, String>>, Serializable {
    public final Map<String, String> map;

    public GeneratorConfiguration(Map<String, String> map) {
        this.map = map;
    }

    public String get(String key) {
        return map.get(key);
    }

    public String get(String key, String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String valueString = this.getTrimmed(key);
        if (null != valueString && !valueString.isEmpty()) {
            if ("true".equalsIgnoreCase(valueString)) {
                return true;
            } else {
                return "false".equalsIgnoreCase(valueString) ? false : defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public int getInt(String name, int defaultValue) {
        String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        } else {
            String hexString = this.getHexDigits(valueString);
            return hexString != null ? Integer.parseInt(hexString, 16) : Integer.parseInt(valueString);
        }
    }

    public double getDouble(String name, double defaultValue) {
        String valueString = this.getTrimmed(name);
        return valueString == null ? defaultValue : Double.parseDouble(valueString);
    }

    public float getFloat(String name, float defaultValue) {
        String valueString = this.getTrimmed(name);
        return valueString == null ? defaultValue : Float.parseFloat(valueString);
    }

    private String getHexDigits(String value) {
        boolean negative = false;
        String str = value;
        String hexString;
        if (value.startsWith("-")) {
            negative = true;
            str = value.substring(1);
        }

        if (!str.startsWith("0x") && !str.startsWith("0X")) {
            return null;
        } else {
            hexString = str.substring(2);
            if (negative) {
                hexString = "-" + hexString;
            }

            return hexString;
        }
    }

    public String getTrimmed(String name) {
        String value = this.get(name);
        return null == value ? null : value.trim();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return this.map.entrySet().iterator();
    }

    public String getOutputDir() {
        return map.get("generator.outputDir");
    }

    public void printConfig() {
        System.out.println("********* Configuration *********");
        map.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("*********************************");
    }
}
