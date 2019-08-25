package javafx;

import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javafx.event.ActionEvent;
import sun.misc.ExtensionInfo;

import java.io.File;

public class BrowseManager
{
    public File openDirectoryChooser(ActionEvent i_Event)
    {
        Node source = (Node)i_Event.getSource();
        Window theStage = source.getScene().getWindow();

        // get directory from user
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(theStage);

        return selectedDirectory;
    }

    public File openFileChooser(ActionEvent i_Event)
    {
        openFileChooser(i_Event,)
    }

    public File openFileChooser(ActionEvent i_Event, FileChooser.ExtensionFilter ... i_Fillters)
    {

    }


}
