package javafx;

import javafx.fxml.FXMLLoader;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class ComponentControllerConnector
{
    public FXMLLoader getFXMLLoader (String i_FXMLPathString) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(i_FXMLPathString);
        fxmlLoader.setLocation(url);
        fxmlLoader.load(url.openStream());

        return fxmlLoader;
    }

    public void connectComponentToController(Pane o_Component, CreateNewRepositoryController o_Controller, FXMLLoader i_FxmlLoader)
    {
        o_Component = i_FxmlLoader.getRoot();
        o_Controller = i_FxmlLoader.getController();
    }
}
