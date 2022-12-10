
package com.abcbank.application;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class MySQLConnect{
    public static Connection connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://sql7.freesqldatabase.com:3306/sql7582673", "sql7582673", "HPQ4BwASuc");
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Couldn't Connect to The Data Base!");
            return null;
        }            
    }
}