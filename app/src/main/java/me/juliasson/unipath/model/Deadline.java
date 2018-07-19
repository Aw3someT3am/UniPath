package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Deadline")
public class Deadline extends ParseObject {
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_DEADLINE_DATE = "deadlineDate";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public Date getDeadlineDate() {
        return getDate(KEY_DEADLINE_DATE);
    }
}
