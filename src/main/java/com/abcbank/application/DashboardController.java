package com.abcbank.application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class DashboardController implements Initializable {

    @FXML
    private Pane withdrawPane;

    @FXML
    private Pane transferPane;

    @FXML
    private TextField withdrawAmountTextField;

    @FXML
    private Label emailLabel;
    @FXML
    private Label withdrawConfirmationText;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private TextField recieverTextField;

    @FXML
    private Label transferConfirmationText;

    @FXML
    private Label accNumber;

    @FXML
    private Label balance;

    @FXML
    private Label depositConfirmationText;

    @FXML
    private Pane depositPane;

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private Label dob;

    @FXML
    private Pane homePane;

    @FXML
    private Label name;

    @FXML
    void showDepositPane() {
        homePane.setVisible(false);
        depositPane.setVisible(true);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
    }

    @FXML
    void ShowHomePane() {
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        setLabels();
    }

    @FXML
    void showTransferPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(true);
    }

    @FXML
    void showWithdrawPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(true);
        transferPane.setVisible(false);
    }

    protected
    String errorStyle = "-fx-text-fill: RED;";
    String successStyle = "-fx-text-fill: GREEN;";
    
    public User currentUser = new User();

    ResultSet rs = null;
    PreparedStatement pst = null;

    public void setLabels() {
        name.setText(currentUser.getName());
        dob.setText(currentUser.getDob().toString());
        accNumber.setText(Integer.toString(currentUser.getAcc_num()));
        balance.setText(Double.toString(currentUser.getBalance()));
        emailLabel.setText(currentUser.getEmail());
    }

    
    public void deposit(double amount) {
        double total = amount + currentUser.getBalance();
        currentUser.setBalance(total);
        try{
            if(amount<= 0.0){
                throw new IllegalArgumentException();
            }
            Connection conn = MySQLConnect.connectDB();
            String sql = "UPDATE users SET balance = ? Where email = ?";
            assert conn != null;
            pst = conn.prepareStatement(sql);
            pst.setDouble(1, total);
            pst.setString(2, currentUser.getEmail());
            pst.executeUpdate();
            depositConfirmationText.setText("Deposit Succeeded");
            depositConfirmationText.setStyle(successStyle);
            depositAmountTextField.setText("");
        }
        catch(Exception e){
            depositConfirmationText.setText("Please Enter a Positive Value");
            depositConfirmationText.setStyle(errorStyle);
            depositAmountTextField.setText("");            
    }
}

    @FXML
    public void confirmDeposit() {
        try{
            double amount = Double.parseDouble(depositAmountTextField.getText());
            deposit(amount);}
        catch(NumberFormatException e){
            depositConfirmationText.setText("Please Enter a Numeric Value");
            depositConfirmationText.setStyle(errorStyle);
            depositAmountTextField.setText("");            
        }
    }
    
    
    public void withdraw(double amount) {
        double total = currentUser.getBalance() - amount;
        currentUser.setBalance(total);
        try{
            if(((currentUser.getBalance() - amount) < 0.0)  || (amount <= 0)){
                throw new IllegalArgumentException();
            }
            Connection conn = MySQLConnect.connectDB();
            String sql = "UPDATE users SET balance = ? Where email = ?";
            assert conn != null;
            pst = conn.prepareStatement(sql);
            pst.setDouble(1, total);
            pst.setString(2, currentUser.getEmail());
            pst.executeUpdate();
            withdrawConfirmationText.setText("Withdraw Succeeded");
            withdrawConfirmationText.setStyle(successStyle);
            withdrawAmountTextField.setText("");
        }catch(Exception e){
            withdrawConfirmationText.setText("Sorry, Your Balance is Too Low");
            withdrawConfirmationText.setStyle(errorStyle);
            withdrawAmountTextField.setText("");
    }
}

     public void confirmWithdraw() {
        try{
            double amount = Double.parseDouble(withdrawAmountTextField.getText());
            withdraw(amount);}
        catch(Exception e){
            withdrawConfirmationText.setText("Please Enter a Numeric Value");
            withdrawConfirmationText.setStyle(errorStyle);
            withdrawAmountTextField.setText("");
        }
    }
     
     
    public void transfer(double amount) throws SQLException{
        if(recieverTextField.getText().isBlank() || Objects.equals(recieverTextField.getText(), String.valueOf(currentUser.getAcc_num()))){
            throw new IllegalArgumentException();
        }
        int recieverAccNumber = Integer.parseInt(recieverTextField.getText());
        Connection conn = MySQLConnect.connectDB();
        String sql = "SELECT* FROM users WHERE account_number = ?";
        assert conn != null;
        pst = conn.prepareStatement(sql);
        pst.setInt(1, recieverAccNumber);
        rs = pst.executeQuery();
        if(rs.next()){
            User reciever = new User(rs.getString(2), rs.getString(3) ,rs.getDate(6).toLocalDate(), rs.getInt(4), rs.getDouble(5));
            try{
                if(amount < 0 || amount > currentUser.getBalance() || recieverTextField.getText().length() != 8){
                    throw new IllegalArgumentException();
                }
                withdraw(Double.parseDouble(transferAmountTextField.getText()));
                User temp = currentUser;
                currentUser = reciever;
                deposit(Double.parseDouble(transferAmountTextField.getText()));
                currentUser = temp;
                transferConfirmationText.setText("Transfer Succeeded!");
                transferConfirmationText.setStyle(successStyle);
                recieverTextField.setText("");
                transferAmountTextField.setText("");
            }
            catch(Exception e){
                transferConfirmationText.setText("Transfer Failed!");
                transferConfirmationText.setStyle(errorStyle);
            }
        }
    }
    
    public void confirmTransfer(){
        try{
            double amount = Double.parseDouble(transferAmountTextField.getText());
            transfer(amount);}
        catch(Exception e){
            transferConfirmationText.setText("Transfer Failed!");
            transferConfirmationText.setStyle(errorStyle);
        }
    }
    
    public void logout() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("ABC Bank");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/icon.png"))));
        stage.setScene(new Scene(root1));
        stage.setResizable(false);
        stage.show();
        Stage stage2 = (Stage) balance.getScene().getWindow();
        stage2.close();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginController.limitTextField(recieverTextField);
        loginController.limitTextField(depositAmountTextField);
        loginController.limitTextField(withdrawAmountTextField);
        loginController.limitTextField(transferAmountTextField);
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
    }
}
