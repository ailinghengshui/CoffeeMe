package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
public class CollectCoffeeDialog extends BaseCustomDialog{

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
        // Call super onResume after sizing
    }

    public interface CollectDialogable{
        void onCollectDialog(String string);
    }

    private CollectDialogable mCollecDialogable;
    public CollectCoffeeDialog(){

    }

    public CollectCoffeeDialog setListener(CollectDialogable mCollecDialogable){
        this.mCollecDialogable=mCollecDialogable;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_circle);
        return inflater.inflate(R.layout.dialog_collect_coffee,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(view!=null){
            final EditText etCollectDialogName = (EditText) view.findViewById(R.id.etCollectDialogName);
         /*   etCollectDialogName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });*/
            TextView tvCollectDialogCancel = (TextView) view.findViewById(R.id.tvCollectDialogCancel);
            TextView tvCollectDialogOK = (TextView) view.findViewById(R.id.tvCollectDialogOK);

            tvCollectDialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            etCollectDialogName.requestFocus();
            tvCollectDialogOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(etCollectDialogName.getText().toString().trim())){
                        ToastUtil.showShort(getActivity(),"咖啡没有名字呢!");
                    }else{
                        mCollecDialogable.onCollectDialog(etCollectDialogName.getText().toString().trim());
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
