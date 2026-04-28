package model;

import java.sql.Date;

public class ExtraCharge {
    private int chargeId;
    private Date chargeDate;
    private String studentId;
    private String item;
    private int qty;
    private double amount;

    public ExtraCharge() {}

    public ExtraCharge(int chargeId, Date chargeDate, String studentId, String item, int qty, double amount) {
        this.chargeId = chargeId;
        this.chargeDate = chargeDate;
        this.studentId = studentId;
        this.item = item;
        this.qty = qty;
        this.amount = amount;
    }

    public int getChargeId() { return chargeId; }
    public void setChargeId(int chargeId) { this.chargeId = chargeId; }
    public Date getChargeDate() { return chargeDate; }
    public void setChargeDate(Date chargeDate) { this.chargeDate = chargeDate; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String toJson() {
        return "{" +
                "\"chargeId\":" + chargeId + "," +
                "\"chargeDate\":\"" + chargeDate + "\"," +
                "\"studentId\":\"" + studentId + "\"," +
                "\"item\":\"" + item + "\"," +
                "\"qty\":" + qty + "," +
                "\"amount\":" + amount +
                "}";
    }
}
