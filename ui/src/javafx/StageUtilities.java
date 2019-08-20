package javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class StageUtilities
{
    public static Stage createPopupStage(String i_Title, Pane i_ComponentToLoad, Modality i_InitModality)
    {
        Stage stage = new Stage();
        stage.setScene(new Scene(i_ComponentToLoad));
        stage.setTitle(i_Title);
        stage.initModality(i_InitModality);

        return stage;
    }

    public static void closeOpenSceneByActionEvent(ActionEvent i_Event)
    {
        Node source = (Node)i_Event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }
}
