
package com.abcbank.application;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnect{
    public static Connection connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com:3306/sql11589964", "sql11589964", "UnAPsV6nsM");
        }
        catch(Exception e){
            return null;
        }            
    }
}