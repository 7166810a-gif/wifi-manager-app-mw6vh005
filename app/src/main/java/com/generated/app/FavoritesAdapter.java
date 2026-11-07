package com.generated.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FavoritesAdapter extends BaseAdapter {
    private Context context;
    private List<String> favorites;
    private OnRemoveClickListener removeClickListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(String ssid, int position);
    }

    public FavoritesAdapter(Context context, List<String> favorites, OnRemoveClickListener listener) {
        this.context = context;
        this.favorites = favorites;
        this.removeClickListener = listener;
    }

    @Override
    public int getCount() {
        return favorites.size();
    }

    @Override
    public Object getItem(int position) {
        return favorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_favorite_network, parent, false);
            holder = new ViewHolder();
            holder.tvSsid = convertView.findViewById(R.id.tv_fav_item_ssid);
            holder.tvStatus = convertView.findViewById(R.id.tv_fav_item_status);
            holder.ivRemove = convertView.findViewById(R.id.iv_remove_favorite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String ssid = favorites.get(position);
        holder.tvSsid.setText(ssid);
        holder.tvStatus.setText(context.getString(R.string.favorite_saved));

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeClickListener != null) {
                    removeClickListener.onRemoveClick(ssid, position);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvSsid;
        TextView tvStatus;
        ImageView ivRemove;
    }
}
