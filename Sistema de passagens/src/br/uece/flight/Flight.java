/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.uece.flight;

import java.util.ArrayList;
import br.uece.database.Database;
import br.uece.seat.Seat;
import br.uece.trip.Trip;
import br.uece.user.User;
import java.sql.*;
import java.util.Date;
import java.util.Map;


public class Flight {
    private Integer id;
    private String originCity;
    private String destinationCity;
    private Date departureDate;
    private Date returnDate;
    private Float price;
    private Float priceInMiles;
    private Integer numberOfSeats;
    private Integer numberOfSeatsAvailable;
    
    public Flight(Integer id, String origin_city, String destination_city, Date departure_date, Float price, Float price_in_miles, Integer number_of_seats) {
        this(id, origin_city, destination_city, departure_date, null, price, price_in_miles, number_of_seats);
    }

    public Flight(Integer id, String origin_city, String destination_city, Date departure_date, Date return_date, Float price, Float price_in_miles, Integer number_of_seats) {
        this(id, origin_city, destination_city, departure_date, return_date, price, price_in_miles, number_of_seats, number_of_seats);
    }
    
    public Flight(Integer id, String origin_city, String destination_city, Date departure_date, Date return_date, Float price, Float price_in_miles, Integer number_of_seats, Integer number_of_seats_available) {
        this.id = id;
        this.originCity = origin_city;
        this.destinationCity = destination_city;
        this.departureDate = departure_date;
        this.returnDate = return_date;
        this.price = price;
        this.priceInMiles = price_in_miles;
        this.numberOfSeats = number_of_seats;
        this.numberOfSeatsAvailable = number_of_seats_available;
    }

