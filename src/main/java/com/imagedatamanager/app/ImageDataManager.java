package com.imagedatamanager.app;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ImageDataManager extends Application {
    public static void main(String[] args) throws IOException, CsvValidationException {
        System.out.println("Running...");
    }

    public static void csvToDirectories(String pathColumnName, String labelColumnName, String csvPath, String imgPath)
            throws IOException, CsvValidationException
    {
        ArrayList<String>[] pathsAndLabels = processCsv(pathColumnName, labelColumnName, csvPath);
        ArrayList<String> paths = pathsAndLabels[0];
        ArrayList<String> labels = pathsAndLabels[1];
        createDirectories(paths, labels, imgPath);
    }

    public static ArrayList<String>[] processCsv(String pathColumnName, String labelColumnName, String csvPath)
        throws IOException, CsvValidationException
    {
        ArrayList<String> paths = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        try (FileReader fr = new FileReader(csvPath, StandardCharsets.UTF_8); CSVReader reader = new CSVReader(fr)) {
            String[] header = reader.readNext();
            int labelIndex = getColumnIndex(header, labelColumnName);
            int pathIndex = getColumnIndex(header, pathColumnName);
            if (labelIndex == -1) {
                return null;
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                paths.add(nextLine[pathIndex]);
                labels.add(nextLine[labelIndex]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } catch (CsvValidationException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return new ArrayList[]{paths, labels};
    }

    public static int getColumnIndex(String[] row, String columnName) {
        for (int i = 0; i < row.length; i++) {
            if (row[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public static void createDirectories(ArrayList<String> paths, ArrayList<String> labels, String imgPath) throws IOException {
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            String label = labels.get(i);

            String filePath = "./src/main/resources/train/".concat(label);
            File file = new File(filePath);
            file.mkdir();
            String fromFile = "./src/main/resources/train/".concat(path);
            String toFile = filePath.concat("/").concat(path);

            Path source = Paths.get(fromFile);
            Path target = Paths.get(toFile);

            try {
                System.out.println(source);
                if (Files.exists(source)) {
                    Files.move(source, target);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    public static void directoriesToCsv(String pathColumnName, String labelColumnName, String csvPath, String imgPath)
            throws IOException, CsvValidationException
    {
        ArrayList<String>[] pathsAndLabels = processDirectories(imgPath);
        ArrayList<String> paths = pathsAndLabels[0];
        ArrayList<String> labels = pathsAndLabels[1];
        createCsv(pathColumnName, labelColumnName, paths, labels, csvPath);
    }

    public static ArrayList<String>[] processDirectories(String imgPath)
            throws IOException, CsvValidationException
    {
        ArrayList<String> paths = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();

        try {
            File f = new File(imgPath);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We only want folders
                    return f.isDirectory() && !name.equals(".DS_Store");
                }
            };

            File[] files = f.listFiles(filter);

            for (int i = 0; i < files.length; i++) {
                System.out.println(files.length);
                File file = files[i];
                String label = file.getName();
                String[] imgNames = file.list();
                for (int j = 0; j < imgNames.length; j++) {
                    String imgName = imgNames[j];
                    paths.add(imgName);
                    labels.add(label);

                    String fromFile = imgPath.concat("/").concat(label).concat("/").concat(imgName);
                    String toFile = imgPath.concat("/").concat(imgName);

                    Path source = Paths.get(fromFile);
                    Path target = Paths.get(toFile);

                    try {
                        if (Files.exists(source)) {
                            Files.move(source, target);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        throw e;
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("Help");
            System.out.println(e.getMessage());
        }

        return new ArrayList[]{paths, labels};
    }

    public static void createCsv(String pathColumnName, String labelColumnName, ArrayList<String> paths, ArrayList<String> labels, String csvPath)
        throws IOException
    {
        try {
            CSVWriter csvWrite = new CSVWriter(new FileWriter(csvPath));
            String[] headers = {pathColumnName, labelColumnName};
            csvWrite.writeNext(headers);
            for (int i = 0; i < paths.size(); i++) {
                String[] rows = {paths.get(i), labels.get(i)};
                csvWrite.writeNext(rows);
            }
            csvWrite.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public void start(Stage primaryStage) {
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

        //label column name textfield
        final TextField labelColumnName = new TextField();
        labelColumnName.setPromptText("Enter label column");
        GridPane.setConstraints(labelColumnName, 0, 1);
        grid.getChildren().add(labelColumnName);

        //csv file path textfield
        final TextField csvPath = new TextField();
        csvPath.setPromptText("Enter csv path");
        GridPane.setConstraints(csvPath, 0, 2);
        grid.getChildren().add(csvPath);

        //image file path textfield
        final TextField imgPath = new TextField();
        imgPath.setPromptText("Enter image path");
        GridPane.setConstraints(imgPath, 0, 3);
        grid.getChildren().add(imgPath);

        //mode type dropdown
        ComboBox comboBox = new ComboBox();
        GridPane.setConstraints(comboBox, 0, 4);
        comboBox.setPrefWidth(200);
        comboBox.getItems().add("CSV to Directories");
        comboBox.getItems().add("Directories to CSV");
        comboBox.getSelectionModel().selectFirst();
        grid.getChildren().add(comboBox);

        //start button for processing image data
        Button start = new Button("Start");
        GridPane.setConstraints(start, 0, 5);
        start.setPrefWidth(200);
        grid.getChildren().add(start);
        start.setOnAction(new EventHandler<>() {

            @Override
            public void handle(ActionEvent arg0) {
                // TODO Auto-generated method stub
                if (comboBox.getValue() == "CSV to Directories") {
                    try {
                        csvToDirectories(pathColumnName.getText(), labelColumnName.getText(), csvPath.getText(), imgPath.getText());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    } catch (CsvValidationException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    try {
                        directoriesToCsv(pathColumnName.getText(), labelColumnName.getText(), csvPath.getText(), imgPath.getText());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    } catch (CsvValidationException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });

        //clear button for clearing all textfields
        Button clearFields = new Button("Clear");
        GridPane.setConstraints(clearFields, 0, 6);
        clearFields.setPrefWidth(200);
        grid.getChildren().add(clearFields);
        clearFields.setOnAction(new EventHandler<>() {

            @Override
            public void handle(ActionEvent arg0) {
                // TODO Auto-generated method stub
                pathColumnName.setText("");
                labelColumnName.setText("");
                csvPath.setText("");
                imgPath.setText("");
            }
        });

        //setup scene
        grid.setAlignment(Pos.CENTER);
        Scene scene=new Scene(grid,600,400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Data Manager");
        primaryStage.show();
    }
}
