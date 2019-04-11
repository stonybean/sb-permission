package com.github.stonybean;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.github.stonybean.sbpermission.R;

import java.util.ArrayList;

/**
 * Created by stonybean on 2019. 4. 11.
 */
@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
public class PermissionCheckActivity extends AppCompatActivity {
    private static final String TAG = PermissionCheckActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION = 0;
    private static final int REQUEST_SYSTEM_ALERT_WINDOW = 1;
    private static final int REQUEST_SYSTEM_SETTINGS = 2;

    private PermissionListenerList permissionListenerList = PermissionListenerList.getInstance();
    private PermissionListener permissionListener;
    private String[] requiredPermissions;
    private String[] dangerousPermissions = {
            /* CALENDAR */
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            /* CAMERA */
            Manifest.permission.CAMERA,
            /* CONTACTS */
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            /* LOCATION */
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            /* MICROPHONE */
            Manifest.permission.RECORD_AUDIO,
            /* PHONE */
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            /* SENSORS */
            Manifest.permission.BODY_SENSORS,
            /* SMS */
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            /* STORAGE */
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean showDeniedDialog;
    private String deniedDialogMessage;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        permissionListener = permissionListenerList.get(getIntent().getStringExtra("LISTENER")); // permission listener
        Log.d(TAG, "permissionListener : " + permissionListener);
        String[] permissions = getIntent().getStringArrayExtra("PERMISSIONS");             // required permission list
        showDeniedDialog = getIntent().getBooleanExtra("SET_DIALOG", false);    // set the dialog
        deniedDialogMessage = getIntent().getStringExtra("DENIAL");                        // dialog message (denial)
        String windowDialogMessage = getIntent().getStringExtra("WINDOW");                 // dialog message (window)

        // check permission(s) to request
        if (permissions == null) {
            requiredPermissions = addRequiredPermissions(this, dangerousPermissions);
        } else {
            requiredPermissions = addRequiredPermissions(this, permissions);
        }

        // if message(getIntent) is null, set the default message string
        if (TextUtils.isEmpty(deniedDialogMessage)) {
            deniedDialogMessage = getString(R.string.denied_dialog_message);
        }

        // if message(getIntent) is null, set the default message string
        if (TextUtils.isEmpty(windowDialogMessage)) {
            windowDialogMessage = getString(R.string.window_dialog_message);
        }

        // check window (overlay) permission
        if (!hasWindowPermission()) {
            requestWindowPermission(windowDialogMessage);
        } else {
            if (requiredPermissions.length > 0) {
                Log.d(TAG, "111");
                setDeniedDialog(showDeniedDialog, deniedDialogMessage);
                ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSION);
            } else {
                Log.d(TAG, "222");
                finish();
            }
        }
    }

    @NonNull
    private String[] addRequiredPermissions(Context context, String... permissions) {
        ArrayList<String> requiredPermissions = new ArrayList<>();

        if (context == null) return requiredPermissions.toArray(new String[1]);

        // Determine whether or not to add the permission by the number of parameters(permissions to request)
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
                Log.d(TAG, "permission = " + permission);
            }
        }
        return requiredPermissions.toArray(new String[requiredPermissions.size()]);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasWindowPermission() {
        return Settings.canDrawOverlays(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestWindowPermission(String message) {
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.window_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_SYSTEM_ALERT_WINDOW);
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
//        permissionListenerList.remove(permissionListener);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    int grantedNum = 0;
                    for (int grantResult : grantResults) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            grantedNum += 1;
                        }
                    }

//                    ArrayList<String> grantedList = new ArrayList();
//                    for (String permission : permissions) {
//                        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//                            Log.d(TAG, "granted permission = " + permission);
//                            grantedList.add(permission);
//                        }
//                    }
//                    permissionListener.onPermissionGranted(grantedList);

                    if (grantedNum == grantResults.length) {
                        // (all) permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Log.i(TAG, "permission was granted");
                        finish();
                    } else {
                        Log.i(TAG, "permission was denied");
                        ArrayList<String> deniedList = new ArrayList();
                        for (String permission : permissions) {
                            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                                Log.d(TAG, "granted permission = " + permission);
                                deniedList.add(permission);
                            }
                        }

                        permissionListener.onPermissionDenied(deniedList); // add denied permission list
                        int deniedNum = 0;
                        for (String permission : permissions) {
                            // Don't ask again
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                                deniedNum += 1;
                            }
                        }

                        if (deniedNum == permissions.length) {
                            finish();
                            return;
                        }

                        if (dialogBuilder != null) {
                            dialogBuilder.show();
                        } else {
                            finish();
                        }
                    }
                }
                break;
            }

            default:
                Log.e(TAG, "Error..");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SYSTEM_ALERT_WINDOW:
                if (requiredPermissions.length > 0) {
                    setDeniedDialog(showDeniedDialog, deniedDialogMessage);
                    ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSION);
                } else {
                    finish();
                }
                break;

            case REQUEST_SYSTEM_SETTINGS:
                // from System Settings..
                finish();
                break;
        }
    }

    public void setDeniedDialog(boolean showDeniedDialog, String deniedDialogMessage) {
        if (showDeniedDialog) {
            dialogBuilder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
            dialogBuilder.setCancelable(false)
                    .setPositiveButton(R.string.denied_dialog_positive_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivityForResult(intent, REQUEST_SYSTEM_SETTINGS);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivityForResult(intent, REQUEST_SYSTEM_SETTINGS);
                                    }
                                }
                            })
                    .setNegativeButton(R.string.denied_dialog_negative_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            })
                    .setMessage(deniedDialogMessage);
        }
    }
}