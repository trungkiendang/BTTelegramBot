package model;

import java.sql.Date;
import java.util.List;

public class Issue {
    public int id;
    public Project project;
    public Tracker tracker;
    public Status status;
    public Priority priority;
    public Author author;
    public AssignedTo assigned_to;
    public Parent parent;
    public String subject;
    public String description;
    public String start_date;
    public String due_date;
    public int done_ratio;
    public boolean is_private;
    public Object estimated_hours;
    public Object total_estimated_hours;
    public double spent_hours;
    public double total_spent_hours;
    public Date created_on;
    public Date updated_on;
    public Object closed_on;
    public List<Journal> journals;

    private int hashCode;

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (obj instanceof Issue) {
            Issue temp = (Issue) obj;
            if (this.id == temp.id) {
                hashCode = temp.hashCode;
                return true;
            } else {
                hashCode = super.hashCode();
                return false;
            }
        }
        return false;

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}

