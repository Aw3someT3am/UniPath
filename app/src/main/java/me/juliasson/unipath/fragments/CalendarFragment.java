package me.juliasson.unipath.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class CalendarFragment extends Fragment {

    private static final String TAG = "MainActivity";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private boolean shouldShow = false;
    private CompactCalendarView compactCalendarView;
    private TextView monthYearTv;
    private ImageView refreshBtn;
    private Button btnToday;
    private Button btnPrevious;
    private Button btnNext;
    private Context mContext;
    private ArrayAdapter calendarAdapter;

    private CompactCalendarView.CompactCalendarViewListener listener;
    private PointF accumulatedScrollOffset = new PointF();
    private int monthsScrolledSoFar = 0;

    private Date currentCalendarDate;

    // A list of strings of format "description, college" to display for each specific date when clicked
    final List<String> mutableBookings = new ArrayList<>();

    private List<UserDeadlineRelation> mDataList = new ArrayList<>();

    private static final String KEY_USER = "user";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_calendar, parent, false);
        setHasOptionsMenu(true);

//        mContext = parent.getContext();

        //Title textview shows in form "Mmm YYYY"
        monthYearTv = view.findViewById(R.id.monthYearBtn);

        currentCalendarDate = Calendar.getInstance().getTime();

        // Listview of details for selected date in calendar
        final ListView bookingsListView = view.findViewById(R.id.bookings_listview);
//        bookingsListView.setEmptyView(view.findViewById(R.id.empty_listview));

        // This adapter will feed information into the listview depending on selected date
        calendarAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mutableBookings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                return view;
            }
        };

        bookingsListView.setAdapter(calendarAdapter);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);

        // below allows you to configure color for the current day in the month
        // compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
        // below allows you to configure colors for the current day the user has selected
        // compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.dark_red));
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
        compactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);
        loadEvents();
        loadEventsForYear(Calendar.getInstance().get(Calendar.YEAR));
        compactCalendarView.invalidate();

        logEventsByMonth(compactCalendarView);

        //set initial title
        monthYearTv.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        // remove hard-coded events
        compactCalendarView.removeAllEvents();

        // fetch list of userDeadlineRelations from parse and feed into calendar
//        compactCalendarView.addEvents(mDataList);
        setDataListItems();

        //TODO: Display "No events for this day" on days where there are no deadlines.
        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectDay(dateClicked);
                currentCalendarDate = dateClicked;
                calendarAdapter.notifyDataSetChanged();
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthYearTv.setText(dateFormatForMonth.format(firstDayOfNewMonth));
//                currentCalendarDate = firstDayOfNewMonth;
            }

        });

        refreshBtn = view.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(parent.getContext(), R.anim.image_view_click));
                compactCalendarView.removeAllEvents();
                loadEvents();
                setDataListItems();
            }
        });

        btnToday = view.findViewById(R.id.btnToday);
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(CalendarFragment.this).attach(CalendarFragment.this).commit();
                selectDay(Calendar.getInstance().getTime());
                currentCalendarDate = Calendar.getInstance().getTime();
            }
        });


        btnNext = view.findViewById(R.id.btnNextDeadline);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the list of deadlines, compare each deadline date to today's date
                Date nextClosestDeadline = Calendar.getInstance().getTime();
                nextClosestDeadline.setYear(nextClosestDeadline.getYear() + 1);

//                Date nextClosestDeadline =  mDataList.get(0).getDeadline().getDeadlineDate();

//                int counter = 0;
//                while (nextClosestDeadline == currentCalendarDate) {
//                    nextClosestDeadline = mDataList.get(counter + 1).getDeadline().getDeadlineDate();
//                }

                // Get the next closest deadline
                for (int i = 0; i < mDataList.size(); i ++) {
                    Date date = mDataList.get(i).getDeadline().getDeadlineDate();

                    if (date.after(currentCalendarDate) && date.before(nextClosestDeadline)) {
                        nextClosestDeadline = date;
                    }
                }

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
                // get the list of deadlines, compare each deadline date to today's date
                Date nextClosestDeadline = Calendar.getInstance().getTime();

                // Get the next closest deadline
                for (int i = 0; i < mDataList.size(); i ++) {
                    Date date = mDataList.get(i).getDeadline().getDeadlineDate();

                    if (date.before(currentCalendarDate) && date.after(nextClosestDeadline)) {
                        nextClosestDeadline = date;
                    }
                }


                int yearDifference = currentCalendarDate.getYear() - nextClosestDeadline.getYear() ;
                int difference = yearDifference * 12 + currentCalendarDate.getMonth() - nextClosestDeadline.getMonth();

//                int previousMonth = nextClosestDeadline.getMonth();
//                int currentMonth = Calendar.getInstance().getTime().getMonth();

               //  int difference = currentMonth - previousMonth;

                for (int j = 0; j < difference; j ++) {
                    compactCalendarView.scrollLeft();
                }
                compactCalendarView.setCurrentDate(nextClosestDeadline);
                selectDay(nextClosestDeadline);
            }
        });

        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        monthYearTv.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        monthYearTv.setText(dateFormatForMonth.format(new Date()));
    }

    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);
        addEvents(Calendar.AUGUST, -1);
    }

    public void selectDay(Date dateClicked) {
        currentCalendarDate = dateClicked;
        monthYearTv.setText(dateFormatForMonth.format(dateClicked));
        List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
        Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
        if (bookingsFromMap != null) {
            Log.d(TAG, bookingsFromMap.toString());
            mutableBookings.clear();
            for (Event booking : bookingsFromMap) {
                // Query through Parse to find additional info for each event for given date
                booking.getTimeInMillis();
                mutableBookings.add((String) booking.getData());
            }
            calendarAdapter.notifyDataSetChanged();
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

    private void setDataListItems(){
        //ParseQuery go through each of the current user's deadlines and add them.
        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
        udQuery.getTop().withUser().withDeadline().withCollege();
        udQuery.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());

        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {

                Log.d("I'm in here", "asdkfjals");
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
                        Event event = new Event(Color.argb(255, 0 ,221, 255), date.getTime(), String.format("%s: %s", college, description.toUpperCase()));
                        compactCalendarView.addEvent(event);
                        mDataList.add(relation);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_deadline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_deadline:
//                Intent intent = new Intent(mContext, NewDeadlineDialog.class);
//                startActivity(intent);
                break;
        }
        return true;
    }
}
