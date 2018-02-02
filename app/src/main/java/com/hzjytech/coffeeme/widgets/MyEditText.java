package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.DensityUtil;


/**
 * Created by Hades on 2016/1/20.
 */
public class MyEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {

    private static final String TAG=MyEditText.class.getSimpleName();

    private boolean isVisible=false;

    public enum EditTextStatus {CLEAR_EDIT_TEXT_STATUS, GET_SMS_CODE, SEE_PASSWORD_STATUE}

    private Drawable mClearDrawable;
    private boolean hasFocus = false;
    private EditTextStatus status;

    public MyEditText(Context context) {
        this(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mClearDrawable = getCompoundDrawables()[2];

        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.icon_clear);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    public void setVisible(boolean isVisible){this.isVisible=isVisible;}

    public void setDrawable(Drawable mClearDrawable) {
        this.mClearDrawable = mClearDrawable;
    }

    public void setAction(EditTextStatus status) {
        this.status = status;
    }

    public void action(EditTextStatus status) {
        switch (status) {
            case CLEAR_EDIT_TEXT_STATUS:
                this.setText("");
                break;
            case SEE_PASSWORD_STATUE:
                if (this.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    this.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.action(status);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
//        setIconVisible(true);
        if (hasFocus) {
            setIconVisible(getText().length() > 0);
        } else {
            setIconVisible(false);
        }

    }

    private void setIconVisible(boolean visible) {
        Drawable right;
        if(mClearDrawable!=null){

            right = visible ? mClearDrawable : null;
        }else{
            right=getResources().getDrawable(R.mipmap.ic_launcher);
        }
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFocus) {
            setIconVisible(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
