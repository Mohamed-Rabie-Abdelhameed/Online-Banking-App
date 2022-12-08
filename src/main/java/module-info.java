module com.abcbank.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.abcbank.application to javafx.fxml;
    exports com.abcbank.application;
}