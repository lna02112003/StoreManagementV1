/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConnectDB;

/**
 *
 * @author NamAnh
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/storemanager";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi kết nối CSDL.");
        }
        return conn;
    }
}