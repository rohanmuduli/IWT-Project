package model;

import java.sql.Date;

public class OptOut {
    private int optoutId;
    private String studentId;
    private Date optoutDate;
    private String mealType;
    private String status;

    public OptOut() {}

    public OptOut(int optoutId, String studentId, Date optoutDate, String mealType, String status) {
        this.optoutId = optoutId;
        this.studentId = studentId;
        this.optoutDate = optoutDate;
        this.mealType = mealType;
        this.status = status;
    }

    public int getOptoutId() { return optoutId; }
    public void setOptoutId(int optoutId) { this.optoutId = optoutId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public Date getOptoutDate() { return optoutDate; }
    public void setOptoutDate(Date optoutDate) { this.optoutDate = optoutDate; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toJson() {
        return "{" +
                "\"optoutId\":" + optoutId + "," +
                "\"studentId\":\"" + studentId + "\"," +
                "\"optoutDate\":\"" + optoutDate.toString() + "\"," +
                "\"mealType\":\"" + mealType + "\"," +
                "\"status\":\"" + status + "\"" +
                "}";
    }
}
