package com.tjtanjin.idm.operations;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static com.tjtanjin.idm.ui.alert.flashAlert;

public class CsvToDirectories {
    public static void csvToDirectories(String pathColumnName, String labelColumnName, String csvPath, Text text)
            throws IOException, CsvValidationException
    {
        ArrayList<ArrayList<String>> pathsAndLabels = processCsv(pathColumnName, labelColumnName, csvPath);
        if (pathsAndLabels != null) {
            ArrayList<String> paths = pathsAndLabels.get(0);
            ArrayList<String> labels = pathsAndLabels.get(1);
            if (!paths.isEmpty() && createDirectories(paths, labels, text)) {
                flashAlert("Success", "CSV to Directories operation complete.", Alert.AlertType.INFORMATION);
            } else {
                flashAlert("Error", "An error has occurred. Please ensure that your images are present.", Alert.AlertType.ERROR);
            }
        } else {
            flashAlert("Error", "An error has occurred. Please check your CSV image paths and labels.", Alert.AlertType.ERROR);
        }
    }

    public static ArrayList<ArrayList<String>> processCsv(String pathColumnName, String labelColumnName, String csvPath) {
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
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
        } catch (IOException | CsvValidationException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>(Arrays.asList(paths, labels));
    }

    public static int getColumnIndex(String[] row, String columnName) {
        for (int i = 0; i < row.length; i++) {
            if (row[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean createDirectories(ArrayList<String> paths, ArrayList<String> labels, Text text) {
        int count = 0;
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            String label = labels.get(i);

            String filePath = "./src/main/resources/train/".concat(label);
            File file = new File(filePath);
            //noinspection ResultOfMethodCallIgnored
            file.mkdir();
            String fromFile = "./src/main/resources/train/".concat(path);
            String toFile = filePath.concat("/").concat(path);

            Path source = Paths.get(fromFile);
            Path target = Paths.get(toFile);

            try {
                if (Files.exists(source)) {
                    Files.move(source, target);
                    count += 1;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }

            String percentageDone = String.format("%.2f", (double)i/paths.size() * 100);
            text.setText("Progress: " + percentageDone + "%");
        }
        return count != 0;
    }
}
