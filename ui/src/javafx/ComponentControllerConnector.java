package javafx;

import javafx.fxml.FXMLLoader;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.scene.layout.VBox;

import java.net.URL;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class ComponentControllerConnector
{
    public void foo ()
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(CREATE_NEW_REPOSITORY_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        VBox createNewRepositoryComponent = fxmlLoader.load(url.openStream());
        CreateNewRepositoryController createNewRepositoryController = fxmlLoader.getController();
    }
}
