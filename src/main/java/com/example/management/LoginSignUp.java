package com.example.management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.concurrent.Flow;


public class LoginSignUp extends Application {
    public static String jdbcUrl = "jdbc:mysql://localhost:3306/employeemanage";
    public static String username = "root";
    public static String password = "";
    static FlowPane wholeContainer = new FlowPane(Orientation.VERTICAL);
    static Scene firstScene = new Scene(wholeContainer,600,600);

    static VBox adminBtnContainer = new VBox();

    static Scene adminScene = new Scene(adminBtnContainer,600,600);

    static FlowPane adminControlsContainer = new FlowPane(Orientation.VERTICAL);

    static Scene addEmpScene = new Scene(adminControlsContainer,600,600);

    static VBox empButtons = new VBox();

    static Scene empOperationScene = new Scene(empButtons,600,600);
    static Label errorLabel = new Label();

    public ScrollPane scpane = new ScrollPane();
    static VBox empInfoContainer = new VBox();
    static Scene viewEmpScene = new Scene(empInfoContainer,600,600);

    static ScrollPane scrollPane = new ScrollPane();
    static VBox viewHoursContainer = new VBox();
    static VBox wholeContainerForViewHours = new VBox();
    static Scene viewHoursScene = new Scene(wholeContainerForViewHours,600,600);

    static Label nameOfLoggedIn = new Label();



