package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountForm {
    public TextField txtUserName;
    public TextField txtEmail;
   
    public Button btnRegister;
    public PasswordField txtNewPassword;
    public PasswordField txtComformPassword;
    public Label lblUserId;
    public Label lblComfirmPassword;
    public Label lblNewPassowrd;
    public AnchorPane root;


    public  void initialize(){
        txtUserName.setDisable(true);
        txtEmail.setDisable(true);
        txtNewPassword.setDisable(true);
        txtComformPassword.setDisable(true);
        btnRegister.setDisable(true);

        lblNewPassowrd.setVisible(false);
        lblComfirmPassword.setVisible(false);
    }

    public void btnAddNewUser(ActionEvent actionEvent) {

        txtUserName.setDisable(false);
        txtEmail.setDisable(false);
        txtNewPassword.setDisable(false);
        txtComformPassword.setDisable(false);
        btnRegister.setDisable(false);

        txtUserName.requestFocus();

       autogenerateId();
    }

    private void autogenerateId() {
        Connection connection = DBConnection.getInstence().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");
            boolean isExist = resultSet.next();
            if(isExist){

                String oldId = resultSet.getString(1);
                int length=oldId.length();

                String id = oldId.substring(1, length);
                int intId=Integer.parseInt(id);

                intId=intId+1;
                if(intId<10){
                    lblUserId.setText("U00"+intId);
                }
                else if(intId<100){
                    lblUserId.setText("U0"+intId);
                }
                else {
                    lblUserId.setText("U"+intId);
                }
            }else{
                lblUserId.setText("U001");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void btnRegister(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String comfirmPassword = txtComformPassword.getText();

        boolean isEquals = newPassword.equals(comfirmPassword);

        if(isEquals){
            txtNewPassword.setStyle("-fx-border-color: transparent");
            txtComformPassword.setStyle("-fx-border-color: transparent");

            lblNewPassowrd.setVisible(false);
            lblComfirmPassword.setVisible(false);
            
            register();
        }else{
            txtNewPassword.setStyle("-fx-border-color: red");
            txtComformPassword.setStyle("-fx-border-color: red");

            txtNewPassword.requestFocus();

            lblNewPassowrd.setVisible(true);
            lblComfirmPassword.setVisible(true);
        }
    }

    public void register(){

        String id = lblUserId.getText();
        String userName = txtUserName.getText();
        String password = txtComformPassword.getText();
        String email = txtEmail.getText();

        Connection connection = DBConnection.getInstence().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,password);
            preparedStatement.setObject(4,email);

            int i = preparedStatement.executeUpdate();

            if(i !=0){
                new Alert(Alert.AlertType.CONFIRMATION,"Success").showAndWait();


                try {
                    Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));

                    Scene scene=new Scene(parent);
                    Stage primaryStage= (Stage) this.root.getScene().getWindow();


                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Login Form");
                    primaryStage.centerOnScreen();
                    primaryStage.show();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{

                new Alert(Alert.AlertType.ERROR).showAndWait();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }




    }
}
