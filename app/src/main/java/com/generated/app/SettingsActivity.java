package com.generated.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private Switch switchAutoTurnOn;
    private Switch switchConfirmOpen;
    private Switch switchAutoConnectFav;
    private Switch switchAutoRescan;
    private Button btnClearFavorites;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchAutoTurnOn = findViewById(R.id.switch_auto_turn_on);
        switchConfirmOpen = findViewById(R.id.switch_confirm_open);
        switchAutoConnectFav = findViewById(R.id.switch_auto_connect_fav);
        switchAutoRescan = findViewById(R.id.switch_auto_rescan);
        btnClearFavorites = findViewById(R.id.btn_clear_favorites);
        btnBack = findViewById(R.id.btn_settings_back);

        switchAutoTurnOn.setChecked(PreferencesManager.isAutoTurnOnWifi(this));
        switchConfirmOpen.setChecked(PreferencesManager.isConfirmOpenNetworks(this));
        switchAutoConnectFav.setChecked(PreferencesManager.isAutoConnectFavorites(this));
        switchAutoRescan.setChecked(PreferencesManager.isAutoRescan(this));

        switchAutoTurnOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setAutoTurnOnWifi(SettingsActivity.this, isChecked);
            }
        });

        switchConfirmOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setConfirmOpenNetworks(SettingsActivity.this, isChecked);
            }
        });

        switchAutoConnectFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setAutoConnectFavorites(SettingsActivity.this, isChecked);
            }
        });

        switchAutoRescan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setAutoRescan(SettingsActivity.this, isChecked);
            }
        });

        btnClearFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.clearFavorites(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, R.string.favorites_cleared, Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
