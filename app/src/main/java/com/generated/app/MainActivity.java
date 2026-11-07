package com.generated.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 2001;

    private WifiManager wifiManager;
    private Switch switchWifiToggle;
    private TextView tvSensorStatus;
    private TextView tvConnectedSsid;
    private TextView tvConnectedDetails;
    private TextView tvBadgeStatus;
    private Button btnScanNow;
    private Button btnFavorites;
    private Button btnSettings;
    private ProgressBar progressScanning;
    private TextView tvEmptyState;
    private ListView lvWifiNetworks;

    private List<WifiNetworkItem> wifiList = new ArrayList<>();
    private WifiAdapter wifiAdapter;

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                progressScanning.setVisibility(View.GONE);
                updateWifiList();
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                updateWifiStateUI();
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                updateCurrentConnectionUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        initViews();
        setupListeners();

        wifiAdapter = new WifiAdapter(this, wifiList, new WifiAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(WifiNetworkItem item, int position) {
                toggleFavorite(item);
            }
        });
        lvWifiNetworks.setAdapter(wifiAdapter);

        if (PreferencesManager.isAutoTurnOnWifi(this)) {
            if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                setWifiEnabledIndependent(true);
            }
        }
    }

    private void initViews() {
        switchWifiToggle = findViewById(R.id.switch_wifi_toggle);
        tvSensorStatus = findViewById(R.id.tv_sensor_status);
        tvConnectedSsid = findViewById(R.id.tv_connected_ssid);
        tvConnectedDetails = findViewById(R.id.tv_connected_details);
        tvBadgeStatus = findViewById(R.id.tv_badge_status);
        btnScanNow = findViewById(R.id.btn_scan_now);
        btnFavorites = findViewById(R.id.btn_favorites);
        btnSettings = findViewById(R.id.btn_settings);
        progressScanning = findViewById(R.id.progress_scanning);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        lvWifiNetworks = findViewById(R.id.lv_wifi_networks);
    }

    private void setupListeners() {
        switchWifiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (wifiManager != null) {
                    setWifiEnabledIndependent(isChecked);
                }
            }
        });

        btnScanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndScan();
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        lvWifiNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiNetworkItem item = wifiList.get(position);
                handleNetworkClick(item);
            }
        });
    }

    private void setWifiEnabledIndependent(boolean enable) {
        if (wifiManager != null) {
            boolean success = wifiManager.setWifiEnabled(enable);
            if (success) {
                String msg = enable ? getString(R.string.wifi_enabled_msg) : getString(R.string.wifi_disabled_msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "שינוי מצב Wi-Fi ע\"י האפליקציה", Toast.LENGTH_SHORT).show();
            }
            updateWifiStateUI();
        }
    }

    private void checkPermissionAndScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        startScan();
    }

    private void startScan() {
        if (wifiManager == null) return;
        if (!wifiManager.isWifiEnabled()) {
            setWifiEnabledIndependent(true);
        }
        progressScanning.setVisibility(View.VISIBLE);
        boolean started = wifiManager.startScan();
        if (!started) {
            progressScanning.setVisibility(View.GONE);
            updateWifiList();
        }
    }

    private void updateWifiStateUI() {
        boolean isEnabled = wifiManager != null && wifiManager.isWifiEnabled();
        switchWifiToggle.setChecked(isEnabled);
        tvSensorStatus.setText(isEnabled ? getString(R.string.wifi_on) : getString(R.string.wifi_off));
        tvBadgeStatus.setText(isEnabled ? getString(R.string.wifi_on) : getString(R.string.wifi_off));
        tvBadgeStatus.setBackgroundResource(isEnabled ? R.drawable.bg_status_badge : R.drawable.bg_button_secondary);

        if (isEnabled) {
            updateCurrentConnectionUI();
        } else {
            tvConnectedSsid.setText(getString(R.string.not_connected));
            tvConnectedDetails.setText("Wi-Fi כבוי");
            wifiList.clear();
            wifiAdapter.notifyDataSetChanged();
            tvEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void updateCurrentConnectionUI() {
        if (wifiManager == null || !wifiManager.isWifiEnabled()) return;
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null && info.getNetworkId() != -1) {
            String ssid = WifiHelper.sanitizeSsid(info.getSSID());
            tvConnectedSsid.setText(ssid);
            String ip = WifiHelper.formatIpAddress(info.getIpAddress());
            int speed = info.getLinkSpeed();
            tvConnectedDetails.setText("IP: " + ip + " | " + speed + " Mbps | " + info.getRssi() + " dBm");
        } else {
            tvConnectedSsid.setText(getString(R.string.not_connected));
            tvConnectedDetails.setText("IP: -- | Signal: --");
        }
    }

    private void updateWifiList() {
        if (wifiManager == null || !wifiManager.isWifiEnabled()) return;

        List<ScanResult> results = wifiManager.getScanResults();
        wifiList.clear();

        WifiInfo currentInfo = wifiManager.getConnectionInfo();
        String currentSsid = currentInfo != null ? WifiHelper.sanitizeSsid(currentInfo.getSSID()) : "";

        if (results != null) {
            for (ScanResult res : results) {
                if (res.SSID == null || res.SSID.trim().isEmpty()) continue;

                boolean isOpen = WifiHelper.isOpenNetwork(res.capabilities);
                boolean isFav = PreferencesManager.isFavorite(this, res.SSID);
                boolean isConnected = currentSsid.equals(res.SSID);

                wifiList.add(new WifiNetworkItem(
                        res.SSID,
                        res.BSSID,
                        res.capabilities,
                        res.level,
                        res.frequency,
                        isOpen,
                        isFav,
                        isConnected
                ));
            }
        }

        Collections.sort(wifiList, new Comparator<WifiNetworkItem>() {
            @Override
            public int compare(WifiNetworkItem o1, WifiNetworkItem o2) {
                return Integer.compare(o2.getLevel(), o1.getLevel());
            }
        });

        wifiAdapter.notifyDataSetChanged();
        tvEmptyState.setVisibility(wifiList.isEmpty() ? View.VISIBLE : View.GONE);

        autoConnectFavoritesCheck();
    }

    private void autoConnectFavoritesCheck() {
        if (!PreferencesManager.isAutoConnectFavorites(this)) return;
        WifiInfo currentInfo = wifiManager.getConnectionInfo();
        if (currentInfo != null && currentInfo.getNetworkId() != -1) return;

        for (WifiNetworkItem item : wifiList) {
            if (item.isFavorite() && item.isOpen()) {
                WifiHelper.connectToNetwork(wifiManager, item.getSsid(), "", true);
                break;
            }
        }
    }

    private void handleNetworkClick(final WifiNetworkItem item) {
        if (item.isOpen()) {
            if (PreferencesManager.isConfirmOpenNetworks(this)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_open_connect_title)
                        .setMessage(String.format(getString(R.string.confirm_open_connect_msg), item.getSsid()))
                        .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                performConnect(item.getSsid(), "", true);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            } else {
                performConnect(item.getSsid(), "", true);
            }
        } else {
            showPasswordDialog(item);
        }
    }

    private void showPasswordDialog(final WifiNetworkItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_connect_password, null);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        final EditText etPassword = view.findViewById(R.id.et_wifi_password);

        tvTitle.setText(String.format(getString(R.string.password_prompt_title), item.getSsid()));

        builder.setView(view)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = etPassword.getText().toString().trim();
                        performConnect(item.getSsid(), password, false);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void performConnect(String ssid, String password, boolean isOpen) {
        Toast.makeText(this, getString(R.string.connecting), Toast.LENGTH_SHORT).show();
        boolean result = WifiHelper.connectToNetwork(wifiManager, ssid, password, isOpen);
        if (!result) {
            Toast.makeText(this, getString(R.string.connect_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavorite(WifiNetworkItem item) {
        if (item.isFavorite()) {
            PreferencesManager.removeFavorite(this, item.getSsid());
            item.setFavorite(false);
            Toast.makeText(this, getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
        } else {
            PreferencesManager.addFavorite(this, item.getSsid());
            item.setFavorite(true);
            Toast.makeText(this, getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
        }
        wifiAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);

        updateWifiStateUI();
        checkPermissionAndScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(wifiReceiver);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }
}
