package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
 * 新获取的优惠券弹窗
 * Created by hehongcan on 2017/3/27.
 */
public class NewBenefitDialog  extends DialogFragment {
    private static final String PARAM_TITLE = "param_title";

    private ITwoButtonClick iTwoButtonClick;
    private TextView tv_detail_count;
    private Button bt_my_coupons;
    private ImageView bt_close;

    public static NewBenefitDialog newInstance(String title) {
        NewBenefitDialog newBenefitDialog = new NewBenefitDialog();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE,title);
        newBenefitDialog.setArguments(args);
        return newBenefitDialog;
    }

    public void setOnTwoButtonClick(ITwoButtonClick iTwoButtonClick) {
        this.iTwoButtonClick = iTwoButtonClick;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragmet_new_benefit, container, false);
    }

    @Override
    public void onResume() {

        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout( WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            tv_detail_count = (TextView) view.findViewById(R.id.tv_desc_new_benefit);
            bt_my_coupons =(Button) view.findViewById(R.id.bt_my_coupons);
            bt_close =(ImageView) view.findViewById(R.id.bt_close);

            if (getArguments() != null) {
                tv_detail_count.setText("获得一张"+getArguments().getString(PARAM_TITLE));
                bt_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            iTwoButtonClick.onLeftButtonClick();
                        }
                    }
                });
                bt_my_coupons.setOnClickListener(new View.OnClickListener() {
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
