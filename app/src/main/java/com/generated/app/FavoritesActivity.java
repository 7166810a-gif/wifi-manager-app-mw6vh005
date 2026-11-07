package com.generated.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends Activity {

    private ListView lvFavorites;
    private TextView tvEmpty;
    private Button btnBack;
    private FavoritesAdapter adapter;
    private List<String> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        lvFavorites = findViewById(R.id.lv_favorites);
        tvEmpty = findViewById(R.id.tv_fav_empty);
        btnBack = findViewById(R.id.btn_fav_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadFavorites();
    }

    private void loadFavorites() {
        Set<String> set = PreferencesManager.getFavorites(this);
        favoritesList = new ArrayList<>(set);

        if (favoritesList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvFavorites.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvFavorites.setVisibility(View.VISIBLE);
            adapter = new FavoritesAdapter(this, favoritesList, new FavoritesAdapter.OnRemoveClickListener() {
                @Override
                public void onRemoveClick(String ssid, int position) {
                    PreferencesManager.removeFavorite(FavoritesActivity.this, ssid);
                    favoritesList.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(FavoritesActivity.this, R.string.favorite_removed, Toast.LENGTH_SHORT).show();

                    if (favoritesList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        lvFavorites.setVisibility(View.GONE);
                    }
                }
            });
            lvFavorites.setAdapter(adapter);
        }
    }
}
