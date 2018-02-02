package com.hzjytech.coffeeme.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.ScreenUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hzjytech.coffeeme.widgets.PayMethodPopWindow.payMethod.ALIPAY;
import static com.hzjytech.coffeeme.widgets.PayMethodPopWindow.payMethod.COFFEEMEPAY;
import static com.hzjytech.coffeeme.widgets.PayMethodPopWindow.payMethod.WXPAY;


/**
 * 用于首页弹出的PopUpWindow
 * Created by hehongcan on 2017/4/25.
 */
public class PayMethodPopWindow extends PopupWindow {

    private Activity activity;
    private Context context;
    private IPayMethodListener iPayMethodListener;
    @IntDef({COFFEEMEPAY , WXPAY ,ALIPAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface payMethod{
        int COFFEEMEPAY = 0,WXPAY  = 1,ALIPAY=2;
    }

    public PayMethodPopWindow(Activity activity) {
        super(activity);
        this.activity = activity;
        context = activity;
        View inflate = LayoutInflater.from(context)
                .inflate(R.layout.popview_item, null, false);
        setContentView(inflate);
        int width = ScreenUtil.getScreenWidth(context) - 2 * DensityUtil.dp2px(context, 12);
        setWidth(width);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        initView(inflate);
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.gravity= Gravity.CENTER_HORIZONTAL;
        lp.alpha = 0.4f;
        activity.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow()
                .setAttributes(lp);


    }

    private void initView(View inflate) {
        LinearLayout ll_coffeeme_pay = (LinearLayout) inflate.findViewById(R.id.ll_coffeeme_pay);
        LinearLayout ll_wx_pay = (LinearLayout) inflate.findViewById(R.id.ll_wx_pay);
        LinearLayout ll_ali_pay = (LinearLayout) inflate.findViewById(R.id.ll_ali_pay);
        ll_coffeeme_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethodInterface(COFFEEMEPAY);
            }
        });
        ll_wx_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethodInterface(WXPAY);
            }
        });
        ll_ali_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethodInterface(ALIPAY);
            }
        });

    }

    private void callMethodInterface(int coffeemepay) {
        dismiss();
        if(iPayMethodListener!=null){
            iPayMethodListener.payMethod(coffeemepay);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 1f;
        activity.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow()
                .setAttributes(lp);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 0.4f;
        activity.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow()
                .setAttributes(lp);
    }

    public void setPayMethodListener(IPayMethodListener iPayMethodListener) {
        this.iPayMethodListener = iPayMethodListener;
    }
    public interface IPayMethodListener{
        void payMethod(int method);
    }
}
