package com.hzjytech.coffeeme.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.ToastUtil;

/**
 * Created by Hades on 2016/4/6.
 */
public class SelectDialog extends DialogFragment{

    private static final String BALANCE = "balance";
    public static int[] icons = {R.drawable.img_jijiapay, R.drawable.img_alipay, R.drawable.img_wechatpay};
    public static String[] titles = {"Coffee Me钱包支付", "支付宝支付", "微信支付"};
    public static String[] descs = {"推荐Coffee Me用户使用", "推荐支付宝用户使用", "推荐微信账户使用"};

    private GridLayoutManager mWrappableGridLayoutManager;

    private RecyclerView rcyViewSelectdialogLst;

    public interface SelectDialogListener {
        void onSelectDialogListener(int which);
    }

    private SelectDialogListener mListener;

    public void setWrappableGridLayoutManager(GridLayoutManager wrappableGridLayoutManager) {
        this.mWrappableGridLayoutManager = wrappableGridLayoutManager;
    }

    public void setListener(SelectDialogListener listener) {
        this.mListener = listener;
    }

    private static final String VIEWID = "viewId";

    public static SelectDialog newInstance(int viewId, String balance) {

        Bundle args = new Bundle();

        args.putInt(VIEWID, viewId);
        args.putString(BALANCE, balance);
        SelectDialog selectDialog = new SelectDialog();
        selectDialog.setArguments(args);
        return selectDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_circle);
        return inflater.inflate(R.layout.dialog_select, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvSelectdialogCancel = (TextView) view.findViewById(R.id.tvSelectdialogCancel);
        tvSelectdialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rcyViewSelectdialogLst = (RecyclerView) view.findViewById(R.id.rcyViewSelectdialogLst);
        rcyViewSelectdialogLst.setAdapter(new SelectDialogAdapter());
        rcyViewSelectdialogLst.setLayoutManager(mWrappableGridLayoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.CollectDialogAnimation;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        //set width=screenwidth, height=wrap_content
        window.setLayout((int) (size.x), WindowManager.LayoutParams.WRAP_CONTENT);
        //set dialog location center
        window.setGravity(Gravity.BOTTOM);
        // Call super onResume after sizing
        super.onResume();
    }

    class SelectDialogAdapter extends RecyclerView.Adapter<SelectDialogAdapter.SelectViewHolder> {

        @Override
        public SelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_item, parent, false);
            SelectViewHolder selectViewHolder = new SelectViewHolder(view);

            return selectViewHolder;
        }

        @Override
        public void onBindViewHolder(SelectViewHolder holder, int position) {
            holder.ivSltdlgitemLogo.setImageResource(icons[position]);
            holder.tvSltdlgitemName.setText(titles[position]);
            if (position == 0) {
                holder.tvSltdlgitemBalance.setVisibility(View.VISIBLE);
                holder.tvSltdlgitemBalance.setText(getString(R.string.str_balance, getArguments().getString(BALANCE, "0.0")));
            } else {
                holder.tvSltdlgitemBalance.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return icons.length;
        }

        class SelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final ImageView ivSltdlgitemLogo;
            private final TextView tvSltdlgitemName;
            private final TextView tvSltdlgitemBalance;

            public SelectViewHolder(View itemView) {
                super(itemView);
                ivSltdlgitemLogo = (ImageView) itemView.findViewById(R.id.ivSltdlgitemLogo);
                tvSltdlgitemName = (TextView) itemView.findViewById(R.id.tvSltdlgitemName);
                tvSltdlgitemBalance = (TextView) itemView.findViewById(R.id.tvSltdlgitemBalance);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

                mListener.onSelectDialogListener(getLayoutPosition());
                dismiss();
            }
        }
    }

}
