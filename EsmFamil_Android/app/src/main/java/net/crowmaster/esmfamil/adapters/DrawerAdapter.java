package net.crowmaster.esmfamil.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.entities.IconTitleEnt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 4/27/15.
 */
public class DrawerAdapter extends ArrayAdapter<IconTitleEnt> {
    Context context;
    int layoutResourceId;
    ArrayList<IconTitleEnt> data = null;


    public DrawerAdapter(Context context, int resource, ArrayList<IconTitleEnt> data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }
    static class IconTitleEntHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        IconTitleEntHolder holder = null;
        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new IconTitleEntHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.icon);
            holder.txtTitle = (TextView)row.findViewById(R.id.title);
            Typeface tf = Typeface.createFromAsset(context.getResources().getAssets(),
                    "bkoodak.ttf");
            holder.txtTitle.setTypeface(tf);

            row.setTag(holder);
        }else{
            holder = (IconTitleEntHolder) row.getTag();
        }

        IconTitleEnt mIconTitleEnt = data.get(position);
        holder.txtTitle.setText(mIconTitleEnt.title);
        holder.imgIcon.setImageResource(mIconTitleEnt.icon);

        return row;

    }
}
