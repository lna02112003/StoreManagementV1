/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller.Admin;

import ConnectDB.Connect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author NamAnh
 */

public class AddCategoryController {
    @FXML
    private TextField categorynameField;

    @FXML
    private TextArea description;
    
    public void initialize() {
     
    }
    
    @FXML
    public void handleAddCategory(ActionEvent event) {
        String categoryName = categorynameField.getText();
        String categoryDescription = description.getText();

        if (categoryName.isEmpty()) {
            showErrorDialog("Category name cannot be empty.");
            return;
        }

        try (Connection conn = Connect.connect();) {
            String insertQuery = "INSERT INTO category (category_name, description) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, categoryName);
            preparedStatement.setString(2, categoryDescription);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                loadCategoryScene(event);
            } else {
  
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }
    private void loadCategoryScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Admin/Category.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   @FXML
    public void handleGoBack(ActionEvent event) {
        loadCategoryScene(event);
    }

}
