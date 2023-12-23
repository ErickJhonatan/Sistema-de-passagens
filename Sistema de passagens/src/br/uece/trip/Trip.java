/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.uece.trip;

import br.uece.database.Database;
import br.uece.flight.Flight;
import br.uece.seat.Seat;
import br.uece.user.User;
import java.sql.*;
import java.util.ArrayList;


public class Trip {
    
    private Integer id;
    private User user;
    private Flight flight;
    private Seat seatOne;
    private Seat seatTwo;
    private Seat seatThree;
    private Boolean checkIn;
    private Boolean boughtWithMiles;
    private Boolean boughtLuggage;
    private Boolean boughtHotel;
    private Boolean boughtCar;

    public Trip(Integer id, User user, Flight flight, Seat seatOne, Seat seatTwo, Seat seatThree, Boolean checkIn, Boolean boughtWithMiles, Boolean boughtLuggage, Boolean boughtHotel, Boolean boughtCar) {
        this.id = id;
        this.user = user;
        this.flight = flight;
        this.seatOne = seatOne;
        this.seatTwo = seatTwo;
        this.seatThree = seatThree;
        this.checkIn = checkIn;
        this.boughtWithMiles = boughtWithMiles;
        this.boughtLuggage = boughtLuggage;
        this.boughtHotel = boughtHotel;
        this.boughtCar = boughtCar;
    }
    
    public static boolean create(User user, Flight flight, Seat seatOne, Seat seatTwo, Seat seatThree, Boolean boughtWithMiles, Boolean boughtLuggage, Boolean boughtHotel, Boolean boughtCar) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        System.out.println(user.getName() + " " + flight.getId());
        String sql = "INSERT INTO trips (user_id, flight_id, seat_one_id, seat_two_id, seat_three_id, bought_with_miles, bought_luggage, bought_hotel, bought_car) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setInt(2, flight.getId());
            if(seatOne != null)
                pstmt.setInt(3, seatOne.getId());
            else
                pstmt.setNull(3, 1);
            if(seatTwo != null)
                pstmt.setInt(4, seatTwo.getId());
            else
                pstmt.setNull(4, 1);if(seatTwo != null)
            if(seatThree != null)
                pstmt.setInt(5, seatThree.getId());
            else
                pstmt.setNull(5, 1);
            pstmt.setBoolean(6, boughtWithMiles);
            pstmt.setBoolean(7, boughtLuggage);
            pstmt.setBoolean(8, boughtHotel);
            pstmt.setBoolean(9, boughtCar);
            pstmt.executeUpdate();
            System.out.println("Trip created successfully.");
            database.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public static boolean update(Integer id, User user, Flight flight, Seat seatOne, Seat seatTwo, Seat seatThree, Boolean checkIn, Boolean boughtWithMiles, Boolean boughtLuggage, Boolean boughtHotel, Boolean boughtCar) {
    Database database = Database.getInstance();
    database.openConnection();
    Connection connection = database.getConnection();
    System.out.println(user.getName() + " " + flight.getId());

    String sql = "UPDATE trips SET user_id = ?, flight_id = ?, seat_one_id = ?, seat_two_id = ?, seat_three_id = ?, check_in = ?, bought_with_miles = ?, bought_luggage = ?, bought_hotel = ?, bought_car = ? WHERE id = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, user.getId());
        pstmt.setInt(2, flight.getId());
        if (seatOne != null)
            pstmt.setInt(3, seatOne.getId());
        else
            pstmt.setNull(3, Types.INTEGER);
        if (seatTwo != null)
            pstmt.setInt(4, seatTwo.getId());
        else
            pstmt.setNull(4, Types.INTEGER);
        if (seatThree != null)
            pstmt.setInt(5, seatThree.getId());
        else
            pstmt.setNull(5, Types.INTEGER);
        pstmt.setBoolean(6, checkIn);
        pstmt.setBoolean(7, boughtWithMiles);
        pstmt.setBoolean(8, boughtLuggage);
        pstmt.setBoolean(9, boughtHotel);
        pstmt.setBoolean(10, boughtCar);
        pstmt.setInt(11, id);
        pstmt.executeUpdate();
        
        System.out.println("Trip updated successfully.");
        database.closeConnection();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}
    
    public static ArrayList<Trip> read() {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        ArrayList<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer user_id = rs.getInt("user_id");
                Integer flightId = rs.getInt("flight_id");
                Integer seatOneId = rs.getInt("seat_one_id");
                Integer seatTwoId = rs.getInt("seat_two_id");
                Integer seatThreeId = rs.getInt("seat_three_id");
                Boolean checkIn = rs.getBoolean("check_in");
                Boolean boughtWithMiles = rs.getBoolean("bought_with_miles");
                Boolean boughtLuggage = rs.getBoolean("bought_luggage");
                Boolean boughtHotel = rs.getBoolean("bought_hotel");
                Boolean boughtCar = rs.getBoolean("bought_car");
                User user = User.readById(user_id);
                Flight flight = Flight.readById(flightId);
                Seat seatOne = Seat.readById(seatOneId);
                Seat seatTwo = Seat.readById(seatTwoId);
                Seat seatThree = Seat.readById(seatThreeId);
                Trip trip = new Trip(id, user, flight, seatOne, seatTwo, seatThree, checkIn, boughtWithMiles, boughtLuggage, boughtHotel, boughtCar);
                trips.add(trip);
                System.out.println("User: " + user.getName() + ", Flight: " + flight.getId());
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }
    
