import com.mongodb.client.MongoClients;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    public static void main(String[] args) {
        try {
            // Create a connection to MongoDB
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

            // Access the database (creates it if it doesn’t exist)
            MongoDatabase database = mongoClient.getDatabase("myDatabase");

            System.out.println("Connected to MongoDB successfully!");

            // Close the connection
            mongoClient.close();
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }
}
