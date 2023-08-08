package com.example.raquelmossassignmentone;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.raquelmossassignmentone.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String phoneNumber = "";
    private ActivityMainBinding mActivityMain;
    private String PHONE_NUMBER_KEY = "phone_number";

    // TODO why the hell doesn't intent work
    // TODO very first installation on a fresh phone - view doesn't work
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMain.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            Log.i("INTENT!", intent.toString());

            if (Objects.equals(intent.getAction(), "android.intent.action.DIAL")) {
                Uri data = intent.getData();
                if (data != null) {
                    phoneNumber = data.getSchemeSpecificPart();
                    setPhoneNumberDisplay();
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            initializeView();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }

        if (savedInstanceState != null && savedInstanceState.getString(PHONE_NUMBER_KEY) != null) {
            phoneNumber = savedInstanceState.getString(PHONE_NUMBER_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(PHONE_NUMBER_KEY, phoneNumber);
        super.onSaveInstanceState(outState);
    }

    private void initializeNumeralButtons() {
        Button[] buttons = new Button[]{
                mActivityMain.zero,
                mActivityMain.one,
                mActivityMain.two,
                mActivityMain.three,
                mActivityMain.four,
                mActivityMain.five,
                mActivityMain.six,
                mActivityMain.seven,
                mActivityMain.eight,
                mActivityMain.nine,
                mActivityMain.star,
                mActivityMain.hash
        };

        for(Button button: buttons) {
            button.setOnClickListener(v -> addNumeral(button));
        }
    }

    private void initializeSendButton() {
        mActivityMain.send.setOnClickListener(v -> makePhoneCall());
    }

    private void initializeBackButton() {
        mActivityMain.back.setOnClickListener(v -> removeNumeral());
    }

    private void makePhoneCall() {
        if (phoneNumber.length() == 0) {
            return;
        }

        String uri = "tel:" + phoneNumber;

        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
        startActivity(dialIntent);
    }

    private void removeNumeral() {
        if (phoneNumber.length() == 0) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(phoneNumber);
        stringBuilder.deleteCharAt(phoneNumber.length() - 1);
        phoneNumber = stringBuilder.toString();
        setPhoneNumberDisplay();

    }

    private void setPhoneNumberDisplay() {
        TextView textView = findViewById(R.id.num_input);
        textView.setText(phoneNumber);
    }

    private void addNumeral(Button button) {
        phoneNumber = phoneNumber + button.getText();
        setPhoneNumberDisplay();
    }

    private void initializeView() {
        initializeNumeralButtons();
        initializeBackButton();
        initializeSendButton();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.i("is_granted", isGranted.toString());
                if (isGranted) {
                    initializeView();
                } else {
                    setContentView(R.layout.permission_denied_view);
                }
            });
}