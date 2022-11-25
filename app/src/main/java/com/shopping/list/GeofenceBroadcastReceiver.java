package com.shopping.list;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.constraintlayout.widget.StateSet.TAG;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public String geofenceID;                                                           //Geofence id
    private static final String CHANNEL_ID = "Geofence";

    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofence that were triggered. A single event can trigger
            // multiple geofence.
            List<Geofence> triggeringGeofence = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofence);

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails, context);
            //Toast.makeText(context, "Pop" + geofenceTransitionDetails, Toast.LENGTH_SHORT).show();
            Log.i(TAG, geofenceTransitionDetails);

        }

        else {
            // Log the error.
            Log.e(TAG, context.getString(R.string.geofence_transition_invalid_type));
        }
    }

    private void sendNotification(String message, Context context){
        Log.i(TAG, "sendNotification: " + message );
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("geofenceID", geofenceID);                      //Adding shoplist id
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(Integer.parseInt(geofenceID), createNotification(message, pendingIntent, context));

    }

    private Notification createNotification(String message, PendingIntent notificationPendingIntent, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setColor(Color.GREEN)
                .setContentTitle("Shop Notification")
                .setContentText("Click to open shopping list")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    //Method that Received the transition type and triggering geofence
    private String getGeofenceTransitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofence) {
        //Declaration and Initialisation
        ArrayList<String> triggeringGeofenceList = new ArrayList<>();                  //List of triggering geofence
        for ( Geofence geofence : triggeringGeofence ) {                               // get the ID of each geofence triggered
            triggeringGeofenceList.add( geofence.getRequestId() );
            geofenceID = geofence.getRequestId();
        }

        String status = null;                                                       //Status of geofence ie entering
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            status = "Entering ";
        }
        return status + TextUtils.join( ", ", triggeringGeofenceList);             //Returning geofence status
    }

    //Method to get geofence error that may occur
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
