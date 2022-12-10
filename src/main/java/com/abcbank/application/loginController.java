package com.abcbank.application;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class loginController implements Initializable {
    
    private final Image closedEye = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/eyes_closed.png")));
    private final Image openEye = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/eyes_open.png")));
    
    @FXML
    ImageView eyesImageView;
    
    @FXML
    private Pane loginPane;

    @FXML
    private Pane signUpPane;

    @FXML
    private TextField email;

    @FXML
    private PasswordField signupPassword;
    @FXML
    private Label signupConfirmationText;
    @FXML
    private Label loginLabel;
    @FXML
    private TextField signupName;
    
    @FXML
    private TextField signupEmail;
    
    @FXML
    private TextField signupAccountNumber;
    
    @FXML
    private DatePicker signupDOB;    
    
    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField shownPassword;

    @FXML
    private ToggleButton toggleButton;
   
    @FXML
    void showPassword() {
        if(toggleButton.isSelected()){
            shownPassword.setVisible(true);
            eyesImageView.setImage(closedEye);
        }
        else{
            shownPassword.setVisible(false);
            eyesImageView.setImage(openEye);
        }
    }
    

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    // Strings which hold css elements to easily re-use in the application
    protected
    String errorStyle = "-fx-border-color: RED;";
    String successStyle = "-fx-border-color: #A9A9A9;";
    String textFillError = "-fx-text-fill: RED";
    
    private boolean emailAlreadyExists() throws SQLException{
        try{
            conn = MySQLConnect.connectDB();}
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Please Check Your Internet Connection");
        }
        String sql = "Select * From users Where email = ?";
        assert conn != null;
        pst = conn.prepareStatement(sql);
        pst.setString(1, signupEmail.getText());
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }
    private boolean accountNumberAlreadyExists() throws SQLException{
        try{
            conn = MySQLConnect.connectDB();}
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Please Check Your Internet Connection");
        }
        String sql = "Select * From users Where account_number = ?";
        assert conn != null;
        pst = conn.prepareStatement(sql);
        pst.setString(1, signupAccountNumber.getText());
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }
    
    private void validateInput()throws Exception{
        if(signupName.getText().isBlank() || (signupName.getText().length() < 10)){
            signupName.setStyle(errorStyle);
        }
        else{signupName.setStyle(successStyle);}
        
        if(signupEmail.getText().isBlank() || (emailAlreadyExists())){
            signupEmail.setStyle(errorStyle);
        }
        else{signupEmail.setStyle(successStyle);}

        if(signupAccountNumber.getText().isBlank() || (signupAccountNumber.getText().length() < 8) || accountNumberAlreadyExists()){
            signupAccountNumber.setStyle(errorStyle);
        }
        else{signupAccountNumber.setStyle(successStyle);}
        
        if(signupPassword.getText().isBlank() || (signupPassword.getText().length() < 8)){
            signupPassword.setStyle(errorStyle);
            throw new NullPointerException();
        }
        else{signupPassword.setStyle(successStyle);}

        if(signupDOB.getValue().toString().isBlank()){
            signupDOB.setStyle(errorStyle);
        }
        else{signupDOB.setStyle(successStyle);}
    }
    
    
    private void closeWindow(){
          Stage stage = (Stage) email.getScene().getWindow();
          stage.close();}

    @FXML
    private void Login(){
        try{
            if(email.getText().isBlank() || loginPassword.getText().isBlank()){
                throw new NullPointerException();
            }
            conn = MySQLConnect.connectDB();
            String sql = "Select * From users Where email = ? And password = ?";
            assert conn != null;
            pst = conn.prepareStatement(sql);
            pst.setString(1, email.getText());
            pst.setString(2, loginPassword.getText());
            rs = pst.executeQuery();
            if(rs.next()){
                closeWindow();
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                    Parent root1 = fxmlLoader.load();
                    DashboardController dController = fxmlLoader.getController();
                    dController.currentUser = new User(rs.getString(2), rs.getString(3) , rs.getDate(6).toLocalDate(), rs.getInt(4), rs.getDouble(5));
                    dController.setLabels();
                    Stage stage = new Stage();      
                    stage.setTitle("ABC Bank");
                    stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/icon.png"))));
                    stage.setScene(new Scene(root1));
                    stage.show();
                }
                catch(Exception e){
                    loginLabel.setText("Connection failed, please check your internet.");
                    loginLabel.setStyle(textFillError);
                }
            }
            else{
                email.setStyle(errorStyle);
                loginPassword.setStyle(errorStyle);
                loginLabel.setText("Email or password is invalid!");
                loginLabel.setStyle(textFillError);
            }
        }
        catch (NullPointerException e){
            email.setStyle(errorStyle);
            loginPassword.setStyle(errorStyle);
            loginLabel.setText("Email or password is empty!");
            loginLabel.setStyle(textFillError);
        }
        catch(Exception e){
            loginLabel.setText("Connection failed, please check your internet.");
            loginLabel.setStyle(textFillError);
        }
    }
    
    
    public void addUser(){
        try{
            validateInput();
            conn = MySQLConnect.connectDB();
            String sql = "insert into users (name, email, account_number,balance ,dob, password) values (?,?,?,?,?,?)";
            User newUser = new User(signupName.getText(), signupEmail.getText(), signupDOB.getValue(), Integer.parseInt(signupAccountNumber.getText()));
            pst = conn.prepareStatement(sql);
            pst.setString(1, newUser.getName());
            pst.setString(2, newUser.getEmail());
            pst.setInt(3, newUser.getAcc_num());
            pst.setDouble(4, newUser.getBalance());
            pst.setDate(5,Date.valueOf(newUser.getDob()));
            pst.setString(6, signupPassword.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Successful SignUp!");
            showLoginPane();
        }
        catch(Exception e){
            signupConfirmationText.setText("Please Fill In The Data Correctly!");
            signupConfirmationText.setStyle(textFillError);
        }
    }
    
    public void showLoginPane(){
        loginPane.setVisible(true);
        signUpPane.setVisible(false);
    }
    
    public void showSignUPPane(){
        loginPane.setVisible(false);
        signUpPane.setVisible(true);
    }

    public static void limitTextField(TextField tf){
        final int max = 8;
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf.setText(newValue.replaceAll("\\D", ""));
            }
            if (tf.getText().length() > max) {
                String s = tf.getText().substring(0, max);
                tf.setText(s);
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitTextField(signupAccountNumber);
        signupDOB.setValue(LocalDate.of(2000,1,1));
        Bindings.bindBidirectional(signupPassword.textProperty(), shownPassword.textProperty());
        showLoginPane();
    }
}
