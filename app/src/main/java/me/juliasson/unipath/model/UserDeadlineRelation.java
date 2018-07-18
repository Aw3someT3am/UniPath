package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserDeadlineRelation")
public class UserDeadlineRelation extends ParseObject {
    final static private String KEY_DEADLINE = "deadline";
    final static private String KEY_USER = "user";

    public UserDeadlineRelation() {

    }

    public College getDeadline() {
        return (College) getParseObject(KEY_DEADLINE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setDeadline(Deadline deadline) {
        put(KEY_DEADLINE, deadline);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public static class Query extends ParseQuery<UserDeadlineRelation> {
        public Query() {
            super(UserDeadlineRelation.class);
        }

        public UserDeadlineRelation.Query getTop() {
            setLimit(20);
            return this;
        }

        public UserDeadlineRelation.Query withCollege() {
            include(KEY_DEADLINE);
            return this;
        }

        public UserDeadlineRelation.Query withDeadline() {
            include(KEY_USER);
            return this;
        }
    }
}
