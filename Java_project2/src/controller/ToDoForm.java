package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.TodoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoForm {
    public Label lblWelcomeNote;
    public Label lblId;
    public Pane subRoot;
    public TextField txtNewTodo;
    public AnchorPane root;
    public ListView <TodoTM>listTodolist;
    public TextField txtselectedtxt;
    public Button btnupdate;
    public Button btnDelete;

    String id;
    public void initialize(){

        lblId.setText(LoginForm.enteredUserId);
        lblWelcomeNote.setText("Hi "+ LoginForm.enteredUserName +" welcome To do list");

        subRoot.setVisible(false);
        
        loadlist();

        txtselectedtxt.setDisable(true);
        btnupdate.setDisable(true);
        btnDelete.setDisable(true);

        listTodolist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoTM>() {
            @Override
            public void changed(ObservableValue<? extends TodoTM> observable, TodoTM oldValue, TodoTM newValue) {
                txtselectedtxt.setDisable(false);
                btnupdate.setDisable(false);
                btnDelete.setDisable(false);

                txtselectedtxt.requestFocus();

                subRoot.setVisible(false);

                // get selected items in a list
                TodoTM selectedItem =   listTodolist.getSelectionModel().getSelectedItem();
    // likewise follow we can get description
               // String description=   newValue.getDescription();


                if(newValue==null){ //if null stop run and return
                    return;
                }
                     String description=   selectedItem.getDescription();
                txtselectedtxt.setText(description);
                id= newValue.getId();
            }
        });


    }

    private void loadlist() {

        ObservableList<TodoTM> todos = listTodolist.getItems();
        todos.clear();

        Connection connection = DBConnection.getInstence().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todos where user_id=?");
            preparedStatement.setObject(1,LoginForm.enteredUserId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                String id= resultSet.getString(1);
                String description= resultSet.getString(2);
                String user_id= resultSet.getString(3);

                TodoTM todoTM=new TodoTM(id,description,user_id);

                todos.add(todoTM);

            }
            listTodolist.refresh();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void btnAddNewTodo(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        txtNewTodo.requestFocus();

        txtselectedtxt.setDisable(true);
        btnupdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to logout", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){

            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));

            Scene scene=new Scene(parent);
            Stage primaryStage= (Stage) this.root.getScene().getWindow();


            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }



    }

    public void btnAddTodoListOnAction(ActionEvent actionEvent) {

        String id=autoGenerateId();

        System.out.println(id);
        String description = txtNewTodo.getText();
        String user_id=lblId.getText();

        Connection connection = DBConnection.getInstence().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO todos values (?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_id);

            int i = preparedStatement.executeUpdate();

            System.out.println(i);
            subRoot.setVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadlist();
    }

    public String autoGenerateId() {
        Connection connection = DBConnection.getInstence().getConnection();
        String newId = "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todos order by id desc limit 1");
            //point resultset's result
            boolean isExist = resultSet.next();
            if(isExist){
                //return 1st column value
                String oldId=resultSet.getString(1);
                System.out.println(oldId);
                oldId=oldId.substring(1,oldId.length());
                int intId=Integer.parseInt(oldId);
                intId++;

                if(intId<10){
                    newId="T00"+intId;

                }
                else if(intId<100){
                    newId="T0"+intId;
                }else {
                    newId="T"+intId;
                }
            }else {
                newId="T001";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    return newId;
    }


    public void btnupdateOnAction(ActionEvent actionEvent) {

       String description= txtselectedtxt.getText();
       Connection connection= DBConnection.getInstence().getConnection();
        System.out.println(description);
        System.out.println(" ");
        System.out.println(id);
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("update todos set description=? where id=? ");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            int i= preparedStatement.executeUpdate();
            System.out.println(i);
            loadlist();

            txtselectedtxt.clear();
            txtselectedtxt.setDisable(true);
            btnupdate.setDisable(true);
            btnDelete.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

       Alert alert=  new Alert(Alert.AlertType.CONFIRMATION,"Do You want to Delete ?",ButtonType.YES,ButtonType.NO);
       Connection connection=DBConnection.getInstence().getConnection();

         Optional<ButtonType>buttonType= alert.showAndWait();

         if(buttonType.get().equals(ButtonType.YES)){

             try {
                 PreparedStatement preparedStatement= connection.prepareStatement("delete from todos where id=?");
                 preparedStatement.setObject(1,id);
                 preparedStatement.executeUpdate();

                 loadlist();

             } catch (SQLException e) {
                 e.printStackTrace();
             }

         }


    }
}
