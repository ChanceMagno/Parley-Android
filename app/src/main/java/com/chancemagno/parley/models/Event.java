package com.chancemagno.parley.models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    String title;
    Date dateofevent;
    Time timeofevent;
    ArrayList<String> invited;
    ArrayList<String> attending;
    ArrayList<String> maybeattending;
    ArrayList<String> notattending;

    public Event(String title, Date dateofevent, Time timeofevent, ArrayList<String> invited, ArrayList<String> attending, ArrayList<String> maybeattending, ArrayList<String> notattending) {
        this.title = title;
        this.dateofevent = dateofevent;
        this.timeofevent = timeofevent;
        this.invited = invited;
        this.attending = attending;
        this.maybeattending = maybeattending;
        this.notattending = notattending;
    }

    public Date getDateofevent() {
        return dateofevent;
    }

    public Time getTimeofevent() {
        return timeofevent;
    }

    public ArrayList<String> getInvited() {
        return invited;
    }

    public ArrayList<String> getAttending() {
        return attending;
    }

    public ArrayList<String> getMaybeattending() {
        return maybeattending;
    }

    public ArrayList<String> getNotattending() {
        return notattending;
    }

    public String getTitle() {
        return title;
    }
}


