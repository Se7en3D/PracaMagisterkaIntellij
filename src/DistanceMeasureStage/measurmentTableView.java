package DistanceMeasureStage;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class measurmentTableView {
    private static TableView tableView;
    private static ArrayList<measurmentTableViewMeasurClass> measurList=new ArrayList<>();
    private static int referenceMeasurment;

    public measurmentTableView(){};

    public measurmentTableView(TableView tableView){
        this.tableView=tableView;
    }


    public void showTable(){
        if(tableView!=null){
            TableColumn<measurmentTableViewMeasurClass, Integer> tcReferenceMeasurment =new TableColumn<>("Pomiar rzeczywisty");
            tcReferenceMeasurment.setCellValueFactory(new PropertyValueFactory<>("referenceMeasurment"));

            TableColumn<measurmentTableViewMeasurClass, Double> tcUltrasonicSensor =new TableColumn<>("Czujnik ultradzwiękowy");
            tcUltrasonicSensor.setCellValueFactory(new PropertyValueFactory<>("ultrasonicSensor"));

            TableColumn<measurmentTableViewMeasurClass, Float> tcLaserSensor = new TableColumn<>("Czujnik laserowy");
            tcLaserSensor.setCellValueFactory(new PropertyValueFactory<>("laserSensor"));

            tcReferenceMeasurment.setMinWidth(200);
            tcUltrasonicSensor.setMinWidth(200);
            tcLaserSensor.setMinWidth(200);
            tableView.getColumns().addAll(tcReferenceMeasurment,tcUltrasonicSensor,tcLaserSensor);
            if(measurList.size()>0){
                tableView.getItems().addAll(measurList);
            }

        }else{
            try {
                throw new Exception("Tabela measurmentTableView nie istnieje");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    public void addData(ArrayList<Integer> data){
        measurList.add(new measurmentTableViewMeasurClass(referenceMeasurment,data));
        if(tableView!=null){
            tableView.getItems().clear();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tableView.getItems().addAll(measurList);
                }
            });
        }else{
            try {
                throw new Exception("Tabela measurmentTableView nie istnieje");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    public void setReferenceMeasurment(int referenceMeasurment){
        this.referenceMeasurment=referenceMeasurment;
    }
    public void clearData(){
        measurList.clear();
        if(tableView!=null)
            tableView.getItems().clear();
    }

    public static ArrayList<measurmentTableViewMeasurClass> getMeasurList() {
        return measurList;
    }
    public int getSizeData(){
        return measurList.size();
    }
}
