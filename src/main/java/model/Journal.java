package model;

import java.util.Date;
import java.util.List;

public class Journal{
    public int id;
    public User user;
    public String notes;
    public Date created_on;
    public boolean private_notes;
    public List<Detail> details;
}
