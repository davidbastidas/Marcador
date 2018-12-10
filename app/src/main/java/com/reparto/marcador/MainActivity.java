package com.reparto.marcador;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText nicFoto;
    private Button tomarFoto, guardarFoto;
    private Toast succesMsg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        nicFoto = findViewById(R.id.nicFoto);
        tomarFoto = findViewById(R.id.tomarFoto);
        guardarFoto = findViewById(R.id.guardarFoto);

        final Toast fail = Toast.makeText(this, "Debe llenar el NIC o MEDIDOR.", Toast.LENGTH_SHORT);
        succesMsg = Toast.makeText(this, "LIMPIADO.", Toast.LENGTH_SHORT);

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nicFoto.getText().toString().equals("")){
                    takePhoto();
                } else {
                    fail.show();
                }
            }
        });

        guardarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
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

    private File photoFile;
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
            outputFileUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
        } else{
            outputFileUri = Uri.fromFile(photoFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 1888);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1888:
                if (resultCode == Activity.RESULT_OK){
                    Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    imageView.setImageBitmap(imageBitmap);
                    if(photoFile != null){
                        galleryAddPic(photoFile);
                    }
                }
                break;
        }
    }

    public void savePhoto(){
        succesMsg.show();
        resetLayout();
    }

    private void resetLayout() {
        imageView.setImageResource(R.mipmap.ic_launcher);
        nicFoto.setText("");
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        //creando el folder
        String folder_main = "MARCADOR";

        File folder = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = nicFoto.getText().toString() + "_" + timeStamp;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                folder      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
