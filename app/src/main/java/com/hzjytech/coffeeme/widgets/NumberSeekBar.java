package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;

import java.text.DecimalFormat;

/**
 * Created by Hades on 2016/3/24.
 */
public class NumberSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    public final static int SEEKBARTYPE_DEFAULT=0;
    public final static int SEEKBARTYPE_MULTIPLYTEN=1;
    public final static int SEEKBARTYPE_NUMBER=2;
    public static final int SEEKBARTYPE_FINE = 3;
    private int mPopupWidth;
    private int mPopupTop;
    private int mPopupLeft;
    private int mPopupStyle;
    public static final int POPUP_FIXED = 1;
    public static final int POPUP_FOLLOW = 0;

    private int type=SEEKBARTYPE_DEFAULT;

    private PopupWindow mPopup;
    private TextView mPopupTextView;
    private int mYLocationOffset;

    private OnSeekBarChangeListener mInternalListener;
    private OnSeekBarChangeListener mExternalListener;

    private OnSeekBarHintProgressChangeListener mProgressChangeListener;

    private OnSeekBarHintProgressStopListener mProgressStopListener;
    private float min=0.0f;
    private float max=100.0f;
    private DecimalFormat df;

    public void setRange(float min_weight, float max_weight) {
        this.min=min_weight;
        this.max=max_weight;
    }

    public interface OnSeekBarHintProgressChangeListener {
       String onHintTextChanged(NumberSeekBar seekBarHint, float progress);
    }

    public interface OnSeekBarHintProgressStopListener {
        void onHintTextStop();
    }

    public NumberSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public NumberSeekBar (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public NumberSeekBar (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setType(int type){
        this.type=type;

    }
    private void init(Context context, AttributeSet attrs){

        mPopupTop = (int)context.getResources().getDimension(R.dimen.seekbar_popt);
        mPopupLeft = (int)context.getResources().getDimension(R.dimen.seekbar_popl);
        df = new DecimalFormat("0.0");
        setOnSeekBarChangeListener(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberSeekBar);
        mPopupWidth = (int) a.getDimension(R.styleable.NumberSeekBar_popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mYLocationOffset = (int) a.getDimension(R.styleable.NumberSeekBar_yOffset, 0);
        mPopupStyle = a.getInt(R.styleable.NumberSeekBar_popupStyle, POPUP_FOLLOW);
        a.recycle();
        initHintPopup();
    }


    public void setPopupStyle(int style){
        mPopupStyle = style;
    }

    public int getPopupStyle(){
        return mPopupStyle;
    }
    private void initHintPopup(){
        String popupText = null;
//
//        if (mProgressChangeListener!=null){
//            popupText = mProgressChangeListener.onHintTextChanged(this, getValue());
//        }

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View undoView = inflater.inflate(R.layout.popup, null);
        mPopupTextView = (TextView)undoView.findViewById(R.id.text);
        if(type==SEEKBARTYPE_DEFAULT){
            mPopupTextView.setText(popupText!=null? popupText : String.valueOf(getValue()));
        }else if(type==SEEKBARTYPE_FINE){
            mPopupTextView.setText(popupText!=null? popupText : String.valueOf(getValue()));
        }else{
            mPopupTextView.setText(popupText!=null? popupText : String.valueOf(((int)getValue())));
        }

        mPopup = new PopupWindow(undoView, mPopupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        mPopup.setAnimationStyle(R.style.fade_animation);

    }


    public void showPopup(){

        if(mPopupStyle==POPUP_FOLLOW){
            final int[] locations=new int[2];
            this.getLocationInWindow(locations);
            mPopup.showAtLocation(this, Gravity.NO_GRAVITY,(int) (this.getX() + (int) getXPosition(this)+mPopupLeft),
                    locations[1] - this.getHeight()-mPopupTop);
//            mPopup.showAtLocation(this,  Gravity.NO_GRAVITY, (int) (this.getX()+(int) getXPosition(this)), (int) (this.getY()+mYLocationOffset+this.getHeight()));
//            Log.d("Number","thisGetY"+getY()+",mYLocationOffset"+mYLocationOffset+",getHeight()"+getHeight());
        }
        if (mPopupStyle==POPUP_FIXED){
            mPopup.showAtLocation(this,  Gravity.NO_GRAVITY, 0, (int) (this.getY()+mYLocationOffset+this.getHeight()));
        }
    }

    public void hidePopup(){
        if(mPopup.isShowing()) {
            mPopup.dismiss();
        }
    }

    public void setHintView(View view){
        //initHintPopup();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("x",event.getX()+"");
        int width = getWidth();
        float x = getLeft();
        Log.e("x","left"+x);
        int right = getRight();
        Log.e("x","right"+right);
        if(event.getX()<right&&event.getX()>x){
            float v = (event.getX() - x) / (float) width;
            Log.e("x","百分比"+v);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        if (mInternalListener==null){
            mInternalListener = l;
            super.setOnSeekBarChangeListener(l);
        }else {
            mExternalListener = l;
        }
    }
    public void setOnProgressChangeListener(OnSeekBarHintProgressChangeListener l){
        mProgressChangeListener = l;
    }

    public void setOnProgressStopListener(OnSeekBarHintProgressStopListener l){
        this.mProgressStopListener=l;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Log.e("offset",getXPosition(seekBar)+"offset");
        Log.e("offset",getX()+"x");
        Log.e("offset",getScrollX()+"");
        String popupText = null;
        if (b&&mProgressChangeListener!=null){
            popupText = mProgressChangeListener.onHintTextChanged(this, getValue());
        }

        if(mExternalListener !=null){
            mExternalListener.onProgressChanged(seekBar, progress, b);
        }


        if(type==SEEKBARTYPE_DEFAULT){

            mPopupTextView.setText(popupText!=null? popupText : String.valueOf(getValue()));
        }else if(type==SEEKBARTYPE_FINE){
            mPopupTextView.setText(popupText!=null? popupText :df.format(getValue()));
        }else{
            mPopupTextView.setText(popupText!=null? popupText : String.valueOf(((int)getValue())));
        }

        if(mPopupStyle==POPUP_FOLLOW){
            final int[] locations=new int[2];
            this.getLocationInWindow(locations);
//            mPopup.update((int) (this.getX()+(int) getXPosition(seekBar)), (int) (this.getY()+mYLocationOffset+this.getHeight()), -1, -1);
            mPopup.update((int) (this.getX()+(int)getXPosition(seekBar)+mPopupLeft), locations[1]-this.getHeight()-mPopupTop, -1, -1);
//            mPopup.update((int) (this.getX()+(int) getXPosition(seekBar)), this.getTop(), -1, -1);
        }

    }

    public float getValue(){
        if(type==SEEKBARTYPE_NUMBER) {
            this.setMax(1);
            return getProgress()+this.min;

        }else if(type==SEEKBARTYPE_MULTIPLYTEN){
            float sum = ((max - min) / 100f * (float)getProgress()) + min;
            return ((int)(sum/10)*10);

        }else if(type==SEEKBARTYPE_FINE){
            Log.e("progress",getProgress()+"进度条");
            float sum = (max - min) / 100.0f * (float) getProgress() + min;
            return (float) (Math.round(sum * 100f)) / 100f;
        }else{
            float sum = (max - min) / 100.0f * (float) getProgress() + min;
            return (float) (Math.round(sum * 100f)) / 100f;
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        showPopup();
        if(mExternalListener !=null){
            mExternalListener.onStartTrackingTouch(seekBar);
        }

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mExternalListener !=null){
            mExternalListener.onStopTrackingTouch(seekBar);
        }

        if(mProgressStopListener!=null){
            mProgressStopListener.onHintTextStop();
        }
        hidePopup();
    }

    private float getXPosition(SeekBar seekBar){
        float val = (((float)seekBar.getProgress() * (float)(seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax());
        float offset = seekBar.getThumbOffset();

        int textWidth = mPopupWidth;
        float textCenter = (textWidth/2.0f);

        float newX = val+offset - textCenter;

        return newX;
    }

}
