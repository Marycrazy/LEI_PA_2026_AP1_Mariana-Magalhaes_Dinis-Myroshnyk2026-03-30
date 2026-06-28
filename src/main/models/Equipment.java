package main.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import com.surrealdb.RecordId;
import main.utils.Input;

/**
 * Tracks physical hardware or assets submitted for maintenance, managing manufacturing
 * timelines, serial codes, and repair tracking fields.
 */
public class Equipment {
    private RecordId id;
    private String brand, model, batch;
    private int sku;
    private ZonedDateTime manufacturing_date;
    private ZonedDateTime last_repair_date;
    private ZonedDateTime last_submission_date;

    /**
     * Default constructor for DB reflection or manual setup.
     */
    public Equipment() {}

    /**
     * Creates a new managed equipment instance with an auto-generated SKU.
     *
     * @param brand              the hardware manufacturer/brand name
     * @param model              the specific model designation
     * @param batch              the production batch code
     * @param manufacturing_date the date the equipment was produced
     */
    public Equipment(String brand, String model, String batch, ZonedDateTime manufacturing_date) {
        this.brand = brand;
        this.model = model;
        this.batch = batch;
        this.manufacturing_date = manufacturing_date;
        this.sku = generateSku();
    }

    /**
     * Recursively generates a non-zero, unique random integer identifier for the SKU field.
     *
     * @return a 6-digit random SKU number
     */
    private static int generateSku() {
        int num = (int) (Math.random() * 1_000_000);
        if (num == 0) return generateSku();
        return num;
    }

    public RecordId getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getBatch() { return batch; }
    public int getSku() { return sku; }
    public ZonedDateTime getManufacturingDate() { return manufacturing_date; }
    public ZonedDateTime getLastRepairDate() { return last_repair_date; }
    public ZonedDateTime getLastSubmissionDate() { return last_submission_date; }

    public void setId(RecordId id) { this.id = id; }
    /** Forces a regeneration of the item's unique SKU barcode identifier. */
    public void regenerateSku() { this.sku = generateSku(); }

    /**
     * Interactive console workflow that walks a user through creating equipment.
     *
     * @return a fully populated {@link Equipment} entity, or {@code null} if cancelled or invalid
     */
    public static Equipment create() {
        String brand = Input.getInput("Brand");
        if (brand == null) return null;

        String model = Input.getInput("Model");
        if (model == null) return null;

        String batch = Input.getInput("Batch");
        if (batch == null) return null;

        String dateStr = Input.getInput("Manufacturing date (YYYY-MM-DD)");
        if (dateStr == null) return null;

        try {
            ZonedDateTime date = LocalDate.parse(dateStr).atStartOfDay(ZoneId.systemDefault());
            return new Equipment(brand, model, batch, date);
        } catch (Exception e) {
            System.err.println("Invalid date format.");
            return null;
        }
    }

    /**
     * Converts equipment data into a structured format for storage queries.
     *
     * @param equipment the asset to map
     * @return map payload containing base core equipment fields
     */
    public static Map<String, Object> toMap(Equipment equipment) {
        return Map.of(
            "brand", equipment.getBrand(),
            "model", equipment.getModel(),
            "batch", equipment.getBatch(),
            "sku", equipment.getSku(),
            "manufacturing_date", equipment.getManufacturingDate()
        );
    }
}