import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import BluetoothPackage.Bluetooth;


import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();

    @FXML
    Button BTSearchDevice;
    @FXML
    Button BTConnectToDevice;
    @FXML
    Button BTShowError;
    @FXML
    Label LBConnectionStatus;
    @FXML
    ComboBox<String> CBBluetoothDevices;
    @FXML
    VBox VBControlCar;
    @FXML
    Button BTMeasurment;
    @FXML
    ImageView IVbellImage;
    @FXML
    Canvas CVController;
    @FXML
    Canvas CVcarWithSensors;
    @FXML
    Canvas CVBattery;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
