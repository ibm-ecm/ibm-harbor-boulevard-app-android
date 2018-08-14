package com.futureworkshops.datacap.common.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.futureworkshops.core.R;



public class SnackUtils {

    public static void showSimpleSnackbar(@NonNull Activity activity, String message) {
        final Snackbar snackbar = Snackbar.make(getViewForActivity(activity), message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public static void showSnackbarWithLabel(@NonNull Activity activity, String message) {
        final Snackbar snackbar = Snackbar.make(getViewForActivity(activity), message, Snackbar.LENGTH_LONG)
                .setAction(activity.getString(R.string.action_ok), null);

        snackbar.show();
    }

    public static void showSnackbarWithAction(@NonNull Activity activity, String message, View.OnClickListener actionListener) {
        final Snackbar snackbar = Snackbar.make(getViewForActivity(activity), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(activity.getString(R.string.action_ok), actionListener);
        snackbar.show();
    }

    private static View getViewForActivity(@NonNull Activity activity) {
        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }
}
