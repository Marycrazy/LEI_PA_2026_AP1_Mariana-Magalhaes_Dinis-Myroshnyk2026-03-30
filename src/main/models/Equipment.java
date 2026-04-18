package main.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import com.surrealdb.RecordId;
import main.utils.Input;

public class Equipment {
    private RecordId id;
    private String brand, model, batch;
    private int sku;
    private ZonedDateTime manufacturing_date;
    private ZonedDateTime last_repair_date;
    private ZonedDateTime last_submission_date;

    public Equipment() {}

    public Equipment(String brand, String model, String batch, ZonedDateTime manufacturing_date) {
        this.brand = brand;
        this.model = model;
        this.batch = batch;
        this.manufacturing_date = manufacturing_date;
        this.sku = generateSku();
    }

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
    public void regenerateSku() { this.sku = generateSku(); }

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