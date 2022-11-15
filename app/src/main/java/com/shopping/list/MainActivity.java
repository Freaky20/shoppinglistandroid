package com.shopping.list;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.shopping.list.ui.home.HomeFragment;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
    }

    private void showPlacePicker() {
        PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
        builder.setAndroidApiKey("AIzaSyAaOSozlNmzZcjQ_8rgnSiZ1CX92wuskus")
                .setMapsApiKey("AIzaSyDaDQRoc087nDE5_vfvwOGyYGCNRhNH4s4");

        // Instead of using the position of the device as it is right now, you can set an initial location.
        // IMPORTANT: enable nearby search MUST be set to true.
        // setLatLng(new LatLng(37.4219999, -122.0862462)) in the builder

        try {
            Intent placeIntent = builder.build(this);
            startActivityForResult(placeIntent, 1);
        }
        catch (Exception ex) {
            Toast.makeText(this, "Google Play services is not available...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null) {
                DataBase dataBase = new DataBase(this);
                Location location = new Location(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
                if (new DataBase(this).saveLocation(location)) {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
