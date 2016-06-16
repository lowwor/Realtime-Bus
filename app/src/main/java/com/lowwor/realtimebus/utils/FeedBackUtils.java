package com.lowwor.realtimebus.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Gao on 16/6/16.
 */

public class FeedBackUtils {

    public static final String FEEDBACK_EMAIL = "lowwor@foxmail.com";
    public static final String FEEDBACK_SUBJECT = "珠海公交应用 反馈";

    /**
     * Starts an intent to let the user send an email
     */
    public static void sendFeedbackEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{FEEDBACK_EMAIL});
            intent.putExtra(Intent.EXTRA_SUBJECT, FEEDBACK_SUBJECT);
            context.startActivity(intent);
        }
    }
}
