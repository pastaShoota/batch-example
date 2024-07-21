package fr.chevallier31.my_batch;

import java.time.LocalDate;

public class Points {

    private LocalDate       activityDate;
    private String          fidelityNumber;
    private String          firstName;
    private String          lastName;
    private String          fidelityCode;
    private Integer         points;

    public LocalDate getActivityDate() {
        return activityDate;
    }
    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }
    public String getFidelityNumber() {
        return fidelityNumber;
    }
    public void setFidelityNumber(String fidelityNumber) {
        this.fidelityNumber = fidelityNumber;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFidelityCode() {
        return fidelityCode;
    }
    public void setFidelityCode(String fidelityCode) {
        this.fidelityCode = fidelityCode;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }
    public void setFiller(String str) {}
    @Override
    public String toString() {
        return "Points [activityDate=" + activityDate + ", fidelityNumber=" + fidelityNumber + ", firstName="
                + firstName + ", lastName=" + lastName + ", fidelityCode=" + fidelityCode + ", points=" + points + "]";
    }

    
    
}
