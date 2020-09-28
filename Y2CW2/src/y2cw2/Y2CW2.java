/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package y2cw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author colbertlm
 */
public class Y2CW2 extends Application {

    //first scene
    private TextField tfx = new TextField();
    private TextField tft = new TextField();
    private TextField tfy = new TextField();
    private Text xLabel = new Text("X-Axis Label: ");
    private Text yLabel = new Text("Y-Axis Label: ");
    private Label title = new Label("Title:              ");
    private Button gbc = new Button("Generate Bar Chart");
    private Button gfcsv = new Button("Generate from CSV");
    private ArrayList<TextField> itemNameColumn = new ArrayList<TextField>();
    private ArrayList<TextField> valueColumn = new ArrayList<TextField>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
    private Label itemName = new Label("Item Name");
    private Label value = new Label("Value");
    private Label enable = new Label("Enable");
    private Label errors = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Fisrt Scene
        tft.setPromptText("Title of the chart");
        tfx.setPromptText("Description of Items");
        tfy.setPromptText("Description of Item Value");

        boxCreator(itemNameColumn, valueColumn, checkBoxes);

        VBox verticle1 = new VBox(15);
        VBox verticle2 = new VBox(15);
        VBox verticle3 = new VBox(15);
        VBox verticle4 = new VBox(24);
        HBox horizontalTitle = new HBox(15);
        HBox horizontalX = new HBox(15);
        HBox horizontalY = new HBox(15);
        HBox horizontal = new HBox(15);
        horizontal.setAlignment(Pos.CENTER);
        horizontalTitle.setAlignment(Pos.CENTER);
        horizontalX.setAlignment(Pos.CENTER);
        horizontalY.setAlignment(Pos.CENTER);
        verticle1.setAlignment(Pos.CENTER);
        verticle2.setAlignment(Pos.CENTER);
        verticle3.setAlignment(Pos.CENTER);
        verticle4.setAlignment(Pos.CENTER);

        horizontal.getChildren().add(verticle1);
        verticle1.getChildren().add(horizontalTitle);
        verticle1.getChildren().add(horizontalX);
        verticle1.getChildren().add(horizontalY);
        horizontalTitle.getChildren().add(title);
        horizontalTitle.getChildren().add(tft);
        horizontalX.getChildren().add(xLabel);
        horizontalX.getChildren().add(tfx);
        horizontalY.getChildren().add(yLabel);
        horizontalY.getChildren().add(tfy);
        verticle1.getChildren().add(gbc);
        verticle1.getChildren().add(gfcsv);
        verticle1.getChildren().add(errors);

        horizontal.getChildren().add(verticle2);
        verticle2.getChildren().add(itemName);
        for (int i = 0; i < itemNameColumn.size(); i++) {
            verticle2.getChildren().add(itemNameColumn.get(i));
        }

        horizontal.getChildren().add(verticle3);
        verticle3.getChildren().add(value);
        for (int i = 0; i < valueColumn.size(); i++) {
            verticle3.getChildren().add(valueColumn.get(i));
        }

        horizontal.getChildren().add(verticle4);
        verticle4.getChildren().add(enable);
        for (int i = 0; i < checkBoxes.size(); i++) {
            verticle4.getChildren().add(checkBoxes.get(i));
        }
        Scene firstScene = new Scene(horizontal, 900, 600);

        //secondScene
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        XYChart.Series series = new XYChart.Series();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setPrefSize(800, 550);

        Pane pane = new Pane();
        pane.getChildren().add(barChart);
        barChart.relocate(40, 30);
        final Scene secondScene = new Scene(pane, 900, 600);

        //function
        gbc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                barChart.getData().clear();
                xAxis.setLabel(tfx.getText());
                yAxis.setLabel(tfy.getText());
                barChart.setTitle(tft.getText());
                boolean emptyItemTextFields = false;
                try {
                    for (int i = 0; i < itemNameColumn.size(); i++) {
                        if (itemNameColumn.get(i).isDisable() == false) {
                            if (itemNameColumn.get(i).getText().isEmpty()) {
                                emptyItemTextFields = true;
                            } else {
                                series.getData().add(new XYChart.Data(itemNameColumn.get(i).getText(), Double.parseDouble(valueColumn.get(i).getText())));
                            }
                        }
                    }
                    if (emptyItemTextFields == true) {
                        errors.setText("One of the text fields for item name is empty");
                    } else {
                        barChart.getData().addAll(series);
                        primaryStage.setScene(secondScene);
                    }

                } catch (NumberFormatException ex4) {
                    errors.setText("Error:\n One of the text fields has the wrong input type");
                }
            }

        });

        gfcsv.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                barChart.getData().clear();
                try {
                    try {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Import a CSV file");
                        fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
                        File selectedFile = fileChooser.showOpenDialog(primaryStage);
                        try {
                            Scanner scanner = new Scanner(selectedFile);
                            scanner.useDelimiter(",|\n");
                            barChart.setTitle(scanner.next());
                            yAxis.setLabel(scanner.next());
                            xAxis.setLabel(scanner.next());
                            while (scanner.hasNext()) {
                                series.getData().add(new XYChart.Data(scanner.next(), Double.parseDouble(scanner.next())));
                            }
                            barChart.getData().addAll(series);
                            primaryStage.setScene(secondScene);
                        } catch (FileNotFoundException ex) {
                            errors.setText("Error: File not found");
                        }
                    } catch (NullPointerException ex2) {
                        errors.setText("Error: No selected files");
                    }
                } catch (NumberFormatException ex3) {
                    errors.setText("Error: Wrong csv format");
                }
            }
        });

        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxesFunction(i);
        }

        //Primary Satge
        primaryStage.setTitle("Bar Chart Generator");
        primaryStage.setScene(firstScene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    /**
     * This method is for creating the text fields, and the check boxes that are
     * needed
     *
     * @param a An array list that contains text fields
     * @param b An array list that contains text fields
     * @param c An array list that contains check boxes
     */
    public void boxCreator(ArrayList<TextField> a, ArrayList<TextField> b, ArrayList<CheckBox> c) {
        for (int i = 0; i < 10; i++) {
            TextField textField = new TextField();
            textField.setPromptText("Item name");
            textField.setDisable(false);
            a.add(textField);
        }
        for (int i = 0; i < 10; i++) {
            TextField textField = new TextField();
            textField.setPromptText("e.g 50");
            textField.setDisable(false);
            b.add(textField);
        }
        for (int i = 0; i < 10; i++) {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            c.add(checkBox);
        }
    }

    /**
     * A method for tthe function that when a check box is clicked, the text
     * fields in the same roll will be enabled or disabled
     *
     * @param i the index for roll
     */
    public void checkBoxesFunction(int i) {
        checkBoxes.get(i).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (itemNameColumn.get(i).isDisable() == false) {
                    itemNameColumn.get(i).setDisable(true);
                    valueColumn.get(i).setDisable(true);
                } else {
                    itemNameColumn.get(i).setDisable(false);
                    valueColumn.get(i).setDisable(false);
                }
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
