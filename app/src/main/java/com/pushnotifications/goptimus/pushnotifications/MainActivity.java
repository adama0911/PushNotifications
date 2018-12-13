package com.pushnotifications.goptimus.pushnotifications;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        //button pour se suscrir a un sujet . pour recevoir les broadcast sur le sujet
        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Subscribing to weather topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("weather")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]
            }
        });

        // button pour demander un token unique pour identifier l'appareil
        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewNotificationToken();
            }
        });

    }

    // Demander un token unique pour indentifier l'apareil
    public String getNewNotificationToken(){
        final String[] myToken = {""};
        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed=================>",  task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        myToken[0] = token;
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, "====================>"+msg);
                        Toast.makeText(MainActivity.this, "my token ==> "+ msg, Toast.LENGTH_SHORT).show();
                        Map<String, String> postMap = new HashMap();
                        postMap.put("token", token);

                        String url = "http://192.168.1.112/app_mounir_backend/notification.php";
                        handleHttp(url, postMap);
                    }
                });
        return  myToken[0];
    }

    // function  ajax avec la librery valley
    public void  handleHttp(final String requestUrl, final Map<String, String> postMap) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {

            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", "=============>" + response); //the response contains the result from the server, a json string or any other object returned by your server;
                /*try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.d("indexHistory","----------------"+indexHistory+"------------------");
                    Log.d("lastID","----------------"+lastID+"------------------");
                    lastID = (jsonArray.getJSONObject(jsonArray.length() -1)).getString("id");
                    Log.d("lastID","----------------"+lastID+"------------------");

                    for (int i = indexHistory, max = indexHistory + Maxrow, j= 0 ; (j < jsonArray.length() ) && (i < max); i++, j++){
                        JSONObject obj = jsonArray.getJSONObject(j);

                        indexHistory = indexHistory + 1;
                        history.put(obj);
                        String traitement = obj.getString("traitement");
                        String infoclient = obj.getString("infoclient");
                        String montant = obj.getString("montant");
                        String dateoperation = obj.getString("dateoperation");

                        String[] splitStr = dateoperation.split("\\s+");

                        if(targetDate.compareTo(splitStr[0]) != 0) {
                            longrowAdapter(splitStr[0]);
                            targetDate = splitStr[0];
                        }
                        rowAdapter(traitement,infoclient,montant,splitStr[1]);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //..... Add as many key value pairs in the map as necessary for your request
                //Log.d("postMap",(postMap.toString()));
                return postMap;
            }
        };
        //make the request to your server as indicated in your request url

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
}
