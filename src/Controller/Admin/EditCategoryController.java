/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller.Admin;

import Model.Category;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ConnectDB.Connect;

public class EditCategoryController implements Initializable {
    @FXML
    private TextField categoryNameField;
    @FXML
    private Button saveButton;

    private Category categoryToEdit;

    public void setCategoryToEdit(Category category) {
        categoryToEdit = category;
        categoryNameField.setText(categoryToEdit.getCategoryName());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveButton.setOnAction(event -> {
            String editedCategoryName = categoryNameField.getText();
            boolean updated = updateCategory(categoryToEdit.getCategory_id(), editedCategoryName);
            if (updated) {
                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                currentStage.close();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Admin/Category.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.show();
                } catch (IOException e) {
                    // Handle the FXML loading exception
                    e.printStackTrace();
                }
            } else {
                // Handle the case where the updateCategory method returns false
                // Show an error message to the user
            }
        });
    }

    private boolean updateCategory(int categoryID, String editedCategoryName) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = Connect.connect();
            String updateQuery = "UPDATE category SET category_name = ? WHERE category_id = ?";
            preparedStatement = conn.prepareStatement(updateQuery);
            preparedStatement.setString(1, editedCategoryName);
            preparedStatement.setInt(2, categoryID);

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            // Handle the database exception
            e.printStackTrace(); // Replace with proper logging
            return false;
        } finally {
            // Close resources in a finally block
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Replace with proper logging
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Replace with proper logging
                }
            }
        }
    }
}