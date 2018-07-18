package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("CollegeDeadlineRelation")
public class CollegeDeadlineRelation extends ParseObject {

    final static private String KEY_COLLEGE = "college";
    final static private String KEY_DEADLINE = "deadline";

    public CollegeDeadlineRelation() {

    }

    public College getCollege() {
        return (College) getParseObject(KEY_COLLEGE);
    }

    public Deadline getDeadline() {
        return (Deadline) getParseObject(KEY_DEADLINE);
    }

    public void setCollege(College college) {
        put(KEY_COLLEGE, college);
    }

    public void setDeadline(Deadline deadline) {
        put(KEY_DEADLINE, deadline);
    }

    public static class Query extends ParseQuery<CollegeDeadlineRelation> {
        public Query() {
            super(CollegeDeadlineRelation.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withCollege() {
            include(KEY_COLLEGE);
            return this;
        }

        public Query withDeadline() {
            include(KEY_DEADLINE);
            return this;
        }
    }
}
