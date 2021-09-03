package com.moutamid.foregroundexample;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.foregroundexample.other.Restarter;
import com.moutamid.foregroundexample.other.YourService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;

    private final String serverKey = "key=" + "AAAAAwadxXg:APA91bHStEd7gHcNbyzKFsD8T_dhHpuX4be-02qQWadgP987CnNmZq4osNKFLXrGLxID9BZPJ-5x2_KDniBAzGMzLDHbrYbk-op8cU1gzcwmvCKc-Xz5dZ3mpkef-DsWMvrloQfKnsO-";

    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String contentType = "application/json";

    private EditText editTextInput;

    Intent mServiceIntent;
    private YourService mYourService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/Enter_your_topic_name");

        editTextInput = findViewById(R.id.edit_text_input);

//        startInitService();

//        overrideFonts(getApplicationContext(), editTextInput.getRootView());

        findViewById(R.id.notifyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(editTextInput.getText().toString())) {
                    //topic has to match what the receiver subscribed to
                    String topic = "/topics/Enter_your_topic_name";

                    JSONObject notification = new JSONObject();
                    JSONObject notifcationBody = new JSONObject();

                    try {

                        notifcationBody.put("title", "Enter_title");
                        //Enter your notification message
                        notifcationBody.put("message", editTextInput.getText().toString());
                        notification.put("to", topic);
                        notification.put("data", notifcationBody);
                        Log.e(TAG, "try");

                    } catch (JSONException e) {
                        Log.e(TAG, "onClick: exception: " + e.getMessage().toString());
                    }

                    sendNotification(notification);
                }
            }
        });

    }

    private RequestQueue requestQueue() {
        return Volley.newRequestQueue(this.getApplicationContext());
    }

    private void sendNotification(JSONObject notification) {
        Log.e("TAG", "sendNotification");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                FCM_API, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                editTextInput.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", "onErrorResponse: Didn't work");
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);

//                val params = HashMap < String, String>()
//                params[""] = serverKey
//                params[""] = contentType
                return params;

//                return super.getHeaders();
            }
        };

        requestQueue().add(jsonObjectRequest);
    }

    private void startInitService() {
        mYourService = new YourService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }
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