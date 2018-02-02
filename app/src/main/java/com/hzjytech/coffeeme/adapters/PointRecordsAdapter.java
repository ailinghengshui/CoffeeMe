package com.hzjytech.coffeeme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.PointRecord;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.widgets.row.GroupRowView;
import com.hzjytech.coffeeme.widgets.row.RowViewDesc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Hades on 2016/7/28.
 */
public class PointRecordsAdapter extends RecyclerView.Adapter<PointRecordsAdapter.PointRecordsOrderViewHolder> {

    private static final String TAG = PointRecordsAdapter.class.getSimpleName();
    private Context context;
    private List<PointRecord> mRecords = new ArrayList<>();

    public PointRecordsAdapter(Context context, List<PointRecord> records) {
        this.context = context;
        if (records != null && records.size() > 0) {
            this.mRecords.addAll(records);
        }
    }

    @Override
    public PointRecordsOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pointrecord_order, parent, false);
        return new PointRecordsOrderViewHolder(view);
    }

    public void refreshData(List<PointRecord> recordLst) {
        if (recordLst != null && recordLst.size() > 0) {
            if (!mRecords.isEmpty()) {
                mRecords.clear();
            }

            this.mRecords.addAll(recordLst);
            this.notifyDataSetChanged();
        } else {
            Log.d(TAG + "refreshData", "recordLst is null");
        }
    }

    public void loadMoreData(List<PointRecord> recordLst) {
        if (recordLst != null && recordLst.size() > 0) {
            this.mRecords.addAll(recordLst);
            this.notifyDataSetChanged();
        } else {
            Log.d(TAG + "loadMoreData", "recordLst is null");
        }
    }


    @Override
    public void onBindViewHolder(PointRecordsAdapter.PointRecordsOrderViewHolder holder, int position) {
        switch (mRecords.get(position).getSource_type()) {
            case 2:
                holder.tvPointrecorditem.setText("付款获得");
                holder.tvPointrecorditemValue.setText("+" + mRecords.get(position).getValue());
                holder.tvPointrecorditemValue.setTextColor(context.getResources().getColor(R.color.grey_green));
                break;
            case 1:
                holder.tvPointrecorditem.setText("订单抵扣");
                holder.tvPointrecorditemValue.setText("-" + mRecords.get(position).getValue());
                holder.tvPointrecorditemValue.setTextColor(context.getResources().getColor(R.color.light_red));
                break;

            default:
                holder.tvPointrecorditem.setText("兑换优惠券");

                holder.tvPointrecorditemValue.setText("-" + mRecords.get(position).getValue());
                holder.tvPointrecorditemValue.setTextColor(context.getResources().getColor(R.color.light_red));

                break;
        }

        if(!TextUtils.isEmpty(mRecords.get(position).getCreated_at())){
            holder.tvPointrecorditemDate.setText(getDateString(mRecords.get(position).getCreated_at()));
        }

    }

    private String getDoubleString(double d) {
        return "" + d;
    }

    private String getDateString(String date) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Calendar calendar = DateUtil.ISO8601toCalendar(date);
            stringBuilder.append(DateUtil.getYear(calendar)+".");
            stringBuilder.append(DateUtil.getMonth(calendar) + ".");
            stringBuilder.append(DateUtil.getDay(calendar) + " ");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @Override
    public int getItemCount() {
        return mRecords == null ? 0 : mRecords.size();
    }

    public static class PointRecordsOrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvPointrecorditemValue;
        private final TextView tvPointrecorditemDate;
        private final TextView tvPointrecorditem;

        public PointRecordsOrderViewHolder(View itemView) {
            super(itemView);
            tvPointrecorditem = (TextView) itemView.findViewById(R.id.tvPointrecorditem);
            tvPointrecorditemValue = (TextView) itemView.findViewById(R.id.tvPointrecorditemValue);
            tvPointrecorditemDate = (TextView) itemView.findViewById(R.id.tvPointrecorditemDate);
        }
    }


}
