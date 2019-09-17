package javafx.primary.include;

import javafx.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

import static javafx.CommonResourcesPaths.APP_FXML_INCLUDE_RESOURCE;
import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;
import static javafx.application.Application.launch;

public class Main extends Application
{
    public static void main(String[] args)
    {
        Thread.currentThread().setName("main");
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("M.A Git");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_INCLUDE_RESOURCE);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        AppController appController = fxmlLoader.getController();
        appController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root,1800,800 );
        primaryStage.setScene(scene);
        primaryStage.show();


        //--------------------------
     /*   Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");

        alert.showAndWait();*/
        //--------------------------
    }
}