    public static Trip readById(Integer id) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        Trip trip = null;
        String sql = "SELECT * FROM trips WHERE id = " + id;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer user_id = rs.getInt("user_id");
                Integer flightId = rs.getInt("flight_id");
                Integer seatOneId = rs.getInt("seat_one_id");
                Integer seatTwoId = rs.getInt("seat_two_id");
                Integer seatThreeId = rs.getInt("seat_three_id");
                Boolean checkIn = rs.getBoolean("check_in");
                Boolean boughtWithMiles = rs.getBoolean("bought_with_miles");
                Boolean boughtLuggage = rs.getBoolean("bought_luggage");
                Boolean boughtHotel = rs.getBoolean("bought_hotel");
                Boolean boughtCar = rs.getBoolean("bought_car");
                User user = User.readById(user_id);
                Flight flight = Flight.readById(flightId);
                Seat seatOne = Seat.readById(seatOneId);
                Seat seatTwo = Seat.readById(seatTwoId);
                Seat seatThree = Seat.readById(seatThreeId);
                trip = new Trip(id, user, flight, seatOne, seatTwo, seatThree, checkIn, boughtWithMiles, boughtLuggage, boughtHotel, boughtCar);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trip;
    }
    
    public static ArrayList<Trip> readByUserId(Integer user_id) {
        Database database = Database.getInstance();
        database.openConnection();
        Connection connection = database.getConnection();
        ArrayList<Trip> trips = new ArrayList<>();
        String sql = "SELECT f.id, f.origin_city, f.destination_city, ";
        sql += "f.departure_date, f.return_date, f.price, f.price_in_miles, ";
        sql += "t.id AS id2, t.seat_one_id, t.seat_two_id, t.seat_three_id, t.check_in, t.bought_with_miles, t.bought_luggage, t.bought_hotel, t.bought_car ";
        sql += "FROM flights f JOIN trips t ON t.flight_id = f.id WHERE t.user_id = " + user_id;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String origin_city = rs.getString("origin_city");
                String destination_city = rs.getString("destination_city");
                java.util.Date departure_date = rs.getDate("departure_date");
                java.util.Date return_date = rs.getDate("return_date");
                Float price = rs.getFloat("price");
                Float price_in_miles = rs.getFloat("price_in_miles");
                Integer trip_id = rs.getInt("id2");
                Integer seatOne_id = rs.getInt("seat_one_id");
                Integer seatTwo_id = rs.getInt("seat_two_id");
                Integer seatThree_id = rs.getInt("seat_three_id");
                Boolean checkIn = rs.getBoolean("check_in");
                Boolean boughtWithMiles = rs.getBoolean("bought_with_miles");
                Boolean boughtLuggage = rs.getBoolean("bought_luggage");
                Boolean boughtHotel = rs.getBoolean("bought_hotel");
                Boolean boughtCar = rs.getBoolean("bought_car");
                Flight flight = new Flight(id, origin_city, destination_city, departure_date, return_date, price, price_in_miles, 0);
                Trip trip = new Trip(trip_id, User.readById(user_id), flight, Seat.readById(seatOne_id), Seat.readById(seatTwo_id), Seat.readById(seatThree_id), checkIn, boughtWithMiles, boughtLuggage, boughtHotel, boughtCar);
                trips.add(trip);
            }
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Seat getSeatOne() {
        return seatOne;
    }

    public void setSeatOne(Seat seatOne) {
        this.seatOne = seatOne;
    }

    public Seat getSeatTwo() {
        return seatTwo;
    }

    public void setSeatTwo(Seat seatTwo) {
        this.seatTwo = seatTwo;
    }

    public Seat getSeatThree() {
        return seatThree;
    }

    public void setSeatThree(Seat seatThree) {
        this.seatThree = seatThree;
    }

    public Boolean getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Boolean checkIn) {
        this.checkIn = checkIn;
    }

    public Boolean getBoughtWithMiles() {
        return boughtWithMiles;
    }

    public void setBoughtWithMiles(Boolean boughtWithMiles) {
        this.boughtWithMiles = boughtWithMiles;
    }

    public Boolean getBoughtLuggage() {
        return boughtLuggage;
    }

    public void setBoughtLuggage(Boolean boughtLuggage) {
        this.boughtLuggage = boughtLuggage;
    }

    public Boolean getBoughtHotel() {
        return boughtHotel;
    }

    public void setBoughtHotel(Boolean boughtHotel) {
        this.boughtHotel = boughtHotel;
    }

    public Boolean getBoughtCar() {
        return boughtCar;
    }

    public void setBoughtCar(Boolean boughtCar) {
        this.boughtCar = boughtCar;
    }
    
    
    
}
