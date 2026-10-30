package com.example.travelbooking.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.travelbooking.R;
import com.example.travelbooking.database.DatabaseHelper;
import com.example.travelbooking.models.TravelPackage;
import com.example.travelbooking.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class PackageDetailActivity extends AppCompatActivity {
    private TravelPackage travelPackage;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private String selectedDate = "";
    private int travelerCount = 1;
    private double unitPrice = 0.0;

    private TextView tvTotalPrice, tvTravelersCount;
    private MaterialButton btnSelectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);

        travelPackage = (TravelPackage) getIntent().getSerializableExtra("package_data");
        if (travelPackage == null) {
            Toast.makeText(this, "Package data unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        unitPrice = travelPackage.getPrice();

        setupUI();
    }

    private void setupUI() {
        ImageView ivHero = findViewById(R.id.ivDetailHero);
        ImageButton btnBack = findViewById(R.id.btnDetailBack);
        TextView tvServiceType = findViewById(R.id.tvDetailServiceType);
        TextView tvDestination = findViewById(R.id.tvDetailDestination);
        TextView tvDuration = findViewById(R.id.tvDetailDuration);
        TextView tvUnitPrice = findViewById(R.id.tvDetailUnitPrice);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvTravelersCount = findViewById(R.id.tvTravelersCount);
        tvTotalPrice = findViewById(R.id.tvDetailTotalPrice);

        ImageButton btnDecrease = findViewById(R.id.btnDecreaseTravelers);
        ImageButton btnIncrease = findViewById(R.id.btnIncreaseTravelers);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirmBooking);

        // Populate package data
        tvDestination.setText(travelPackage.getDestination());
        tvServiceType.setText(travelPackage.getServiceType().toUpperCase(Locale.ROOT));
        tvDuration.setText(travelPackage.getDuration());
        tvDescription.setText(travelPackage.getDetails());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        tvUnitPrice.setText(formatter.format(unitPrice));

        Glide.with(this)
                .load(travelPackage.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_map)
                .centerCrop()
                .into(ivHero);

        btnBack.setOnClickListener(v -> finish());

        // Date selection
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Travelers counter
        btnDecrease.setOnClickListener(v -> {
            if (travelerCount > 1) {
                travelerCount--;
                updatePricing();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (travelerCount < 20) {
                travelerCount++;
                updatePricing();
            }
        });

        btnConfirm.setOnClickListener(v -> confirmBooking());

        updatePricing();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            btnSelectDate.setText("Travel Date: " + selectedDate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        // Enforce minimum date as today
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.setTitle("Select Travel Date");
        dialog.show();
    }

    private void updatePricing() {
        tvTravelersCount.setText(String.valueOf(travelerCount));
        double total = unitPrice * travelerCount;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        tvTotalPrice.setText(formatter.format(total));
    }

    private void confirmBooking() {
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select your travel date before booking", Toast.LENGTH_LONG).show();
            showDatePicker();
            return;
        }

        double totalAmount = unitPrice * travelerCount;
        long bookingId = dbHelper.createBooking(
                sessionManager.getUserId(),
                travelPackage.getId(),
                selectedDate,
                travelerCount,
                totalAmount
        );

        if (bookingId != -1) {
            Intent intent = new Intent(PackageDetailActivity.this, BookingConfirmationActivity.class);
            intent.putExtra("booking_id", (int) bookingId);
            intent.putExtra("destination", travelPackage.getDestination());
            intent.putExtra("travel_date", selectedDate);
            intent.putExtra("travelers", travelerCount);
            intent.putExtra("amount", totalAmount);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
