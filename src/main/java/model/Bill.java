package model;

public class Bill {
    private int billId;
    private String studentId;
    private String billMonth;
    private double baseAmount;
    private double deductions;
    private double extras;
    private double total;

    public Bill() {}

    public Bill(int billId, String studentId, String billMonth, double baseAmount, double deductions, double extras, double total) {
        this.billId = billId;
        this.studentId = studentId;
        this.billMonth = billMonth;
        this.baseAmount = baseAmount;
        this.deductions = deductions;
        this.extras = extras;
        this.total = total;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getBillMonth() { return billMonth; }
    public void setBillMonth(String billMonth) { this.billMonth = billMonth; }
    public double getBaseAmount() { return baseAmount; }
    public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    public double getExtras() { return extras; }
    public void setExtras(double extras) { this.extras = extras; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String toJson() {
        return "{" +
                "\"billId\":" + billId + "," +
                "\"studentId\":\"" + studentId + "\"," +
                "\"billMonth\":\"" + billMonth + "\"," +
                "\"baseAmount\":" + baseAmount + "," +
                "\"deductions\":" + deductions + "," +
                "\"extras\":" + extras + "," +
                "\"total\":" + total +
                "}";
    }
}
