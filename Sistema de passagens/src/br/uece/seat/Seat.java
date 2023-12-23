/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.uece.seat;

import br.uece.trip.*;
import br.uece.database.Database;
import br.uece.flight.Flight;
import br.uece.user.User;
import java.sql.*;
import java.util.ArrayList;


public class Seat {
    
    private Integer id;
    private Integer number;
    private String name;
    private String cpf;

    public Seat(Integer id, Integer number, String name, String cpf) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.cpf = cpf;
    }
    
    public static boolean create(Integer number, String name, String cpf) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        System.out.println(number + " " + name + " " + cpf);
        String sql = "INSERT INTO seats (number, name, cpf) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, number);
            pstmt.setString(2, name);
            pstmt.setString(3, cpf);
            pstmt.executeUpdate();
            System.out.println("Seat created successfully.");
            database.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    public static void delete(Integer id) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        String sql = "DELETE FROM seats WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Seat deleted successfully.");
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            database.closeConnection();
        }
    }
    
    public static ArrayList<Seat> read() {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        ArrayList<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer number = rs.getInt("number");
                String name = rs.getString("name");
                String cpf = rs.getString("cpf");
                Seat seat = new Seat(id, number, name, cpf);
                seats.add(seat);
                System.out.println("Number: " + number + ", Name: " + name + ", CPF: " + cpf);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }
    
    public static Seat readById(Integer id) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        Seat seat = null;
        String sql = "SELECT * FROM seats WHERE id = " + id;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer number = rs.getInt("number");
                String name = rs.getString("name");
                String cpf = rs.getString("cpf");
                seat = new Seat(id, number, name, cpf);
                System.out.println("Number: " + number + ", Name: " + name + ", CPF: " + cpf);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seat;
    }
    
    public static Seat readHighestId() {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        Seat seat = null;
        String sql = "SELECT * FROM seats WHERE id = (SELECT MAX(id) FROM seats)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer number = rs.getInt("number");
                String name = rs.getString("name");
                String cpf = rs.getString("cpf");
                seat = new Seat(id, number, name, cpf);
                System.out.println("ID: " + id + ", Number: " + number + ", Name: " + name + ", CPF: " + cpf);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    
}
