import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// --- Abstract Base Class with Polymorphic Type Handling ---
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Shirt.class, name = "shirt"),
    @JsonSubTypes.Type(value = Pants.class, name = "pants"),
    @JsonSubTypes.Type(value = Shoes.class, name = "shoes")
})
abstract class ClothingItem {
    private String name;
    private String category;
    private String size;
    private String color;

    public ClothingItem() {} // Required for Jackson deserialization

    public ClothingItem(String name, String category, String size, String color) {
        this.name = name;
        this.category = category;
        this.size = size;
        this.color = color;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSize() { return size; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return String.format("Name: %s | Category: %s | Size: %s | Color: %s", name, category, size, color);
    }
}

// --- Subclass: Shirt ---
class Shirt extends ClothingItem {
    private String sleeveLength;

    public Shirt() {}

    public Shirt(String name, String size, String color, String sleeveLength) {
        super(name, "Shirt", size, color);
        this.sleeveLength = sleeveLength;
    }

    public String getSleeveLength() { return sleeveLength; }

    @Override
    public String toString() {
        return super.toString() + " | Sleeve: " + sleeveLength;
    }
}

// --- Subclass: Pants ---
class Pants extends ClothingItem {
    private int waistSize;
    private int inseam;

    public Pants() {}

    public Pants(String name, String size, String color, int waistSize, int inseam) {
        super(name, "Pants", size, color);
        this.waistSize = waistSize;
        this.inseam = inseam;
    }

    public int getWaistSize() { return waistSize; }
    public int getInseam() { return inseam; }

    @Override
    public String toString() {
        return super.toString() + " | Waist: " + waistSize + " | Inseam: " + inseam;
    }
}

// --- Subclass: Shoes ---
class Shoes extends ClothingItem {
    private double shoeSize;

    public Shoes() {}

    public Shoes(String name, String color, double shoeSize) {
        super(name, "Shoes", String.valueOf(shoeSize), color);
        this.shoeSize = shoeSize;
    }

    public double getShoeSize() { return shoeSize; }

    @Override
    public String toString() {
        return super.toString() + " | US Shoe Size: " + shoeSize;
    }
}

// --- Closet Class to Manage Inventory ---
class Closet {
    private List<ClothingItem> items = new ArrayList<>();

    public void addItem(ClothingItem item) {
        items.add(item);
    }

    public List<ClothingItem> getItems() {
        return items;
    }

    public void displayInventory() {
        if (items.isEmpty()) {
            System.out.println("\nYour closet is currently empty.");
        } else {
            System.out.println("\n--- Current Digital Closet Inventory ---");
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". " + items.get(i));
            }
        }
    }
}

// --- Main Application Loop & Local Storage Handler ---
public class DigitalClosetApp {
    private static final String FILE_PATH = "closet_data.json";
    private static ObjectMapper mapper;

    public static void main(String[] args) {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty-print JSON

        Closet closet = loadCloset();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Welcome to your Digital Closet Inventory System ===");

        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. View Closet Inventory");
            System.out.println("2. Add a Shirt");
            System.out.println("3. Add Pants");
            System.out.println("4. Add Shoes");
            System.out.println("5. Save & Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    closet.displayInventory();
                    break;
                case "2":
                    System.out.print("Enter shirt name: ");
                    String sName = scanner.nextLine();
                    System.out.print("Enter size (S/M/L/XL): ");
                    String sSize = scanner.nextLine();
                    System.out.print("Enter color: ");
                    String sColor = scanner.nextLine();
                    System.out.print("Enter sleeve length (Short/Long): ");
                    String sleeve = scanner.nextLine();

                    closet.addItem(new Shirt(sName, sSize, sColor, sleeve));
                    saveCloset(closet);
                    System.out.println("Shirt added and saved locally!");
                    break;
                case "3":
                    System.out.print("Enter pants name: ");
                    String pName = scanner.nextLine();
                    System.out.print("Enter general size (e.g., Medium): ");
                    String pSize = scanner.nextLine();
                    System.out.print("Enter color: ");
                    String pColor = scanner.nextLine();
                    System.out.print("Enter waist size (inches): ");
                    int waist = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter inseam (inches): ");
                    int inseam = Integer.parseInt(scanner.nextLine());

                    closet.addItem(new Pants(pName, pSize, pColor, waist, inseam));
                    saveCloset(closet);
                    System.out.println("Pants added and saved locally!");
                    break;
                case "4":
                    System.out.print("Enter shoe name/brand: ");
                    String shName = scanner.nextLine();
                    System.out.print("Enter color: ");
                    String shColor = scanner.nextLine();
                    System.out.print("Enter US shoe size (e.g., 10.5): ");
                    double sizeNum = Double.parseDouble(scanner.nextLine());

                    closet.addItem(new Shoes(shName, shColor, sizeNum));
                    saveCloset(closet);
                    System.out.println("Shoes added and saved locally!");
                    break;
                case "5":
                    saveCloset(closet);
                    System.out.println("Changes saved. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 5.");
            }
        }
        scanner.close();
    }

    private static Closet loadCloset() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return mapper.readValue(file, Closet.class);
            } catch (IOException e) {
                System.out.println("Error reading local data file. Starting with a fresh closet.");
            }
        }
        return new Closet();
    }

    private static void saveCloset(Closet closet) {
        try {
            mapper.writeValue(new File(FILE_PATH), closet);
        } catch (IOException e) {
            System.out.println("Error saving data to local file: " + e.getMessage());
        }
    }
}