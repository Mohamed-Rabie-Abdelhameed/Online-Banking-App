package com.abcbank.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class DashboardController implements Initializable {

    @FXML
    private Label accNumber;

    @FXML
    private Label balance;

    @FXML
    private Button confirmDepositButton;

    @FXML
    private Button confirmTransferButton;

    @FXML
    private Button confirmWithdrawButton;

    @FXML
    private TextField convertAmount;

    @FXML
    private Button convertButton;

    @FXML
    private Button converterButton;

    @FXML
    private Pane converterPane;

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private Button depositButton;

    @FXML
    private Label depositConfirmationText;

    @FXML
    private Pane depositPane;

    @FXML
    private Label dob;

    @FXML
    private Label emailLabel;

    @FXML
    private ComboBox<String> firstCurrency;

    @FXML
    private Button homeButton;

    @FXML
    private Pane homePane;

    @FXML
    private Label name;

    @FXML
    private TextField recieverTextField;

    @FXML
    private Button resetButton;

    @FXML
    private TextField resultAmount;

    @FXML
    private ComboBox<String> secondCurrency;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private Button transferButton;

    @FXML
    private Button swapButton;

    @FXML
    private Label transferConfirmationText;

    @FXML
    private Label converterLabel;

    @FXML
    private Pane transferPane;

    @FXML
    private TextField withdrawAmountTextField;

    @FXML
    private Button withdrawButton;

    @FXML
    private Label withdrawConfirmationText;

    @FXML
    private Pane withdrawPane;





    @FXML
    void showDepositPane() {
        homePane.setVisible(false);
        depositPane.setVisible(true);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        converterPane.setVisible(false);
    }

    @FXML
    void showHomePane() {
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        converterPane.setVisible(false);
        setLabels();
    }

    @FXML
    void showTransferPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(true);
        converterPane.setVisible(false);
    }

    @FXML
    void showWithdrawPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(true);
        transferPane.setVisible(false);
        converterPane.setVisible(false);
    }

    @FXML
    void showConverterPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        converterPane.setVisible(true);
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
        else{
            transferConfirmationText.setText("User Not Found!");
            transferConfirmationText.setStyle(errorStyle);
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

    @FXML
    public void resetConverter(){
        firstCurrency.setValue("");
        secondCurrency.setValue("");
        resultAmount.setText("");
        convertAmount.setText("");
    }


    public double convert(String from, String to, double amount) throws MalformedURLException, IOException {
        double result;
        String url_str = "https://api.exchangerate.host/convert?from=" + from + "&to=" + to;
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestMethod("GET");
        request.connect();
        int responseCode = request.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();
            String req_result = jsonobj.get("result").getAsString();
            result = Double.parseDouble(req_result);
            return amount * result;
        } else {
            converterLabel.setText("Connection Failed!");
        }
        return 0;
    }
    @FXML
    public void convertButtonAction(){
        double amount;
        String from = firstCurrency.getValue();
        String to = secondCurrency.getValue();
        String value = "";
        if (Objects.equals(firstCurrency.getValue(), "") || Objects.equals(secondCurrency.getValue(), "")) {
            converterLabel.setText("Please Select a Currency!");
            return;
        }
        else
            converterLabel.setText("");
        try {
            amount = Double.parseDouble(convertAmount.getText());
            value = String.format("%.2f", convert(from, to, amount));
            resultAmount.setText(value + " " + secondCurrency.getValue());
        } catch (NumberFormatException e) {
            return;
        } catch (IOException ex) {
            converterLabel.setText("Connection Failed!");
        }
    }

    public void swap(){
        String val = firstCurrency.getValue();
        firstCurrency.setValue(secondCurrency.getValue());
        secondCurrency.setValue(val);
        convertButtonAction();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginController.limitTextField(recieverTextField);
        loginController.limitTextField(depositAmountTextField);
        loginController.limitTextField(withdrawAmountTextField);
        loginController.limitTextField(transferAmountTextField);
        String[] currencies = new String[]{"USD","EUR", "GBP", "CAD", "AED", "EGP", "SAR", "INR", "JPY", "CHF", "RUB", "SGD", "SEK", "BRL", "IQD", "MAD", "CNY", "MXN", "KWD", "TRY", "ARS", "LYD", "AUD"};
        firstCurrency.getItems().addAll(currencies);
        secondCurrency.getItems().addAll(currencies);
    }
}
