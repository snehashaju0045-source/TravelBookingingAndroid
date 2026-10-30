package com.example.travelbooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelbooking.R;
import com.example.travelbooking.models.TravelPackage;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
    private final Context context;
    private List<TravelPackage> list;
    private final OnPackageClickListener listener;

    public interface OnPackageClickListener {
        void onPackageClick(TravelPackage pkg);
    }

    public PackageAdapter(Context context, List<TravelPackage> list, OnPackageClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<TravelPackage> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_package_card, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        TravelPackage pkg = list.get(position);
        holder.tvDestination.setText(pkg.getDestination());
        holder.tvDetails.setText(pkg.getDetails());
        holder.tvBadge.setText(pkg.getServiceType().toUpperCase(Locale.ROOT));
        holder.tvRating.setText(String.format(Locale.US, "⭐ %.1f", pkg.getRating()));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvPrice.setText(formatter.format(pkg.getPrice()));

        // Load image with Glide and error placeholder
        Glide.with(context)
                .load(pkg.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_map)
                .centerCrop()
                .into(holder.ivThumb);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPackageClick(pkg);
        });

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) listener.onPackageClick(pkg);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvDestination, tvDetails, tvPrice, tvBadge, tvRating;
        MaterialButton btnAction;

        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivPackageThumb);
            tvDestination = itemView.findViewById(R.id.tvPackageDestination);
            tvDetails = itemView.findViewById(R.id.tvPackageDetails);
            tvPrice = itemView.findViewById(R.id.tvPackagePrice);
            tvBadge = itemView.findViewById(R.id.tvPackageBadge);
            tvRating = itemView.findViewById(R.id.tvPackageRating);
            btnAction = itemView.findViewById(R.id.btnPackageAction);
        }
    }
}
