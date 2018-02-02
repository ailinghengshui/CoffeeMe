package com.hzjytech.coffeeme.Dialogs;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

/**
 * Created by Hades on 2016/8/16.
 */
public class ValidateCodeDialog extends BaseCustomDialog {

    private static final String TITLE = "title";
    private static final String DESC = "desc";
    private static final String RES="res";
    private static final String IMAGEURL = "imageurl";
    private static final String CANCELSTR = "cancelstr";
    private static final String OKSTR = "okstr";
    private ITwoButtonClickWithOneParam iTwoButtonClick;
    private IImageClickListener iImageClickListener;
    private TextView tvValidateDialogTitle;
    private TextView tvValidateDialogDesc;
    private ImageView ivValidateDialogCode;
    private EditText etValidateDialogCode;
    private TextView btnValidateDialogOpleft;
    private TextView btnValidateDialogOpright;

    private static ValidateCodeDialog mUpdateDialog;

    public interface IImageClickListener {
        void onImageClickListener();
    }


    public static ValidateCodeDialog newInstance(String imgUrl) {
        return newInstance("请输入图片中的内容", "安全验证，点击图片更换", imgUrl, "取消", "确定");
    }
    public static ValidateCodeDialog newInstance(int imgRes) {
        return newInstance("请输入图片中的内容", "安全验证，点击图片更换", imgRes, "取消", "确定");
    }

    @Override
    protected void onBaseResume(Window window) {
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
    }
    //通过资源文件设置dialog
    public static ValidateCodeDialog newInstance(String title, String desc, int imgRes, String cancelStr, String okStr) {
        if(mUpdateDialog==null){
            mUpdateDialog=new ValidateCodeDialog();
        }
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESC, desc);
        args.putInt(RES, imgRes);
        args.putString(CANCELSTR, cancelStr);
        args.putString(OKSTR, okStr);
        mUpdateDialog.setArguments(args);
        return mUpdateDialog;
    }

    public static ValidateCodeDialog newInstance(String title, String desc, String imageUrl, String cancelStr, String okStr) {
        if (mUpdateDialog == null) {
            mUpdateDialog = new ValidateCodeDialog();
        }
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESC, desc);
        args.putString(IMAGEURL, imageUrl);
        args.putString(CANCELSTR, cancelStr);
        args.putString(OKSTR, okStr);
        mUpdateDialog.setArguments(args);
        return mUpdateDialog;
    }


    public void setImageurl(String imageurl) {
        if (!TextUtils.isEmpty(imageurl)) {
            loadValidateImage(imageurl);
        }
    }
    //加入资源文件设置验证码图片
    public void setImageRes(int Res){

        mUpdateDialog.ivValidateDialogCode.setImageResource(Res);
    }

    public void setOnImageClick(IImageClickListener iImageClickListener) {
        this.iImageClickListener = iImageClickListener;
    }

    public void setOnTwoClickListener(ITwoButtonClickWithOneParam iTwoButtonClick) {
        this.iTwoButtonClick = iTwoButtonClick;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_circle);
        return inflater.inflate(R.layout.dialog_validate_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            tvValidateDialogTitle = (TextView) view.findViewById(R.id.tvValidateDialogTitle);
            tvValidateDialogDesc = (TextView) view.findViewById(R.id.tvValidateDialogDesc);
            ivValidateDialogCode = (ImageView) view.findViewById(R.id.ivValidateDialogCode);
            etValidateDialogCode = (EditText) view.findViewById(R.id.etValidateDialogCode);
            btnValidateDialogOpleft = (TextView) view.findViewById(R.id.btnValidateDialogOpleft);
            btnValidateDialogOpright = (TextView) view.findViewById(R.id.btnValidateDialogOpright);

            if (getArguments() != null) {
                tvValidateDialogTitle.setText(getArguments().getString(TITLE));
                tvValidateDialogDesc.setText(getArguments().getString(DESC));

                if (!TextUtils.isEmpty(getArguments().getString(IMAGEURL))) {
                    loadValidateImage(getArguments().getString(IMAGEURL));
                }else {
                    ivValidateDialogCode.setImageResource(getArguments().getInt(RES));
                };
                btnValidateDialogOpleft.setText(getArguments().getString(CANCELSTR));
                btnValidateDialogOpright.setText(getArguments().getString(OKSTR));

                ivValidateDialogCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.isFastClick()) {
                            return;
                        }

                        if (iImageClickListener != null) {
                            iImageClickListener.onImageClickListener();
                        }

                    }
                });

                btnValidateDialogOpleft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            iTwoButtonClick.onLeftButtonClick();
                            etValidateDialogCode.setText("");
                            dismiss();
                        }
                    }
                });
                btnValidateDialogOpright.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iTwoButtonClick != null) {
                            if (TextUtils.isEmpty(etValidateDialogCode.getText().toString())) {
                                ToastUtil.showShort(getContext(), "验证码不能为空");
                            } else {
                                iTwoButtonClick.onRightButtonClick(etValidateDialogCode.getText().toString());
                                etValidateDialogCode.setText("");
                                dismiss();
                            }
                        }
                    }
                });
            }

        }
    }

    private void loadValidateImage(String url) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        ImageLoader.getInstance().displayImage(url, ivValidateDialogCode, options);

    }
}
