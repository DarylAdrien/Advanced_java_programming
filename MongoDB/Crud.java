import com.mongodb.client.*;


import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.Arrays;
import java.util.Scanner;

public class Crud {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Connect to MongoDB
        String connectionString = "mongodb://localhost:27017"; // Change if needed
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        MongoCollection<Document> collection = database.getCollection("myCollection");

        while (true) {
            System.out.println("\n--- MongoDB CRUD Operations ---");
            System.out.println("1. Insert Data");
            System.out.println("2. Read Data");
            System.out.println("3. Update Data");
            System.out.println("4. Delete Data");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline
            
            switch (choice) {
                case 1:
                    // CREATE: Insert a document
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter age: ");
                    int age = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    System.out.print("Enter skills (comma separated): ");
                    String skillsInput = scanner.nextLine();
                    String[] skills = skillsInput.split(",");

                    Document newDocument = new Document("name", name)
                            .append("age", age)
                            .append("skills", Arrays.asList(skills));
                    collection.insertOne(newDocument);
                    System.out.println("✅ Document Inserted!");
                    break;

                case 2:
                    // READ: Find a document
                    System.out.print("Enter name to search: ");
                    String searchName = scanner.nextLine();
                    Document foundDocument = collection.find(Filters.eq("name", searchName)).first();
                    if (foundDocument != null) {
                        System.out.println("🔍 Found Document: " + foundDocument.toJson());
                    } else {
                        System.out.println("❌ No document found!");
                    }
                    break;

                case 3:
                    // UPDATE: Modify a document
                    System.out.print("Enter name to update: ");
                    String updateName = scanner.nextLine();
                    System.out.print("Enter new age: ");
                    int newAge = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    collection.updateOne(Filters.eq("name", updateName), Updates.set("age", newAge));
                    System.out.println("🛠️ Document Updated!");
                    break;

                case 4:
                    // DELETE: Remove a document
                    System.out.print("Enter name to delete: ");
                    String deleteName = scanner.nextLine();
                    collection.deleteOne(Filters.eq("name", deleteName));
                    System.out.println("🗑️ Document Deleted!");
                    break;

                case 5:
                    // EXIT
                    System.out.println("👋 Exiting...");
                    mongoClient.close();
                    scanner.close();
                    return;

                default:
                    System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }
}
