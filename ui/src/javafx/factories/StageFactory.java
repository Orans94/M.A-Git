package javafx.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class StageFactory
{
    public Stage createPopupStage(String i_Title, Pane i_ComponentToLoad, Modality i_InitModality)
    {
        Stage stage = new Stage();
        stage.setScene(new Scene(i_ComponentToLoad));
        stage.setTitle(i_Title);
        stage.initModality(i_InitModality);

        return stage;
    }
}
