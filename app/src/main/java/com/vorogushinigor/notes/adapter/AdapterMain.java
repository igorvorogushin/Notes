package com.vorogushinigor.notes.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vorogushinigor.notes.R;
import com.vorogushinigor.notes.other.Notes;

import java.util.ArrayList;

/**
 * Created by viv on 19.12.2015.
 */
public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {
    private Context mContext;
    private ArrayList<Notes> mNotesList;
    private OnItemsClickListener onItemsClickListener;
    private int lastPosition = -1;

    public interface OnItemsClickListener {
        void OnClick(int pos);

        void OnRemove(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextName;
        private TextView mTextMain;
        private TextView mTextDateCreate;
        private TextView mTextDateChange;
        private ImageButton mBtRemove;

        private ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.adapter__mynotes_cardview);
            mTextName = (TextView) itemView.findViewById(R.id.adapter__mynotes_name);
            mTextMain = (TextView) itemView.findViewById(R.id.adapter__mynotes_main);
            mBtRemove = (ImageButton) itemView.findViewById(R.id.adapter__mynotes_remove);
            mTextDateCreate = (TextView) itemView.findViewById(R.id.adapter__mynotes_time1);
            mTextDateChange = (TextView) itemView.findViewById(R.id.adapter__mynotes_time2);
        }
    }

    public AdapterMain(Context context, ArrayList<Notes> notesList) {
        this.mContext = context;
        this.mNotesList = notesList;
    }

    public void setOnItemsClickListener(OnItemsClickListener onItemsClickListener) {
        this.onItemsClickListener = onItemsClickListener;
    }

    public void remove(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mNotesList.size());
    }

    public void insert(int position) {
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mNotesList.size());
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_main, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemsClickListener != null) {
                    onItemsClickListener.OnClick(position);
                }
            }
        });

        viewHolder.mBtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemsClickListener != null) {
                    onItemsClickListener.OnRemove(position);
                }
            }
        });

        viewHolder.mTextName.setText(mNotesList.get(position).getName());
        String main = mNotesList.get(position).getMain();
        String mainShort = main;
        if (main.length() > 100) {
            mainShort = main.substring(0, 100);
            mainShort += "...";
        }
        viewHolder.mTextMain.setText(mainShort);
        String time1 = mNotesList.get(position).getTimeCreated();
        String time2 = mNotesList.get(position).getTimeChanged();
        viewHolder.mTextDateCreate.setText(mContext.getString(R.string.created) + " " + time1);
        if (time2 != null) {
            viewHolder.mTextDateChange.setText(mContext.getString(R.string.change) + " " + time2);
            viewHolder.mTextDateChange.setVisibility(View.VISIBLE);
        } else
            viewHolder.mTextDateChange.setVisibility(View.INVISIBLE);
        setAnimation(viewHolder.mCardView, position);
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}