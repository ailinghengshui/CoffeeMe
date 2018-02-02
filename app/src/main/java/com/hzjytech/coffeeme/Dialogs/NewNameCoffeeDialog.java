package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;

/**
 * Created by Hades on 2016/6/2.
 */
public class NewNameCoffeeDialog extends BaseCustomDialog{

    @Override
    protected void onBaseResume(Window window) {
//        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
         // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Call super onResume after sizing
    }

    public interface NewNameCoffeeDialogable{
        void onCollectDialog(String string);
    }

    private NewNameCoffeeDialogable mNewNameCoffeeDialogable;
    public NewNameCoffeeDialog(){

    }

    public NewNameCoffeeDialog setListener(NewNameCoffeeDialogable mNewNameCoffeeDialogable){
        this.mNewNameCoffeeDialogable=mNewNameCoffeeDialogable;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_circle);
        return inflater.inflate(R.layout.dialog_new_name_coffee,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(view!=null){
            final EditText etCollectDialogName = (EditText) view.findViewById(R.id.etCollectDialogName);
//            etCollectDialogName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    if(b){
//                        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                    }
//                }
//            });
            final TextView tvCollectDialogCancel = (TextView) view.findViewById(R.id.tvCollectDialogCancel);
            final TextView tvCollectDialogOK = (TextView) view.findViewById(R.id.tvCollectDialogOK);
            etCollectDialogName.requestFocus();
            tvCollectDialogOK.setTextColor(getResources().getColor(R.color.normal_grey));
            etCollectDialogName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(TextUtils.isEmpty(charSequence)){
                        tvCollectDialogOK.setClickable(false);
                        tvCollectDialogOK.setTextColor(getResources().getColor(R.color.normal_grey));


                    }else{
                        tvCollectDialogOK.setClickable(true);
                        tvCollectDialogOK.setTextColor(getResources().getColor(R.color.normal_blue));

                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(TextUtils.isEmpty(charSequence)){
                        tvCollectDialogOK.setClickable(false);
                        tvCollectDialogOK.setTextColor(getResources().getColor(R.color.normal_grey));
                    }else{
                        tvCollectDialogOK.setClickable(true);
                        tvCollectDialogOK.setTextColor(getResources().getColor(R.color.normal_blue));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            tvCollectDialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            tvCollectDialogOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(etCollectDialogName.getText().toString().trim())){
                        ToastUtil.showShort(getActivity(),"咖啡没有名字呢!");
                    }else{
                        mNewNameCoffeeDialogable.onCollectDialog(etCollectDialogName.getText().toString().trim());
                        dismiss();
                    }
                }
            });
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
//        alertDialogBuilder.setTitle("收藏");
//        final EditText editText=new EditText(getActivity());
//        editText.setHint("收藏咖啡的名字");
//        alertDialogBuilder.setView(editText, DensityUtil.dp2px(getContext(),16),0,DensityUtil.dp2px(getContext(),16),0);
//
//        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(!TextUtils.isEmpty(editText.getText().toString().trim())){
//                    if(mCollecDialogable!=null){
//                        mCollecDialogable.onCollectDialog(editText.getText().toString().trim());
//                    }
//                    dismiss();
//                }else{
//                    ToastUtil.showShort(getActivity(),"咖啡没有名字呢!");
//                }
//            }
//        });
//        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//
//            }
//        });
//        return alertDialogBuilder.create();
//    }

}
