package me.juliasson.unipath.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.TimelineActivity;
import me.juliasson.unipath.adapters.CalendarDeadlineAdapter;
import me.juliasson.unipath.internal.UpdateFavCollegeListCalendar;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.Constants;

public class CalendarFragment extends Fragment implements UpdateFavCollegeListCalendar {

    private static final String TAG = "MainActivity";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat fullDateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private TextView monthYearTv;
    private Button btnToday;
    private Button btnPrevious;
    private Button btnNext;
    private TextView tvNoDeadlines;
    private Context mContext;
    private CalendarDeadlineAdapter calendarAdapter;

    private HashMap<Event, UserDeadlineRelation> eventRelationMap = new HashMap<>();

    private Date currentSelectedDate;

    // List of relations specific to one day when selected
    final List<UserDeadlineRelation> mutableBookings = new ArrayList<>();

    // User's complete list of deadlines being fed into the calendar
    private List<UserDeadlineRelation> mDataList = new ArrayList<>();


    private List<Date> mDates = new ArrayList<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        View view = inflater.inflate(R.layout.fragment_calendar, parent, false);
        setHasOptionsMenu(true);

        TimelineActivity.updateFavCollegeListInterfaceCalendar(this);

        //Title textview shows in form "Mmm YYYY"
        monthYearTv = view.findViewById(R.id.monthYearBtn);

        // Default message over list view when a date contains no deadlines
        tvNoDeadlines = (TextView) view.findViewById(R.id.tvNoDeadlines);

        currentSelectedDate = Calendar.getInstance().getTime();

        // Listview of details for selected date in calendar
        final DiscreteScrollView bookingsListView = view.findViewById(R.id.bookings_listview);

        calendarAdapter = new CalendarDeadlineAdapter(mutableBookings);

        bookingsListView.setAdapter(calendarAdapter);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
        compactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        loadEvents();
        loadEventsForYear(Calendar.getInstance().get(Calendar.YEAR));
        selectDay(Calendar.getInstance().getTime());
        compactCalendarView.invalidate();
        logEventsByMonth(compactCalendarView);

