package javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class StageFactory
{
    public Stage createStage(String i_Title,String i_StringPathToFXMLScene , Modality i_InitModality) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource((i_StringPathToFXMLScene))));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();

        stage.setTitle(i_Title);
        stage.setScene(new Scene(root));
        stage.initModality(i_InitModality);

        return stage;
    }
}
