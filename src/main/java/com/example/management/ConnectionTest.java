package com.example.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConnectionTest {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/student_manage";
        String username = "root";
        String password = "";
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to the database!");
            String query = "insert into teacher (name,password) values (?, ?)";
            PreparedStatement prepStatement= connection.prepareStatement(query);
            prepStatement.setString(1,"teacher1");
            prepStatement.setString(2,"pass1");
            int rowsInserted = prepStatement.executeUpdate();
            if (rowsInserted > 0){
                System.out.println("rows inserted successfully!");
                String query2 = "select * from teacher";
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    String uname = resultSet.getString("name");
                    String upass = resultSet.getString("password");
                    System.out.println("Username: "+uname + " Password: "+upass);
                }
                prepStatement.close();
            }
            else{
                System.out.println("Failed to insert the record!");
            }
            prepStatement.close();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database.");
        }
    }
}
