package com.vorogushinigor.notes.other;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.vorogushinigor.notes.R;

/**
 * Created by viv on 13.07.2016.
 */
public class CustomAlertDialog {

    private static final String TAG_ALERT_DIALOG = "LogFragmentDialog";
    private AlertDialog.Builder mBuilder;
    private OnClickListener mOnClickListener;
    private boolean isShow = false;

    public interface OnClickListener {
        void positiveButton();
    }
    public CustomAlertDialog(Context context, String title, String message, OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        mBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialogStyle);
        mBuilder.setTitle(title);
        mBuilder.setCancelable(true);
        mBuilder.setMessage(message);
        mBuilder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnClickListener != null)
                    mOnClickListener.positiveButton();
            }
        });
        mBuilder.setNegativeButton(context.getString(R.string.dialog_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG_ALERT_DIALOG, "dismiss");
                isShow = false;
            }
        });
    }

    public boolean isShow() {
        return isShow;
    }
    public void show() {
        try {
            mBuilder.show();
            isShow = true;
            Log.i(TAG_ALERT_DIALOG, "show");
        } catch (Exception e) {
            Log.i(TAG_ALERT_DIALOG, "show error " + e.toString());
            isShow = false;
        }

    }



}
