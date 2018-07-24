package me.juliasson.unipath;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.UserCollegeRelation;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(College.class);
        ParseObject.registerSubclass(Deadline.class);
        ParseObject.registerSubclass(CollegeDeadlineRelation.class);
        ParseObject.registerSubclass(UserCollegeRelation.class);
        ParseObject.registerSubclass(UserDeadlineRelation.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("unipath")
                .clientKey("juliajorg3kath3rin3")
                .server("https://unipath.herokuapp.com/parse/")
                .build();

//        Stetho.initializeWithDefaults(this);
//
//        Parse.addNetworkInterceptor(new ParseStethoInterceptor());
//
//        Parse.addParseNetworkInterceptor(new ParseLogInterceptor());

        Parse.initialize(configuration);
    }
}
