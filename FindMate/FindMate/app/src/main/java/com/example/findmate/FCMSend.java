package com.example.findmate;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAAfrn2tqQ:APA91bE6kN9zcSTJ5pVa120lcBxIg4llJ1iTIoXVa0eP_kYA8P6D_l6BOeHpn_HgiEowiF5XpKFdfAaNqGZYUjxLxdhicTfTtE5-GhgnvBnBtB55dZjTP9Ldiuv7uH_D4kosS9jaABr_";

    public static void pushNotification(Context context, String token, String title, String message){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(context);

        try{
            JSONObject json = new JSONObject();
            json.put("to",token);
            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body",message);
            json.put("notification",notification);
            System.out.println("TOOOOOOOOOOOOKEN ------> "+token+" "+notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if(error instanceof ServerError && response!=null){
                        try{
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                            JSONObject obj = new JSONObject(res);
                        }catch (UnsupportedEncodingException e1){
                            e1.printStackTrace();
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }
                    }
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String ,String> params = new HashMap<>();
                    params.put("Content-Type","application/json; charset=utf-8");
                    params.put("Authorization",SERVER_KEY);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
            System.out.println("QUEUE ------------------->>>>>>>>>>>>>>>>> "+jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
