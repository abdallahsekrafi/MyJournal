package com.zwir.myjournal.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.zwir.myjournal.R;

public class CustomProgressDialog {
    ProgressDialog dialog;
    public CustomProgressDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);

    }
    public void show (){
        dialog.show();
        dialog.setContentView(R.layout.my_progress);
    }
    public void dismiss(){
        dialog.dismiss();
    }
}
