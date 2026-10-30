package com.example.travelbooking.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbooking.R;
import com.example.travelbooking.adapters.BookingAdapter;
import com.example.travelbooking.database.DatabaseHelper;
import com.example.travelbooking.models.Booking;

import java.util.List;

public class AdminBookingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ImageButton btnBack = findViewById(R.id.btnAdminBookingsBack);
        RecyclerView rv = findViewById(R.id.rvAdminAllBookings);

        btnBack.setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));
        List<Booking> bookings = dbHelper.getAllBookingsAdmin();
        BookingAdapter adapter = new BookingAdapter(this, bookings, null);
        rv.setAdapter(adapter);
    }
}