    public static boolean create(String origin_city, String destination_city, Date departure_date, Date return_date, Float price, Integer number_of_seats) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        String sql = "INSERT INTO flights (origin_city, destination_city, departure_date, return_date, price, price_in_miles, number_of_seats, number_of_seats_available) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, origin_city);
            pstmt.setString(2, destination_city);
            pstmt.setDate(3, new java.sql.Date(departure_date.getTime()));
            if(return_date != null)
                pstmt.setDate(4, new java.sql.Date(return_date.getTime()));
            else
                pstmt.setNull(4, 1);
            pstmt.setFloat(5, price);
            pstmt.setFloat(6, price*100);
            pstmt.setInt(7, number_of_seats);
            pstmt.setInt(8, number_of_seats);
            pstmt.executeUpdate();
            System.out.println("Flight created successfully.");
            database.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(Integer id, String origin_city, String destination_city, Date departure_date, Date return_date, Float price, Integer number_of_seats, Integer number_of_seats_available) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        String sql = "UPDATE flights SET origin_city = ?, destination_city = ?, departure_date = ?, return_date = ?, price = ?, price_in_miles = ?, number_of_seats = ?, number_of_seats_available = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, origin_city);
            pstmt.setString(2, destination_city);
            pstmt.setDate(3, new java.sql.Date(departure_date.getTime()));
            if(return_date != null)
                pstmt.setDate(4, new java.sql.Date(return_date.getTime()));
            else
                pstmt.setNull(4, 1);
            pstmt.setFloat(5, price);
            pstmt.setFloat(6, price*100);
            pstmt.setInt(7, number_of_seats);
            pstmt.setInt(8, number_of_seats_available);
            pstmt.setInt(9, id);
            pstmt.executeUpdate();
            System.out.println("Flight edited successfully.");
            database.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean checkIfFlightCanBeDeleted(Integer flightId){
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        try {
            String sql = "SELECT COUNT(t.id) total ";
                sql += "FROM trips t ";
                sql += "JOIN flights f ON t.flight_id = f.id ";
                sql += "WHERE f.id = ? AND t.check_in = 1";
            try (PreparedStatement pstmt  = connection.prepareStatement(sql)) {
                pstmt .setInt(1, flightId);
                ResultSet resultSet = pstmt .executeQuery();
                while (resultSet.next()) {
                    int total = resultSet.getInt("total");
                    return total == 0;
                }
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            database.closeConnection();
        }
        return false;
    }

    public static void deleteFlightAndRefundMiles(Integer flightId) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();

        try {
            String sql = "SELECT t.id, t.user_id, f.price_in_miles ";
                sql += "FROM trips t ";
                sql += "JOIN flights f ON t.flight_id = f.id ";
                sql += "WHERE f.id = ? AND t.bought_with_miles = 1";
            try (PreparedStatement pstmt  = connection.prepareStatement(sql)) {
                pstmt .setInt(1, flightId);
                ResultSet resultSet = pstmt .executeQuery();
                while (resultSet.next()) {
                    Integer tripId = resultSet.getInt("id");
                    Integer userId = resultSet.getInt("user_id");
                    Float priceInMiles = resultSet.getFloat("price_in_miles");
                    Trip trip = Trip.readById(tripId);
                    String updateUserMilesSql = "UPDATE users SET miles = miles + ? WHERE id = ?";
                    try (PreparedStatement updateUserMilesStmt = connection.prepareStatement(updateUserMilesSql)) {
                        Integer numberOfSeats = 1;
                        if(trip.getSeatTwo() != null)
                            numberOfSeats++;
                        if(trip.getSeatThree()!= null)
                            numberOfSeats++;
                        Float price = priceInMiles*numberOfSeats;
                        if(trip.getBoughtLuggage())
                            price += 10*100;
                        if(trip.getBoughtHotel())
                            price += 10*100;
                        if(trip.getBoughtCar())
                            price += 10*100;
                        updateUserMilesStmt.setFloat(1, price);
                        updateUserMilesStmt.setInt(2, userId);
                        updateUserMilesStmt.executeUpdate();
                    }
                    String deleteTripsSql = "DELETE FROM trips WHERE id = ?";
                    try (PreparedStatement deleteTripStmt = connection.prepareStatement(deleteTripsSql)) {
                        deleteTripStmt.setInt(1, trip.getId());
                        deleteTripStmt.executeUpdate();
                        System.out.println("Trip deleted successfully");
                    }
                    String deleteSeatsSql = "DELETE FROM seats WHERE id = ?";
                    try (PreparedStatement deleteSeatsStmt = connection.prepareStatement(deleteSeatsSql)) {
                        deleteSeatsStmt.setInt(1, trip.getSeatOne().getId());
                        deleteSeatsStmt.executeUpdate();
                        System.out.println("Seat one deleted successfully");
                    }
                    if(trip.getSeatTwo() != null){
                        try (PreparedStatement deleteSeatsStmt = connection.prepareStatement(deleteSeatsSql)) {
                            deleteSeatsStmt.setInt(1, trip.getSeatTwo().getId());
                            deleteSeatsStmt.executeUpdate();
                            System.out.println("Seat two deleted successfully");
                        }
                    }
                    if(trip.getSeatThree() != null){
                        try (PreparedStatement deleteSeatsStmt = connection.prepareStatement(deleteSeatsSql)) {
                            deleteSeatsStmt.setInt(1, trip.getSeatThree().getId());
                            deleteSeatsStmt.executeUpdate();
                            System.out.println("Seat three deleted successfully");
                        }
                    }
                }
                String deleteFlightSql = "DELETE FROM flights WHERE id = ?";
                try (PreparedStatement deleteFlightStmt = connection.prepareStatement(deleteFlightSql)) {
                    deleteFlightStmt.setInt(1, flightId);
                    deleteFlightStmt.executeUpdate();
                    System.out.println("Flight deleted successfully and miles refunded.");
                }
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            database.closeConnection();
        }
    }
    public static ArrayList<Flight> read(Boolean all_flights) {
        return Flight.read(all_flights, null);
    }

    public static ArrayList<Flight> read(Boolean all_flights, Map<String, String> filters) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        ArrayList<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights WHERE number_of_seats_available > 0 ";
        if (all_flights){
            sql = "SELECT * FROM flights ";
        }
        if(filters != null && !filters.isEmpty()){
            if(!sql.contains("WHERE")){
                sql += "WHERE ";
            }else{
                sql += "AND ";
            }
            Integer count = 0;
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                sql += entry.getKey() + " LIKE '%" + entry.getValue() + "%' ";
                if(count != (filters.size()-1))
                    sql += "AND ";
                count++;
            }
            sql += " COLLATE NOCASE";
        }
        System.out.println(sql);
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String origin_city = rs.getString("origin_city");
                String destination_city = rs.getString("destination_city");
                Date departure_date = rs.getDate("departure_date");
                Date return_date = rs.getDate("return_date");
                Float price = rs.getFloat("price");
                Float price_in_miles = rs.getFloat("price_in_miles");
                Integer number_of_seats = rs.getInt("number_of_seats");
                Integer number_of_seats_available = rs.getInt("number_of_seats_available");
                Flight flight = new Flight(id, origin_city, destination_city, departure_date, return_date, price, price_in_miles, number_of_seats, number_of_seats_available);
                flights.add(flight);
                String ans = "ID: " + id + ", Origin City: " + origin_city + ", ";
                ans += "Destination City: " + destination_city + ", Departure Date: " + departure_date + ", ";
                ans += "Return Date: " + return_date + ", Price: " + price + ", Price in Miles: " + price_in_miles + ", ";
                ans += "Number of Seats: " + number_of_seats + ", Number of Seats Available: " + number_of_seats_available;
                System.out.println(ans);
                
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }
   
    
    public static Flight readById(Integer id) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        Flight flight = null;
        String sql = "SELECT * FROM flights WHERE id = " + id;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String origin_city = rs.getString("origin_city");
                String destination_city = rs.getString("destination_city");
                Date departure_date = rs.getDate("departure_date");
                Date return_date = rs.getDate("return_date");
                Float price = rs.getFloat("price");
                Float price_in_miles = rs.getFloat("price_in_miles");
                Integer number_of_seats = rs.getInt("number_of_seats");
                Integer number_of_seats_available = rs.getInt("number_of_seats_available");
                flight = new Flight(id, origin_city, destination_city, departure_date, return_date, price, price_in_miles, number_of_seats, number_of_seats_available);
                String ans = "ID: " + id + ", Origin City: " + origin_city + ", ";
                ans += "Destination City: " + destination_city + ", Departure Date: " + departure_date + ", ";
                ans += "Return Date: " + return_date + ", Price: " + price + ", Price in Miles: " + price_in_miles + ", ";
                ans += "Number of Seats: " + number_of_seats + ", Number of Seats Available: " + number_of_seats_available;
                System.out.println(ans);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flight;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPriceInMiles() {
        return priceInMiles;
    }

    public void setPriceInMiles(Float priceInMiles) {
        this.priceInMiles = priceInMiles;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Integer getNumberOfSeatsAvailable() {
        return numberOfSeatsAvailable;
    }

    public void setNumberOfSeatsAvailable(Integer numberOfSeatsAvailable) {
        this.numberOfSeatsAvailable = numberOfSeatsAvailable;
    }
    
    
    
}
