package javafx;

import javafx.fxml.FXMLLoader;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.stage.Window;

import java.io.IOException;
import java.lang.reflect.Method;

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

        // wiring up the new scene's controller to topController
        PopupController popupController = fxmlLoader.getController();
        popupController.setTopController(i_ControllerToWire);

        // wiring up top controller to new popupcontroller and it component
        // ---------------------- ASSUMPTIONS ----------------------
        // * component name of component in topController it "xComponent"
        // * controller name of controller in topController it "xController"
        // * this operation using setter methods
        // ------------------- END OF ASSUMPTIONS ------------------

        String featureName = popupController.getClass().getSimpleName();
        featureName = featureName.substring(0, featureName.length()-10);
        String setComponentMethodName = "set" + featureName + "Component";
        String setControllerMethodName = "set" + featureName + "ComponentController";

        try
        {
            // getting set methods of topController using reflection
            Method setComponentMethod = i_ControllerToWire.getClass().getMethod(setComponentMethodName, Parent.class);
            Method setControllerMethod = i_ControllerToWire.getClass().getMethod(setControllerMethodName, PopupController.class);

            // invoke setters methods
            setComponentMethod.invoke(root, root);
            setControllerMethod.invoke(popupController,popupController);
        } catch (Exception ex)
        {
            // TODO handle exception
        }

        return stage;
    }

    public static void closeOpenSceneByActionEvent(ActionEvent i_Event)
    {
        Node source = (Node)i_Event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public static Window getCurrentShowedWindow(ActionEvent i_Event)
    {
        Node source = (Node)i_Event.getSource();
        Window theStage = source.getScene().getWindow();

        return theStage;
    }
}
