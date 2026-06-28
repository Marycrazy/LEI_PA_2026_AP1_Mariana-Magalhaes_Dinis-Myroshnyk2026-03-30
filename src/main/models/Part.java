package main.models;

import com.surrealdb.RecordId;
import main.utils.Input;

/**
 * Represents a spare part that can be used in equipment repairs, tracked
 * with a stock quantity.
 */
public class Part {
    private RecordId id;
    private String designation, manufacturer;
    private int stock_quantity;

    public Part() {}

    /**
     * Creates a new part with the given details.
     *
     * @param designation    the part's name/description
     * @param manufacturer   the part's manufacturer
     * @param stock_quantity the quantity currently in stock
     */
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

    /**
     * Interactively prompts for and creates a new part via the console.
     *
     * @return the newly created part, or {@code null} if the user cancelled
     *         or entered invalid input
     */
    public static Part create() {
        String designation  = Input.getInput("Designation");
        if (designation == null) return null;

        String manufacturer = Input.getInput("Manufacturer");
        if (manufacturer == null) return null;

        Integer quantity = promptQuantity(null);
        if (quantity == null) return null;

        return new Part(designation, manufacturer, quantity);
    }

    /**
     * Interactively prompts for updated values for an existing part via the
     * console, pre-filled with its current values.
     *
     * @param part the part to edit
     * @return a new {@code Part} with the updated values and the same id,
     *         or {@code null} if the user cancelled or entered invalid input
     */
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