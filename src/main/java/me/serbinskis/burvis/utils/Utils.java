package me.serbinskis.burvis.utils;

import me.serbinskis.burvis.Main;

public class Utils {
    public static boolean isBetween(float value, float min, float max) {
        return value >= min && value <= max;
    }

    public static void log(Object obj) { System.out.println(obj); }
    public static void debug(Object obj) { if (Main.DEBUG) { System.out.println(obj); } }
}
