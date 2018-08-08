package me.juliasson.unipath.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.CollegeDetailsDialog;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.rows.ChildRow;
import me.juliasson.unipath.rows.ParentRow;
import me.juliasson.unipath.utils.Constants;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private List<College> mColleges;
    private Context mContext;

    private final static String KEY_COLLEGE_NAME = "name";
    private final static String KEY_EARLY_ACTION = "earlyAction";
    private final static String KEY_REGULAR_ACTION = "regularAction";
    private final static String KEY_COLLEGE_IMAGE = "image";

    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;

    public MyExpandableListAdapter(Context context, ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }

    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return parentRowList.get(i).getChildList().size();
    }

    @Override
    public Object getGroup(int i) {
        return parentRowList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return parentRowList.get(i).getChildList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ParentRow parentRow = (ParentRow) getGroup(i);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.parent_row,null);
        }

        TextView heading = (TextView) view.findViewById(R.id.parent_text);

        heading.setText(parentRow.getName().trim());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildRow childRow = (ChildRow) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.child_row, null);
        }

        ImageView childIcon = (ImageView) view.findViewById(R.id.child_icon);
        childIcon.setImageResource(R.drawable.ic_search_white_24dp);

        final TextView childText = (TextView) view.findViewById(R.id.child_text);
        childText.setText(childRow.getText().trim());

        final View finalConvertView = view;
        childText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CollegeDetailsDialog.class);
                context.startActivity(intent);

                Toast toast = Toast.makeText(finalConvertView.getContext(), childText.getText(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
                toast.show();
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        parentRowList.clear();

        if (query.isEmpty()) {
            parentRowList.addAll(originalList);
        } else {
            for (ParentRow parentRow : originalList) {
                ArrayList<ChildRow> childList = parentRow.getChildList();
                ArrayList<ChildRow> newList =  new ArrayList<>();

                for (ChildRow childRow : childList) {
                    if (childRow.getText().toLowerCase().contains(query)) {
                        newList.add(childRow);
                    }
                } // end for(ChildRow childRow: childList)
                if (newList.size() > 0) {
                    ParentRow nParentRow = new ParentRow(parentRow.getName(), newList);
                    parentRowList.add(nParentRow);
                }
            }
        }
        notifyDataSetChanged();
    }
}
