import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add("style/style.css");
        primaryStage.setTitle("Praca magisterska");
        primaryStage.setScene(new Scene(root));
        MainController controller = loader.getController();
        primaryStage.setOnHidden(e -> {
            controller.close();
            Platform.exit();
        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
