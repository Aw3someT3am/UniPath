package me.juliasson.unipath.model;

public class Notify {
    private String userNotified;
    private String deadline;

    public Notify(String userNotified, String deadline) {
        this.deadline = deadline;
        this.userNotified = userNotified;
    }

    public String getUserNotified() {
        return userNotified;
    }

    public void setUserNotified(String userNotified) {
        this.userNotified = userNotified;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
