package com.example.travelbooking.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbooking.R;
import com.example.travelbooking.adapters.PackageAdapter;
import com.example.travelbooking.database.DatabaseHelper;
import com.example.travelbooking.models.TravelPackage;
import com.example.travelbooking.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TextView tvStatPackages, tvStatBookings;
    private RecyclerView rvAdminPackages;
    private PackageAdapter packageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tvStatPackages = findViewById(R.id.tvStatPackages);
        tvStatBookings = findViewById(R.id.tvStatBookings);
        rvAdminPackages = findViewById(R.id.rvAdminPackages);

        ImageButton btnLogout = findViewById(R.id.btnAdminLogout);
        MaterialButton btnAddPackage = findViewById(R.id.btnAddPackage);
        MaterialButton btnViewBookings = findViewById(R.id.btnAdminViewBookings);

        rvAdminPackages.setLayoutManager(new LinearLayoutManager(this));
        packageAdapter = new PackageAdapter(this, null, this::showEditPackageDialog);
        rvAdminPackages.setAdapter(packageAdapter);

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });

        btnAddPackage.setOnClickListener(v -> showAddPackageDialog());

        btnViewBookings.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AdminBookingsActivity.class));
        });

        refreshDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void refreshDashboard() {
        int totalPkg = dbHelper.getCount(DatabaseHelper.TABLE_PACKAGES, null);
        int totalBookings = dbHelper.getCount(DatabaseHelper.TABLE_BOOKINGS, null);

        tvStatPackages.setText(String.valueOf(totalPkg));
        tvStatBookings.setText(String.valueOf(totalBookings));

        List<TravelPackage> list = dbHelper.getPackages("All", null, true);
        packageAdapter.updateList(list);
    }

    private void showAddPackageDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_package, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Add New Travel Package");

        EditText etDest = dialogView.findViewById(R.id.etDlgDestination);
        EditText etDetails = dialogView.findViewById(R.id.etDlgDetails);
        EditText etPrice = dialogView.findViewById(R.id.etDlgPrice);
        EditText etImg = dialogView.findViewById(R.id.etDlgImageUrl);
        Spinner spType = dialogView.findViewById(R.id.spDlgServiceType);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchDlgActive);

        String[] types = {"Package", "Flight", "Hotel", "Transport"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spType.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Save Package", (dialog, which) -> {
                    String dest = etDest.getText().toString().trim();
                    String details = etDetails.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String img = etImg.getText().toString().trim();
                    String serviceType = spType.getSelectedItem().toString();

                    if (dest.isEmpty() || details.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        long id = dbHelper.addPackage(dest, serviceType, details, price, img);
                        if (id != -1) {
                            Toast.makeText(this, "Package created successfully!", Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditPackageDialog(TravelPackage pkg) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_package, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Edit Package #" + pkg.getId());

        EditText etDest = dialogView.findViewById(R.id.etDlgDestination);
        EditText etDetails = dialogView.findViewById(R.id.etDlgDetails);
        EditText etPrice = dialogView.findViewById(R.id.etDlgPrice);
        EditText etImg = dialogView.findViewById(R.id.etDlgImageUrl);
        Spinner spType = dialogView.findViewById(R.id.spDlgServiceType);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchDlgActive);

        String[] types = {"Package", "Flight", "Hotel", "Transport"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spType.setAdapter(adapter);

        // Prepopulate
        etDest.setText(pkg.getDestination());
        etDetails.setText(pkg.getDetails());
        etPrice.setText(String.valueOf(pkg.getPrice()));
        etImg.setText(pkg.getImageUrl());
        spType.setSelection(Math.max(0, Arrays.asList(types).indexOf(pkg.getServiceType())));
        switchActive.setChecked(pkg.isActive());

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String dest = etDest.getText().toString().trim();
                    String details = etDetails.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String img = etImg.getText().toString().trim();
                    String serviceType = spType.getSelectedItem().toString();
                    boolean active = switchActive.isChecked();

                    if (dest.isEmpty() || details.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        boolean updated = dbHelper.updatePackage(pkg.getId(), dest, serviceType, details, price, img, active);
                        if (updated) {
                            Toast.makeText(this, "Package updated successfully!", Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Deactivate", (dialog, which) -> {
                    dbHelper.deactivatePackage(pkg.getId());
                    Toast.makeText(this, "Package deactivated", Toast.LENGTH_SHORT).show();
                    refreshDashboard();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
