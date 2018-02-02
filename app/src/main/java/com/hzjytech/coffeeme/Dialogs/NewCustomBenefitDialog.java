package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
 * 新手优惠券
 * Created by Hades on 2016/7/18.
 */
public class NewCustomBenefitDialog extends DialogFragment {
    private static final String PARAM_COUNT = "param_count";
    private static final String PARAM_UNIT = "param_unit";
    private static final String PARAM_ABLE = "param_able";
    private static final String PARAM_LIFE = "param_life";
    private static final String PARAM_TITLE = "param_title";

    private ITwoButtonClick iTwoButtonClick;
    private TextView tv_count;
    private TextView tv_unit;
    private TextView tv_detail_count;
    private TextView tv_detail_able;
    private TextView tv_detail_life;
    private TextView tv_desc_new_benefit;
    private Button bt_my_coupons;
    private ImageView bt_close;

    public static NewCustomBenefitDialog newInstance(String title,String count, String unit ,String able,String life) {
        NewCustomBenefitDialog newCustomBenefitDialog = new NewCustomBenefitDialog();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE,title);
        args.putString(PARAM_COUNT, count);
        args.putString(PARAM_UNIT, unit);
        args.putString(PARAM_ABLE, able);
        args.putString(PARAM_LIFE, life);
        newCustomBenefitDialog.setArguments(args);
        return newCustomBenefitDialog;
    }

    public void setOnTwoButtonClick(ITwoButtonClick iTwoButtonClick) {
        this.iTwoButtonClick = iTwoButtonClick;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragmet_new_custom_benefit, container, false);
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
            tv_count = (TextView) view.findViewById(R.id.tv_count);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_detail_count = (TextView) view.findViewById(R.id.tv_detail_count);
            tv_detail_able = (TextView) view.findViewById(R.id.tv_detail_able);
            tv_detail_life = (TextView) view.findViewById(R.id.tv_detail_life);
            tv_desc_new_benefit =(TextView) view.findViewById(R.id.tv_desc_new_benefit);
            bt_my_coupons =(Button) view.findViewById(R.id.bt_my_coupons);
            bt_close =(ImageView) view.findViewById(R.id.bt_close);

            if (getArguments() != null) {
                tv_count.setText(getArguments().getString(PARAM_COUNT));
                tv_unit.setText(getArguments().getString(PARAM_UNIT));
                tv_detail_count.setText(getArguments().getString(PARAM_TITLE));
                tv_detail_able.setText(getArguments().getString(PARAM_ABLE));
                tv_detail_life.setText(getArguments().getString(PARAM_LIFE));
                tv_desc_new_benefit.setText("获得一张"+getArguments().getString(PARAM_TITLE));
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
