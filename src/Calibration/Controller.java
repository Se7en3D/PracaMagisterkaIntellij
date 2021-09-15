package Calibration;


import Bluetooth.Bluetooth;
import carRc.FrameInput;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import main.typicalFunction;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class Controller implements Initializable {
    private Bluetooth bluetooth=new Bluetooth();
    private typicalFunction typicalfunction=new typicalFunction();
    @FXML
    TextField tfPeriod;
    @FXML
    TextField tfGenerateImpulse;
    @FXML
    Button btSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = tfPeriod.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);

        UnaryOperator<TextFormatter.Change> filter2 = change -> {
            String text = tfGenerateImpulse.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> textFormatter2 = new TextFormatter<>(filter2);

        tfPeriod.setTextFormatter(textFormatter);
        tfGenerateImpulse.setTextFormatter(textFormatter2);
    }

    public void sendStartMeasurment(){
        try {
            if(tfPeriod.getText().equals("") || tfGenerateImpulse.getText().equals("") ) {
                typicalfunction.showError("Wypełnienie i Ilość wygenerowanych impulsów PWM nie może być pusta ");
            }
            bluetooth.sendCalibrationPWMData((byte)FrameInput.CALIBRATION_PWM_DATA,Integer.parseInt(tfPeriod.getText()),Integer.parseInt(tfGenerateImpulse.getText()));
            System.out.println("Wysłano kalibracje");
        }catch (Exception ex){
            typicalfunction.showError("Nie można wysłąc polecenia. Błąd: "+ex.getMessage());
            ex.printStackTrace();
            typicalfunction.showError("Błąd połączenie z urządzeniem bluetooth \n należy włączyć je ponownie");
        }
    }

}
