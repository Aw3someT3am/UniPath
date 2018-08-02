package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Deadline")
public class Deadline extends ParseObject {
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_DEADLINE_DATE = "deadlineDate";
    public final static String KEY_FINANCIAL = "isFinancial";
    public final static String KEY_CUSTOM = "isCustom";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public Date getDeadlineDate() {
        return getDate(KEY_DEADLINE_DATE);
    }

    public boolean getIsFinancial() {
        return getBoolean(KEY_FINANCIAL);
    }



    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setDeadlineDate(Date date) {
        put(KEY_DEADLINE_DATE, date);
    }

    public void setIsFinancial(boolean isFinancial) {
        put(KEY_FINANCIAL, isFinancial);
    }
}
