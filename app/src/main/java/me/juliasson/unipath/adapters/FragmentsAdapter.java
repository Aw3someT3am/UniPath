package me.juliasson.unipath.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Adapter for {@link ViewPager} that will populated from the collection of Fragments classes. Objects of that classes
 * will be instantiated on demand and used as a pages views.
 */
public class FragmentsAdapter extends FragmentPagerAdapter {

    // -----------------------------------------------------------------------
    //
    // Constructors
    //
    // -----------------------------------------------------------------------
    public FragmentsAdapter(FragmentManager fragmentManager, Context context,
                                        List<Class<? extends Fragment>> pages) {
        super(fragmentManager);
        mPagesClasses = pages;
        mContext = context;
    }

    // -----------------------------------------------------------------------
    //
    // Fields
    //
    // -----------------------------------------------------------------------
    private List<Class<? extends Fragment>> mPagesClasses;
    private Context mContext;

    // -----------------------------------------------------------------------
    //
    // Methods
    //
    // -----------------------------------------------------------------------
    @Override
    public Fragment getItem(int posiiton) {
        return Fragment.instantiate(mContext, mPagesClasses.get(posiiton).getName());
    }

    @Override
    public int getCount() {
        return mPagesClasses.size();
    }
}
