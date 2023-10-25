package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class RegisterController implements Initializable {
    @FXML
    private TextField firstnameField;
    @FXML
    private TextField middlenameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField password_confirmField;
    @FXML
    private Hyperlink link_login;
    @FXML
    private Button btn_register;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String firstName = firstnameField.getText();
        String middleName = middlenameField.getText();
        String lastName = lastnameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String password = passwordField.getText();
        String confirmPassword = password_confirmField.getText();

        if (!isDataValid(firstName, middleName, lastName, email, phone, address, password, confirmPassword)) {
            return;
        }

        String hashedPassword = hashPassword(password);
        saveUserData(firstName, middleName, lastName, email, phone, address, hashedPassword);
        clearFormFields();
    }

    private boolean isDataValid(String firstName, String middleName, String lastName, String email, String phone, String address, String password, String confirmPassword) {
        if (firstName.isEmpty() || middleName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Vui lòng điền đầy đủ thông tin.");
            return false;
        } else if (!isEmailValid(email)) {
            showError("Email không hợp lệ.");
            return false;
        } else if (!isPhoneNumberValid(phone)) {
            showError("Số điện thoại không hợp lệ.");
            return false;
        } else if (!isPasswordValid(password)) {
            showError("Mật khẩu không hợp lệ (ít nhất 8 ký tự).");
            return false;
        } else if (!password.equals(confirmPassword)) {
            showError("Mật khẩu và xác nhận mật khẩu không khớp.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String hashPassword(String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return hashedPassword;
    }

    private void saveUserData(String firstName, String middleName, String lastName, String email, String phone, String address, String hashedPassword) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/storemanager", "root", "");
            String insertQuery = "INSERT INTO Customer (firstname, middlename, lastname, email, phone, address, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, middleName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, hashedPassword);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            showError("Lỗi cơ sở dữ liệu. Vui lòng thử lại sau.");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void clearFormFields() {
        firstnameField.clear();
        middlenameField.clear();
        lastnameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        passwordField.clear();
        password_confirmField.clear();
    }

   private boolean isEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }
    private boolean isPhoneNumberValid(String phoneNumber) {
        String regex = "^[0-9]{10}$";
        return phoneNumber.matches(regex);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }
    @FXML
    private void goToLoginPage(ActionEvent event) {
        try {
            Stage stage = (Stage) link_login.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi khi chuyển đến trang đăng nhập. Vui lòng thử lại sau.");
        }
    }
}
