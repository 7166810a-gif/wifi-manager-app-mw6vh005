package com.generated.app;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

public class WifiHelper {

    public static boolean isOpenNetwork(String capabilities) {
        if (capabilities == null) return true;
        return !capabilities.contains("WPA") && !capabilities.contains("WEP") && !capabilities.contains("EAP") && !capabilities.contains("WPA2") && !capabilities.contains("WPA3");
    }

    public static String getSecurityDescription(Context context, String capabilities) {
        if (isOpenNetwork(capabilities)) {
            return context.getString(R.string.security_open);
        } else if (capabilities.contains("WPA3")) {
            return "WPA3";
        } else if (capabilities.contains("WPA2")) {
            return "WPA2";
        } else if (capabilities.contains("WPA")) {
            return "WPA";
        } else if (capabilities.contains("WEP")) {
            return "WEP";
        }
        return context.getString(R.string.security_secured);
    }

    public static int calculateSignalPercentage(int rssi) {
        return WifiManager.calculateSignalLevel(rssi, 100);
    }

    public static int getSignalLevel(int rssi) {
        return WifiManager.calculateSignalLevel(rssi, 4);
    }

    public static String getFrequencyBand(int frequency) {
        if (frequency >= 5925 && frequency <= 7125) {
            return "6 GHz";
        } else if (frequency >= 4900 && frequency <= 5850) {
            return "5 GHz";
        } else if (frequency >= 2400 && frequency <= 2484) {
            return "2.4 GHz";
        }
        return frequency + " MHz";
    }

    public static String formatIpAddress(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 24) & 0xFF);
    }

    public static String sanitizeSsid(String ssid) {
        if (ssid == null) return "";
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    public static boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm != null && lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            return mode != Settings.Secure.LOCATION_MODE_OFF;
        }
    }

    public static void disconnect(WifiManager wifiManager) {
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            wifiManager.disconnect();
        }
    }

    public static boolean connectToNetwork(WifiManager wifiManager, String ssid, String password, boolean isOpen) {
        if (wifiManager == null) return false;
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);

        if (isOpen) {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            wifiConfig.preSharedKey = String.format("\"%s\"", password);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        }

        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId == -1) {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            if (list != null) {
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                        netId = i.networkId;
                        break;
                    }
                }
            }
        }

        if (netId != -1) {
            wifiManager.disconnect();
            boolean enabled = wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            return enabled;
        }
        return false;
    }
}
