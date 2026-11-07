package com.generated.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WifiAdapter extends BaseAdapter {
    private Context context;
    private List<WifiNetworkItem> items;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(WifiNetworkItem item, int position);
    }

    public WifiAdapter(Context context, List<WifiNetworkItem> items, OnFavoriteClickListener listener) {
        this.context = context;
        this.items = items;
        this.favoriteClickListener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_wifi_network, parent, false);
            holder = new ViewHolder();
            holder.tvSsid = convertView.findViewById(R.id.tv_item_ssid);
            holder.tvDetails = convertView.findViewById(R.id.tv_item_details);
            holder.tvSignalBadge = convertView.findViewById(R.id.tv_signal_badge);
            holder.ivLock = convertView.findViewById(R.id.iv_lock_status);
            holder.ivFav = convertView.findViewById(R.id.iv_favorite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WifiNetworkItem item = items.get(position);
        holder.tvSsid.setText(item.getSsid());

        String securityStr = WifiHelper.getSecurityDescription(context, item.getCapabilities());
        String freqStr = WifiHelper.getFrequencyBand(item.getFrequency());
        int signalPct = WifiHelper.calculateSignalPercentage(item.getLevel());

        String details = securityStr + " | " + freqStr + " (" + item.getLevel() + " dBm)";
        if (item.isConnected()) {
            details = "★ " + context.getString(R.string.current_network) + " • " + details;
        }
        holder.tvDetails.setText(details);

        holder.tvSignalBadge.setText(signalPct + "%");
        int signalLevel = WifiHelper.getSignalLevel(item.getLevel());
        if (signalLevel >= 2) {
            holder.tvSignalBadge.setBackgroundResource(R.drawable.bg_signal_high);
        } else if (signalLevel == 1) {
            holder.tvSignalBadge.setBackgroundResource(R.drawable.bg_signal_mid);
        } else {
            holder.tvSignalBadge.setBackgroundResource(R.drawable.bg_signal_low);
        }

        if (item.isOpen()) {
            holder.ivLock.setImageResource(R.drawable.ic_unlock);
        } else {
            holder.ivLock.setImageResource(R.drawable.ic_lock);
        }

        if (item.isFavorite()) {
            holder.ivFav.setImageResource(R.drawable.ic_star_filled);
        } else {
            holder.ivFav.setImageResource(R.drawable.ic_star);
        }

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(item, position);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvSsid;
        TextView tvDetails;
        TextView tvSignalBadge;
        ImageView ivLock;
        ImageView ivFav;
    }
}
