package main;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class typicalFunction {

    public void showError(String middle){
        Alert alert = new Alert(Alert.AlertType.ERROR,middle, ButtonType.OK);
        alert.show();
    }
    public void showInfo(String middle){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,middle, ButtonType.OK);
        alert.show();
    }
}
