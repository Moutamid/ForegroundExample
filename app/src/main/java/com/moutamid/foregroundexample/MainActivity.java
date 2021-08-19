package com.moutamid.foregroundexample;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.moutamid.foregroundexample.other.Restarter;
import com.moutamid.foregroundexample.other.YourService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;

    private EditText editTextInput;

    Intent mServiceIntent;
    private YourService mYourService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.edit_text_input);

        mYourService = new YourService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }

        // overrideFonts(getApplicationContext(), editTextInput.getRootView());
    }

    private void askToDisableDozeMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "askToDisableDozeMode: ");
                Intent intent = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Doze mode is active", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }


    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    public void startService(View v) {
        String input = editTextInput.getText().toString();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v) {
//        Intent serviceIntent = new Intent(this, ExampleService.class);
//        stopService(serviceIntent);
        askToDisableDozeMode();
    }

    /**
     *------THIS METHOD IS USED TO CHANGE FONT OF ALL VIEWS OF A SCREEN------
     */
//    private void overrideFonts(Context context, View view) {
//        Typeface createFromAsset = Typeface.createFromAsset(context.getAssets(), "about.otf");
//        try {
//            if (view instanceof ViewGroup) {
//                ViewGroup viewGroup = (ViewGroup) view;
//                for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                    overrideFonts(context, viewGroup.getChildAt(i));
//                }
//            } else if (view instanceof TextView) {
//                ((TextView) view).setTypeface(createFromAsset);
//            }
//        } catch (Exception unused) {
//        }
//    }

}