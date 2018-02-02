package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;

/**
 * Created by Hades on 2016/7/18.
 */
public class CustomDialogWithTwoButton extends DialogFragment {

    private static final String PARAM_TITLE = "param_title";
    private static final String PARAM_RESID = "param_resid";
    private static final String PARAM_POINT = "param_point";
    private TextView tvCustomDialogTitle;
    private ImageView ivCustomDialogIcon;
    private TextView tvCustomDialogDesc;
    private Button btnCustomDialogOpleft;
    private Button btnCustomDialogOpright;
    private ITwoButtonClick iTwoButtonClick;

    public static CustomDialogWithTwoButton newInstance(int points) {
        return newInstance("兑换成功", R.drawable.icon_green_right, points);
    }

    public static CustomDialogWithTwoButton newInstance(String title, int resId, int points) {
        CustomDialogWithTwoButton customDialogWithTwoButton = new CustomDialogWithTwoButton();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putInt(PARAM_RESID, resId);
        args.putInt(PARAM_POINT, points);

        customDialogWithTwoButton.setArguments(args);
        return customDialogWithTwoButton;
    }

    public void setOnTwoButtonClick(ITwoButtonClick iTwoButtonClick) {
        this.iTwoButtonClick = iTwoButtonClick;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmet_custom_two_button, container, false);
    }

    @Override
    public void onResume() {

        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            tvCustomDialogTitle = (TextView) view.findViewById(R.id.tvCustomDialogTitle);
            ivCustomDialogIcon = (ImageView) view.findViewById(R.id.ivCustomDialogIcon);
            tvCustomDialogDesc = (TextView) view.findViewById(R.id.tvCustomDialogDesc);
            btnCustomDialogOpleft = (Button) view.findViewById(R.id.btnCustomDialogOpleft);
            btnCustomDialogOpright = (Button) view.findViewById(R.id.btnCustomDialogOpright);

            if (getArguments() != null) {
                tvCustomDialogTitle.setText(getArguments().getString(PARAM_TITLE));
                ivCustomDialogIcon.setImageResource(getArguments().getInt(PARAM_RESID));

                tvCustomDialogDesc.setText(getDescText(getArguments().getInt(PARAM_POINT)));

                btnCustomDialogOpleft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            iTwoButtonClick.onLeftButtonClick();
                        }
                    }
                });
                btnCustomDialogOpright.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            iTwoButtonClick.onRightButtonClick();
                        }
                    }
                });
            }

        }
    }

    private String getDescText(int points) {

        return points + "";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
