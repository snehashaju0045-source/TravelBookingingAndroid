package com.example.travelbooking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.travelbooking.models.Booking;
import com.example.travelbooking.models.TravelPackage;
import com.example.travelbooking.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "travel_booking_system.db";
    private static final int DATABASE_VERSION = 1;

    // Table: Users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_ROLE = "role"; // CUSTOMER or ADMIN

    // Table: Travel Packages
    public static final String TABLE_PACKAGES = "travel_packages";
    public static final String COL_PKG_ID = "id";
    public static final String COL_PKG_DESTINATION = "destination";
    public static final String COL_PKG_SERVICE_TYPE = "service_type"; // Flight, Hotel, Package, Transport
    public static final String COL_PKG_DETAILS = "details";
    public static final String COL_PKG_PRICE = "price";
    public static final String COL_PKG_IMAGE_URL = "image_url";
    public static final String COL_PKG_DURATION = "duration";
    public static final String COL_PKG_RATING = "rating";
    public static final String COL_PKG_ACTIVE = "active"; // 1 = active, 0 = deactivated

    // Table: Bookings
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COL_BOOKING_ID = "id";
    public static final String COL_BOOKING_USER_ID = "user_id";
    public static final String COL_BOOKING_PACKAGE_ID = "package_id";
    public static final String COL_BOOKING_TRAVEL_DATE = "travel_date";
    public static final String COL_BOOKING_TRAVELERS = "travelers_count";
    public static final String COL_BOOKING_AMOUNT = "total_amount";
    public static final String COL_BOOKING_STATUS = "status"; // CONFIRMED or CANCELLED
    public static final String COL_BOOKING_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_USER_ROLE + " TEXT NOT NULL DEFAULT 'CUSTOMER')");

        // Create travel packages table
        db.execSQL("CREATE TABLE " + TABLE_PACKAGES + " (" +
                COL_PKG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PKG_DESTINATION + " TEXT NOT NULL, " +
                COL_PKG_SERVICE_TYPE + " TEXT NOT NULL, " +
                COL_PKG_DETAILS + " TEXT NOT NULL, " +
                COL_PKG_PRICE + " REAL NOT NULL, " +
                COL_PKG_IMAGE_URL + " TEXT, " +
                COL_PKG_DURATION + " TEXT, " +
                COL_PKG_RATING + " REAL DEFAULT 4.8, " +
                COL_PKG_ACTIVE + " INTEGER NOT NULL DEFAULT 1)");

        // Create bookings table
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_USER_ID + " INTEGER NOT NULL, " +
                COL_BOOKING_PACKAGE_ID + " INTEGER NOT NULL, " +
                COL_BOOKING_TRAVEL_DATE + " TEXT NOT NULL, " +
                COL_BOOKING_TRAVELERS + " INTEGER NOT NULL DEFAULT 1, " +
                COL_BOOKING_AMOUNT + " REAL NOT NULL, " +
                COL_BOOKING_STATUS + " TEXT NOT NULL DEFAULT 'CONFIRMED', " +
                COL_BOOKING_CREATED_AT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_BOOKING_PACKAGE_ID + ") REFERENCES " + TABLE_PACKAGES + "(" + COL_PKG_ID + "))");

        // Seed default administrator
        ContentValues admin = new ContentValues();
        admin.put(COL_USER_NAME, "System Administrator");
        admin.put(COL_USER_EMAIL, "admin@travel.com");
        admin.put(COL_USER_PASSWORD_HASH, hashPassword("Admin@123"));
        admin.put(COL_USER_ROLE, "ADMIN");
        db.insert(TABLE_USERS, null, admin);

        // Seed demo customer
        ContentValues demoUser = new ContentValues();
        demoUser.put(COL_USER_NAME, "John Traveler");
        demoUser.put(COL_USER_EMAIL, "john@example.com");
        demoUser.put(COL_USER_PASSWORD_HASH, hashPassword("Password123"));
        demoUser.put(COL_USER_ROLE, "CUSTOMER");
        long userId = db.insert(TABLE_USERS, null, demoUser);

        // Seed initial curated travel packages
        seedPackage(db, "Bali Tropical Retreat", "Package",
                "5 Days / 4 Nights luxury private villa with infinity pool, daily breakfast, Ubud waterfall excursion, and sunset temple tour.",
                649.00, "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80", "5 Days / 4 Nights", 4.9);

        seedPackage(db, "Paris Lights & Culture", "Package",
                "6 Days romantic city break including Eiffel Tower summit pass, Louvre guided tour, Seine river dinner cruise, and boutique hotel stay.",
                899.00, "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=800&q=80", "6 Days / 5 Nights", 4.8);

        seedPackage(db, "Swiss Alpine Expedition", "Package",
                "7 Days scenic train tour across Interlaken & Zermatt, Matterhorn cable car, traditional chalet lodging, and fondue tasting.",
                1199.00, "https://images.unsplash.com/photo-1530122037265-a5f1f91d3b99?auto=format&fit=crop&w=800&q=80", "7 Days / 6 Nights", 4.9);

        seedPackage(db, "Tokyo Neon & Heritage", "Flight",
                "Round-trip international flight with 23kg baggage allowance, complimentary lounge pass, and priority boarding.",
                749.00, "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=800&q=80", "Round Trip Flight", 4.7);

        seedPackage(db, "Santorini Cliffside Suites", "Hotel",
                "4-Star Aegean caldera view hotel with plunge pool, champagne breakfast, and caldera sunset terrace.",
                480.00, "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=800&q=80", "3 Nights Stay", 4.9);

        seedPackage(db, "Dubai Desert & Skyline", "Transport",
                "Premium private airport transfer & 4x4 red dune desert safari with BBQ buffet dinner and stargazing.",
                220.00, "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=800&q=80", "Day Experience", 4.6);

        // Seed sample existing booking for demo user
        ContentValues booking = new ContentValues();
        booking.put(COL_BOOKING_USER_ID, userId);
        booking.put(COL_BOOKING_PACKAGE_ID, 1);
        booking.put(COL_BOOKING_TRAVEL_DATE, "2026-11-20");
        booking.put(COL_BOOKING_TRAVELERS, 2);
        booking.put(COL_BOOKING_AMOUNT, 1298.00);
        booking.put(COL_BOOKING_STATUS, "CONFIRMED");
        booking.put(COL_BOOKING_CREATED_AT, "2026-10-25");
        db.insert(TABLE_BOOKINGS, null, booking);
    }

    private void seedPackage(SQLiteDatabase db, String dest, String type, String details, double price, String img, String dur, double rating) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PKG_DESTINATION, dest);
        cv.put(COL_PKG_SERVICE_TYPE, type);
        cv.put(COL_PKG_DETAILS, details);
        cv.put(COL_PKG_PRICE, price);
        cv.put(COL_PKG_IMAGE_URL, img);
        cv.put(COL_PKG_DURATION, dur);
        cv.put(COL_PKG_RATING, rating);
        cv.put(COL_PKG_ACTIVE, 1);
        db.insert(TABLE_PACKAGES, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }

    // --- Authentication Operations ---
    public User login(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashed = hashPassword(password);
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + ", " + COL_USER_NAME + ", " + COL_USER_EMAIL + ", " + COL_USER_ROLE +
                        " FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD_HASH + " = ?",
                new String[]{email.trim().toLowerCase(Locale.ROOT), hashed}
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            cursor.close();
        }
        return user;
    }

    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name.trim());
        cv.put(COL_USER_EMAIL, email.trim().toLowerCase(Locale.ROOT));
        cv.put(COL_USER_PASSWORD_HASH, hashPassword(password));
        cv.put(COL_USER_ROLE, "CUSTOMER");
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    // --- Travel Packages Operations ---
    public List<TravelPackage> getPackages(String categoryFilter, String searchQuery, boolean includeInactive) {
        List<TravelPackage> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_PACKAGES + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (!includeInactive) {
            query.append(" AND ").append(COL_PKG_ACTIVE).append(" = 1");
        }
        if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equalsIgnoreCase("All")) {
            query.append(" AND ").append(COL_PKG_SERVICE_TYPE).append(" LIKE ?");
            args.add("%" + categoryFilter + "%");
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.append(" AND (").append(COL_PKG_DESTINATION).append(" LIKE ? OR ")
                    .append(COL_PKG_DETAILS).append(" LIKE ?)");
            args.add("%" + searchQuery.trim() + "%");
            args.add("%" + searchQuery.trim() + "%");
        }

        query.append(" ORDER BY ").append(COL_PKG_ID).append(" DESC");

        Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]));
        if (c != null && c.moveToFirst()) {
            do {
                TravelPackage pkg = new TravelPackage(
                        c.getInt(c.getColumnIndexOrThrow(COL_PKG_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_PKG_DESTINATION)),
                        c.getString(c.getColumnIndexOrThrow(COL_PKG_SERVICE_TYPE)),
                        c.getString(c.getColumnIndexOrThrow(COL_PKG_DETAILS)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_PKG_PRICE)),
                        c.getString(c.getColumnIndexOrThrow(COL_PKG_IMAGE_URL)),
                        c.getString(c.getColumnIndexOrThrow(COL_PKG_DURATION)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_PKG_RATING)),
                        c.getInt(c.getColumnIndexOrThrow(COL_PKG_ACTIVE)) == 1
                );
                list.add(pkg);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public TravelPackage getPackageById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PACKAGES + " WHERE " + COL_PKG_ID + " = ?", new String[]{String.valueOf(id)});
        TravelPackage pkg = null;
        if (c != null && c.moveToFirst()) {
            pkg = new TravelPackage(
                    c.getInt(c.getColumnIndexOrThrow(COL_PKG_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_PKG_DESTINATION)),
                    c.getString(c.getColumnIndexOrThrow(COL_PKG_SERVICE_TYPE)),
                    c.getString(c.getColumnIndexOrThrow(COL_PKG_DETAILS)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_PKG_PRICE)),
                    c.getString(c.getColumnIndexOrThrow(COL_PKG_IMAGE_URL)),
                    c.getString(c.getColumnIndexOrThrow(COL_PKG_DURATION)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_PKG_RATING)),
                    c.getInt(c.getColumnIndexOrThrow(COL_PKG_ACTIVE)) == 1
            );
            c.close();
        }
        return pkg;
    }

    public long addPackage(String destination, String serviceType, String details, double price, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PKG_DESTINATION, destination);
        cv.put(COL_PKG_SERVICE_TYPE, serviceType);
        cv.put(COL_PKG_DETAILS, details);
        cv.put(COL_PKG_PRICE, price);
        cv.put(COL_PKG_IMAGE_URL, imageUrl != null && !imageUrl.isEmpty() ? imageUrl : "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80");
        cv.put(COL_PKG_DURATION, "Custom Duration");
        cv.put(COL_PKG_RATING, 4.8);
        cv.put(COL_PKG_ACTIVE, 1);
        return db.insert(TABLE_PACKAGES, null, cv);
    }

    public boolean updatePackage(int id, String destination, String serviceType, String details, double price, String imageUrl, boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PKG_DESTINATION, destination);
        cv.put(COL_PKG_SERVICE_TYPE, serviceType);
        cv.put(COL_PKG_DETAILS, details);
        cv.put(COL_PKG_PRICE, price);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            cv.put(COL_PKG_IMAGE_URL, imageUrl);
        }
        cv.put(COL_PKG_ACTIVE, active ? 1 : 0);
        return db.update(TABLE_PACKAGES, cv, COL_PKG_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deactivatePackage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PKG_ACTIVE, 0);
        return db.update(TABLE_PACKAGES, cv, COL_PKG_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // --- Booking Operations ---
    public long createBooking(int userId, int packageId, String travelDate, int travelers, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOKING_USER_ID, userId);
        cv.put(COL_BOOKING_PACKAGE_ID, packageId);
        cv.put(COL_BOOKING_TRAVEL_DATE, travelDate);
        cv.put(COL_BOOKING_TRAVELERS, travelers);
        cv.put(COL_BOOKING_AMOUNT, amount);
        cv.put(COL_BOOKING_STATUS, "CONFIRMED");
        cv.put(COL_BOOKING_CREATED_AT, new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));
        return db.insert(TABLE_BOOKINGS, null, cv);
    }

    public List<Booking> getUserBookings(int userId, String statusFilter) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b." + COL_BOOKING_ID + ", b." + COL_BOOKING_USER_ID + ", b." + COL_BOOKING_PACKAGE_ID + ", " +
                "u." + COL_USER_NAME + ", u." + COL_USER_EMAIL + ", " +
                "p." + COL_PKG_DESTINATION + ", p." + COL_PKG_SERVICE_TYPE + ", p." + COL_PKG_IMAGE_URL + ", " +
                "b." + COL_BOOKING_TRAVEL_DATE + ", b." + COL_BOOKING_TRAVELERS + ", b." + COL_BOOKING_AMOUNT + ", " +
                "b." + COL_BOOKING_STATUS + ", b." + COL_BOOKING_CREATED_AT + " " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON u." + COL_USER_ID + " = b." + COL_BOOKING_USER_ID + " " +
                "JOIN " + TABLE_PACKAGES + " p ON p." + COL_PKG_ID + " = b." + COL_BOOKING_PACKAGE_ID + " " +
                "WHERE b." + COL_BOOKING_USER_ID + " = ?";

        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
            query += " AND b." + COL_BOOKING_STATUS + " = ?";
            args.add(statusFilter.toUpperCase(Locale.ROOT));
        }

        query += " ORDER BY b." + COL_BOOKING_ID + " DESC";

        Cursor c = db.rawQuery(query, args.toArray(new String[0]));
        if (c != null && c.moveToFirst()) {
            do {
                Booking b = new Booking(
                        c.getInt(0),
                        c.getInt(1),
                        c.getInt(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6),
                        c.getString(7),
                        c.getString(8),
                        c.getInt(9),
                        c.getDouble(10),
                        c.getString(11),
                        c.getString(12)
                );
                list.add(b);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public List<Booking> getAllBookingsAdmin() {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b." + COL_BOOKING_ID + ", b." + COL_BOOKING_USER_ID + ", b." + COL_BOOKING_PACKAGE_ID + ", " +
                "u." + COL_USER_NAME + ", u." + COL_USER_EMAIL + ", " +
                "p." + COL_PKG_DESTINATION + ", p." + COL_PKG_SERVICE_TYPE + ", p." + COL_PKG_IMAGE_URL + ", " +
                "b." + COL_BOOKING_TRAVEL_DATE + ", b." + COL_BOOKING_TRAVELERS + ", b." + COL_BOOKING_AMOUNT + ", " +
                "b." + COL_BOOKING_STATUS + ", b." + COL_BOOKING_CREATED_AT + " " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON u." + COL_USER_ID + " = b." + COL_BOOKING_USER_ID + " " +
                "JOIN " + TABLE_PACKAGES + " p ON p." + COL_PKG_ID + " = b." + COL_BOOKING_PACKAGE_ID + " " +
                "ORDER BY b." + COL_BOOKING_ID + " DESC";

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Booking b = new Booking(
                        c.getInt(0),
                        c.getInt(1),
                        c.getInt(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6),
                        c.getString(7),
                        c.getString(8),
                        c.getInt(9),
                        c.getDouble(10),
                        c.getString(11),
                        c.getString(12)
                );
                list.add(b);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public boolean cancelBooking(int bookingId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOKING_STATUS, "CANCELLED");
        return db.update(TABLE_BOOKINGS, cv, COL_BOOKING_ID + " = ? AND " + COL_BOOKING_USER_ID + " = ?",
                new String[]{String.valueOf(bookingId), String.valueOf(userId)}) > 0;
    }

    public int getCount(String table, String whereClause) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT COUNT(*) FROM " + table;
        if (whereClause != null) q += " WHERE " + whereClause;
        Cursor c = db.rawQuery(q, null);
        int count = 0;
        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
            c.close();
        }
        return count;
    }
}
