package com.shopping.list.broadcast;

import android.app.Notification;

import android.app.PendingIntent;

import android.app.TaskStackBuilder;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.graphics.Color;

import android.text.TextUtils;

import android.util.Log;

import com.shopping.list.model.Location;

import com.shopping.list.MainActivity;

import com.shopping.list.database.DataBase;

import com.shopping.list.R;

import com.google.android.gms.location.Geofence;

import com.google.android.gms.location.GeofenceStatusCodes;

import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;

import java.util.List;

import androidx.core.app.NotificationCompat;

import androidx.core.app.NotificationManagerCompat;

import static androidx.constraintlayout.widget.StateSet.TAG;

 public class GeofenceBroadcastReceiver extends BroadcastReceiver
 {
    private DataBase DB;
    private static final String CHANNEL_ID="Geofence";

     private Notification createNotification(String message,PendingIntent notificationPendingIntent,Context context)
     {
         NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANNEL_ID).setSmallIcon(R.drawable.notification_icon).setColor(Color.GREEN).setContentTitle("You've reached your shopping list destination!").setContentText("click to open " + message + " list").setContentIntent(notificationPendingIntent).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
         return builder.build();
     }

     private void sendNotification(Context context,String geofenceID,String shopName)
     {
        Log.i(TAG,"sendNotification: GID = " +geofenceID);
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("shopID",geofenceID);
        intent.putExtra("notification","external");
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent=stackBuilder.getPendingIntent(Integer.parseInt(geofenceID),PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(context);
        notificationManager.notify(Integer.parseInt(geofenceID),createNotification(shopName,pendingIntent,context));
     }

     private static String getErrorString(int errorCode)
     {
         switch(errorCode)
         {
             case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                 return "Geolocation not available";

             case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                 return "Too many Geolocation";

             case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                 return "Too many pending intents";

             default:
                 return "Unknown error.";
         }
     }

     public void onReceive(Context text, Intent intent)
     {
         DB=new DataBase(text);
         GeofencingEvent geofencingEvent=GeofencingEvent.fromIntent(intent);
         if(geofencingEvent.hasError())
         {
             String errorMessage=getErrorString(geofencingEvent.getErrorCode());
             Log.e(TAG, errorMessage);
             return;
         }
         int geofenceTransition=geofencingEvent.getGeofenceTransition();
         if(geofenceTransition==Geofence.GEOFENCE_TRANSITION_ENTER||geofenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT)
         {
             List<Geofence> triggeringGeofence=geofencingEvent.getTriggeringGeofences();
             String geofenceTransitionDetails=getGeofenceTransitionDetails(geofenceTransition, triggeringGeofence);
             for(Geofence geofence:triggeringGeofence)
             {
                 try
                 {
                     String geofenceID=geofence.getRequestId();
                     Location location=DB.getLocation(Integer.parseInt(geofenceID));
                     String shopName=DB.getShopList(location.getShoppingListID()).getName();
                     boolean Geofence=location.isGeofence();
                     if(!shopName.isEmpty()&&Geofence)
                     {
                         sendNotification(text,geofenceID,shopName);
                     }
                 }
                 catch(Exception e)
                 {
                     e.printStackTrace();
                 }
             }
             Log.i(TAG,geofenceTransitionDetails);
         }
         else
         {
             Log.e(TAG,text.getString(R.string.geofence_transition_invalid_type));
         }
     }

     private String getGeofenceTransitionDetails(int geoFenceTransition,List<Geofence> triggeringGeofence)
     {
        ArrayList<String> triggeringGeofenceList=new ArrayList<>();
        for(Geofence geofence:triggeringGeofence)
        {
            triggeringGeofenceList.add(geofence.getRequestId());
        }
        String status=null;
        if(geoFenceTransition==Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            status="Entering ";
        }
        return status + TextUtils.join(", ", triggeringGeofenceList);
     }
 }
