package com.github.stonybean;

import java.util.ArrayList;

/**
 * Created by stonybean on 2019. 4. 11.
 */
public interface PermissionListener {
    void onPermissionGranted(ArrayList<String> grantedPermissions);
    void onPermissionDenied(ArrayList<String> deniedPermissions);

}
