package com.wtoll.demeter.util;

import java.util.TreeMap;

/**
 * @author <Wtoll> Will Toll on 2020-05-29
 * @project demeter
 */
public class TooltipHelper {
    public static String parseTooltipValue(String s) {
        String parsed = s;
        if (parsed.charAt(0) == '"') {
            parsed = parsed.substring(1);
        }
        if (parsed.charAt(parsed.length()-1) == '"') {
            parsed = parsed.substring(0, parsed.length()-1);
        }
        if (parsed.equals("null")) {
            parsed = "";
        }
        return parsed;
    }

    public static String capitalizeTooltip(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    public static String toRoman(int number) {
        if (number > 0) {
            int value = map.floorKey(number);
            if (number == value) {
                return map.get(number);
            }
            return map.get(value) + toRoman(number - value);
        } else {
            return "0";
        }
    }
}
