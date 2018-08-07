package me.juliasson.unipath.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.CollegeDetailsDialog;
import me.juliasson.unipath.activities.MapActivity;
import me.juliasson.unipath.internal.GetItemDetailOpenedInterface;
import me.juliasson.unipath.internal.GetCollegeLikedOnSearchListView;
import me.juliasson.unipath.internal.GetCollegeUnlikedFromProfileAdapter;
import me.juliasson.unipath.internal.LikedRefreshInterface;
import me.juliasson.unipath.internal.LikesInterface;
import me.juliasson.unipath.internal.SearchInterface;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.model.UserCollegeRelation;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.Constants;
import me.juliasson.unipath.utils.DateTimeUtils;

public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.ViewHolder> implements Filterable, LikesInterface {

    private ArrayList<College> mColleges;
    private static Context mContext;

    private ArrayList<College> mFilteredList;

    private final static int LIKED = 10;

    private static final String TAG = "AddToDatabase";

    private static FirebaseDatabase mFirebaseDatabase;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static DatabaseReference myRef;

    private static SearchInterface searchInterface;
    private static LikesInterface likesInterface;
    private static LikedRefreshInterface likedRefreshInterface;
    private static GetCollegeLikedOnSearchListView likedOnSearchListView;
    private static GetCollegeUnlikedFromProfileAdapter unlikedFromProfileAdapterInterface;
    private static GetItemDetailOpenedInterface collegeDetailOpenedInterface;

    public CollegeAdapter(ArrayList<College> arrayList,
                          SearchInterface searchInterface,
                          LikedRefreshInterface likedRefreshInterface,
                          GetCollegeLikedOnSearchListView likedOnSearchListView,
                          GetItemDetailOpenedInterface collegeDetailOpenedInterface) {
        CollegeAdapter.searchInterface = searchInterface;
        CollegeAdapter.likedRefreshInterface = likedRefreshInterface;
        CollegeAdapter.likedOnSearchListView = likedOnSearchListView;
        CollegeAdapter.collegeDetailOpenedInterface = collegeDetailOpenedInterface;
        mColleges = arrayList;
        mFilteredList = arrayList;
    }

    Button mapsButton;

    public CollegeAdapter(ArrayList<College> arrayList, GetCollegeUnlikedFromProfileAdapter unlikedFromProfileadapterInterface) {
        mColleges = arrayList;
        mFilteredList = arrayList;
        CollegeAdapter.unlikedFromProfileAdapterInterface = unlikedFromProfileadapterInterface;
    }

    @NonNull
    @Override
    public CollegeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        View collegeView = inflater.inflate(R.layout.card_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(collegeView);

        mapsButton = (Button) collegeView.findViewById(R.id.mapsButton);

        likesInterface = this;
        CollegeDetailsDialog.setLiked(likesInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CollegeAdapter.ViewHolder viewHolder, int position) {
        final College college = mFilteredList.get(position);
        viewHolder.tvCollegeName.setText(college.getCollegeName());

        mAuth = FirebaseAuth.getInstance();

        Glide.with(mContext)
                .load(college.getParseFile(Constants.KEY_COLLEGE_IMAGE).getUrl())
                .into(viewHolder.ivCollegeImage);

        viewHolder.lbLikeButton.setLiked(false);
        loadFavoriteColleges(viewHolder, college);

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
                likedOnSearchListView.getCollegeLikedOnSearchListView(true);
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send one college to map activity as arraylist object
                Intent i = new Intent(mContext, MapActivity.class);
                ArrayList<College> singleCollege = new ArrayList<>();
                singleCollege.add(college);
                i.putParcelableArrayListExtra("favoritedList", singleCollege);
                mContext.startActivity(i);
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
        public Button mapsButton;


        public ViewHolder(View itemView) {
            super(itemView);

            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            ivCollegeImage = itemView.findViewById(R.id.ivCollegeImage);
            lbLikeButton = itemView.findViewById(R.id.lbLikeButton);
            mapsButton = (Button) itemView.findViewById(R.id.mapsButton);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                collegeDetailOpenedInterface.getItemDetailOpened(true);
                College college = mFilteredList.get(position);
                Intent intent = new Intent(mContext, CollegeDetailsDialog.class);
                intent.putExtra(College.class.getSimpleName(), Parcels.wrap(college));
                ((Activity) mContext).startActivityForResult(intent, LIKED);
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
                searchInterface.setValues(mFilteredList);
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<College>) filterResults.values;
                searchInterface.setValues(mFilteredList);
                notifyDataSetChanged();
            }
        };
    }

    public Filter getSelectionFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                final int SIZE_INDEX = 0;
                final int ISCOST_INDEX = 1;
                final int OSCOST_INDEX = 2;
                final int ACCEPTANCE_INDEX = 3;
                final int STATE_INDEX = 4;
                List<String> items = Arrays.asList(charString.split("\\s*,\\s*"));

                //Each of the filtered items will have SIZE: 2 objects separated by space: 2 objects, 2 objects, one object, one string
                String[] size_bounds = items.get(SIZE_INDEX).split(" ");
                int filter_pop_low = Integer.parseInt(size_bounds[0]);
                int filter_pop_high = Integer.parseInt(size_bounds[1]);

                String[] isCost_bounds = items.get(ISCOST_INDEX).split(" ");
                int filter_isCost_low = Integer.parseInt(isCost_bounds[0]);
                int filter_isCost_high = Integer.parseInt(isCost_bounds[1]);

                String[] osCost_bounds = items.get(OSCOST_INDEX).split(" ");
                int filter_osCost_low = Integer.parseInt(osCost_bounds[0]);
                int filter_osCost_high = Integer.parseInt(osCost_bounds[1]);

                double filter_acceptanceRate = Double.parseDouble(items.get(ACCEPTANCE_INDEX));
                String filter_address = items.get(STATE_INDEX);

                mFilteredList = mColleges;
                ArrayList<College> filteredList = new ArrayList<>();
                for (College college: mFilteredList) {
                    int population = college.getStudentPopulation();
                    int isCost = college.getInStateCost();
                    int osCost = college.getOutOfStateCost();
                    double acceptanceRate = college.getAcceptanceRate();
                    String address = college.getAddress();

                    if ((population >= filter_pop_low && population <= filter_pop_high) &&
                            (isCost >= filter_isCost_low && isCost <= filter_isCost_high) &&
                            (osCost >= filter_osCost_low && osCost <= filter_osCost_high) &&
                            acceptanceRate >= filter_acceptanceRate) {
                        if (filter_address.equals("Any") || address.contains(String.format(", %s", filter_address)))
                        filteredList.add(college);
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                searchInterface.setValues(mFilteredList);
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<College>) filterResults.values;
                searchInterface.setValues(mFilteredList);
                notifyDataSetChanged();
            }
        };
    }

    // Clean all elements of the recycler
    public void clear() {
        mColleges.clear();
        notifyDataSetChanged();
    }

    // Clean all elements of the recycler
    public void clearWithFilter() {
        mFilteredList.clear();
        notifyDataSetChanged();
    }

        // Add a list of items -- change to type used
    public void addAll(List<College> list) {
        mColleges.addAll(list);
        notifyDataSetChanged();
    }

    public void addAllFiltered(List<College> list) {
        if(list != null) {
            mFilteredList.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * Grabs all of the "liked" colleges related to the user.
     * @param viewHolder the view of the college being determined as "liked" or "not liked"
     * @param college the college being deemed "liked" or "not liked"
     */
    private void loadFavoriteColleges(@NonNull final CollegeAdapter.ViewHolder viewHolder, final College college) {
        UserCollegeRelation.Query ucQuery = new UserCollegeRelation.Query();
        ucQuery.getTop().withUser().withCollege();
        ucQuery.whereEqualTo("college", college);
        ucQuery.whereEqualTo("user", ParseUser.getCurrentUser());

        ucQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        viewHolder.lbLikeButton.setLiked(true);
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
    public static void removeUserCollegeRelation(final College college) {
        UserCollegeRelation.Query ucQuery = new UserCollegeRelation.Query();
        ucQuery.getTop().withCollege().withUser();
        ucQuery.whereEqualTo(Constants.KEY_COLLEGE, college);
        ucQuery.whereEqualTo(Constants.KEY_USER, ParseUser.getCurrentUser());

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
    public static void removeUserDeadlinesRelation(final College college) {
        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
        udQuery.getTop().withDeadline().withUser().withCollege();
        udQuery.whereEqualTo(Constants.KEY_COLLEGE, college);
        udQuery.whereEqualTo(Constants.KEY_USER, ParseUser.getCurrentUser());

        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        try {
                            UserDeadlineRelation relation = objects.get(i);
                            Log.d(TAG, "onClick: Attempting to add object to database.");
                            String date = DateTimeUtils.parseDateTime(relation.getDeadline().getDeadlineDate().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat);
                            String collegeName = relation.getCollege().getCollegeName();
                            if(!date.equals("")){
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                myRef.child(mContext.getString(R.string.dbnode_users)).child(userID).child("dates").child(collegeName).child(date).removeValue();
                                //toastMessage("Removing " + date + " to database...");
                            }
                            relation.delete();
                            relation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    //Toast.makeText(mContext, "College and Deadlines removed!", Toast.LENGTH_SHORT).show();
                                    Log.d("College Adapter", "User Deadline Relation removed");
                                }
                            });
                        } catch (ParseException o) {
                            o.printStackTrace();
                        }

                    }
                    likedOnSearchListView.getCollegeLikedOnSearchListView(true);
                    unlikedFromProfileAdapterInterface.getCollegeUnliked(true);
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
    public static void addUserCollegeRelation(final College college) {
        UserCollegeRelation userCollegeRelation = new UserCollegeRelation();
        userCollegeRelation.setUser(ParseUser.getCurrentUser());
        userCollegeRelation.setCollege(college);
        userCollegeRelation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("CollegeAdapter", "Create UserCollegeRelation success");
                } else {
                    Log.d("CollegeAdapter", "Create UserCollegeRelation failure");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Adds UserDeadlineRelations to parse-dashboard databased based on college.
     * @param college the college whose deadlines are now being related to the user.
     */
    public static void addUserDeadlineRelations(final College college) {
        CollegeDeadlineRelation.Query cdQuery = new CollegeDeadlineRelation.Query();
        cdQuery.getTop().withDeadline().withCollege();
        cdQuery.whereEqualTo(Constants.KEY_COLLEGE, college);

        cdQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
            @Override
            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        CollegeDeadlineRelation relation = objects.get(i);
                        UserDeadlineRelation userDeadlineRelation = new UserDeadlineRelation();
                        userDeadlineRelation.setUser(ParseUser.getCurrentUser());
                        userDeadlineRelation.setDeadline(relation.getDeadline());
                        mAuthListener = new FirebaseAuth.AuthStateListener() {
                            @Override
                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    // User is signed in
                                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                                    toastMessage("Successfully signed in with: " + user.getEmail());
                                } else {
                                    // User is signed out
                                    Log.d(TAG, "onAuthStateChanged:signed_out");
                                    toastMessage("Successfully signed out.");
                                }
                                    // ...
                                }
                            };

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Object value = dataSnapshot.getValue();
                                Log.d(TAG, "Value is: " + value);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

                        Log.d(TAG, "onClick: Attempting to add object to database.");
                        String date = DateTimeUtils.parseDateTime(relation.getDeadline().getDeadlineDate().toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat);
                        String collegeName = relation.getCollege().getCollegeName();
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();
                        myRef.child(mContext.getString(R.string.dbnode_users)).child(userID).child("dates").child(collegeName).child(date).setValue(true);

                        userDeadlineRelation.setCompleted(false);
                        userDeadlineRelation.setCollege(college);
                        userDeadlineRelation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("CollegeAdapter", "Create UserDeadlineRelation success");
                                } else {
                                    Log.d("CollegeAdapter", "Create UserDeadlineRelation failure");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    likedOnSearchListView.getCollegeLikedOnSearchListView(true);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private static void toastMessage(String message){
        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setValues(boolean isChanged) {
        collegeDetailOpenedInterface.getItemDetailOpened(false);
        if (isChanged) {
            likedRefreshInterface.setValues(true);
        }
    }
}
