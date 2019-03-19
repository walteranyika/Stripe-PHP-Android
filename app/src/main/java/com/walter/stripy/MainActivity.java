package com.walter.stripy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    CardInputWidget mCardInputWidget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCardInputWidget = findViewById(R.id.card_input_widget);
    }

    public void pay(View view) {
        Card cardToSave = mCardInputWidget.getCard();
        if (cardToSave==null){
            Toast.makeText(this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Stripe stripe = new Stripe(this, "pk_test_MzdQvKBSHJ8aV7AMVGVMNtYp");
        stripe.createToken(
                cardToSave,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                        Log.d("STRIPE_TOKEN", "onSuccess: "+token.toString());
                        Log.d("STRIPE_TOKEN", "onSuccess: "+token.getId());
                        sendTokenToServer(token.getId());

                    }
                    public void onError(Exception error) {
                        // Show localized error message
                        Toast.makeText(MainActivity.this, "Error While processing. Please Try again", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        Log.e("STRIPE_ERROR", "onError: "+error.getLocalizedMessage());
                    }
                }
        );

    }

    private void sendTokenToServer(String id) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("stripe_token", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, "", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("STRIPE_SERVER", "onResponse: "+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("STRIPE_SERVER_ERROR", "onErrorResponse: "+error.getMessage());
            }
        });

        //PHP Code
        /*\Stripe\Stripe::setApiKey("sk_test_22XWvG5s4hRY4mYuNVlsdT8p");

            // Token is created using Checkout or Elements!
            // Get the payment token ID submitted by the form:
            $token = $_POST['stripeToken'];
            $charge = \Stripe\Charge::create([
                'amount' => 999,
                'currency' => 'usd',
                'description' => 'Example charge',
                'source' => $token,
            ]);*/
    }
}
