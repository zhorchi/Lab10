package com.gshalashov.lab10;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;

public class MainActivity extends AppCompatActivity {
    SurfaceView sf;
    Button takePicButton;
    Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 2);
        sf = findViewById(R.id.surface_view);
        takePicButton = findViewById(R.id.take_picture);
    }
    public void openCamera(View v) {
        camera = Camera.open();
        sf.setVisibility(View.VISIBLE);
        takePicButton.setVisibility(View.VISIBLE);
        SurfaceHolder sh = sf.getHolder();
        try {
            camera.setPreviewDisplay(sh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void takePicture(View v){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                saveImageToInternalStorage(data);
                camera.startPreview(); // Restart preview
            }
        });
    }

    public void closeCamera(View view){
        sf.setVisibility(View.GONE);
        takePicButton.setVisibility(View.GONE);
        camera.release();
    }

    private void saveImageToInternalStorage(byte[] data) {
        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, filename);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(data);
            fos.close();

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}