package com.cpsc.efiling.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringUtil {
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public static String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value.trim();
    }

    public static String normalizeYN(String value, String defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        }
        String v = value.trim();
        String upper = v.toUpperCase(Locale.ROOT);
        if ("Y".equals(upper) || "YES".equals(upper) || "TRUE".equals(upper) || "是".equals(v) || v.contains("更新") || v.contains("新建")) {
            return "Y";
        }
        if ("N".equals(upper) || "NO".equals(upper) || "FALSE".equals(upper) || "否".equals(v) || v.contains("新增") || v.contains("已存在")) {
            return "N";
        }
        return v;
    }

    public static boolean toBoolean(String value) {
        if (isBlank(value)) {
            return false;
        }
        String v = value.trim().toLowerCase(Locale.ROOT);
        return "true".equals(v) || "yes".equals(v) || "y".equals(v) || "1".equals(v) || "是".equals(value.trim());
    }

    public static List<String> splitList(String value) {
        List<String> list = new ArrayList<String>();
        if (isBlank(value)) {
            return list;
        }
        String[] parts = value.split("[,，\\n;；]+");
        for (String part : parts) {
            String item = part == null ? "" : part.trim();
            if (!item.isEmpty()) {
                list.add(item);
            }
        }
        return list;
    }
}
