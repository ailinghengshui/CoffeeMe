package com.hzjytech.coffeeme.culture;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.StringBuilderPrinter;
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
import com.hzjytech.coffeeme.adapters.SharedFragmentAdapter;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;

/**
 * Created by Hades on 2016/3/9.
 */
public class SharedFragment extends DialogFragment {


    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mTitles;
    private IMethod1Listener mIMethod1Listener;

    public void setAdapter(String[] titles, IMethod1Listener iMethod1Listener, RecyclerView.LayoutManager layoutManager) {
        this.mTitles = titles;
        this.mIMethod1Listener = iMethod1Listener;
        this.mLayoutManager = layoutManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_circle);
        return inflater.inflate(R.layout.dialog_bottom_select, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rcyViewSelectbottomdialog = (RecyclerView) view.findViewById(R.id.rcyViewSelectbottomdialog);
        rcyViewSelectbottomdialog.setLayoutManager(mLayoutManager);
        rcyViewSelectbottomdialog.setAdapter(new SharedFragmentAdapter(mTitles, mIMethod1Listener));
        view.findViewById(R.id.tvSelectbottomdialogCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    public static class SharedFragmentAdapter extends RecyclerView.Adapter<SharedFragmentAdapter.SharedFragmentAdapterViewHolder> {

        private final String[] mTitles;
        private final IMethod1Listener mIMethod1Listener;

        public SharedFragmentAdapter(String[] titles) {
            this(titles, null);
        }

        public SharedFragmentAdapter(String[] titles, IMethod1Listener iMethod1Listener) {
            this.mTitles = titles;
            this.mIMethod1Listener = iMethod1Listener;
        }

        @Override
        public SharedFragmentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_count_item, parent, false);
            return new SharedFragmentAdapterViewHolder(root);
        }

        @Override
        public void onBindViewHolder(SharedFragmentAdapterViewHolder holder, final int position) {
            holder.tvCountItem.setText(mTitles[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIMethod1Listener != null) {
                        mIMethod1Listener.OnMethod1Listener(position);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {

            return mTitles.length;
        }

        class SharedFragmentAdapterViewHolder extends RecyclerView.ViewHolder {


            private final TextView tvCountItem;

            public SharedFragmentAdapterViewHolder(View itemView) {
                super(itemView);
                tvCountItem = (TextView) itemView.findViewById(R.id.tvCountItem);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        //set width=screenwidth, height=wrap_content
        window.setLayout((int) (size.x*0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        //set dialog location center
        window.setGravity(Gravity.BOTTOM);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.CollectDialogAnimation;
    }

//    class SpacesItemDecoration extends RecyclerView.ItemDecoration{
//
//        private final int mSpace;
//
//        public SpacesItemDecoration(int mSpace) {
//            this.mSpace = mSpace;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.left=mSpace;
//            outRect.right=mSpace;
//            outRect.bottom=mSpace;
//            outRect.top=mSpace;
//        }
//    }

}