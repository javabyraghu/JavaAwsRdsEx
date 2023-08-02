package com.app.raghu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.RdsException;

public class RDSExample {

    public static void main(String[] args) {
        // Replace 'YOUR_REGION' with the AWS region you want to work with (e.g., "us-east-1")
        Region region = Region.of("ap-south-1");

        // Create an RDS client using default credentials provider
        RdsClient rdsClient = RdsClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        // Create a new RDS instance (database)
        String instanceIdentifier = "raghu-db-instance";
        String masterUsername = "root";
        String masterPassword = "Admin1234";
        String dbName = "springdb";
        String dbInstanceClass = "db.t2.micro"; // Replace with the desired instance class
        String engine = "MySQL"; // Replace with the desired database engine
        Integer allocatedStorage = 20; // Replace with the desired allocated storage (in GB)

        try {
            CreateDbInstanceRequest createDbInstanceRequest = CreateDbInstanceRequest.builder()
                    .dbInstanceIdentifier(instanceIdentifier)
                    .engine(engine)
                    .dbInstanceClass(dbInstanceClass)
                    .allocatedStorage(allocatedStorage)
                    .masterUsername(masterUsername)
                    .masterUserPassword(masterPassword)
                    .build();

            rdsClient.createDBInstance(createDbInstanceRequest);
            System.out.println("RDS instance created successfully.");
        } catch (RdsException e) {
            System.err.println("Error creating RDS instance: " + e.getMessage());
        }

        // Create a table in the RDS instance
        String tableName = "students";
        String jdbcUrl = "jdbc:mysql://raghu-db-instance.cymzfbi6tout.ap-south-1.rds.amazonaws.com:3306/" + dbName;

        try {
            // Replace "jdbc:mysql://your-db-instance-endpoint:3306/your_database" with the actual connection string
            // You can find the endpoint in the RDS console under "Connectivity & security"
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, masterUsername, masterPassword);
                 Statement statement = connection.createStatement()) {

                String createTableQuery = "CREATE TABLE " + tableName + " (id INT PRIMARY KEY, name VARCHAR(255))";
                statement.execute(createTableQuery);

                System.out.println("Table created successfully.");
            } catch (SQLException ex) {
                System.err.println("Error creating table: " + ex.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
        }

        // Insert data into the table
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, masterUsername, masterPassword);
                 Statement statement = connection.createStatement()) {

                String insertDataQuery = "INSERT INTO " + tableName + " (id, name) VALUES (1, 'Raghu')";
                statement.executeUpdate(insertDataQuery);

                System.out.println("Data inserted successfully.");
            } catch (SQLException ex) {
                System.err.println("Error inserting data: " + ex.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
        }

        // Don't forget to close the RDS client when you're done.
        rdsClient.close();
    }
}
