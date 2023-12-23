/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.uece.database;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:src/database/database.db";
    private Connection connection;
    private static Database instance;

    private Database() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
        System.out.println("teste");
            openConnection();
            createTables();
            closeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    
    
    // Open the database connection
    public void openConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createTables() {
        createUserTable();
        createFlightTable();
        createTripTable();
        createSeatTable();
        createDefaultUsers();
    }
    
    private void createUserTable() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS "main"."users" (
                       "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
                       "name" TEXT NOT NULL,
                       "cpf" TEXT NOT NULL,
                       "miles" NUMBER NOT NULL,
                       "username" TEXT NOT NULL,
                       "password" TEXT NOT NULL,
                       "is_admin" integer DEFAULT 0,
                       CONSTRAINT "username" UNIQUE ("username") ON CONFLICT ABORT,
                       CONSTRAINT "cpf" UNIQUE ("cpf") ON CONFLICT ABORT
                     );""";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table users created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createFlightTable() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS "main"."flights" (
                       "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                       "origin_city" TEXT NOT NULL,
                       "destination_city" TEXT NOT NULL,
                       "departure_date" DATE NOT NULL,
                       "return_date" DATE,
                       "price" NUMBER NOT NULL,
                       "price_in_miles" NUMBER NOT NULL,
                       "number_of_seats" INTEGER NOT NULL,
                       "number_of_seats_available" INTEGER NOT NULL
                     );""";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table flights created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createTripTable() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS "main"."trips" (
                       "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                       "user_id" INTEGER NOT NULL,
                       "flight_id" INTEGER NOT NULL,
                       "seat_one_id" INTEGER NOT NULL,
                       "seat_two_id" INTEGER,
                       "seat_three_id" INTEGER,
                       "check_in" INTEGER NOT NULL DEFAULT 0,
                       "bought_with_miles" INTEGER NOT NULL,
                       "bought_luggage" INTEGER NOT NULL,
                       "bought_hotel" INTEGER NOT NULL,
                       "bought_car" INTEGER NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users(id),
                       FOREIGN KEY (flight_id) REFERENCES flights(id),
                       FOREIGN KEY (seat_one_id) REFERENCES seats(id),
                       FOREIGN KEY (seat_two_id) REFERENCES seats(id),
                       FOREIGN KEY (seat_three_id) REFERENCES seats(id)
                     );""";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table trips created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createSeatTable() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS "main"."seats" (
                       "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                       "number" INTEGER NOT NULL,
                       "name" TEXT NOT NULL,
                       "cpf" TEXT NOT NULL
                     );""";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table seats created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createDefaultUsers(){
        String sql = """
                     INSERT OR IGNORE INTO "main"."users" 
                     ("id", "name", "cpf", "miles", "username", "password", "is_admin") 
                     VALUES (1, 'admin', '235.173.030-54', 10000000, 'admin', 'admin', 1);
                     """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Default users created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    
    
}
