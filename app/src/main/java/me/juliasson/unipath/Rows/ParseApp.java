package me.juliasson.unipath.Rows;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import me.juliasson.unipath.model.College;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(College.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("unipath")
                .clientKey("juliajorg3kath3rin3")
                .server("http://unipath.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
