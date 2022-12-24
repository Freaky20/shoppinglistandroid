package com.shopping.list;

import android.app.NotificationChannel;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.view.MenuItem;

import android.view.View;

import android.content.Context;

import android.content.SharedPreferences;

import android.content.Intent;

import android.os.Build;

import android.os.Bundle;

import android.widget.Toast;

import com.google.android.gms.location.Geofence;

import com.google.android.gms.location.GeofencingClient;

import com.google.android.gms.location.GeofencingRequest;

import com.shopping.list.activity.HelpActivity;

import com.shopping.list.adapter.LocationListViewAdapter;

import com.shopping.list.adapter.ShoppingListViewAdapter;

import com.shopping.list.broadcast.GeofenceBroadcastReceiver;

import com.shopping.list.database.DataBase;

import com.shopping.list.model.Location;

import com.shopping.list.model.MainViewModel;

import com.firebase.ui.auth.AuthUI;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;

import com.firebase.ui.auth.IdpResponse;

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.List;

import androidx.activity.result.ActivityResultLauncher;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProviders;

import androidx.navigation.NavController;

import androidx.navigation.Navigation;

import androidx.navigation.ui.AppBarConfiguration;

import androidx.navigation.ui.NavigationUI;

 public class MainActivity extends AppCompatActivity implements ShoppingListViewAdapter.LinkShops,ShoppingListViewAdapter.HandleGeofence, LocationListViewAdapter.GeoFenceInterface
 {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofence;
    private MainViewModel mainViewModel;
    private DataBase dataBase;
    private SharedPreferences sharedpreferences;
    private static final float GEOFENCE_RADIUS = 300.0f;
    private static final String CHANNEL_ID = "Geofence";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        geofence = new ArrayList<>();
        createNotificationChannel();
        mAuth = FirebaseAuth.getInstance();
        geofencingClient = LocationServices.getGeofencingClient(this);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_slideshow, R.id.nav_share, R.id.nav_send).setDrawerLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        NavigationUI.setupActionBarWithNavController(this,navController,drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                if(menuItem.getItemId() == R.id.nav_tools)
                {
                    Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                    startActivity(intent);
                }
                else if(menuItem.getItemId() == R.id.nav_logout)
                {
                    AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                            );
                }
                boolean result = NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawers();
                return result;
            }
        }
        );
        final FloatingActionButton fab = this.findViewById(R.id.fab);
        dataBase = new DataBase(this);
        dataBase.setupItem();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getText().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                getSupportActionBar().setTitle(s);
            }
        }
        );
        String notification = getIntent().getStringExtra("notification");
        if(notification != null)
        {
            int shopID = Integer.parseInt(getIntent().getStringExtra("shopID"));
            Location location = dataBase.getLocation(shopID);
            int shopListID = location.getShoppingListID();
            location.setGeofence(false);
            if(dataBase.updateLocation(location))
            {
                removeGeofenceData(shopID);
                Bundle bundle = new Bundle();
                bundle.putInt("shopID", shopListID);
                navController.navigate(R.id.nav_send, bundle);
            }
        }
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (sharedpreferences.getBoolean("first run", true))
        {
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
            sharedpreferences.edit().putBoolean("first run", false).commit();
        }
        else
        {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser == null)
            {
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), new AuthUI.IdpConfig.PhoneBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build());
                Intent signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setLogo(R.drawable.new_logo).setTheme(R.style.LoginTheme).build();
                signInLauncher.launch(signInIntent);
            }
        }
    }
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(new FirebaseAuthUIActivityResultContract(), result -> onSignInResult(result)
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result)
    {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK)
        {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        }
        else
        {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
        }
    }

    public void sendLinkShops(String name, int id)
    {
        Bundle bundle = new Bundle();
        bundle.putString("shopListName", name);
        bundle.putInt("shopListID", id);
        navController.navigate(R.id.nav_slideshow, bundle);
    }

    public void openShopListFragment(View view,int shopID)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("shopID", shopID);
        Navigation.findNavController(view).navigate(R.id.nav_send, bundle);
    }

    public void openAddItemFragment(View view,int shopID)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("shopID", shopID);
        Navigation.findNavController(view).navigate(R.id.nav_share, bundle);
    }

    public void openRecipeDetailsFragment(View view, String Id,String name)
    {
        Bundle bundle = new Bundle();
          bundle.putString("Id", Id);
        bundle.putString("name", name);
        Navigation.findNavController(view).navigate(R.id.recipeDetailsFragment, bundle);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (intent!=null)
        {
            String data=intent.getStringExtra("notification");
            if (data!=null)
            {
                if(dataBase==null)
                {
                    dataBase=new DataBase(getApplicationContext());
                }
                int shopID=Integer.parseInt(intent.getStringExtra("shopID"));
                Location location=dataBase.getLocation(shopID);
                int shopListID=location.getShoppingListID();
                location.setGeofence(false);
                if(dataBase.updateLocation(location))
                {
                    removeGeofenceData(shopID);
                    Bundle bundle=new Bundle();
                    bundle.putInt("shopID", shopListID);
                    navController.navigate(R.id.nav_send, bundle);
                }
            }
        }
    }

     public void createGeofenceData(LatLng latLng, int id)
     {
         createGeofence(latLng, id + "");
         addGeofence(id);
     }

     public void removeGeofenceData(int id)
     {
         List<String> geofenceList = Arrays.asList(id + "");
         removeGeofenceID(geofenceList);
         removeGeofencePending(id);
         Intent intent = new Intent(this,GeofenceBroadcastReceiver.class);
         PendingIntent.getBroadcast(this, id,intent,PendingIntent.FLAG_UPDATE_CURRENT).cancel();
     }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createGeofence(LatLng latLng, String id)
    {
        geofence.add(new Geofence.Builder().setRequestId(id).setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS).setExpirationDuration(Geofence.NEVER_EXPIRE).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build());
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofence);
        return builder.build();
    }

    public void removeGeofencePending(int id)
    {
        geofencingClient.removeGeofences(getGeofencePendingIntent(id)).addOnSuccessListener(this, new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        // Geofence removed
                    }
                }
                )
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Failed to remove location.", Toast.LENGTH_SHORT).show();
                    }
                }
                );
    }

    public void removeGeofenceID(List<String> geofence)
    {
        geofencingClient.removeGeofences(geofence).addOnSuccessListener(this, new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        // Geolocation removed

                    }
                }
                )
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Failed to remove location.", Toast.LENGTH_SHORT).show();
                    }
                }
                );
    }

    public void addGeofence(int geofenceID)
    {
        geofencingClient.addGeofences(getGeofencingRequest(),getGeofencePendingIntent(geofenceID)).addOnSuccessListener(this, new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        // Geolocation added
                    }
                }
                )
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Failed to add geofence. Make sure location services are enabled.", Toast.LENGTH_SHORT).show();
                    }
                }
                );
    }

    private PendingIntent getGeofencePendingIntent(int requestCode)
    {
        Context context = this;
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
 }
