package com.hzjytech.coffeeme.Dialogs;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.List;

/**
 * Created by Hades on 2016/8/16.
 */
public class OtherLoginDialog extends BaseForceCustomDialog {

    private static final String TITLE = "title";
    private static final String DESC ="desc" ;
    private static final String CANCELSTRING ="cancelstring" ;
    private static final String OKSTRING ="okstring" ;
    private TextView tvUpdateDialogDescContainer;
    private TextView btnUpdateDialogOpleft;
    private TextView btnUpdateDialogOpright;
    private TextView tvUpdateDialogTitle;
    private ITwoButtonClick iTwoButtonClick;
    private List<String> descs;


    public static OtherLoginDialog newInstance(String title, String desc) {
       return newInstance(title,desc,"忽略该版本","更新");
    }

    public static OtherLoginDialog newInstance(String title, String desc, String cancelString, String okString){
        OtherLoginDialog updateDialog = new OtherLoginDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESC, desc);
        args.putString(CANCELSTRING,cancelString);
        args.putString(OKSTRING,okString);
        updateDialog.setArguments(args);
        return updateDialog;
    }

    public void setOnTwoClickListener(ITwoButtonClick iTwoButtonClick) {
        this.iTwoButtonClick = iTwoButtonClick;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_other_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            tvUpdateDialogTitle = (TextView) view.findViewById(R.id.tvUpdateDialogTitle);
            tvUpdateDialogDescContainer = (TextView) view.findViewById(R.id.tvUpdateDialogDescContainer);
            btnUpdateDialogOpleft = (TextView) view.findViewById(R.id.btnUpdateDialogOpleft);

            btnUpdateDialogOpright = (TextView) view.findViewById(R.id.btnUpdateDialogOpright);


            if (getArguments() != null) {
                tvUpdateDialogTitle.setText(getArguments().getString(TITLE));
                tvUpdateDialogDescContainer.setText(getArguments().getString(DESC));
                btnUpdateDialogOpleft.setText(getArguments().getString(CANCELSTRING));
                btnUpdateDialogOpright.setText(getArguments().getString(OKSTRING));

                btnUpdateDialogOpleft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            iTwoButtonClick.onLeftButtonClick();
                            dismiss();
                        }
                    }
                });
                btnUpdateDialogOpright.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            iTwoButtonClick.onRightButtonClick();
                            dismiss();
                        }
                    }
                });
            }

        }
    }

    @Override
    protected void onBaseResume(Window window) {
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setBackgroundDrawableResource(R.drawable.dialog_circle);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
