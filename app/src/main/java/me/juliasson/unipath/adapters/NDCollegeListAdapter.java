package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;

import java.util.ArrayList;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.internal.GetCollegeInterface;

public class NDCollegeListAdapter extends RecyclerView.Adapter<NDCollegeListAdapter.ViewHolder> {

    private ArrayList<College> mColleges;
    private static Context mContext;
    private GetCollegeInterface ndGetCollegeInterface;

    private final static String KEY_COLLEGE_IMAGE = "image";

    public NDCollegeListAdapter(ArrayList<College> arrayList, GetCollegeInterface ndInterface) {
        mColleges = arrayList;
        ndGetCollegeInterface = ndInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View collegeView = inflater.inflate(R.layout.card_row, viewGroup, false);
        NDCollegeListAdapter.ViewHolder viewHolder = new NDCollegeListAdapter.ViewHolder(collegeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final College college = mColleges.get(position);
        viewHolder.tvCollegeName.setText(college.getCollegeName());

        Glide.with(mContext)
                .load(college.getParseFile(KEY_COLLEGE_IMAGE).getUrl())
                .into(viewHolder.ivCollegeImage);

        viewHolder.lbLikeButton.setLiked(true);
    }

    @Override
    public int getItemCount() {
        return mColleges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvCollegeName;
        public ImageView ivCollegeImage;
        public LikeButton lbLikeButton;


        public ViewHolder(View itemView) {
            super(itemView);

            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            ivCollegeImage = itemView.findViewById(R.id.ivCollegeImage);
            lbLikeButton = itemView.findViewById(R.id.lbLikeButton);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                College college = mColleges.get(position);
                ndGetCollegeInterface.setCollege(college);
            }
        }
    }
}
