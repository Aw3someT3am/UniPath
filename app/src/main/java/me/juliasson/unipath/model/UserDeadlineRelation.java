package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserDeadlineRelation")
public class UserDeadlineRelation extends ParseObject {
    final static private String KEY_DEADLINE = "deadline";
    final static private String KEY_USER = "user";
    final static private String KEY_COMPLETED = "completed";

    public UserDeadlineRelation() {

    }

    public Deadline getDeadline() {
        return (Deadline) getParseObject(KEY_DEADLINE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public Boolean getCompleted() {
        return getBoolean(KEY_COMPLETED);
    }

    public void setDeadline(Deadline deadline) {
        put(KEY_DEADLINE, deadline);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setCompleted(Boolean completed) {
        put(KEY_COMPLETED, completed);
    }

    public static class Query extends ParseQuery<UserDeadlineRelation> {
        public Query() {
            super(UserDeadlineRelation.class);
        }

        public UserDeadlineRelation.Query getTop() {
            setLimit(20);
            return this;
        }

        public UserDeadlineRelation.Query withDeadline() {
            include(KEY_DEADLINE);
            return this;
        }

        public UserDeadlineRelation.Query withUser() {
            include(KEY_USER);
            return this;
        }
    }
}
