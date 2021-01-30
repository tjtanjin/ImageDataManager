package com.imagedatamanager.app;

import org.junit.Assert;
import org.junit.Test;

public class ImageDataManagerTest {
    @Test
    public void ReadData() {
        String fileName = "./src/main/resources/labels.csv";
        String labelColumnName = "breed";
        String pathColumnName = "id";
        ImageDataManager IDM = new ImageDataManager();
        Assert.assertEquals("Done", IDM.readFile(fileName, labelColumnName, pathColumnName));
    }
}