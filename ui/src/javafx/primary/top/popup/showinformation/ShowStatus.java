package javafx.primary.top.popup.showinformation;

import engine.OpenChanges;
import javafx.AlertFactory;
import javafx.StageUtilities;

import java.nio.file.Path;
import java.util.List;

public class ShowStatus implements Showable
{
    private OpenChanges m_CurrentOpenChanges;

    public ShowStatus(OpenChanges i_OpenChanges)
    {
        m_CurrentOpenChanges = i_OpenChanges;
    }

    public String getOpenChanges()
    {
        String result = "";

        result = result.concat(getList("Deleted", m_CurrentOpenChanges.getDeletedNodes()) + System.lineSeparator());
        result = result.concat(getList("Modified", m_CurrentOpenChanges.getModifiedNodes()) + System.lineSeparator());
        result = result.concat(getList("New", m_CurrentOpenChanges.getNewNodes()) + System.lineSeparator());

        return result;
    }


    public String getList(String i_Status, List<Path> i_OpenChangesList)
    {
        //get an openchanges list with its status
        String result = "";

        for (Path path : i_OpenChangesList)
        {
            result = result.concat(i_Status + ": " + path + System.lineSeparator());
        }

        return result;
    }

    @Override
    public String getInformation()
    {
        return getOpenChanges();
    }
}
