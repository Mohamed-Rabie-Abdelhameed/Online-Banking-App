package com.abcbank.application;



import java.io.IOException;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class loginController implements Initializable {
    
//    private final Image closedEye = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/eyes_closed.png")));
//    private final Image openEye = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/eyes_open.png")));
    
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
    private ToggleButton loginToggleButton;
    @FXML
    private DatePicker signupDOB;    
    
    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField shownPassword;
    @FXML
    private TextField shownLoginPassword;

    @FXML
    private ToggleButton toggleButton;
   
    @FXML
    void showPassword() {
        //            eyesImageView.setImage(closedEye);
        //            eyesImageView.setImage(openEye);
        shownPassword.setVisible(toggleButton.isSelected());
    }

    @FXML
    void showLoginPassword(){
        shownLoginPassword.setVisible(loginToggleButton.isSelected());
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
        conn = MySQLConnect.connectDB();
        String sql = "Select * From users Where email = ?";
        assert conn != null;
        pst = conn.prepareStatement(sql);
        pst.setString(1, signupEmail.getText());
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }
    private boolean accountNumberAlreadyExists() throws SQLException{
        conn = MySQLConnect.connectDB();
        String sql = "Select * From users Where account_number = ?";
        assert conn != null;
        pst = conn.prepareStatement(sql);
        pst.setString(1, signupAccountNumber.getText());
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    private boolean isValid() throws SQLException {
        boolean isValid = true;
        if(signupName.getText().isBlank() || (signupName.getText().length() < 10)){
            signupName.setStyle(errorStyle);
            isValid = false;
        }
        else{signupName.setStyle(successStyle);}

        if(signupEmail.getText().isBlank() || (emailAlreadyExists())){
            signupEmail.setStyle(errorStyle);
            isValid = false;
        }
        else{signupEmail.setStyle(successStyle);}

        if(signupAccountNumber.getText().isBlank() || (signupAccountNumber.getText().length() < 8) || accountNumberAlreadyExists()){
            signupAccountNumber.setStyle(errorStyle);
            isValid = false;
        }
        else{signupAccountNumber.setStyle(successStyle);}

        if(signupPassword.getText().isBlank() || (signupPassword.getText().length() < 8)){
            signupPassword.setStyle(errorStyle);
            isValid = false;
        }
        else{signupPassword.setStyle(successStyle);}

        if(signupDOB.getValue().toString().isBlank()){
            signupDOB.setStyle(errorStyle);
            isValid = false;
        }
        else{signupDOB.setStyle(successStyle);}
        return isValid;
    }

    
    private void closeWindow(){
          Stage stage = (Stage) email.getScene().getWindow();
          stage.close();}

    private void launchDashboard(User c) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        Parent root1 = fxmlLoader.load();
        DashboardController dController = fxmlLoader.getController();
        dController.currentUser = c;
        dController.setLabels();
        dController.showHomePane();
        Stage stage = new Stage();
        stage.setTitle("ABC Bank");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/icon.png"))));
        stage.setScene(new Scene(root1));
        stage.show();
    }
    @FXML
    private void Login() {
        if(email.getText().isBlank() || loginPassword.getText().isBlank()){
            email.setStyle(errorStyle);
            loginPassword.setStyle(errorStyle);
            loginLabel.setText("Missing Email or Password!");
            return;}
        String sql = "Select * From users Where email = ? And password = ?";
        try{
            conn = MySQLConnect.connectDB();
            assert conn != null;
            pst = conn.prepareStatement(sql);
            pst.setString(1, email.getText());
            pst.setString(2, loginPassword.getText());
            rs = pst.executeQuery();
            if(rs.next()){
                closeWindow();
                User c = new User(rs.getString(2), rs.getString(3) , rs.getDate(6).toLocalDate(), rs.getInt(4), rs.getDouble(5));
                launchDashboard(c);
            }
            else{
                email.setStyle(errorStyle);
                loginPassword.setStyle(errorStyle);
                loginLabel.setText("Email or password is invalid!");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            loginLabel.setText("Connection failed, please check your internet.");
            loginLabel.setStyle(textFillError);
        }
    }


    public void addUser(){

        try{
            if(isValid()){
                String sql = "insert into users (name, email, account_number,balance ,dob, password) values (?,?,?,?,?,?)";
                User newUser = new User(signupName.getText(), signupEmail.getText(), signupDOB.getValue(), Integer.parseInt(signupAccountNumber.getText()));
                conn = MySQLConnect.connectDB();
                assert conn != null;
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
            }else{
                signupConfirmationText.setText("Please Fill In The Data Correctly!");
            }
        } catch (Exception e) {
            signupConfirmationText.setText("Please Check Your Internet Connection!");
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
        Bindings.bindBidirectional(loginPassword.textProperty(), shownLoginPassword.textProperty());
        showLoginPane();
    }
}
