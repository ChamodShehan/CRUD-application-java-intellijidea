package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm {

    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String enteredUserName;
    public  static String enteredUserId;

    public void txtbtn(MouseEvent mouseEvent) throws IOException {
        Parent parent= FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene=new Scene(parent);

        Stage primaryStage= (Stage) this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account");
        primaryStage.centerOnScreen();

    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws SQLException {

        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstence().getConnection();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select * from user where password=? and user_name=?;");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        preparedStatement.setObject(1,password);
        preparedStatement.setObject(2,userName);



        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isExist = resultSet.next();
            if(isExist){

                enteredUserName=resultSet.getString(2);
                enteredUserId=resultSet.getString(1);
                Parent parent= null;
                try {
                    parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoForm.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene=new Scene(parent);

                Stage primaryStage= (Stage) this.root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Todo Form");
                primaryStage.centerOnScreen();

            }
            else{
                new Alert(Alert.AlertType.ERROR,"Invalid User Name or Password").showAndWait();

                txtUserName.clear();
                txtPassword.clear();

                txtUserName.requestFocus();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
