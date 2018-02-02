package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.AppUtil;

/**
 * Created by hehongcan on 2017/2/21.
 */
public class ShareClipDialogWithTwoButton extends DialogFragment {
    private static final String PARAM_TITLE = "param_title";
    private static final String PARAM_CONTENT = "param_content";
    private static final String PARAM_RIGHT = "param_right";
    private TextView tv_title;
    private TextView tv_content;
    private ImageView btn_left;
    private ImageView btn_right;
    private OnShareTwoButtonClickListener onShareTwoButtonClickListener;
    public static ShareClipDialogWithTwoButton newInstance(String content) {
        return newInstance("配方卡已生成", content);
    }

    public static ShareClipDialogWithTwoButton newInstance(String title, String content) {
        ShareClipDialogWithTwoButton clipDialog = new ShareClipDialogWithTwoButton();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putString(PARAM_CONTENT,content);
        clipDialog.setArguments(args);
        return clipDialog;
    }

    @Override
    public void onResume() {

        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.share_clip_card_two_button, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        btn_left = (ImageView) view.findViewById(R.id.iv_left);
        btn_right = (ImageView) view.findViewById(R.id.bt_close);
        if(getArguments()!=null){
            Bundle bundle = getArguments();
            String title = bundle.getString(PARAM_TITLE);
            String content = bundle.getString(PARAM_CONTENT);
            tv_title.setText(title);
            tv_content.setText(content);
        }
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onShareTwoButtonClickListener!=null){
                    if(!AppUtil.isFastClick()){
                        onShareTwoButtonClickListener.onLeftClick();
                    }
                }
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onShareTwoButtonClickListener!=null){
                    if(!AppUtil.isFastClick()){
                        onShareTwoButtonClickListener.onRightClick();
                    }
                }
            }
        });
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
    public interface OnShareTwoButtonClickListener{
        void onLeftClick();
        void onRightClick();
    }
    public void setOnTwoButtonClickListener(OnShareTwoButtonClickListener onShareTwoButtonClickListener){
        this.onShareTwoButtonClickListener=onShareTwoButtonClickListener;
    }
}
