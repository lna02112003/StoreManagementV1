/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller.Admin;

import Model.Category;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.Category;  

import ConnectDB.Connect;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author NamAnh
 */
public class CategoryController implements Initializable {
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Integer> stt;
    @FXML
    private TableColumn<Category, Integer> category_id;
    @FXML
    private TableColumn<Category, String> category_name;
    @FXML
    private TableColumn<Category, String> description;
    @FXML
    private TableColumn<Category, Void> action;
    @FXML
    private Button btn_add;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stt.setStyle("-fx-alignment: CENTER;");
        category_id.setStyle("-fx-alignment: CENTER;");
        category_name.setStyle("-fx-alignment: CENTER;");
        description.setStyle("-fx-alignment: CENTER;");
        stt.setCellValueFactory(new PropertyValueFactory<>("stt"));
        category_id.setCellValueFactory(new PropertyValueFactory<>("category_id"));
        category_name.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        action.setCellFactory(generateCellFactory());


        btn_add.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Admin/AddCategory.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);

                Stage currentStage = (Stage) btn_add.getScene().getWindow();
                currentStage.setScene(scene);

                currentStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            Connection conn = Connect.connect();
            String selectQuery = "SELECT * FROM category";
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            int sttValue = 1;

            while (resultSet.next()) {
                int categoryID = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");
                String categoryDescription = resultSet.getString("description");

                Category category = new Category(sttValue, categoryID, categoryName, categoryDescription);

                categoryTable.getItems().add(category);
                sttValue++;
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Callback<TableColumn<Category, Void>, TableCell<Category, Void>> generateCellFactory() {
        return new Callback<TableColumn<Category, Void>, TableCell<Category, Void>>() {
            @Override
            public TableCell<Category, Void> call(final TableColumn<Category, Void> param) {
                return new TableCell<Category, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    

                    {
                        editButton.setOnAction(event -> {
                            Category category = getTableView().getItems().get(getIndex());

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Admin/EditCategory.fxml"));
                                Parent root = loader.load();

                                EditCategoryController editController = loader.getController();
                                editController.setCategoryToEdit(category);

                                Scene scene = new Scene(root);
                                Stage currentStage = (Stage) editButton.getScene().getWindow();
                                currentStage.setScene(scene);
                                currentStage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        deleteButton.setOnAction(event -> {
                            Category category = getTableView().getItems().get(getIndex());

                            boolean deleted = deleteCategory(category.getCategory_id());
                            if (deleted) {
                                categoryTable.getItems().remove(category);
                            } else {
                                // Xử lý lỗi khi xóa danh mục
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi");
                                alert.setHeaderText("Xóa danh mục không thành công");
                                alert.setContentText("Không thể xóa danh mục này. Vui lòng thử lại sau hoặc kiểm tra lỗi.");
                                alert.showAndWait();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(editButton, deleteButton));
                            editButton.setStyle("-fx-alignment: CENTER;");
                            deleteButton.setStyle("-fx-alignment: CENTER;");
                        }
                    }
                    private boolean deleteCategory(int categoryID) {
                        try {
                            Connection conn = Connect.connect();
                            String deleteQuery = "DELETE FROM category WHERE category_id = ?";
                            PreparedStatement preparedStatement = conn.prepareStatement(deleteQuery);
                            preparedStatement.setInt(1, categoryID);

                            int rowsDeleted = preparedStatement.executeUpdate();

                            conn.close();

                            return rowsDeleted > 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                };
            }
        };
    }
}
