import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class ClothingItem {
    // Private Attributes 
    private String type;      
    private String name;      
    private String size;      
    private String color;    

    public ClothingItem(String type, String name, String size, String color) {
        this.type = type;
        this.name = name;
        this.size = size;
        this.color = color;
    }

    // get info about clothing item
    public String getType() { return type; }
    public String getName() { return name; }
    public String getSize() { return size; }
    public String getColor() { return color; }

    // Converts item to a simple CSV line: Type,Name,Size,Color
    public String toCsv() {
        return type + "," + name + "," + size + "," + color;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Size: %s | Color: %s", type, name, size, color);
    }
}

// Main Application 
public class DigitalClosetApp {
    private static final String FILE_PATH = "closet_data.csv";
    
    // all items saved into this list of clothing items
    private static List<ClothingItem> items = new ArrayList<>();

    public static void main(String[] args) {
        loadData();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Digital Closet Inventory ===");

        while (running) {
            System.out.println("\n1. View Closet");
            System.out.println("2. Add Clothing Item");
            System.out.println("3. Search by type of clothing");
            System.out.println("4. Save & Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayCloset();
                    break;
                case "2":
                    System.out.print("Enter type (Shirt, Pants, Shoes): ");
                    String type = scanner.nextLine();
                    System.out.print("Enter name/brand: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter size: ");
                    String size = scanner.nextLine();
                    System.out.print("Enter color: ");
                    String color = scanner.nextLine();

                    items.add(new ClothingItem(type, name, size, color));
                    saveData();
                    System.out.println("Item added and saved!");
                    break;
                case "4":
                    saveData();
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                case "3":
    System.out.print("Enter clothing type to search for: ");
    String searchType = scanner.nextLine();

    boolean found = false;

    for (ClothingItem item : items) {
        if (item.getType().equalsIgnoreCase(searchType)) {
            System.out.println(item);
            found = true;
        }
    }

    if (!found) {
        System.out.println("No clothing items found with that type.");
    }
    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static void displayCloset() {
        if (items.isEmpty()) {
            System.out.println("\nYour closet is empty.");
        } else {
            System.out.println("\n--- Closet Inventory ---");
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". " + items.get(i));
            }
        }
    }

    // Load from CSV file
    private static void loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    items.add(new ClothingItem(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    // Save to CSV file
    private static void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (ClothingItem item : items) {
                writer.println(item.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }
}