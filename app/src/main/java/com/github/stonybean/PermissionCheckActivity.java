package com.github.stonybean;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class PermissionCheckActivity extends AppCompatActivity {

    private static final String TAG = PermissionCheckActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_SYSTEM_ALERT_WINDOW = 1;

    private String[] requiredPermissions;
    private boolean showDeniedDialog;
    private String deniedDialogMessage;

    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] permissions = getIntent().getStringArrayExtra("PERMISSIONS");     // required permission list
        showDeniedDialog = getIntent().getBooleanExtra("SET_DIALOG", false);       // set the dialog
        deniedDialogMessage = getIntent().getStringExtra("DENY");                  // dialog message (denied)
        String windowDialogMessage = getIntent().getStringExtra("WINDOW");         // dialog message (window)

        // check permission(s) to request
        requiredPermissions = addRequiredPermissions(this, permissions);

        // if message(getIntent) is null, set the default message string
        if (TextUtils.isEmpty(deniedDialogMessage)) {
            deniedDialogMessage = getString(R.string.denied_dialog_message);
        }

        // if message(getIntent) is null, set the default message string
        if (TextUtils.isEmpty(windowDialogMessage)) {
            windowDialogMessage = getString(R.string.window_dialog_message);
        }

        // check window permission
        if (!hasWindowPermission()) {
            requestWindowPermission(windowDialogMessage);
        } else {
            if (requiredPermissions.length > 0) {
                setDeniedDialog(showDeniedDialog, deniedDialogMessage);
                ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE);
            } else {
                finish();
            }
        }
    }

    public String[] addRequiredPermissions(Context context, String... permissions) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "permission was granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    return;
                } else {
                    Log.i(TAG, "permission was denied");
                    if (dialogBuilder != null) {
                        dialogBuilder.show();
                    } else {
                        finish();
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
                    ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE);
                } else {
                    finish();
                }
                break;

            case REQUEST_CODE:
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
                                        startActivityForResult(intent, REQUEST_CODE);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivityForResult(intent, REQUEST_CODE);
                                    }
                                }
                            })
                    .setNegativeButton(R.string.denied_dialog_negative_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 다이얼로그를 취소한다
                                    dialog.cancel();
                                    finish();
                                }
                            })
                    .setMessage(deniedDialogMessage);
        }
    }
}