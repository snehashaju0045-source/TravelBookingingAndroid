package com.example.travelbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelbooking.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.Locale;

public class BookingConfirmationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        int bookingId = getIntent().getIntExtra("booking_id", 0);
        String destination = getIntent().getStringExtra("destination");
        String travelDate = getIntent().getStringExtra("travel_date");
        int travelers = getIntent().getIntExtra("travelers", 1);
        double amount = getIntent().getDoubleExtra("amount", 0.0);

        TextView tvRef = findViewById(R.id.tvConfirmationRef);
        TextView tvDest = findViewById(R.id.tvConfirmDestination);
        TextView tvDate = findViewById(R.id.tvConfirmDate);
        TextView tvTravelers = findViewById(R.id.tvConfirmTravelers);
        TextView tvAmount = findViewById(R.id.tvConfirmAmount);

        MaterialButton btnViewBookings = findViewById(R.id.btnViewBookings);
        MaterialButton btnBackHome = findViewById(R.id.btnBackHome);

        tvRef.setText("Reference: TRV-" + (100000 + bookingId));
        tvDest.setText(destination != null ? destination : "Travel Destination");
        tvDate.setText(travelDate);
        tvTravelers.setText(travelers + (travelers == 1 ? " Person" : " Persons"));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        tvAmount.setText(formatter.format(amount));

        btnViewBookings.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
