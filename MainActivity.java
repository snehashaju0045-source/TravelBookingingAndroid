package com.example.travelbooking.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.travelbooking.R;
import com.example.travelbooking.adapters.BookingAdapter;
import com.example.travelbooking.adapters.PackageAdapter;
import com.example.travelbooking.database.DatabaseHelper;
import com.example.travelbooking.models.Booking;
import com.example.travelbooking.models.TravelPackage;
import com.example.travelbooking.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNav;

    // Explore / Home View References
    private View viewHome;
    private PackageAdapter packageAdapter;
    private EditText etSearch;
    private ImageButton btnClearSearch;
    private String selectedCategory = "All";
    private TextView tvPackageCount;
    private LinearLayout layoutEmptyPackages;
    private SwipeRefreshLayout swipeRefreshHome;

    // My Bookings View References
    private View viewBookings;
    private BookingAdapter bookingAdapter;
    private String selectedBookingFilter = "All";
    private LinearLayout layoutEmptyBookings;
    private TextView tvBookingSummary;

    // Profile View References
    private View viewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        bottomNav = findViewById(R.id.bottomNavigationView);

        initViews();
        setupBottomNav();
        showHomeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPackages();
        loadBookings();
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        viewHome = inflater.inflate(R.layout.fragment_home, fragmentContainer, false);
        viewBookings = inflater.inflate(R.layout.fragment_bookings, fragmentContainer, false);
        setupHomeComponents();
        setupBookingsComponents();
    }

    private void setupHomeComponents() {
        TextView tvWelcome = viewHome.findViewById(R.id.tvWelcomeName);
        tvWelcome.setText("Welcome, " + sessionManager.getUserName() + "!");

        etSearch = viewHome.findViewById(R.id.etSearchDestination);
        btnClearSearch = viewHome.findViewById(R.id.btnClearSearch);
        tvPackageCount = viewHome.findViewById(R.id.tvPackageCount);
        layoutEmptyPackages = viewHome.findViewById(R.id.layoutEmptyPackages);
        swipeRefreshHome = viewHome.findViewById(R.id.swipeRefreshHome);

        RecyclerView rv = viewHome.findViewById(R.id.rvPackages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        packageAdapter = new PackageAdapter(this, null, pkg -> {
            Intent intent = new Intent(MainActivity.this, PackageDetailActivity.class);
            intent.putExtra("package_data", pkg);
            startActivity(intent);
        });
        rv.setAdapter(packageAdapter);

        // Search text watcher
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                loadPackages();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            loadPackages();
        });

        // Category Chips
        ChipGroup chipGroup = viewHome.findViewById(R.id.chipGroupCategories);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipFlight) selectedCategory = "Flight";
            else if (checkedId == R.id.chipHotel) selectedCategory = "Hotel";
            else if (checkedId == R.id.chipPackage) selectedCategory = "Package";
            else if (checkedId == R.id.chipTransport) selectedCategory = "Transport";
            else selectedCategory = "All";
            loadPackages();
        });

        swipeRefreshHome.setOnRefreshListener(() -> {
            loadPackages();
            swipeRefreshHome.setRefreshing(false);
        });
    }

    private void setupBookingsComponents() {
        tvBookingSummary = viewBookings.findViewById(R.id.tvBookingSummary);
        layoutEmptyBookings = viewBookings.findViewById(R.id.layoutEmptyBookings);

        RecyclerView rv = viewBookings.findViewById(R.id.rvBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter(this, null, this::confirmCancelBooking);
        rv.setAdapter(bookingAdapter);

        ChipGroup chipGroup = viewBookings.findViewById(R.id.chipGroupBookingFilter);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipFilterConfirmed) selectedBookingFilter = "CONFIRMED";
            else if (checkedId == R.id.chipFilterCancelled) selectedBookingFilter = "CANCELLED";
            else selectedBookingFilter = "All";
            loadBookings();
        });
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showHomeView();
                return true;
            } else if (id == R.id.nav_bookings) {
                showBookingsView();
                return true;
            } else if (id == R.id.nav_profile) {
                showProfileDialog();
                return false;
            }
            return false;
        });
    }

    private void showHomeView() {
        fragmentContainer.removeAllViews();
        fragmentContainer.addView(viewHome);
        loadPackages();
    }

    private void showBookingsView() {
        fragmentContainer.removeAllViews();
        fragmentContainer.addView(viewBookings);
        loadBookings();
    }

    private void showProfileDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Account & Session")
                .setMessage("Logged in as: " + sessionManager.getUserName() + "\nEmail: " + sessionManager.getUserEmail() + "\nRole: " + sessionManager.getUserRole())
                .setPositiveButton("OK", null)
                .setNegativeButton("LOG OUT", (dialog, which) -> {
                    sessionManager.logout();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                })
                .show();
    }

    private void loadPackages() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        List<TravelPackage> list = dbHelper.getPackages(selectedCategory, query, false);
        packageAdapter.updateList(list);

        tvPackageCount.setText(list.size() + (list.size() == 1 ? " package" : " packages"));
        layoutEmptyPackages.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadBookings() {
        List<Booking> list = dbHelper.getUserBookings(sessionManager.getUserId(), selectedBookingFilter);
        bookingAdapter.updateList(list);

        tvBookingSummary.setText(list.size() + " trips");
        layoutEmptyBookings.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmCancelBooking(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking?")
                .setMessage("Are you sure you want to cancel your trip to " + booking.getDestination() + " on " + booking.getTravelDate() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    boolean success = dbHelper.cancelBooking(booking.getId(), sessionManager.getUserId());
                    if (success) {
                        Toast.makeText(this, "Booking #" + booking.getId() + " has been cancelled.", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        Toast.makeText(this, "Failed to cancel booking.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Keep Booking", null)
                .show();
    }
}
