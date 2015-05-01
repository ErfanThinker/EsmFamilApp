package net.crowmaster.esmfamil.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.entities.GameListItemEnt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by root on 4/28/15.
 */
public class GameListRecyclerAdapter extends RecyclerView.Adapter<GameListRecyclerAdapter.ContactViewHolder> {

    private ArrayList<GameListItemEnt> gameList;
    private Activity mHostingActivity;
    IOnListItemClickListener iOnListItemClickListener;
    Typeface tf;


    public interface IOnListItemClickListener{
        void onClick(View view);
    }

    public void setData(ArrayList<GameListItemEnt> data){
        Log.e("setData", "Entered");
        if (gameList != null) {
            gameList.clear();
        } else {
            gameList = new ArrayList<GameListItemEnt>();
        }
        if (data != null) {
            gameList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public GameListRecyclerAdapter(Activity a,ArrayList<GameListItemEnt> cl
            ,IOnListItemClickListener iOnListItemClickListener){
        this.tf = Typeface.createFromAsset(a.getResources().getAssets(),
                "bkoodak.ttf");
        this.gameList=cl;
        this.mHostingActivity=a;
        this.iOnListItemClickListener = iOnListItemClickListener;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.gamelist_grid, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnListItemClickListener.onClick(view);
            }
        });
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        GameListItemEnt ge=gameList.get(position);
        holder.gameName.setText(ge.gname);
        holder.rounds.setText("تعداد دور: " + Integer.toString(ge.rounds));
        holder.creatorName.setText(ge.cname);
        holder.capacityAndJoined.setText("ظرفیت: " + Integer.toString(ge.joined) +
                                         " از " + Integer.toString(ge.capacity));



        holder.grid.setCardBackgroundColor(Color.parseColor(ge.color));
    }


    @Override
    public int getItemCount() {
        return (gameList==null) ? 0 : gameList.size();
    }


    @Override
    public long getItemId(int position) {
        return gameList.get(position).gid;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView gameName;
        public TextView rounds;
        public TextView creatorName;
        public TextView capacityAndJoined;
        public CardView grid;
        public ContactViewHolder(View itemView) {
            super(itemView);
            rounds= (TextView) itemView.findViewById(R.id.rounds);
            gameName= (TextView) itemView.findViewById(R.id.game_name);
            creatorName= (TextView) itemView.findViewById(R.id.creator);
            capacityAndJoined= (TextView) itemView.findViewById(R.id.capacity_and_joined);
            rounds.setTypeface(tf);
            gameName.setTypeface(tf);
            creatorName.setTypeface(tf);
            capacityAndJoined.setTypeface(tf);
            grid= (CardView) itemView.findViewById(R.id.game_grid);
        }
    }
}
