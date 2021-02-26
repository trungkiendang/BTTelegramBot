package model;

import java.sql.Date;

public class Issue{
    public int id;
    public Project project;
    public Tracker tracker;
    public Status status;
    public Priority priority;
    public Author author;
    public Parent parent;
    public String subject;
    public String description;
    public String start_date;
    public String due_date;
    public int done_ratio;
    public boolean is_private;
    public Object estimated_hours;
    public Date created_on;
    public Date updated_on;
    public Object closed_on;
    public AssignedTo assigned_to;

}

