package com.generated.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "wifi_app_prefs";
    private static final String KEY_AUTO_TURN_ON = "auto_turn_on_wifi";
    private static final String KEY_CONFIRM_OPEN = "confirm_open_networks";
    private static final String KEY_AUTO_CONNECT_FAV = "auto_connect_favorites";
    private static final String KEY_FAVORITES = "favorite_ssids";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isAutoTurnOnWifi(Context context) {
        return getPrefs(context).getBoolean(KEY_AUTO_TURN_ON, true);
    }

    public static void setAutoTurnOnWifi(Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_TURN_ON, value).apply();
    }

    public static boolean isConfirmOpenNetworks(Context context) {
        return getPrefs(context).getBoolean(KEY_CONFIRM_OPEN, true);
    }

    public static void setConfirmOpenNetworks(Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_CONFIRM_OPEN, value).apply();
    }

    public static boolean isAutoConnectFavorites(Context context) {
        return getPrefs(context).getBoolean(KEY_AUTO_CONNECT_FAV, true);
    }

    public static void setAutoConnectFavorites(Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_CONNECT_FAV, value).apply();
    }

    public static Set<String> getFavorites(Context context) {
        return new HashSet<>(getPrefs(context).getStringSet(KEY_FAVORITES, new HashSet<String>()));
    }

    public static boolean isFavorite(Context context, String ssid) {
        if (ssid == null) return false;
        return getFavorites(context).contains(ssid);
    }

    public static void addFavorite(Context context, String ssid) {
        if (ssid == null || ssid.isEmpty()) return;
        Set<String> set = getFavorites(context);
        set.add(ssid);
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, set).apply();
    }

    public static void removeFavorite(Context context, String ssid) {
        if (ssid == null) return;
        Set<String> set = getFavorites(context);
        set.remove(ssid);
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, set).apply();
    }

    public static void clearFavorites(Context context) {
        getPrefs(context).edit().remove(KEY_FAVORITES).apply();
    }
}
