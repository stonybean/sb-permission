package com.github.stonybean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.Log;

/**
 * Created by stonybean on 2019. 4. 11.
 */

public class PermissionManager {
    private static final String TAG = PermissionManager.class.getSimpleName();
    private Context context;
    private String listener;
    private String[] permissions;
    private boolean windowPermission;
    private PermissionListenerList permissionListenerList = PermissionListenerList.getInstance();
    private boolean showDeniedDialog;
    private CharSequence deniedDialogMessage;
    private CharSequence windowDialogMessage;

    public PermissionManager(Context context, String listener) {
        this.context = context;
        this.listener = listener;
    }

    // Essential (check permissions)
    public void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "Build version is low level");
            return;
        }

        Intent intent = new Intent(context, PermissionCheckActivity.class);
        intent.putExtra("LISTENER", listener);
        intent.putExtra("WINDOW_PERMISSION", windowPermission);
        intent.putExtra("WINDOW_DIALOG_MESSAGE", windowDialogMessage);
        intent.putExtra("PERMISSIONS", permissions);
        intent.putExtra("SET_DIALOG", showDeniedDialog);
        intent.putExtra("DENIAL", deniedDialogMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Essential
    public PermissionManager setPermissionListener(PermissionListener permissionListener) {
        permissionListenerList.put(listener, permissionListener);
        Log.d(TAG, "permissionListener = " + permissionListenerList.get(listener));
        return this;
    }

    // Optional
    public PermissionManager setWindowPermission(boolean windowPermission) {
        this.windowPermission = windowPermission;
        return this;
    }

    // Optional
    public PermissionManager setWindowDialogMessage(CharSequence windowDialogMessage){
        this.windowDialogMessage = windowDialogMessage;
        return this;
    }

    // Optional
    public PermissionManager setWindowDialogMessage(@StringRes int windowDialogMessage){
        this.windowDialogMessage = getText(windowDialogMessage);
        return this;
    }

    // Optional
    public PermissionManager setPermissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    // Optional
    public PermissionManager setDeniedDialog(boolean showDeniedDialog) {
        this.showDeniedDialog = showDeniedDialog;
        return this;
    }

    // Optional
    public PermissionManager setDeniedDialogMessage(CharSequence deniedDialogMessage) {
        this.deniedDialogMessage = deniedDialogMessage;
        return this;
    }

    // Optional
    public PermissionManager setDeniedDialogMessage(@StringRes int deniedDialogMessage) {
        this.deniedDialogMessage = getText(deniedDialogMessage);
        return this;
    }

    @SuppressLint("ResourceType")
    private CharSequence getText(@StringRes int stringRes) {
        if (stringRes <= 0) {
            throw new IllegalArgumentException("Invalid String resource Id");
        }
        return context.getText(stringRes);
    }
}
