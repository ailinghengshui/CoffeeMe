package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hzjytech.coffeeme.utils.ToastUtil;

/**
 * Created by Hades on 2016/4/6.
 */
public class Base2Dialog extends DialogFragment implements View.OnClickListener {


    @Override
    public void onClick(View v) {
        base2Dialogable.onClick();
        dismiss();
    }

    public interface Base2Dialogable{
        void onBase2Dialog(View view);
        void onClick();
    }

    private Base2Dialogable base2Dialogable;

    private static final String VIEWID = "viewId";
    private int viewId;

    public static Base2Dialog newInstance(int viewId){

        Bundle args=new Bundle();

        args.putInt(VIEWID, viewId);
        Base2Dialog base2Dialog=new Base2Dialog();
        base2Dialog.setArguments(args);
        return base2Dialog;
    }

    public Base2Dialog setListener(Base2Dialogable base2Dialogable){
        this.base2Dialogable=base2Dialogable;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            viewId=getArguments().getInt(VIEWID);

        }else{
            ToastUtil.showShort(getActivity(), "传参失败");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(viewId,container,false);
        base2Dialogable.onBase2Dialog(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog=super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
