package com.hzjytech.coffeeme.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hzjytech.coffeeme.R;

/**
 * Created by hehongcan on 2016/11/23.
 */
public class MaterialBoxView extends RelativeLayout {
    private int buttonSrc;
    private int boxSrc;
    private int materialSrc;
    private int lightingSrc;
    private int titleSrc;
    private int boxOutSrc;
    private View inflateView;
    private ObjectAnimator windRotateAnimation;
    private ObjectAnimator materialDescendAnimation;
    private OnMaterialButtonListener onMaterialButtonListener;
    private ImageView iv_box;
    private ImageView iv_title;
    private ImageButton ib_material;

    public MaterialBoxView(Context context) {
        this(context, null);
    }

    public MaterialBoxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaterialBoxView);
        buttonSrc = ta.getInteger(R.styleable.MaterialBoxView_buttonSrc, 0);
        boxSrc = ta.getInteger(R.styleable.MaterialBoxView_boxSrc, 0);
        materialSrc = ta.getInteger(R.styleable.MaterialBoxView_MaterialSrc, 0);
     /*   lightingSrc = ta.getInteger(R.styleable.MaterialBoxView_windStickSrc, 0);
        boxOutSrc = ta.getInteger(R.styleable.MaterialBoxView_boxoutSrc, 0);*/
        titleSrc = ta.getInteger(R.styleable.MaterialBoxView_titleSrc,0);
        ta.recycle();
        initView(context);
    }

    private void initView(Context context) {
        //将布局填充到自定义的料盒布局
        inflateView = View.inflate(context, R.layout.item_materialbox, MaterialBoxView.this);
        iv_box = (ImageView) inflateView.findViewById(R.id.iv_box);
        iv_title = (ImageView) inflateView.findViewById(R.id.iv_material_title);
        ib_material = (ImageButton) inflateView.findViewById(R.id.ib_material);
        //设置盒，出料口，物料，桨，按钮图片,文字
        iv_box.setImageResource(boxSrc);
        iv_title.setImageResource(titleSrc);
        ib_material.setImageResource(buttonSrc);

        ib_material.setOnTouchListener(mTouchListener);
        //初始化动画
     /*   materialDescendAnimation = new ObjectAnimator().ofFloat(ivMaterial,"translationY",ivMaterial.getY(),ivMaterial.getBottom());
        materialDescendAnimation.setDuration(50000);
        materialDescendAnimation.setRepeatCount(0);*/
    }

    /**设置button的触摸监听，按下时触发螺旋旋转动画和物料下降动画，抬起时关闭两个动画
     * 物料下落动画，考虑帧动画
     */
    private OnTouchListener mTouchListener=new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action){
              case  MotionEvent.ACTION_DOWN:
                  //
                 windRotateAnimation.start();
                  materialDescendAnimation.start();
                  if(onMaterialButtonListener!=null){
                      onMaterialButtonListener.start();
                  }
                break;
               case  MotionEvent.ACTION_UP:
                 /*  windRotateAnimation.cancel();
                   ivWindstick.clearAnimation();
                   materialDescendAnimation.cancel();
                   ivMaterial.clearAnimation();*/
                   if(onMaterialButtonListener!=null){
                       onMaterialButtonListener.close();
                   }
                break;
            }
            return false;
        }
    };
    //用于监听按钮的触摸操作的借口回调
    public void setOnMaterialButtonListener(OnMaterialButtonListener onMaterialButtonListener){
        this.onMaterialButtonListener=onMaterialButtonListener;
    }
  public interface OnMaterialButtonListener{
      void start();
      void close();
  }
}