    @Override
    public void start(Stage stage) throws Exception {

        GridPane gp = new GridPane();

        Text textId = new Text("Employee Id: ");
        textId.getStyleClass().add("text");
        TextField txtIdAns = new TextField();
        Text head = new Text("Employee Login");

        nameOfLoggedIn.getStyleClass().add("text");

        firstScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        empOperationScene.getStylesheets().add(getClass().getResource("scene2.css").toExternalForm());
        adminScene.getStylesheets().add(getClass().getResource("scene2.css").toExternalForm());

        Button backButton = new Button("Back");
        Button backButton2 = new Button("Back");



        FlowPane buttonContainer = new FlowPane();
        buttonContainer.getChildren().add(backButton2);
        wholeContainerForViewHours.getChildren().add(buttonContainer);
        wholeContainerForViewHours.getChildren().add(viewHoursContainer);

        empInfoContainer.getChildren().add(backButton);
        scpane.setContent(empInfoContainer);
        scpane.setFitToHeight(true);
        scpane.setFitToWidth(true);

        backButton.setOnAction(actionEvent -> {
            stage.setScene(adminScene);
        });
        backButton2.setOnAction(actionEvent -> {
            stage.setScene(empOperationScene);
        });

        wholeContainer.getChildren().add(head);
        head.getStyleClass().add("head");

        Text pass = new Text("Employee Password: ");
        pass.getStyleClass().add("text");
        TextField passAns = new TextField();

        Button submit = new Button("Submit");

        gp.add(textId,0,0);
        gp.add(txtIdAns,1,0);
        gp.add(pass,0,1);
        gp.add(passAns,1,1);
        gp.add(submit,1,2);

        wholeContainer.getChildren().add(gp);

        wholeContainer.getChildren().add(errorLabel);


        wholeContainer.setAlignment(Pos.CENTER);

        Button addEmp = new Button("Add Employee");
        Button removeEmp = new Button("Remove Employee");
        Button viewEmp = new Button("View Employees");
        Button logOut = new Button("Log Out");

        addEmp.getStyleClass().add("buttonNormal");
        removeEmp.getStyleClass().add("buttonNormal");
        viewEmp.getStyleClass().add("buttonNormal");
        logOut.getStyleClass().add("buttonNormal");


        adminBtnContainer.getChildren().addAll(addEmp,removeEmp,viewEmp,logOut);


        GridPane gp2 = new GridPane();

        Text addEmpIDTxt = new Text("Employee Id: ");
        addEmpIDTxt.getStyleClass().add("text");

        TextField addEmpIdTxtAns = new TextField();

        Text empNameToAdd = new Text("Employee Name: ");
        TextField empNameToAddAns = new TextField();

        Text addEmpPassTxt = new Text("Employee Password: ");
        addEmpPassTxt.getStyleClass().add("text");
        TextField addEmpPassTxtAns = new TextField();

        Button addEmpButton = new Button("Add Employee");

        gp2.add(addEmpIDTxt,0,0);
        gp2.add(addEmpIdTxtAns,1,0);
        gp2.add(addEmpPassTxt,0,1);
        gp2.add(addEmpPassTxtAns,1,1);
        gp2.add(empNameToAdd,0,2);
        gp2.add(empNameToAddAns,1,2);
        gp2.add(addEmpButton,1,3);

        adminControlsContainer.getChildren().add(gp2);

        Button clockIn = new Button("Clock In");
        clockIn.getStyleClass().add("buttonNormal");
        Button clockout = new Button("Clock out");
        clockout.getStyleClass().add("buttonNormal");
        Button viewHours = new Button("View Hours");
        viewHours.getStyleClass().add("buttonNormal");
        Button log_out = new Button("Log Out");
        log_out.getStyleClass().add("buttonNormal");


        empButtons.getChildren().add(nameOfLoggedIn);
        empButtons.getChildren().addAll(clockIn,clockout,viewHours,log_out);
        empButtons.setSpacing(12);
        empButtons.setAlignment(Pos.CENTER_LEFT);


        log_out.setOnAction(actionEvent -> {
            stage.setScene(firstScene);
            txtIdAns.setText("");
            passAns.setText("");
        });


        Employee currentEmployee = new Employee();
        submit.getStyleClass().add("submitButton");

        submit.setOnAction(actionEvent -> {

            if (txtIdAns.getText().equals("admin") && passAns.getText().equals("admin")){
                stage.setScene(adminScene);
            }

            else{
                currentEmployee.setId(txtIdAns.getText());
                currentEmployee.setPassword(passAns.getText());
                try{
                    Connection connection = DriverManager.getConnection(jdbcUrl,username,password);
                    String query = "select employeeName from employee where employeeId = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1,currentEmployee.getId());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()){
                        String currentEmployeeName = resultSet.getString("employeeName");
                        currentEmployee.setName(currentEmployeeName);
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                EmployeeManager.empManage(txtIdAns.getText(), passAns.getText(), stage);
            }

        });

        logOut.setOnAction(actionEvent -> {
            stage.setScene(firstScene);
        });

        addEmp.setOnAction(actionEvent -> {
            stage.setScene(addEmpScene);
        });

        addEmpButton.setOnAction(actionEvent -> {
            if (!addEmpIdTxtAns.getText().isEmpty()) {
                EmployeeManager.addEmployeeMethod(addEmpIdTxtAns.getText(), empNameToAddAns.getText() ,
                        addEmpPassTxtAns.getText());
                currentEmployee.setId(addEmpIdTxtAns.getText());
                currentEmployee.setName(empNameToAddAns.getText());
            }
            else{
                System.out.println("Enter something!");
            }
        });

        viewEmp.setOnAction(actionEvent -> {
//            stage.setScene(viewEmpScene);
            EmployeeManager.viewEmpMethod(stage);
        });

        clockIn.setOnAction(actionEvent -> {
            EmployeeManager.clockedIn(currentEmployee,stage);
            txtIdAns.setText("");
            passAns.setText("");
        });

        clockout.setOnAction(actionEvent -> {
            EmployeeManager.clockedOut(currentEmployee,stage);
            txtIdAns.setText("");
            passAns.setText("");
        });

        viewHours.setOnAction(actionEvent -> {
            EmployeeManager.viewHoursMethod(currentEmployee.getId(),stage);
        });

        System.out.println("neeche");
        stage.setScene(firstScene);
        stage.show();
    }
}
