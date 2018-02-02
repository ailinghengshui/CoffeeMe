package com.hzjytech.coffeeme.me;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Hades on 2016/3/1.
 */
public class ListDialog extends DialogFragment {

    private DialogFragmentable dialogFragmentable;

    interface DialogFragmentable{
        void onDialogFragemntClick(int which);
    }

    public static final String TITLE = "title";
    public static final String ITEMS = "items";
    private static final String BTNSTR ="btnStr" ;
    private String[] items;
    private String title;
    private String btnStr;

    public static ListDialog newInstance(String title,String btnStr,String[] items){
        ListDialog listDialog=new ListDialog();
        Bundle args=new Bundle();
        args.putString(TITLE,title);
        args.putString(BTNSTR, btnStr);
        args.putStringArray(ITEMS,items);
        listDialog.setArguments(args);

        return listDialog;
    }

    public ListDialog setDialogFragmentable(DialogFragmentable dialogFragmentable){
        this.dialogFragmentable=dialogFragmentable;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            items=getArguments().getStringArray(ITEMS);
            title=getArguments().getString(TITLE);
            btnStr=getArguments().getString(BTNSTR);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogFragmentable.onDialogFragemntClick(which);

            }
        });
        builder.setPositiveButton(btnStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }
}
