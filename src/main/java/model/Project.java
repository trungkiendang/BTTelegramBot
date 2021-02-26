package model;

import java.util.Date;

public class Project{
    public int id;
    public String name;
    public String identifier;
    public String description;
    public int status;
    public boolean is_public;
    public boolean inherit_members;
    public Date created_on;
    public Date updated_on;
    public Parent parent;
}
