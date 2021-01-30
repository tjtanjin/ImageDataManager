package com.tjtanjin.idm;

import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.IOException;

import static com.tjtanjin.idm.operations.CsvToDirectories.csvToDirectories;
import static com.tjtanjin.idm.operations.DirectoriesToCsv.directoriesToCsv;

public class ImageDataManager extends Application {
    public static void main(String[] args) {
        System.out.println("Running...");
    }

    @Override
    public void start(Stage primaryStage) {
        final Tooltip pathTooltip0 = new Tooltip();
        pathTooltip0.setText(
                "The column name which contains image paths"
        );

        final Tooltip pathTooltip1 = new Tooltip();
        pathTooltip1.setText(
                "The column name you wish to give for constructed image paths"
        );

        final Tooltip labelTooltip0 = new Tooltip();
        labelTooltip0.setText(
                "The column name which contains image labels"
        );

        final Tooltip labelTooltip1 = new Tooltip();
        labelTooltip1.setText(
                "The column name you wish to give for the image labels"
        );

        final Tooltip csvTooltip0 = new Tooltip();
        csvTooltip0.setText(
                "The path to the CSV file to read image paths and labels from"
        );

        final Tooltip csvTooltip1 = new Tooltip();
        csvTooltip1.setText(
                "The name and path to where you wish to store your new CSV file"
        );

        final Tooltip imgTooltip0 = new Tooltip();
        imgTooltip0.setText(
                "The path to the directory containing your image files"
        );

        final Tooltip imgTooltip1 = new Tooltip();
        imgTooltip1.setText(
                "The path to the subdirectories of your image files"
        );

        final Tooltip operationTypeTooltip = new Tooltip();
        operationTypeTooltip.setText(
                "The mode of operation to switch from directory labels to csv labels or vice versa"
        );

        final Tooltip startButtonTooltip = new Tooltip();
        startButtonTooltip.setText(
                "Start processing images/csv"
        );

        final Tooltip clearButtonTooltip = new Tooltip();
        clearButtonTooltip.setText(
                "Clear all text fields above"
        );

        //setup gridpane container
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        //image path column name textfield
        final TextField pathColumnName = new TextField();
        pathColumnName.setPromptText("Enter image path column");
        GridPane.setConstraints(pathColumnName, 0, 0);
        grid.getChildren().add(pathColumnName);
        pathColumnName.setTooltip(pathTooltip0);

        //label column name textfield
        final TextField labelColumnName = new TextField();
        labelColumnName.setPromptText("Enter label column");
        GridPane.setConstraints(labelColumnName, 0, 1);
        grid.getChildren().add(labelColumnName);
        labelColumnName.setTooltip(labelTooltip0);

        //csv file path textfield
        final TextField csvPath = new TextField();
        csvPath.setPromptText("Enter csv path");
        GridPane.setConstraints(csvPath, 0, 2);
        grid.getChildren().add(csvPath);
        csvPath.setTooltip(csvTooltip0);

        //image file path textfield
        final TextField imgPath = new TextField();
        imgPath.setPromptText("Enter image path");
        GridPane.setConstraints(imgPath, 0, 3);
        grid.getChildren().add(imgPath);
        imgPath.setTooltip(imgTooltip0);

        //mode type dropdown
        ComboBox<String> comboBox = new ComboBox<>();
        GridPane.setConstraints(comboBox, 0, 4);
        comboBox.setPrefWidth(200);
        comboBox.getItems().add("CSV to Directories");
        comboBox.getItems().add("Directories to CSV");
        comboBox.getSelectionModel().selectFirst();
        grid.getChildren().add(comboBox);
        comboBox.setTooltip(operationTypeTooltip);
        comboBox.setOnAction(e -> {

            if (comboBox.getValue().equals("CSV to Directories")) {
                pathColumnName.setTooltip(pathTooltip0);
                labelColumnName.setTooltip(labelTooltip0);
                csvPath.setTooltip(csvTooltip0);
                imgPath.setTooltip(imgTooltip0);
            } else {
                pathColumnName.setTooltip(pathTooltip1);
                labelColumnName.setTooltip(labelTooltip1);
                csvPath.setTooltip(csvTooltip1);
                imgPath.setTooltip(imgTooltip1);
            }
        });

        //text line for informing user of successful/failed operations
        Text text = new Text();
        GridPane.setConstraints(text, 0, 7);
        GridPane.setHalignment(text, HPos.CENTER);
        grid.getChildren().add(text);

        //start button for processing image data
        Button start = new Button("Start");
        GridPane.setConstraints(start, 0, 5);
        start.setPrefWidth(200);
        grid.getChildren().add(start);
        start.setTooltip(startButtonTooltip);
        start.setOnAction(arg0 -> {
            // TODO Auto-generated method stub
            if (comboBox.getValue().equals("CSV to Directories")) {
                try {
                    csvToDirectories(pathColumnName.getText(), labelColumnName.getText(), csvPath.getText(), text);
                } catch (IOException | CsvValidationException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    directoriesToCsv(pathColumnName.getText(), labelColumnName.getText(), csvPath.getText(), imgPath.getText(), text);
                } catch (IOException | CsvValidationException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        //clear button for clearing all textfields
        Button clearFields = new Button("Clear");
        GridPane.setConstraints(clearFields, 0, 6);
        clearFields.setPrefWidth(200);
        grid.getChildren().add(clearFields);
        clearFields.setTooltip(clearButtonTooltip);
        clearFields.setOnAction(arg0 -> {
            // TODO Auto-generated method stub
            pathColumnName.setText("");
            labelColumnName.setText("");
            csvPath.setText("");
            imgPath.setText("");
        });

        //setup scene
        grid.setAlignment(Pos.CENTER);
        Scene scene=new Scene(grid,600,400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Data Manager");
        primaryStage.show();
    }

    public static void flashText(Text text, String content) {
        //set text to flash
        text.setText(content);

        //Instantiating FadeTransition class
        FadeTransition fade = new FadeTransition();

        //setting the duration for the Fade transition
        fade.setDuration(Duration.millis(5000));

        //setting the initial and the target opacity value for the transition
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        //setting cycle count for the Fade transition
        fade.setCycleCount(1000);

        //the transition will set to be auto reversed by setting this to true
        fade.setAutoReverse(true);

        fade.setCycleCount(1);

        //setting Circle as the node onto which the transition will be applied
        fade.setNode(text);

        fade.play();
    }
}
