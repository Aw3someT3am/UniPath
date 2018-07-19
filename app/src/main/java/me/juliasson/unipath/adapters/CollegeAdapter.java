package me.juliasson.unipath.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.CollegeDetailsActivity;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.model.UserCollegeRelation;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.ViewHolder> implements Filterable{

    private ArrayList<College> mColleges;
    private Context mContext;

    private ArrayList<College> mFilteredList;

    private final static String KEY_COLLEGE_NAME = "name";
    private final static String KEY_COLLEGE_IMAGE = "image";

    public CollegeAdapter(ArrayList<College> arrayList) {
        mColleges = arrayList;
        mFilteredList = arrayList;
    }

    @NonNull
    @Override
    public CollegeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View collegeView = inflater.inflate(R.layout.card_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(collegeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CollegeAdapter.ViewHolder viewHolder, int position) {
        final College college = mColleges.get(position);
        viewHolder.tvCollegeName.setText(college.getString(KEY_COLLEGE_NAME));

        Glide.with(mContext)
                .load(college.getParseFile(KEY_COLLEGE_IMAGE).getUrl())
                .into(viewHolder.ivCollegeImage);

        loadFavoriteColleges(viewHolder, college);

        //TODO: IMPLEMENT
        viewHolder.lbLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addUserCollegeRelation(college);
                addUserDeadlineRelations(college);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
    }

    public void addUserCollegeRelation(final College college) {
        UserCollegeRelation userCollegeRelation = new UserCollegeRelation();
        userCollegeRelation.setUser(ParseUser.getCurrentUser());
        userCollegeRelation.setCollege(college);
        userCollegeRelation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("CollegeAdapter", "Create UserCollegeRelation success");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addUserDeadlineRelations(final College college) {
        CollegeDeadlineRelation.Query cdQuery = new CollegeDeadlineRelation.Query();
        cdQuery.getTop().withDeadline().withCollege();

        cdQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
            @Override
            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        CollegeDeadlineRelation relation = objects.get(i);
                        if (relation.getCollege().getObjectId().equals(college.getObjectId())) {
                            UserDeadlineRelation userDeadlineRelation = new UserDeadlineRelation();
                            userDeadlineRelation.setUser(ParseUser.getCurrentUser());
                            userDeadlineRelation.setDeadline(relation.getDeadline());
                            userDeadlineRelation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("CollegeAdapter", "Create UserDeadlineRelation success");
                                        Toast.makeText(mContext, "College and Deadlines added!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadFavoriteColleges(@NonNull final CollegeAdapter.ViewHolder viewHolder, final College college) {
        UserCollegeRelation.Query ucQuery = new UserCollegeRelation.Query();
        ucQuery.getTop().withUser().withCollege();

        ucQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        if(relation.getCollege().getObjectId().equals(college.getObjectId()) &&
                                relation.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            viewHolder.lbLikeButton.setLiked(true);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
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
//                popInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                ViewGroup container = (ViewGroup) popInflater.inflate(R.layout.activity_college_details,null);
//
//                popupWindow = new PopupWindow(container,600,600,true);
//                popupWindow.showAtLocation(SearchFragment.collegeDetails, Gravity.NO_GRAVITY, 500, 500);
                College college = mColleges.get(position);

                Bundle args = new Bundle();
                Intent intent = new Intent(mContext, CollegeDetailsActivity.class);
                intent.putExtra(College.class.getSimpleName(), Parcels.wrap(college));
                mContext.startActivity(intent);

                args.putParcelable("college", college);
//                ShowDetailsFragment fragmnent = new ShowDetailsFragment();
//                fragmnent.setArguments(args);
//                HomeActivity activity = (HomeActivity)context;
//                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.flContainer, fragmnent);
//                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mColleges;
                } else {
                    ArrayList<College> filteredList = new ArrayList<>();
                    for (College college : mColleges) {
                        if (college.getCollegeName().toLowerCase().contains(charString) || college.getCollegeName().toLowerCase().contains(charString) || college.getCollegeName().toLowerCase().contains(charString)) {
                            filteredList.add(college);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mColleges = (ArrayList<College>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
