package com.abidingtech.rednewsapp.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;


import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ConfirmCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;

import java.util.List;

public class AlertUtil {



    public static AlertDialog showConfirmationDialogV1(Context context, String msg,String clickOk,String clickCancel,ConfirmCallback callback) {
        Resources res = context.getResources();
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                .setTitle("Confirmation")
                .setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)


                .setPositiveButton(clickOk, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        callback.onConfirmed();
//                        Toast.makeText(context, "Yaay", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(clickCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onCancel();
                        if (dialog != null)
                            dialog.dismiss();

                    }
                }).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        return dialog;
    }

}
