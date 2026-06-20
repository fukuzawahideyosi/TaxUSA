package com.panda.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class KanaUtil {
    private static final Map<Character, Character> lower2upperMap = new HashMap<>();
    private static final Map<Character, Character> upper2lowerMap = new HashMap<>();

    private static final String LOWER_CHARS = "ぁぃぅぇぉゃゅょっァィゥェォャュョッｧｨｩｪｫｬｭｮｯ";
    private static final String UPPER_CHARS = "あいうえおやゆよつアイウエオヤユヨツｱｲｳｴｵﾔﾕﾖﾂ";
    static {
        char[] lowerChars = LOWER_CHARS.toCharArray();
        char[] upperChars = UPPER_CHARS.toCharArray();
        if (lowerChars.length != upperChars.length) {
            throw new RuntimeException("char count not match. lower=" + lowerChars.length + ", upper=" + upperChars.length);
        }
        for (int i = 0; i < lowerChars.length; i++) {
            lower2upperMap.put(lowerChars[i], upperChars[i]);
            upper2lowerMap.put(upperChars[i], lowerChars[i]);
        }
    }

    public static String toUpperCase(String s) {
        return conv(s, lower2upperMap);
    }

    public static String toLowerCase(String s) {
        return conv(s, upper2lowerMap);
    }

    private static String conv(String s, Map<Character, Character> convMap) {
    	if (StringUtils.isEmpty(s) == true) {
    		return s;
    	}
        StringBuilder sb = new StringBuilder();
        s.chars().mapToObj(c -> (char) c).forEach(c -> {
            Character conv = convMap.get(c);
            if (conv == null) {
                sb.append(c);
            } else {
                sb.append(conv);
            }
        });
        return sb.toString();
    }

    private static final Map<Character, Character> zenDaku2ZenNotDakuMap = new HashMap<>();

    private static final String ZEN_DAKU_CHARS = "ゔがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ";
    private static final String ZEN_NOT_DAKU_CHARS = "うかきくけこさしすせそたちつてとはひふへほはひふへほウカキクケコサシスセソタチツテトハヒフヘホハヒフヘホ";
    static {
        char[] zenDakuChars = ZEN_DAKU_CHARS.toCharArray();
        char[] zenNotDakuChars = ZEN_NOT_DAKU_CHARS.toCharArray();
        if (zenDakuChars.length != zenNotDakuChars.length) {
            throw new RuntimeException("char count not match. zenDaku=" + zenDakuChars.length + ", zenNotDaku=" + zenNotDakuChars.length);
        }
        for (int i = 0; i < zenDakuChars.length; i++) {
            zenDaku2ZenNotDakuMap.put(zenDakuChars[i], zenNotDakuChars[i]);
        }
    }

    public static String removeDakuten(String s) {
    	if (StringUtils.isEmpty(s) == true) {
    		return s;
    	}
        StringBuilder sb = new StringBuilder();
        s.chars().mapToObj(c -> (char) c).forEach(c -> {
            if (c == '゛' || c == '゜' || c == 'ﾞ' || c == 'ﾟ') {
                // スキップ(削除)
            } else {
                Character conv = zenDaku2ZenNotDakuMap.get(c);
                if (conv == null) {
                    sb.append(c);
                } else {
                    sb.append(conv);
                }
            }
        });
        return sb.toString();
    }
}
