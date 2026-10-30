package com.example.travelbooking.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelbooking.R;
import com.example.travelbooking.models.Booking;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private final Context context;
    private List<Booking> list;
    private final OnCancelBookingListener cancelListener;

    public interface OnCancelBookingListener {
        void onCancelBooking(Booking booking);
    }

    public BookingAdapter(Context context, List<Booking> list, OnCancelBookingListener cancelListener) {
        this.context = context;
        this.list = list;
        this.cancelListener = cancelListener;
    }

    public void updateList(List<Booking> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = list.get(position);
        holder.tvRef.setText("Booking #" + booking.getId());
        holder.tvDestination.setText(booking.getDestination());
        holder.tvDate.setText("Travel Date: " + booking.getTravelDate() + " • " + booking.getTravelersCount() + " Traveler(s)");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvAmount.setText("Total: " + formatter.format(booking.getTotalAmount()));

        holder.tvStatus.setText(booking.getStatus());

        if (booking.isConfirmed()) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_confirmed);
            holder.tvStatus.setTextColor(Color.parseColor("#15803D"));
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> {
                if (cancelListener != null) cancelListener.onCancelBooking(booking);
            });
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_cancelled);
            holder.tvStatus.setTextColor(Color.parseColor("#B91C1C"));
            holder.btnCancel.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(booking.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_map)
                .centerCrop()
                .into(holder.ivThumb);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvRef, tvStatus, tvDestination, tvDate, tvAmount;
        MaterialButton btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivBookingItemThumb);
            tvRef = itemView.findViewById(R.id.tvBookingItemRef);
            tvStatus = itemView.findViewById(R.id.tvBookingItemStatus);
            tvDestination = itemView.findViewById(R.id.tvBookingItemDestination);
            tvDate = itemView.findViewById(R.id.tvBookingItemDate);
            tvAmount = itemView.findViewById(R.id.tvBookingItemAmount);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
