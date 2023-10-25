/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

/**
 * FXML Controller class
 *
 * @author NamAnh
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btn_login;

    @FXML
    private Hyperlink link_register;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/storemanager", "root", "");

            String selectCustomerQuery = "SELECT * FROM customer WHERE email = ?";
            PreparedStatement customerStatement = conn.prepareStatement(selectCustomerQuery);
            customerStatement.setString(1, email);
            ResultSet customerResult = customerStatement.executeQuery();
        
            String selectQuery = "SELECT * FROM user WHERE email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, email);
            ResultSet userResult  = preparedStatement.executeQuery();

            if (customerResult.next()) {
                Stage currentStage = (Stage) emailField.getScene().getWindow();
                   currentStage.close();
                navigateTo("/View/Home.fxml");
            } else if (userResult.next()) {
                String emailInDB = userResult.getString("email");
                String role = getRoleForUser(emailInDB, conn);

                if (role != null) {
                    Stage currentStage = (Stage) emailField.getScene().getWindow();
                    currentStage.close();

                    if (role.equals("admin")) {
                        // Điều hướng tài khoản admin đến trang Admin/Home.fxml
                        navigateTo("/View/Admin/Category.fxml");
                    } else if (role.equals("manager")) {
                        // Điều hướng tài khoản manager đến trang Manage/Home.fxml
                        navigateTo("/View/Manage/Home.fxml");
                    }
                } else {
                    showError("Không thể xác định vai trò.");
                }
            } else {
                showError("Tài khoản không tồn tại.");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi cơ sở dữ liệu. Vui lòng thử lại sau.");
        }
    }
    private String getRoleForUser(String email, Connection conn) throws SQLException {
        String selectRoleQuery = "SELECT role.role_name FROM user " +
                "INNER JOIN user_role ON user.id = user_role.user_id " +
                "INNER JOIN role ON user_role.role_id = role.id " +
                "WHERE user.email = ?";
        PreparedStatement roleStatement = conn.prepareStatement(selectRoleQuery);
        roleStatement.setString(1, email);
        ResultSet roleResult = roleStatement.executeQuery();

        if (roleResult.next()) {
            return roleResult.getString("role_name");
        }
        return null; // Trả về null nếu không tìm thấy vai trò
    }
    private void navigateTo(String fxmlPath) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi khi chuyển đến trang. Vui lòng thử lại sau.");
        }
    }
    @FXML
    private void goToRegisterPage(ActionEvent event) {
        try {
            Stage stage = (Stage) link_register.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi khi chuyển đến trang đăng ký. Vui lòng thử lại sau.");
        }
    }
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
}
