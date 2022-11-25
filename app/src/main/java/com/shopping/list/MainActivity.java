package com.shopping.list;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private ArrayList<Geofence> geofences;
    //Variables for duration of geofence and its radius
    private static final float GEOFENCE_RADIUS = 250.0f; // in meters
    private static final String CHANNEL_ID = "Geofence";
    private static final int REQUEST_CODE_PLACE = 1;
    private static final int REQUEST_CODE_ITEM = 2;

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        geofences = new ArrayList<>();
        createNotificationChannel();
        geofencingClient = LocationServices.getGeofencingClient(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.nav_gallery){
                    showPlacePicker();
                }

                boolean result = NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawers();
                return result;
            }
        });
        FloatingActionButton fab = this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        checkForNotification();
        DataBase dataBase = new DataBase(this);
        dataBase.setupItem();
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }

    //Method that opens link shop activity from the card adapter
    public void addItem(){
        Intent intent = new Intent(this, AddItemActivity.class);
/*        intent.putExtra("shopListID", id);
        intent.putExtra("shopListName", shoppingLists.get(position).getName());*/
        startActivityForResult(intent, REQUEST_CODE_ITEM);
    }

    private void checkForNotification(){
        String intent = getIntent().getStringExtra("geofenceID");
        if(intent != null){
            removeGeofence();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showPlacePicker() {
        PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
        builder.setAndroidApiKey("AIzaSyBmgZS4vsjyULHz4q3e_44sTE9wyw8FbRg")
                .setMapsApiKey("AIzaSyDaDQRoc087nDE5_vfvwOGyYGCNRhNH4s4");

        // Instead of using the position of the device as it is right now, you can set an initial location.
        // IMPORTANT: enable nearby search MUST be set to true.
        // setLatLng(new LatLng(37.4219999, -122.0862462)) in the builder

        try {
            Intent placeIntent = builder.build(this);
            startActivityForResult(placeIntent, REQUEST_CODE_PLACE);
        }
        catch (Exception ex) {
            Toast.makeText(this, "Google Play services is not available...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_PLACE) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null) {
                DataBase dataBase = new DataBase(this);
                Location location = new Location(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
                if (dataBase.saveLocation(location)) { //!dataBase.checkLocationExist(location) &&
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    createGeofence(place.getLatLng(), dataBase.retrieveLocationID(location) + "");
                    addGeofence();
                } else {
                    Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if((requestCode == REQUEST_CODE_ITEM) && (resultCode == RESULT_OK)){

            Item item = new Item(data.getStringExtra("name"), data.getIntExtra("quantity", -1));
            if (new DataBase(this).saveListItem(item)) {
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                if(getFragmentRefreshListener()!= null){
                    getFragmentRefreshListener().onRefresh();
                }
            }
            else {
                Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createGeofence(LatLng latLng, String id){
        geofences.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(id)

                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    public void removeGeofence(){
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Geofences removed", Toast.LENGTH_SHORT).show();
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to remove geofences", Toast.LENGTH_SHORT).show();
                        // Failed to remove geofences
                        // ...
                    }
                });
    }

    public void addGeofence(){
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Geofence added!", Toast.LENGTH_SHORT).show();
                        // Geofences added
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to add geofence!", Toast.LENGTH_SHORT).show();
                        // Failed to add geofences
                        // ...
                    }
                });
    }



    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Expand the menu; if there is an action bar, it will be expanded with new items.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
