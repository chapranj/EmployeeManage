package com.example.management;


import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.security.auth.PrivateCredentialPermission;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeManager {
    public static String jdbcUrl = "jdbc:mysql://localhost:3306/employeemanage";
    public static String username = "root";
    public static String password = "";
    public static void addEmployeeMethod(String id, String name ,String pass){

        try{
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to the database!");

            String checkQuery = "Select * from employee where employeeId = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1,id);
//            checkStatement.setString(2,pass);

            ResultSet resultSet = checkStatement.executeQuery();
            if (!resultSet.next()){
                String insertQuery = "insert into employee (employeeId, employeeName ,employeePass) values (?,?,?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1,id);
                insertStatement.setString(2,name);
                insertStatement.setString(3,pass);
                int rowsInserted = insertStatement.executeUpdate();
                if (rowsInserted>0){
                    System.out.println("successfully added the new employee information!");
                    System.out.println("new employee id: "+ id);
                    System.out.println("new employee name: "+ name);
                }
                else{
                    System.out.println("Failed to add the new Employee");
                }
            }
            else{
                System.out.println("Employee with the same ID already exists....cannot add!");
            }
        }
        catch (Exception e){
            System.out.println("Failed to connect to the database!");
        }

    }

    public static void viewEmpMethod(Stage stage) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String query = "select * from work_records";
            PreparedStatement retrieveStatement = connection.prepareStatement(query);
            ResultSet resultSet = retrieveStatement.executeQuery();
            stage.setScene(LoginSignUp.viewEmpScene);
            while (resultSet.next()) {
                String empId = resultSet.getString("employeeId");
                String employeeName = resultSet.getString("employeeName");
                Date workDate = resultSet.getDate("work_date");
                Timestamp clockInTime = resultSet.getTimestamp("clock_in");
                Timestamp clockOutTime = resultSet.getTimestamp("clock_out");
                double hours = resultSet.getDouble("hours_worked");
                String formattedText = String.format("Employee Id: %s | Employee Name: %s | Date: %s | Clock In Time: %s | Clock Out Time: %s | Hours: %.2f",
                        empId, employeeName, new SimpleDateFormat("yyyy-MM-dd").format(workDate), clockInTime, clockOutTime, hours);

                // Create the Text node with formatted text
                Text txtInfo = new Text(formattedText);

                LoginSignUp.empInfoContainer.getChildren().add(txtInfo);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static void removeEmpMethod(Stage stage, String empIdToRemove){
        try{
            Connection connection = DriverManager.getConnection(jdbcUrl,username,password);
            String query = "select * from employee where employeeId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,empIdToRemove);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String deleteQuery = "delete from employee where employeeId = ?";
                PreparedStatement deletePrep = connection.prepareStatement(deleteQuery);
                deletePrep.setString(1,empIdToRemove);
                int rowsUpdated = deletePrep.executeUpdate();
                if (rowsUpdated > 0){
                    System.out.println("Removed employee");
                }
                else{
                    System.out.println("Cannot remove employee!");
                }
            }
            else{
                System.out.println("No such employee found!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void empManage(String id, String pass, Stage stage){
        try{
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String checkQuery = "select * from employee where employeeId = ? and employeePass = ?";
            PreparedStatement retrieveStatement = connection.prepareStatement(checkQuery);
            retrieveStatement.setString(1,id);
            retrieveStatement.setString(2,pass);
            ResultSet resultSet = retrieveStatement.executeQuery();
            if (resultSet.next()){
                String nameOfEmp = resultSet.getString("employeeName");

                LoginSignUp.nameOfLoggedIn.setText(nameOfEmp);
                stage.setScene(LoginSignUp.empOperationScene);

            }
            else {
                LoginSignUp.errorLabel.setText("Incorrect Credentials!");
                System.out.println("did not match any record!");
            }
        }
        catch (SQLException e) {
//            throw new RuntimeException(e);
            LoginSignUp.errorLabel.setText("Incorrect Credentials!");
        }
    }

    public static void clockedIn(Employee employee,Stage stage){
        if (employee.getClockInTime()==null){
            employee.setClockInTime(LocalDateTime.now());
            System.out.println(employee.getId() + " clocked in Successfully!");
            stage.setScene(LoginSignUp.firstScene);
        }
        else{
            System.out.println("Employee has already clocked in..");
        }
    }

    public static void clockedOut(Employee employee,Stage stage){
        if (employee.getClockInTime() != null && employee.getClockOutTime()==null){
            employee.setClockOutTime(LocalDateTime.now());
            LocalDateTime clockInTime = employee.getClockInTime();
            LocalDateTime clockOutTime = employee.getClockOutTime();
            double hoursWorked = calcHoursWorked(employee.getClockInTime(),employee.getClockOutTime());
            double pay = calculatePay(hoursWorked,16.50);
            System.out.println("Successfully clocked out "+ employee.getId());
            System.out.println("Pay for current session: "+pay);
            System.out.println(employee.getId());
            insertHoursAndPay(employee.getId(),hoursWorked,16.50,pay,stage);
            insertIntoWorkRecords(employee.getId(), employee.getName(), LocalDate.now(),clockInTime,
                   clockOutTime,hoursWorked,16.50);
            employee.setClockInTime(null);
            employee.setClockOutTime(null);
        }
        else{
            System.out.println("Already clocked out!");

        }
    }

    public static void viewHoursMethod(String empId,Stage stage){
        try{
            Connection connection = DriverManager.getConnection(jdbcUrl,username,password);
            String query = "select * from work_records where employeeId = ?";
            PreparedStatement retrieveQuery = connection.prepareStatement(query);
            retrieveQuery.setString(1,empId);
            ResultSet resultSet = retrieveQuery.executeQuery();
            stage.setScene(LoginSignUp.viewHoursScene);

            LoginSignUp.viewHoursContainer.getChildren().clear();
            while (resultSet.next()){
                Date workDate = resultSet.getDate("work_date");
                Timestamp clockInTime = resultSet.getTimestamp("clock_in");
                Timestamp clockOutTime = resultSet.getTimestamp("clock_out");
                double hours = resultSet.getDouble("hours_worked");
                String formattedText = String.format(
                        "Work Date: %s\nClock In Time: %s\nClock Out Time: %s\nHours Worked: %.2f\n\n",
                        workDate, clockInTime, clockOutTime, hours);

                Text txt = new Text(formattedText);
                txt.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-fill: #333;");
                LoginSignUp.viewHoursContainer.getChildren().add(txt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static double calcHoursWorked(LocalDateTime clockInTime, LocalDateTime clockOutTime){
        Duration duration = Duration.between(clockInTime,clockOutTime);
        return duration.toSeconds();
    }



    public static double calculatePay(double hoursWorked, double hourlyRate){
        return hourlyRate*hoursWorked;
    }



    public static void insertHoursAndPay(String empId,double hoursWorked, double hourlyRate, double pay,
                                         Stage stage){
        try{
            Connection connection = DriverManager.getConnection(jdbcUrl,username,password);
            String selectQuery = "select * from hours_and_pay where employeeId = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1,empId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                String query = "update hours_and_pay set hours_worked = hours_worked + ?, pay = pay + ?, hourly_rate = ? " +
                        "where employeeId = ?";
                PreparedStatement insertStatement = connection.prepareStatement(query);
                insertStatement.setDouble(1, hoursWorked);
                insertStatement.setDouble(2, pay);
                insertStatement.setDouble(3, hourlyRate);
                insertStatement.setString(4, empId);

                int rowsInserted = insertStatement.executeUpdate();
                if (rowsInserted > 0){
                    System.out.println("Hours and pay updated for the employee "+ empId);
                    stage.setScene(LoginSignUp.firstScene);
                }
                else{
                    System.out.println("Failed to add Employee Pay information!");
                }
            }
            else {
                String insertQuery = "insert into hours_and_pay (employeeId, hours_worked, hourly_rate, pay) values " +
                        "(?,?,?,?)";
                PreparedStatement firstRecordStatement = connection.prepareStatement(insertQuery);
                firstRecordStatement.setString(1,empId);
                firstRecordStatement.setDouble(2,hoursWorked);
                firstRecordStatement.setDouble(3,hourlyRate);
                firstRecordStatement.setDouble(4,pay);
                int rowsUpdated = firstRecordStatement.executeUpdate();
                if (rowsUpdated > 0){
                    System.out.println("Hours and pay updated for the first employee in the record "+ empId);

                }
                else{
                    System.out.println("Failed to add Employee Pay information!");
                }
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void insertIntoWorkRecords(String empId, String name, LocalDate workDate, LocalDateTime clockInTime,
                                             LocalDateTime clockOutTime, double hoursWorked, double hourlyRate){
        try{
            Connection connection = DriverManager.getConnection(jdbcUrl,username,password);
            String query = "insert into work_records (employeeId,employeeName,work_date,clock_in,clock_out," +
                    "hours_worked) values (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,empId);
            preparedStatement.setString(2,name);
            preparedStatement.setDate(3,Date.valueOf(workDate));
            preparedStatement.setTimestamp(4,Timestamp.valueOf(clockInTime));
            preparedStatement.setTimestamp(5,Timestamp.valueOf(clockOutTime));
            preparedStatement.setDouble(6,hoursWorked);
//            preparedStatement.setDouble(7,hourlyRate);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated>0){
                System.out.println("Updated the second table as well!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
