package javafx;

import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javafx.event.ActionEvent;
import sun.misc.ExtensionInfo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BrowseManager
{
    private static final String EMPTY_STRING = "";

    public File openDirectoryChooser(ActionEvent i_Event)
    {
        Window currentWindow = StageUtilities.getCurrentShowedWindow(i_Event);

        // get directory from user
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(currentWindow);

        return selectedDirectory;
    }

    public File openFileChooser(ActionEvent i_Event)
    {
        return openFileChooser(i_Event, EMPTY_STRING);
    }

    public File openFileChooser(ActionEvent i_Event, String ... i_ExtensionFilters)
    {
        Window currentWindow = StageUtilities.getCurrentShowedWindow(i_Event);
        FileChooser fileChooser = new FileChooser();

        if (i_ExtensionFilters.length == 1 && !i_ExtensionFilters[0].equals(EMPTY_STRING))
        {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files",i_ExtensionFilters);
            fileChooser.getExtensionFilters().add(filter);
        }

        return fileChooser.showOpenDialog(currentWindow);
    }


}
