package com.example.automatedfoodorderingsystem.userPart;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.automatedfoodorderingsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    FirebaseUser mUser;
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannerView = findViewById(R.id.scannerView);
        ///// to ask for camera permissions at runtime //////
        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        //////// Back button /////////

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannerActivity.this, UserDashboardActivity.class));
                finish();
            }
        });
        Toast.makeText(this, "Please Scan the QR code", Toast.LENGTH_SHORT).show();


        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public void handleResult(Result result) {

        String id = result.getText();
        /// to send restaurant id into the usersDatabase
        FirebaseDatabase.getInstance().getReference().child("UsersDatabase").child(mUser.getUid()).child("restaurantId").setValue(id);

        Intent i = new Intent(ScannerActivity.this, MenuActivity.class);
        i.putExtra("id", id);
        startActivity(i);

        finish();
//        onBackPressed();

    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(ScannerActivity.this, UserDashboardActivity.class));
//    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}