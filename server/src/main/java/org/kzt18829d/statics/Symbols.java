package org.kzt18829d.statics;

import java.util.Locale;

public class Symbols {
    public static final String INTEGERS = "0123456789";
    public static final String LOWERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERS = LOWERS.toUpperCase(Locale.ROOT);
    public static final char[] SYMBOLS_ALL = (UPPERS + LOWERS + INTEGERS).toCharArray();
    public static final char[] SYMBOLS_LET = (UPPERS + LOWERS).toCharArray();
    public static final char[] INTEGERS_ARRAY = INTEGERS.toCharArray();
}
