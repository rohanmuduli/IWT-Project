package model;

public class Student {
    private String studentId;
    private String name;
    private String roomNo;
    private String passwordHash;
    private String subscriptionType;
    private String role;

    public Student() {}

    public Student(String studentId, String name, String roomNo, String passwordHash, String subscriptionType, String role) {
        this.studentId = studentId;
        this.name = name;
        this.roomNo = roomNo;
        this.passwordHash = passwordHash;
        this.subscriptionType = subscriptionType;
        this.role = role;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String toJson() {
        return "{" +
                "\"studentId\":\"" + studentId + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"roomNo\":\"" + roomNo + "\"," +
                "\"subscriptionType\":\"" + subscriptionType + "\"," +
                "\"role\":\"" + role + "\"" +
                "}";
    }
}
