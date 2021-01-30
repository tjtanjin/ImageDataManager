package com.tjtanjin.idm.ui;

import javafx.scene.control.Alert;

public class alert {
    public static void flashAlert(String header, String content, Alert.AlertType type) {
        //create alert
        Alert alert = new Alert(type);

        //set title
        alert.setTitle("Message");

        //set header
        alert.setHeaderText(header);

        //set content text
        alert.setContentText(content);

        //show the alert
        alert.show();
    }
}
