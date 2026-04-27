package model;

public class Inventory {
    private int inventoryId;
    private String name;
    private double quantity;
    private String unit;
    private double threshold;

    public Inventory() {}

    public Inventory(int inventoryId, String name, double quantity, String unit, double threshold) {
        this.inventoryId = inventoryId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.threshold = threshold;
    }

    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public String toJson() {
        return "{" +
                "\"inventoryId\":" + inventoryId + "," +
                "\"name\":\"" + name.replace("\"", "\\\"") + "\"," +
                "\"quantity\":" + quantity + "," +
                "\"unit\":\"" + unit + "\"," +
                "\"threshold\":" + threshold +
                "}";
    }
}
