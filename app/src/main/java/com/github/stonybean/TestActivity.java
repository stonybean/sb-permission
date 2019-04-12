package com.github.stonybean;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by stonybean on 2019. 4. 11.
 */
public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button button;
    private ImageView imageView;
    private PermissionListener permissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setOnClickListener();
        setPermissionListener();

        PermissionManager permissionManager = new PermissionManager(this, TAG);
        permissionManager
//                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(permissionListener)
                .setDeniedDialog(true)
//                .setDeniedDialogMessage("권한 거부시 나타나는 메시지입니다.")
                .checkPermissions();
    }

    private void initView() {
        button = findViewById(R.id.cameraBtn);
        imageView = findViewById(R.id.cameraImg);
    }

    private void setOnClickListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = null;
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
            }
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void setPermissionListener() {
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(ArrayList<String> grantedPermissions) {
                Log.d(TAG, "onPermissionGranted");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Log.d(TAG, "onPermissionDenied");
                for (String deniedPermission : deniedPermissions) {
                    if (deniedPermission.equals(Manifest.permission.CAMERA)) {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "카메라를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                Log.d(TAG, "거부 권한 :\n" + deniedPermissions.toString());
            }
        };
    }
}