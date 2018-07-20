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
import me.juliasson.unipath.model.Deadline;
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
        final College college = mFilteredList.get(position);
        viewHolder.tvCollegeName.setText(college.getString(KEY_COLLEGE_NAME));

        Glide.with(mContext)
                .load(college.getParseFile(KEY_COLLEGE_IMAGE).getUrl())
                .into(viewHolder.ivCollegeImage);

        loadFavoriteColleges(viewHolder, college);

//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));
//        Log.d("CollegeAdapter", DateTimeUtils.parseDateTime(college.getEarlyAction().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));


        viewHolder.lbLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addUserCollegeRelation(college);
                addUserDeadlineRelations(college);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removeUserCollegeRelation(college);
                removeUserDeadlinesRelation(college);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
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
                College college = mFilteredList.get(position);

                Bundle args = new Bundle();
                Intent intent = new Intent(mContext, CollegeDetailsActivity.class);
                intent.putExtra(College.class.getSimpleName(), Parcels.wrap(college));
                mContext.startActivity(intent);

                args.putParcelable("college", college);
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

                    for (College college: mColleges) {

                        if (college.getCollegeName().toLowerCase().contains(charString)) {

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
                mFilteredList = (ArrayList<College>) filterResults.values;
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

    /**
     * Grabs all of the "liked" colleges related to the user.
     * @param viewHolder the view of the college being determined as "liked" or "not liked"
     * @param college the college being deemed "liked" or "not liked"
     */
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

    // ---------------------REMOVING AND ADDING RELATIONS BASED ON CLICKING LIKE BUTTON-------------------------

    /**
     * Removes a UserCollegeRelation row from the parse-dashboard database based on inputted college.
     * @param college the college being unrelated to the user.
     */
    private void removeUserCollegeRelation(final College college) {
        UserCollegeRelation.Query ucQuery = new UserCollegeRelation.Query();
        ucQuery.getTop().withCollege().withUser();
        ucQuery.whereEqualTo("college", college);
        ucQuery.whereEqualTo("user", ParseUser.getCurrentUser());

        ucQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        try {
                            UserCollegeRelation relation = objects.get(i);
                            relation.delete();
                            relation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.d("CollegeAdapter", "College User Relation removed");
                                }
                            });
                        } catch (ParseException o) {
                            o.printStackTrace();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Removes all UserDeadlineRelation rows from the parse-dashboard database based on inputted college.
     * @param college the college whose deadlines are being unrelated to the user.
     */
    private void removeUserDeadlinesRelation(final College college) {
        CollegeDeadlineRelation.Query cdQuery = new CollegeDeadlineRelation.Query();
        cdQuery.getTop().withDeadline().withCollege();
        cdQuery.whereEqualTo("college", college);

        cdQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
            @Override
            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        CollegeDeadlineRelation relation = objects.get(i);
                        Deadline deadline = relation.getDeadline();

                        //Making a new query to remove deadlines of a specific college from being related to user.
                        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
                        udQuery.getTop().withDeadline().withUser();

                        udQuery.whereEqualTo("deadline", deadline);
                        udQuery.whereEqualTo("user", ParseUser.getCurrentUser());

                        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
                            @Override
                            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                                if (e == null) {
                                    for (int i = 0; i < objects.size(); i++) {
                                        try {
                                            UserDeadlineRelation relation = objects.get(i);
                                            relation.delete();
                                            relation.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Log.d("College Adapter", "User Deadline Relation removed");
                                                }
                                            });
                                        } catch (ParseException o) {
                                            o.printStackTrace();
                                        }
                                    }
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Adds UserCollegeRelation to parse-dashboard database based on college.
     * @param college the college the user wants to be related to.
     */
    private void addUserCollegeRelation(final College college) {
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

    /**
     * Adds UserDeadlineRelations to parse-dashboard databased based on college.
     * @param college the college whose deadlines are now being related to the user.
     */
    private void addUserDeadlineRelations(final College college) {
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
                            userDeadlineRelation.setCompleted(false);
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
}
