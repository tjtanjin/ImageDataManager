package com.tjtanjin.idm.operations;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static com.tjtanjin.idm.ui.alert.flashAlert;

public class DirectoriesToCsv {
    public static void directoriesToCsv(String pathColumnName, String labelColumnName, String csvPath, String imgPath, Text text)
            throws IOException, CsvValidationException
    {
        ArrayList<ArrayList<String>> pathsAndLabels = processDirectories(imgPath);
        ArrayList<String> paths = pathsAndLabels.get(0);
        ArrayList<String> labels = pathsAndLabels.get(1);
        if (!paths.isEmpty() && createCsv(pathColumnName, labelColumnName, paths, labels, csvPath, text)) {
            flashAlert("Success", "Directories to CSV operation complete.", Alert.AlertType.INFORMATION);
        } else {
            flashAlert("Error", "An error has occurred. Please ensure that your directories are present.", Alert.AlertType.ERROR);
        }
    }

    public static ArrayList<ArrayList<String>> processDirectories(String imgPath) {
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        try {
            File f = new File(imgPath);

            FileFilter filter = (file) -> {
                // We only want folders
                return file.isDirectory() && !file.getName().equals(".DS_Store");
            };

            File[] files = f.listFiles(filter);

            if (files != null) {
                for (File value : files) {
                    String label = value.getName();
                    String[] imgNames = value.list();
                    if (imgNames != null) {
                        for (String imgName : imgNames) {
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
                    }
                    //noinspection ResultOfMethodCallIgnored
                    value.delete();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>(Arrays.asList(paths, labels));
    }

    public static boolean createCsv(String pathColumnName, String labelColumnName, ArrayList<String> paths, ArrayList<String> labels, String csvPath, Text text) {
        try {
            CSVWriter csvWrite = new CSVWriter(new FileWriter(csvPath));
            String[] headers = {pathColumnName, labelColumnName};
            csvWrite.writeNext(headers);
            for (int i = 0; i < paths.size(); i++) {
                String[] rows = {paths.get(i), labels.get(i)};
                csvWrite.writeNext(rows);
                String percentageDone = String.format("%.2f", (double)i/paths.size() * 100);
                text.setText("Progress: " + percentageDone + "%");
            }
            csvWrite.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
