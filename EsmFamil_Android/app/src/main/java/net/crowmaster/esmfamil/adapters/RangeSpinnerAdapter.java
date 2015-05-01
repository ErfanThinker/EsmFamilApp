package net.crowmaster.esmfamil.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.entities.IconTitleEnt;

/**
 * Created by root on 5/1/15.
 */
public class RangeSpinnerAdapter extends BaseAdapter {
    int low;
    int high;
    Activity mActivity;
    public RangeSpinnerAdapter (Activity activity, int Low, int High){
        low = Low;
        high = High;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return high -low + 1;
    }

    @Override
    public Object getItem(int i) {
        return low+i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        RangeSpinnerViewHolder holder;
        if(row==null){
            LayoutInflater inflater = ((Activity)mActivity).getLayoutInflater();
            row = inflater.inflate(R.layout.range_spinner_adapter, null, false);

            holder = new RangeSpinnerViewHolder();
            holder.mTextView = (TextView)row.findViewById(R.id.spinner_range_num);
            Typeface tf = Typeface.createFromAsset(mActivity.getResources().getAssets(),
                    "bkoodak.ttf");
            holder.mTextView.setTypeface(tf);

            row.setTag(holder);
        }else{
            holder = (RangeSpinnerViewHolder) row.getTag();
        }


        holder.mTextView.setText(Integer.toString(low + i));

        return row;
    }

    public static class RangeSpinnerViewHolder{
        public TextView mTextView;
    }
}
