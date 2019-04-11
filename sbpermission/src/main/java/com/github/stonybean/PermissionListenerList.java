package com.github.stonybean;

import java.util.HashMap;

/**
 * Created by stonybean on 2019. 4. 11.
 */
public class PermissionListenerList extends HashMap<String, PermissionListener> {
    private static PermissionListenerList permissionListenerList;

    static PermissionListenerList getInstance() {
        if (permissionListenerList == null) {
            permissionListenerList = new PermissionListenerList();
        }
        return permissionListenerList;
    }
}
