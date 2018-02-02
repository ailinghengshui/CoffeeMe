package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hzjytech.coffeeme.R;

/**
 * Created by Hades on 2016/1/26.
 */
public class FixedEditText extends EditText {

    private static final String TAG=FixedEditText.class.getSimpleName();

    public interface  FixedEditTextable{
       void onFixedEditTextClick();
    }

    private Drawable mClearDrawable;

    private FixedEditTextable fixedEditTextable;

    public FixedEditText(Context context) {
        this(context,null);
    }

    public FixedEditText(Context context, AttributeSet attrs) {
       this(context, attrs, R.attr.editTextStyle);
    }

    public FixedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mClearDrawable=getCompoundDrawables()[2];
        if(mClearDrawable==null){
            mClearDrawable=getResources().getDrawable(R.mipmap.ic_launcher);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],mClearDrawable,getCompoundDrawables()[3]);
    }

    public void setFixedEditTextable(FixedEditTextable fixedEditTextable) {
        this.fixedEditTextable = fixedEditTextable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                InputMethodManager imm = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (touchable) {
                    imm.hideSoftInputFromWindow(getWindowToken(),0);
                    fixedEditTextable.onFixedEditTextClick();
                }else{
                    imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
