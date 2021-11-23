package com.example.mycanvas;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestService {
    public static final String QUERY_FOR_STOCK_DATA = "https://finnhub.io/api/v1/quote?symbol=";
    public static final String QUERY_API_TOKEN = "&token=c5tma92ad3ifck7diqg0";
    public static final String QUERY_FOR_FINANCIAL_SYMBOLS= "https://finnhub.io/api/v1/stock/financials?symbol=";
    public static final String QUERY_FOR_FINANCIAL_FORMAT ="&statement=";
    public static final String QUERY_FOR_FINANCIAL_DURATION ="&freq=";
    public static final String QUERY_SANDBOX_TOKEN = "&token=sandbox_c5tma92ad3ifck7diqgg";
    Context context;


    public RequestService(Context context) {
        this.context = context;

    }
    public void getStockQuote(String quote, TextView H , TextView L , TextView O , TextView P , TextView C , TextView T , TextView D , TextView DP){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = QUERY_FOR_STOCK_DATA+quote+QUERY_API_TOKEN;
        JSONObject jsonObject = new JSONObject();
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{


                    JSONObject jo = new JSONObject(response.toString());
                    String current = jo.getString("c");
                    String changes = jo.getString("d");
                    String percentage = jo.getString("dp");
                    String high = jo.getString("h");
                    String low = jo.getString("l");
                    String open = jo.getString("o");
                    String previous = jo.getString("pc");
                    String tick = jo.getString("t");

                    C.setText(current);
                    D.setText( changes);
                    DP.setText( percentage);
                    H.setText(high);
                    L.setText( low);
                    O.setText(open);
                    P.setText(previous);
                    T.setText(tick);

                    Toast.makeText(context, "success" ,Toast.LENGTH_SHORT).show();
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context,"something wrong",Toast.LENGTH_SHORT).show();
            }
        }


        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
