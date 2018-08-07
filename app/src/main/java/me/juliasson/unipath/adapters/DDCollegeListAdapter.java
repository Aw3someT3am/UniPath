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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.internal.GetDeadlineDeletedInterface;
import me.juliasson.unipath.internal.GetDeadlineDialogStatusInterface;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class DDCollegeListAdapter extends RecyclerView.Adapter<DDCollegeListAdapter.ViewHolder> {

    private List<UserDeadlineRelation> mCollegeDeadlines;
    private Context mContext;
    private static GetDeadlineDialogStatusInterface ddStatusInterface;
    private static GetDeadlineDeletedInterface getDeadlineDeletedInterface;

    private final String ALERT_MESSAGE = "Delete this deadline?";
    private final String ALERT_POSITIVE = "Delete";
    private final String ALERT_NEGATIVE = "Cancel";

    public DDCollegeListAdapter (List<UserDeadlineRelation> deadlines, GetDeadlineDialogStatusInterface updateInterface) {
        mCollegeDeadlines = deadlines;
        ddStatusInterface = updateInterface;
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
                            ddStatusInterface.isDialogChanged(true);
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
                            ddStatusInterface.isDialogChanged(true);
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
                builder.setMessage(ALERT_MESSAGE)
                        .setPositiveButton(ALERT_POSITIVE, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteDeadline(relation, position);
                                getDeadlineDeletedInterface.getDeadlineDeleted(true);
                                //updating the main linear timeline
                                if (mCollegeDeadlines.isEmpty()) {
                                    ddStatusInterface.isDialogEmpty(true);
                                }
                                Toast.makeText(mContext, "Deadline deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(ALERT_NEGATIVE, new DialogInterface.OnClickListener() {
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

    public static void setDeadlineDeletedInterface(GetDeadlineDeletedInterface deletedInterface) {
        getDeadlineDeletedInterface = deletedInterface;
    }
}
