package me.juliasson.unipath.Model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("College")
public class College extends ParseObject {
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NAME = "name";
    private static final String KEY_EARLY_ACTION = "earlyAction";
    private static final String KEY_REGULAR_ACTION = "regularAction";

    public ParseFile getCollegeImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getCollegeName() {
        return getString(KEY_NAME);
    }

    public Date getEarlyAction() {
        return getDate(KEY_EARLY_ACTION);
    }

    public Date getRegularAction() {
        return getDate(KEY_REGULAR_ACTION);
    }
}