        //set initial title
        monthYearTv.setText(fullDateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        // remove hard-coded events
        compactCalendarView.removeAllEvents();

        // fetch list of userDeadlineRelations from parse and feed into calendar
        setDataListItems();

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectDay(dateClicked);
                currentSelectedDate = dateClicked;
                calendarAdapter.notifyDataSetChanged();
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthYearTv.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }

        });

        btnToday = view.findViewById(R.id.btnToday);
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(CalendarFragment.this).attach(CalendarFragment.this).commit();
                selectDay(Calendar.getInstance().getTime());
                compactCalendarView.setCurrentDate(Calendar.getInstance().getTime());
            }
        });


        btnNext = view.findViewById(R.id.btnNextDeadline);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                // get the list of deadlines, compare each deadline date to today's date

                Date nextClosestDeadline = Calendar.getInstance().getTime();
                nextClosestDeadline.setYear(nextClosestDeadline.getYear() + 1);

                // Get the next closest deadline
                for (int i = 0; i < mDataList.size(); i ++) {
                    Date date = mDataList.get(i).getDeadline().getDeadlineDate();

                    if (date.after(currentSelectedDate) && date.before(nextClosestDeadline)) {
                        nextClosestDeadline = date;
                    }
                }

                // Be sure not to select default future date that likely doesn't contain deadlines
                getDates();
                if (! mDates.contains(nextClosestDeadline)) { nextClosestDeadline = currentSelectedDate; }

                // Calculate scrolls to show date in custom calendar view
                int nextMonth = nextClosestDeadline.getMonth();
                int currentMonth = Calendar.getInstance().getTime().getMonth();

                int difference = nextMonth - currentMonth;


                for (int j = 0; j < difference; j ++) {
                    compactCalendarView.scrollRight();
                }
                compactCalendarView.setCurrentDate(nextClosestDeadline);
                selectDay(nextClosestDeadline);
            }
        });

        btnPrevious = view.findViewById(R.id.btnPreviousDeadine);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                // get the list of deadlines, compare each deadline date to today's date
                Date nextClosestDeadline = Calendar.getInstance().getTime();
                nextClosestDeadline.setYear(nextClosestDeadline.getYear() - 1);

                // Get the next closest deadline
                for (int i = 0; i < mDataList.size(); i ++) {
                    Date date = mDataList.get(i).getDeadline().getDeadlineDate();

                    if (date.before(currentSelectedDate) && date.after(nextClosestDeadline)) {
                        nextClosestDeadline = date;
                    }
                }

                // Be sure not to select default past date that likely doesn't contain deadlines
                getDates();
                if (! mDates.contains(nextClosestDeadline)) {
                    nextClosestDeadline = currentSelectedDate;
                }

                // Calculate how many times to scroll custom calendar view
                int yearDifference = currentSelectedDate.getYear() - nextClosestDeadline.getYear() ;
                int difference = yearDifference * 12 + currentSelectedDate.getMonth() - nextClosestDeadline.getMonth();

                for (int j = 0; j < difference; j ++) {
                    compactCalendarView.scrollLeft();
                }

                // Show circle on selected date
                compactCalendarView.setCurrentDate(nextClosestDeadline);
                selectDay(nextClosestDeadline);
            }
        });

        return view;
    }

    public void showNoDeadlines() {
        tvNoDeadlines.setVisibility(View.VISIBLE);
    }

    public void hideNoDeadlines() {
        tvNoDeadlines.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        monthYearTv.setText(fullDateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        monthYearTv.setText(fullDateFormatForMonth.format(new Date()));
    }

    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);
        addEvents(Calendar.AUGUST, -1);
    }

    private void getDates() {
        for (int i = 0; i < mDataList.size(); i ++) {
            mDates.add(mDataList.get(i).getDeadline().getDeadlineDate());
        }
    }

    // Show pink circle on clicked day, load events into mutable bookings
    public void selectDay(Date dateClicked) {
        currentSelectedDate = dateClicked;
        monthYearTv.setText(fullDateFormatForMonth.format(dateClicked));
        List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
        Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
        if (bookingsFromMap != null) {
            Log.d(TAG, bookingsFromMap.toString());
            mutableBookings.clear();
            for (Event booking : bookingsFromMap) {

                UserDeadlineRelation relation = eventRelationMap.get(booking);

                // Query through Parse to find additional info for each event for given date
                booking.getTimeInMillis();
                mutableBookings.add(relation);
            }
            calendarAdapter.notifyDataSetChanged();
        }
        if (!mutableBookings.isEmpty()) {
            hideNoDeadlines();
        } else {
            showNoDeadlines();
        }
    }

    private void loadEventsForYear(int year) {
        addEvents(Calendar.DECEMBER, year);
        addEvents(Calendar.AUGUST, year);
    }

    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }

        String currentMonth = new DateFormatSymbols().getMonths()[Calendar.getInstance().get(Calendar.MONTH)];
        Log.d(TAG, String.format("Events for %s with simple date formatter: %s", currentMonth, dates));
        Log.d(TAG, String.format("Events for %s month using default local and timezone: %s", currentMonth, compactCalendarView.getEventsForMonth(currentCalender.getTime())));
    }

    private void addEvents(int month, int year) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
        }
    }

    private void openCalendarOnCreate(View v) {
        final RelativeLayout layout = v.findViewById(R.id.main_content);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                compactCalendarView.showCalendarWithAnimation();
            }
        });
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    // Find events to display in scrollview for entire calendar
    private void setDataListItems(){
        //ParseQuery go through each of the current user's deadlines and add them.
        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
        udQuery.getTop().withUser().withDeadline().withCollege();
        udQuery.whereEqualTo(Constants.KEY_USER, ParseUser.getCurrentUser());

        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                mDataList.clear();
                eventRelationMap.clear();
                compactCalendarView.removeAllEvents();
                loadEvents();
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        // Access each deadline associated with current user
                        UserDeadlineRelation relation = objects.get(i);
                        Deadline deadline = relation.getDeadline();

                        String description = deadline.getDescription();
                        College collegeCollege = relation.getCollege();
                        String college = collegeCollege.getCollegeName();
                        Date date = deadline.getDeadlineDate();

                        // Create event for each deadline and add to CompactCalendarView
                        Event event = new Event(Color.argb(255, 255 ,100, 30), date.getTime(), String.format("%s: %s", college, description.toUpperCase()));
                        compactCalendarView.addEvent(event);
                        mDataList.add(relation);

                        eventRelationMap.put(event, relation);
                    }
                    selectDay(currentSelectedDate);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refresh() {
        setDataListItems();
    }

    //---------------------Implementing interface----------------------

    @Override
    public void updateList(boolean update) {
        if (update) {
            refresh();
        }
    }
}
