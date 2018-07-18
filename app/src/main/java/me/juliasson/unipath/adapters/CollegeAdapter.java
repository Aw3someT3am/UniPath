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

import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;

public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.ViewHolder>{

    private List<College> mColleges;
    private Context mContext;

    private final static String KEY_COLLEGE_NAME = "name";
    private final static String KEY_EARLY_ACTION = "earlyAction";
    private final static String KEY_REGULAR_ACTION = "regularAction";
    private final static String KEY_COLLEGE_IMAGE = "image";

    @NonNull
    @Override
    public CollegeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View collegeView = inflater.inflate(R.layout.college, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(collegeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CollegeAdapter.ViewHolder viewHolder, int position) {
        College college = mColleges.get(position);
        viewHolder.tvCollegeName.setText(college.getString(KEY_COLLEGE_NAME));
        viewHolder.tvEarlyActionDate.setText(college.getDate(KEY_EARLY_ACTION).toString());
        viewHolder.tvRegularActionDate.setText(college.getDate(KEY_REGULAR_ACTION).toString());

        Glide.with(mContext)
                .load(college.getParseFile(KEY_COLLEGE_IMAGE).getUrl())
                .into(viewHolder.ivCollegeImage);
    }

    @Override
    public int getItemCount() {
        return mColleges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvCollegeName;
        public TextView tvEarlyActionDate;
        public TextView tvRegularActionDate;
        public ImageView ivCollegeImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            tvEarlyActionDate = itemView.findViewById(R.id.tvEarlyActionDate);
            tvRegularActionDate = itemView.findViewById(R.id.tvRegularActionDate);
            ivCollegeImage = itemView.findViewById(R.id.ivCollegeImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                College college = mColleges.get(position);
                //TODO: implement viewing a detail fragment
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mColleges.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<College> list) {
        mColleges.addAll(list);
        notifyDataSetChanged();
    }
}
