package com.example.demo1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.example.demo1.Product;

public class HelloApplication extends Application {
    public static ArrayList<Product> products = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        Button button = new Button("Select Excel File");
        root.getChildren().add(button);

        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files","*.xlsx"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) { // Check if a file was selected
                try {
                    readExcelFile(selectedFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Excel Reader");
        stage.setScene(scene);
        stage.show();
    }

    public static void readExcelFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);

             Workbook workbook = new XSSFWorkbook(fis)) {  // Fix workbook initialization

            Sheet sheet = workbook.getSheetAt(0); // Get first sheet
            boolean skip = false;
            for (Row row : sheet) {
                if (!skip){
                    skip = true;
                    continue;
                }
                int id = (int) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                double price = row.getCell(2).getNumericCellValue();
                int quantity = (int) row.getCell(3).getNumericCellValue();
                double finalPrice = row.getCell(4).getNumericCellValue();


                Product product = new Product(id, name, price, quantity, finalPrice);
                products.add(product);

            }
            System.out.println(products.get(0).getName());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
