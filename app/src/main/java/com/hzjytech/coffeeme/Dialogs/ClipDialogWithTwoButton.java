package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public class ClipDialogWithTwoButton extends DialogFragment {
    private static final String PARAM_TITLE = "param_title";
    private static final String PARAM_CONTENT = "param_content";
    private static final String PARAM_RIGHT = "param_right";
    private TextView tv_title;
    private TextView tv_content;
    private TextView btn_left;
    private TextView btn_right;
    private OnTwoButtonClickListener onTwoButtonClickListener;
    public static ClipDialogWithTwoButton newInstance(String content) {
        return newInstance("配方卡已生成","去粘贴", content);
    }

    public static ClipDialogWithTwoButton newInstance(String title, String rightText,String content) {
        ClipDialogWithTwoButton clipDialog = new ClipDialogWithTwoButton();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putString(PARAM_RIGHT,rightText);
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
        return inflater.inflate(R.layout.fragment_clip_two_button, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_clip_content);
        btn_left = (TextView) view.findViewById(R.id.btn_left);
        btn_right = (TextView) view.findViewById(R.id.btn_right);
        if(getArguments()!=null){
            Bundle bundle = getArguments();
            String title = bundle.getString(PARAM_TITLE);
            String content = bundle.getString(PARAM_CONTENT);
            String right = bundle.getString(PARAM_RIGHT);
            tv_title.setText(title);
            tv_content.setText(content);
            btn_right.setText(right);
        }
       btn_left.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(onTwoButtonClickListener!=null){
                   if(!AppUtil.isFastClick()){
                       onTwoButtonClickListener.onLeftClick();
                   }
               }
           }
       });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onTwoButtonClickListener!=null){
                    if(!AppUtil.isFastClick()){
                        onTwoButtonClickListener.onRightClick();
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
    public interface OnTwoButtonClickListener{
        void onLeftClick();
        void onRightClick();
    }
   public void setOnTwoButtonClickListener(OnTwoButtonClickListener onTwoButtonClickListener){
         this.onTwoButtonClickListener=onTwoButtonClickListener;
   }
}
