package model;

import java.sql.Date;

public class Attendance {
    private int attendanceId;
    private String studentId;
    private Date attendanceDate;
    private int breakfast;
    private int lunch;
    private int dinner;

    public Attendance() {}

    public Attendance(int attendanceId, String studentId, Date attendanceDate, int breakfast, int lunch, int dinner) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.attendanceDate = attendanceDate;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }
    public int getBreakfast() { return breakfast; }
    public void setBreakfast(int breakfast) { this.breakfast = breakfast; }
    public int getLunch() { return lunch; }
    public void setLunch(int lunch) { this.lunch = lunch; }
    public int getDinner() { return dinner; }
    public void setDinner(int dinner) { this.dinner = dinner; }

    public String toJson() {
        return "{" +
                "\"attendanceId\":" + attendanceId + "," +
                "\"studentId\":\"" + studentId + "\"," +
                "\"attendanceDate\":\"" + attendanceDate.toString() + "\"," +
                "\"breakfast\":" + breakfast + "," +
                "\"lunch\":" + lunch + "," +
                "\"dinner\":" + dinner +
                "}";
    }
}
