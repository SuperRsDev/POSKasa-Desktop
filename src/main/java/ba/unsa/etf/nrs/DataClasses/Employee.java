package ba.unsa.etf.nrs.DataClasses;

import java.time.LocalDate;

public class Employee {
    private int id;
    private User user;
    private User manager;
    private LocalDate hireDate;
    private String jobTitle;

    public Employee(int id, User user, User manager, LocalDate hireDate, String jobTitle) {
        this.id = id;
        this.user = user;
        this.manager = manager;
        this.hireDate = hireDate;
        this.jobTitle = jobTitle;
    }

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}

