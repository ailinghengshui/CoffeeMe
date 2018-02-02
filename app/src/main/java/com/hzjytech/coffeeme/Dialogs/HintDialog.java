package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.widgets.TitleBar;

import java.net.InterfaceAddress;

/**
 * Created by Hades on 2016/5/10.
 */
public class HintDialog extends BaseCustomDialog{

    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String BTNSTR = "btnstr";

    public static HintDialog newInstance(String title,String message,String btnStr){
        HintDialog hintDialog=new HintDialog();
        Bundle args=new Bundle();
        args.putString(TITLE,title);
        args.putString(MESSAGE,message);
        args.putString(BTNSTR,btnStr);
        hintDialog.setArguments(args);
        return hintDialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_circle);
        return inflater.inflate(R.layout.dialog_hint, container, false);
    }

    @Override
    protected void onBaseResume(Window window) {
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.dialog_circle);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(view!=null){
            TextView tvHintDialogTitle = (TextView) view.findViewById(R.id.tvHintDialogTitle);
            TextView btnHintDialog = (TextView) view.findViewById(R.id.btnHintDialog);

            if(getArguments()!=null){
                tvHintDialogTitle.setText(getArguments().getString(MESSAGE));
                btnHintDialog.setText(getArguments().getString(BTNSTR));
                btnHintDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                    }
                });
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(forseCloseActivity!=null){
            forseCloseActivity.close();
        }
    }

    private ForseCloseActivity forseCloseActivity;
    public void setForseCloseActivityListener(ForseCloseActivity forseCloseActivity){
        this.forseCloseActivity=forseCloseActivity;
    }
    public interface  ForseCloseActivity{
        void  close();
    }
}
