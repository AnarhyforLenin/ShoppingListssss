

package com.mirea.productapp.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mirea.productapp.activity.SettingsFragment;

import java.io.File;


class DirectoryStatus {
    public enum Status {IS_OK, NOT_A_DIRECTORY, CANNOT_WRITE}

    private static final String DEFAULT_DIRECTORY = "ShoppingLists";
    private Status reason;
    private String directory;

    public DirectoryStatus(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String directory = sharedPreferences.getString(SettingsFragment.KEY_DIRECTORY_LOCATION, "").trim();
        String defaultDir = ctx.getFileStreamPath(DEFAULT_DIRECTORY).getAbsolutePath();
        File file = new File(directory);

        if (directory.equals("")) {
            init(Status.IS_OK, defaultDir);
        } else if (!file.isDirectory()) {
            init(Status.NOT_A_DIRECTORY, defaultDir);
        } else if (!file.canWrite()) {
            init(Status.CANNOT_WRITE, defaultDir);
        } else {
            init(Status.IS_OK, directory);
        }
    }

    private void init(Status reason, String directory) {
        this.reason = reason;
        this.directory = directory;
        new File(directory).mkdirs();
    }

    public boolean isFallback() {
        return reason != Status.IS_OK;
    }

    public String getDirectory() {
        return directory;
    }

    public Status getReason() {
        return reason;
    }
}
