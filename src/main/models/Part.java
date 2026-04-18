package main.models;

import com.surrealdb.RecordId;
import main.utils.Input;

public class Part {
    private RecordId id;
    private String designation, manufacturer;
    private int stock_quantity;

    public Part() {}

    public Part(String designation, String manufacturer, int stock_quantity) {
        this.designation = designation;
        this.manufacturer = manufacturer;
        this.stock_quantity = stock_quantity;
    }

    public RecordId getId() { return id; }
    public String getDesignation() { return designation; }
    public String getManufacturer() { return manufacturer; }
    public int getStockQuantity() { return stock_quantity; }

    public void setId(RecordId id) { this.id = id; }
    public void setStockQuantity(int quantity) { this.stock_quantity = quantity; }

    private static Integer promptQuantity(String defaultValue) {
        String quantityString = defaultValue == null
            ? Input.getInput("Stock quantity")
            : Input.getInput("Stock quantity", defaultValue, true);
        if (quantityString == null) return null;
        try {
            int quantity = Integer.parseInt(quantityString);
            if (quantity < 0) { System.err.println("Quantity cannot be negative."); return null; }
            return quantity;
        } catch (NumberFormatException e) {
            System.err.println("Invalid quantity.");
            return null;
        }
    }

    public static Part create() {
        String designation  = Input.getInput("Designation");
        if (designation == null) return null;

        String manufacturer = Input.getInput("Manufacturer");
        if (manufacturer == null) return null;

        Integer quantity = promptQuantity(null);
        if (quantity == null) return null;

        return new Part(designation, manufacturer, quantity);
    }

    public static Part edit(Part part) {
        String designation  = Input.getInput("Designation",  part.getDesignation(),  true);
        if (designation == null) return null;

        String manufacturer = Input.getInput("Manufacturer", part.getManufacturer(), true);
        if (manufacturer == null) return null;

        Integer quantity = promptQuantity(String.valueOf(part.getStockQuantity()));
        if (quantity == null) return null;

        Part updated = new Part(designation, manufacturer, quantity);
        updated.setId(part.getId());

        return updated;
    }
}