/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.uece.user;

import java.util.ArrayList;
import br.uece.database.Database;
import java.sql.*;


public class User {

    private Integer id;
    private String name;
    private String cpf;
    private Float miles;
    private String username;
    private String password;
    private Boolean is_admin;

    public User(Integer id, String name, String cpf, Float miles, String username, String password, Boolean is_admin) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.miles = miles;
        this.username = username;
        this.password = password;
        this.is_admin = is_admin;
    }

    public static boolean create(String name, String cpf, Float miles, String username, String password) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        String sql = "INSERT INTO users (name, cpf, miles, username, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, cpf);
            pstmt.setFloat(3, miles);
            pstmt.setString(4, username);
            pstmt.setString(5, password);
            pstmt.executeUpdate();
            System.out.println("User created successfully.");
            database.closeConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean update(Integer id, String name, String cpf, Float miles, String username, String password) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        String sql = "UPDATE users SET name = ?, cpf = ?, miles = ?, username = ?, password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, cpf);
            pstmt.setFloat(3, miles);
            pstmt.setString(4, username);
            pstmt.setString(5, password);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            System.out.println("User edited successfully.");
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
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("User deleted successfully.");
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            database.closeConnection();
        }
    }

    public static ArrayList<User> read() {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String cpf = rs.getString("cpf");
                Float miles = rs.getFloat("miles");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Boolean is_admin = rs.getInt("is_admin") != 0;
                User user = new User(id, name, cpf, miles, username, password, is_admin);
                users.add(user);
                System.out.println("ID: " + id + ", Name: " + name + ", CPF: " + cpf + ", miles: " + miles + ", Username: " + username + ", is_admin: " + is_admin);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public static User readById(Integer id) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        User user = null;
        String sql = "SELECT * FROM users WHERE id = " + id;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                String cpf = rs.getString("cpf");
                Float miles = rs.getFloat("miles");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Boolean is_admin = rs.getInt("is_admin") != 0;
                user = new User(id, name, cpf, miles, username, password, is_admin);
                System.out.println("ID: " + id + ", Name: " + name + ", CPF: " + cpf + ", miles: " + miles + ", Username: " + username + ", is_admin: " + is_admin);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Float getMiles() {
        return miles;
    }

    public void setMiles(Float miles) {
        this.miles = miles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
        return is_admin;
    }

    public void setIsAdmin(Boolean is_admin) {
        this.is_admin = is_admin;
    }

}
