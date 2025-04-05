package com.example.demo1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

public class HelloApplication extends Application {
    private static final List<Product> products = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Button loadButton = new Button("Upload Excel File");
        ComboBox<Integer> yearComboBox = new ComboBox<>();
        Button chartButton = new Button("Show Chart");
        yearComboBox.setDisable(true);
        chartButton.setDisable(true);

        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    readExcelFile(file);
                    Set<Integer> years = new TreeSet<>();
                    for (Product p : products) {
                        years.add(p.date().getYear());
                    }
                    yearComboBox.setItems(FXCollections.observableArrayList(years));
                    if (!years.isEmpty()) yearComboBox.setValue(LocalDate.now().getYear());
                    yearComboBox.setDisable(false);
                    chartButton.setDisable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        root.getChildren().addAll(loadButton, new Label("Select Year:"), yearComboBox, chartButton);

        chartButton.setOnAction(e -> {
            int selectedYear = yearComboBox.getValue();
            List<MonthSales> monthlySales = calculateMonthlySales(selectedYear);
            LineChart<String, Number> chart = buildLineChart(monthlySales);
            if (root.getChildren().size() > 4) {
                root.getChildren().remove(4);
            }
            root.getChildren().add(chart);
        });

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Sales Analyzer (Line Chart)");
        stage.setScene(scene);
        stage.show();
    }

    public static void readExcelFile(File file) throws IOException {
        products.clear();
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            int id = (int) row.getCell(0).getNumericCellValue();
            String name = row.getCell(1).getStringCellValue();
            double price = row.getCell(2).getNumericCellValue();
            int quantity = (int) row.getCell(3).getNumericCellValue();
            double totalPrice = row.getCell(4).getNumericCellValue();
            LocalDate date = row.getCell(5).getDateCellValue().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            products.add(new Product(id, name, price, quantity, totalPrice, date));
        }
        workbook.close();
    }

    public static List<MonthSales> calculateMonthlySales(int year) {
        double[] monthlyTotals = new double[12];

        for (Product p : products) {
            if (p.date().getYear() == year) {
                int monthIndex = p.date().getMonthValue() - 1;
                monthlyTotals[monthIndex] += p.totalPrice();
            }
        }

        List<MonthSales> sales = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            MonthSales ms = new MonthSales();
            ms.month_name = LocalDate.of(2000, i + 1, 1)
                    .getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            ms.sales = monthlyTotals[i];
            sales.add(ms);
        }

        return sales;
    }

    public static LineChart<String, Number> buildLineChart(List<MonthSales> salesList) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Total Sales");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Monthly Sales Trend");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        for (MonthSales ms : salesList) {
            series.getData().add(new XYChart.Data<>(ms.month_name, ms.sales));
        }

        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    public static void main(String[] args) {
        launch();
    }
}
