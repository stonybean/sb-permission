package com.github.stonybean;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.Log;

/**
 * Created by Joo on 2018. 3. 13.
 */

public class PermissionManager {

    private static final String TAG = PermissionManager.class.getSimpleName();
    private Context context;
    private String[] permissions;
    private boolean showDeniedDialog;
    private CharSequence deniedDialogMessage;
    private CharSequence windowDialogMessage;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "Build version is low level");
            return;
        }

        Intent intent = new Intent(context, PermissionCheckActivity.class);
        intent.putExtra("PERMISSIONS", permissions);
        intent.putExtra("SET_DIALOG", showDeniedDialog);
        intent.putExtra("DENY", deniedDialogMessage);
        intent.putExtra("WINDOW", windowDialogMessage);

        context.startActivity(intent);
    }

    public PermissionManager setPermissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionManager setDeniedDialog(boolean showDeniedDialog) {
        this.showDeniedDialog = showDeniedDialog;
        return this;
    }

    public PermissionManager setDeniedDialogMessage(CharSequence deniedDialogMessage) {
        this.deniedDialogMessage = deniedDialogMessage;
        return this;
    }

    public PermissionManager setDeniedDialogMessage(@StringRes int deniedDialogMessage) {
        this.deniedDialogMessage = getText(deniedDialogMessage);
        return this;
    }

    public PermissionManager setWindowDialogMessage(CharSequence windowDialogMessage){
        this.windowDialogMessage = windowDialogMessage;
        return this;
    }

    public PermissionManager setWindowDialogMessage(@StringRes int windowDialogMessage){
        this.windowDialogMessage = getText(windowDialogMessage);
        return this;
    }

    private CharSequence getText(@StringRes int stringRes) {
        if (stringRes <= 0) {
            throw new IllegalArgumentException("Invalid String resource Id");
        }
        return context.getText(stringRes);
    }
}
