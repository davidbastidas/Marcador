package com.reparto.marcador;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText nicFoto;
    Button tomarFoto, guardarFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        nicFoto = findViewById(R.id.nicFoto);
        tomarFoto = findViewById(R.id.tomarFoto);
        guardarFoto = findViewById(R.id.guardarFoto);

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        final Toast fail = Toast.makeText(this, "Debe llenar el nombre.", Toast.LENGTH_SHORT);
        guardarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nicFoto.getText().toString().equals("")){
                    savePhoto();
                } else {
                    fail.show();
                }
            }
        });

        checkPermission();
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //no es version 6 api 23
        } else {
            int permsRequestCode = 100;
            String[] perms = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA
            };
            int accessInternetPermission = checkSelfPermission(Manifest.permission.INTERNET);
            int accessWiteExternalPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int accessFinePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);

            if (accessInternetPermission == PackageManager.PERMISSION_GRANTED &&
                    accessWiteExternalPermission == PackageManager.PERMISSION_GRANTED &&
                    accessFinePermission == PackageManager.PERMISSION_GRANTED &&
                    cameraPermission == PackageManager.PERMISSION_GRANTED) {
                //se realiza metodo si es necesario...
            } else {
                requestPermissions(perms, permsRequestCode);
            }
        }

        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                // accion o metodo realizar
                break;
        }
    }

    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1888);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1888:
                System.out.println("recibe");
                if (resultCode == Activity.RESULT_OK){
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    System.out.println("foto soporte");
                    imageView.setImageBitmap(imageBitmap);
                }
                break;
        }
    }

    public void savePhoto(){
        try {
            //creando el folder
            String folder_main = "MARCADOR";

            File folder = new File(Environment.getExternalStorageDirectory(), folder_main);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            Bitmap _bitmapScaled = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            _bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            //you can create a new file name "test.jpg" in sdcard folder.
            File f = new File(
                    Environment.getExternalStorageDirectory()
                    + File.separator
                    + folder_main
                    + File.separator
                    + nicFoto.getText().toString() + ".jpg");

            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            // remember close de FileOutput
            fo.close();
            galleryAddPic(f);
        } catch (Exception e) {
            System.out.println("Error guardando la foto" + e);
        }
    }

    private void galleryAddPic(File f) {
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{f.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        System.out.println("Scaneado");
                    }
                });
    }
}
