package com.generated.app;

public class WifiNetworkItem {
    private String ssid;
    private String bssid;
    private String capabilities;
    private int level;
    private int frequency;
    private boolean isOpen;
    private boolean isFavorite;
    private boolean isConnected;

    public WifiNetworkItem(String ssid, String bssid, String capabilities, int level, int frequency, boolean isOpen, boolean isFavorite, boolean isConnected) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.level = level;
        this.frequency = frequency;
        this.isOpen = isOpen;
        this.isFavorite = isFavorite;
        this.isConnected = isConnected;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public int getLevel() {
        return level;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
