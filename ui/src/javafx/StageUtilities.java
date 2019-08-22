package javafx;

import javafx.fxml.FXMLLoader;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Method;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class StageUtilities
{
    public static Stage createPopupStage(String i_Title, String i_FXMLResourcePath, TopController i_ControllerToWire) throws IOException
    {

        // getting fxmlLoader for the current FXMLResource
        ComponentControllerConnector connector = new ComponentControllerConnector();
        FXMLLoader fxmlLoader = connector.getFXMLLoader(i_FXMLResourcePath);
        Parent root = fxmlLoader.getRoot();

        // configure the new stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(i_Title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        // wire up the new scene's controller to topController
        PopupController popupController = fxmlLoader.getController();
        popupController.setTopController(i_ControllerToWire);
        i_ControllerToWire.setCreateNewRepositoryComponent(root);
        i_ControllerToWire.setCreateNewRepositoryComponentController(popupController);

        String setMethodname = popupController.getClass().getSimpleName();
        setMethodname = setMethodname.substring(0,setMethodname.length()-10);
        setMethodname = "set" + setMethodname + "ComponentController";
        Method setMethodToInvoke;

 /*       try
        {
            setMethodToInvoke = i_ControllerToWire.getClass().getMethod(methodName, param1.class, param2.class, ..);
        } catch (SecurityException e) { ... }
        catch (NoSuchMethodException e) { ... }

*/
        return stage;
    }

    public static void closeOpenSceneByActionEvent(ActionEvent i_Event)
    {
        Node source = (Node)i_Event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }
}
