package me.juliasson.unipath.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class DDCollegeListAdapter extends RecyclerView.Adapter<DDCollegeListAdapter.ViewHolder> {

    private List<UserDeadlineRelation> mCollegeDeadlines;
    private Context mContext;

    public DDCollegeListAdapter (List<UserDeadlineRelation> deadlines) {
        mCollegeDeadlines = deadlines;
    }

    @NonNull
    @Override
    public DDCollegeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View collegeView = inflater.inflate(R.layout.item_deadlinedetails_college, viewGroup, false);
        DDCollegeListAdapter.ViewHolder viewHolder = new DDCollegeListAdapter.ViewHolder(collegeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DDCollegeListAdapter.ViewHolder viewHolder, final int position) {
        final UserDeadlineRelation relation = mCollegeDeadlines.get(position);
        viewHolder.tvCollegeName.setText(relation.getCollege().getCollegeName());
        viewHolder.tvDeadlineDescription.setText(relation.getDeadline().getDescription());
        viewHolder.lbCheckBox.setLiked(relation.getCompleted());

        viewHolder.lbCheckBox.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                relation.setCompleted(true);
                relation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("DDCLAdapter", "Deadline completion saved");
                        } else {
                            e.printStackTrace();
                            Log.d("DDCLAdapter", "Deadline completion failed to save");
                        }
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                relation.setCompleted(false);
                relation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("DDCLAdapter", "Deadline incompletion saved");
                        } else {
                            e.printStackTrace();
                            Log.d("DDCLAdapter", "Deadline incompletion failed to save");
                        }
                    }
                });
            }
        });

        viewHolder.ivRemoveDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Delete this deadline?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteDeadline(relation, position);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User cancelled the dialog
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if (relation.getDeadline().getIsFinancial()) {
            Glide.with(mContext)
                    .load(R.drawable.ic_attach_money)
                    .into(viewHolder.ivIsFinancial);
        }
    }

    public void deleteDeadline(UserDeadlineRelation relation, int position) {
        try {
            mCollegeDeadlines.remove(relation);
            notifyItemRemoved(position);
            if (relation.getDeadline().getIsCustom()) {
                relation.getDeadline().delete();
            }
            relation.delete();
            relation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("DDCollegeListAdapter", "Deadline removed.");
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mCollegeDeadlines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCollegeName;
        TextView tvDeadlineDescription;
        LikeButton lbCheckBox;
        ImageView ivRemoveDeadline;
        ImageView ivIsFinancial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            tvDeadlineDescription = itemView.findViewById(R.id.tvDeadlineDesc);
            lbCheckBox = itemView.findViewById(R.id.lbLikeButton);
            ivRemoveDeadline = itemView.findViewById(R.id.ivRemoveDeadline);
            ivIsFinancial = itemView.findViewById(R.id.ivIsFinancial);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mCollegeDeadlines.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<UserDeadlineRelation> list) {
        mCollegeDeadlines.addAll(list);
        notifyDataSetChanged();
    }
}
