package javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class AlertFactory
{
    public static Alert createYesNoAlert(String i_Title, String i_Content)
    {
        Alert yesNoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        yesNoAlert.setTitle(i_Title);
        yesNoAlert.setContentText(i_Content);
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        yesNoAlert.getButtonTypes().setAll(okButton, noButton);

        return yesNoAlert;
    }

    public static Alert createErrorAlert(String i_Title, String i_Content)
    {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(i_Title);
        errorAlert.setContentText(i_Content);

        return errorAlert;
    }

    public static Alert createInformationAlert(String i_Title, String i_Content)
    {
        Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
        informationAlert.setTitle(i_Title);
        informationAlert.setHeaderText(null);
        informationAlert.setContentText(i_Content);

        return informationAlert;
    }


}
