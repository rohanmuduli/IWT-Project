package model;

import java.sql.Date;

public class Menu {
    private int menuId;
    private Date menuDate;
    private String mealType;
    private String items;

    public Menu() {}

    public Menu(int menuId, Date menuDate, String mealType, String items) {
        this.menuId = menuId;
        this.menuDate = menuDate;
        this.mealType = mealType;
        this.items = items;
    }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public Date getMenuDate() { return menuDate; }
    public void setMenuDate(Date menuDate) { this.menuDate = menuDate; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }

    public String toJson() {
        return "{" +
                "\"menuId\":" + menuId + "," +
                "\"menuDate\":\"" + menuDate.toString() + "\"," +
                "\"mealType\":\"" + mealType + "\"," +
                "\"items\":\"" + items.replace("\"", "\\\"") + "\"" +
                "}";
    }
}
