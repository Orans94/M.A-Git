package javafx.primary.top.popup.showinformation;

import engine.utils.DateUtils;
import engine.objects.Folder;
import engine.objects.Item;
import engine.dataobjects.NodeMaps;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ShowCurrentCommitDetails implements Showable
{
    private ShowInformationController m_ShowInformationController;
    private NodeMaps m_NodeMaps;

    public ShowCurrentCommitDetails(NodeMaps i_NodeMaps, ShowInformationController i_ShowInformationController)
    {
        m_ShowInformationController = i_ShowInformationController;
        m_NodeMaps = i_NodeMaps;
    }

    private String getCurrentCommitInformation()
    {
        String result = "";
        for (Map.Entry<Path, String> entry : m_NodeMaps.getSHA1ByPath().entrySet())
        {
            if (m_ShowInformationController.isDirectory(entry.getKey()))
            {
                Folder folder = (Folder) m_NodeMaps.getNodeBySHA1().get(entry.getValue());
                List<Item> items = folder.getItems();
                Path folderPath = entry.getKey();
                for (Item item : items)
                {
                    result = result.concat(getCurrentItemDetails(folderPath, item) + System.lineSeparator());
                }
            }
        }

        return result;
    }

    private String getCurrentItemDetails(Path i_FolderPath, Item i_Item)
    {
        String result = "";
        result = result.concat("Full name: " + i_FolderPath.resolve(i_Item.getName()) + System.lineSeparator());
        result = result.concat("Type: " + i_Item.getType() + System.lineSeparator());
        result = result.concat("SHA-1: " + i_Item.getSHA1() + System.lineSeparator());
        result = result.concat("Last modifier name: " + i_Item.getAuthor() + System.lineSeparator());
        result = result.concat("Date modified: " + DateUtils.FormatToString(i_Item.getModificationDate()) + System.lineSeparator());
        return result;
    }

    @Override
    public String getInformation()
    {
        return getCurrentCommitInformation();
    }
}
