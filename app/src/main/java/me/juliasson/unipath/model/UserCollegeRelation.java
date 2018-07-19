package me.juliasson.unipath.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserCollegeRelation")
public class UserCollegeRelation extends ParseObject{
    final static private String KEY_COLLEGE = "college";
    final static private String KEY_USER = "user";

    public UserCollegeRelation() {

    }

    public College getCollege() {
        return (College) getParseObject(KEY_COLLEGE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setCollege(College college) {
        put(KEY_COLLEGE, college);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public static class Query extends ParseQuery<UserCollegeRelation> {
        public Query() {
            super(UserCollegeRelation.class);
        }

        public UserCollegeRelation.Query getTop() {
            setLimit(20);
            return this;
        }

        public UserCollegeRelation.Query withCollege() {
            include(KEY_COLLEGE);
            return this;
        }

        public UserCollegeRelation.Query withUser() {
            include(KEY_USER);
            return this;
        }
    }
}
