package com.moutamid.foregroundexample;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.moutamid.foregroundexample.App.CHANNEL_ID;


public class ExampleService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    String title = "Running";
    Notification notification1;

    int count = 1;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

//            if (title.equals("12-07-2021")){
//                stopSelf();
//                return;
//            }

            title = getNDate();
            builder.setContentTitle(title);
            notification1 = builder.build();

            notificationManagerCompat.notify(1, notification1);

            handler.postDelayed(runnable, 2000);

        }
    };
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManagerCompat;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setContentTitle(title);
        builder.setContentText(input);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentIntent(pendingIntent);
        builder.setOnlyAlertOnce(true);
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(input)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .build();
        notification1 = builder.build();

        startForeground(1, notification1);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("value").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean value = snapshot.getValue(Boolean.class);

                if (value) {

                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                    notificationHelper.sendHighPriorityNotification(
                            "New notification Arrived",
                            "Sample body text arrived!", MainActivity.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        handler.postDelayed(runnable, 3000);

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    public String getNDate() {

        try {

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Calendar c = Calendar.getInstance();

            c.setTime(sdf.parse(sdf.format(date)));
            c.add(Calendar.DATE, count++);
            return sdf.format(c.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
