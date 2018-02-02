package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.AppUtil;

/**
 * Created by Hades on 2016/7/18.
 */
public class CustomDialogWithTwoDiffButton extends DialogFragment {

    private static final String PARAM_TITLE = "param_title";

    private static final String PARAM_RESID = "param_resid";
    private static final String PARAM_POINT = "param_point";

    private static final String PARAM_HINT = "param_hint";
    private static final String PARAM_DESC = "param_desc";
    private TextView tvCustomDialogTitle;
    private ImageView ivCustomDialogIcon;
    private LinearLayout tvCustomDialogDescContainer;
    private TextView tvCustomDialogDesc;
    private ImageView ivCustomDialogOpleft;
    private Button btnCustomDialogOpright;
    private ITwoButtonClick iTwoButtonClick;
    private TextView tvRealDesc;

    public static CustomDialogWithTwoDiffButton newInstance(int points) {
        return newInstance("支付成功", R.drawable.icon_green_right, points);
    }

    public static CustomDialogWithTwoDiffButton newInstance(String title, int resId, int points) {
        return CustomDialogWithTwoDiffButton.newInstance(title, resId, points, true);
    }

    public static CustomDialogWithTwoDiffButton newInstance(String title, int resId, int points, boolean hasHint) {
        CustomDialogWithTwoDiffButton customDialogWithTwoButton = new CustomDialogWithTwoDiffButton();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putInt(PARAM_RESID, resId);
        args.putInt(PARAM_POINT, points);
        args.putBoolean(PARAM_HINT,hasHint);

        customDialogWithTwoButton.setArguments(args);
        return customDialogWithTwoButton;
    }
    public static CustomDialogWithTwoDiffButton newInstance(String title,int resId,String desc){
        CustomDialogWithTwoDiffButton customDialogWithTwoButton = new CustomDialogWithTwoDiffButton();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE,title);
        args.putInt(PARAM_RESID,resId);
        args.putString(PARAM_DESC,desc);
        customDialogWithTwoButton.setArguments(args);
        return customDialogWithTwoButton;
    }

    public void setOnTwoButtonClick(ITwoButtonClick iTwoButtonClick) {
        this.iTwoButtonClick = iTwoButtonClick;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmet_custom_two_diff_button, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            tvCustomDialogTitle = (TextView) view.findViewById(R.id.tvCustomDialogTitle);
            ivCustomDialogIcon = (ImageView) view.findViewById(R.id.ivCustomDialogIcon);
            tvCustomDialogDesc = (TextView) view.findViewById(R.id.tvCustomDialogDesc);
            ivCustomDialogOpleft = (ImageView) view.findViewById(R.id.ivCustomDialogOpleft);
            btnCustomDialogOpright = (Button) view.findViewById(R.id.btnCustomDialogOpright);
            tvCustomDialogDescContainer=(LinearLayout)view.findViewById(R.id.tvCustomDialogDescContainer);
            tvRealDesc = (TextView) view.findViewById(R.id.tv_real_desc);

            if (getArguments() != null) {
                tvCustomDialogTitle.setText(getArguments().getString(PARAM_TITLE));
                ivCustomDialogIcon.setImageResource(getArguments().getInt(PARAM_RESID));
                String string = getArguments().getString(PARAM_DESC);
                Log.e("desc",String.valueOf(string==null));
                if(string==null){
                    tvRealDesc.setVisibility(View.INVISIBLE);
                    tvCustomDialogDescContainer.setVisibility(View.VISIBLE);
                    tvCustomDialogDesc.setText(""+getArguments().getInt(PARAM_POINT));
                }else{
                    tvRealDesc.setVisibility(View.VISIBLE);
                    tvCustomDialogDescContainer.setVisibility(View.INVISIBLE);
                    tvRealDesc.setText(string);
                }
                ivCustomDialogOpleft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            if(!AppUtil.isFastClick()){
                                iTwoButtonClick.onLeftButtonClick();
                            }
                        }
                    }
                });
                btnCustomDialogOpright.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            if(!AppUtil.isFastClick()) {
                                iTwoButtonClick.onRightButtonClick();
                            }
                        }
                    }
                });

                if(getArguments().getBoolean(PARAM_HINT,false)==true){
                    tvCustomDialogDescContainer.setVisibility(View.VISIBLE);
                }else{
                    tvCustomDialogDescContainer.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_SEARCH||keyCode==KeyEvent.KEYCODE_BACK)
                {
                    return true;
                }
                else
                {
                    return false; //默认返回 false
                }
            }
        });
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations= R.style.CollectDialogAnimation;
    }
}
