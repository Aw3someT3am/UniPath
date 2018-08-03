package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("College")
public class College extends ParseObject {
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_STUDENT_POPULATION = "studentPopulation";
    private static final String KEY_OUT_OF_STATE_COST = "outOfStateCost";
    private static final String KEY_IN_STATE_COST = "inStateCost";
    private static final String KEY_ACCEPTANCE_RATE = "acceptanceRate";
    private static final String KEY_WEBSITE = "website";

    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";

    private static final String KEY_EARLY_ACTION = "earlyAction";
    private static final String KEY_REGULAR_ACTION = "regularAction";

    public ParseFile getCollegeImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getCollegeName() {
        return getString(KEY_NAME);
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public Integer getStudentPopulation() {
        return getNumber(KEY_STUDENT_POPULATION).intValue();
    }

    public Integer getOutOfStateCost() {
        return getNumber(KEY_OUT_OF_STATE_COST).intValue();
    }

    public Integer getInStateCost() {
        return getNumber(KEY_IN_STATE_COST).intValue();
    }

    public Double getAcceptanceRate() {
        return getNumber(KEY_ACCEPTANCE_RATE).doubleValue();
    }

    public Double getLongitude() {
        return  getNumber(KEY_LONGITUDE).doubleValue();
    }

    public Double getLatitude() {
        return  getNumber(KEY_LATITUDE).doubleValue();
    }

    public String getWebsite() {
        return getString(KEY_WEBSITE);
    }

    public static Query query() {
        return new Query();
    }

    public static class Query extends ParseQuery<College> {

        public Query() {
            super(College.class);
        }

        public Query newestFirst() {
            orderByDescending("createdAt");
            return this;
        }

        public Query limit20() {
            setLimit(20);
            return this;
        }
    }
}
