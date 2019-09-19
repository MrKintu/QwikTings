/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.BodyActivities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import com.bdhobare.mpesa.Mode;
import com.bdhobare.mpesa.Mpesa;
import com.bdhobare.mpesa.interfaces.AuthListener;
import com.bdhobare.mpesa.interfaces.MpesaListener;
import com.bdhobare.mpesa.models.STKPush;
import com.bdhobare.mpesa.utils.Pair;

import com.example.declan.qwiktings.R;

public class PayActivity extends AppCompatActivity implements AuthListener, MpesaListener
{
    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String CONSUMER_KEY = "cGCHkjt8r8QAfo8MhqlFoy1n3qjWF5A4";
    public static final String CONSUMER_SECRET = "jugGZjC7bJWANcjq";
    public static final String CALLBACK_URL = "http://mpesa-requestbin.herokuapp.com/152n2261";
    public static final String  NOTIFICATION = "PushNotification";
    public static final String SHARED_PREFERENCES = "com.bdhobare.mpesa_android_sdk";

    private Button pay;
    private ProgressDialog dialog;
    private EditText phone;
    private TextView amount;
    private String price;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        pay = findViewById(R.id.pay);
        phone = findViewById(R.id.phone);
        amount = findViewById(R.id.amount);
        Mpesa.with(this, CONSUMER_KEY, CONSUMER_SECRET, Mode.SANDBOX);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Processing");
        dialog.setIndeterminate(true);

        //Get Cart Price
        if(getIntent() != null) {
            price = getIntent().getStringExtra("Price");
        }

        if(!price.isEmpty())
        {
            amount.setText(price);

            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String num = phone.getText().toString();
                    int amount = 10;
                    if (num.isEmpty()){
                        phone.setError("Enter phone.");
                        return;
                    }
                    payMethod(num, amount);
                }
            });

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(NOTIFICATION)) {
                        String title = intent.getStringExtra("title");
                        String message = intent.getStringExtra("message");
                        int code = intent.getIntExtra("code", 0);
                        showDialog(title, message, code);

                    }
                }
            };
        }
    }

    private void payMethod(String phone, int amount)
    {
        dialog.show();
        STKPush.Builder builder = new STKPush.Builder(BUSINESS_SHORT_CODE, PASSKEY, amount, BUSINESS_SHORT_CODE, phone);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String token = sharedPreferences.getString("InstanceID", "");

        builder.setFirebaseRegID(token);
        STKPush push = builder.build();

        Mpesa.getInstance().pay(this, push);

    }

    private void showDialog(String title, String message,int code)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .titleGravity(GravityEnum.CENTER)
                .customView(R.layout.success_dialog, true)
                .positiveText("OK")
                .cancelable(false)
                .widgetColorRes(R.color.colorPrimary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();
                        finish();
                    }
                }).build();
        View view = dialog.getCustomView();
        TextView messageText = view.findViewById(R.id.message);
        ImageView imageView = view.findViewById(R.id.success);
        if (code != 0){
            imageView.setVisibility(View.GONE);
        }
        messageText.setText(message);
        dialog.show();
    }

    @Override
    public void onAuthError(Pair<Integer, String> result) {
        Log.e("Error", result.message);
    }

    @Override
    public void onAuthSuccess() {
        //TODO make payment
        pay.setEnabled(true);
    }

    @Override
    public void onMpesaError(Pair<Integer, String> result) {
        dialog.hide();
        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMpesaSuccess(String MerchantRequestID, String CheckoutRequestID, String CustomerMessage)
    {
        dialog.hide();
        Toast.makeText(this, CustomerMessage, Toast.LENGTH_SHORT).show();
    }


}
