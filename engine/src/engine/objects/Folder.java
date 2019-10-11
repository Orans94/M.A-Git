package engine.objects;

import engine.core.Magit;
import engine.utils.DateUtils;
import engine.utils.FileUtilities;
import engine.utils.StringUtilities;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static engine.utils.StringFinals.EMPTY_STRING;

public class Folder extends Node
{
    public List<Item> getItems() { return m_Items; }
    private List<Item> m_Items;
    public void setItems(List<Item> i_Items) { m_Items = i_Items; }

    public Folder(String i_Content)
    {
        super(i_Content);
        m_Items = new LinkedList<>();
    }

    @Override
    public String SHA1()
    {
        return DigestUtils.sha1Hex(StringUtilities.makeSHA1Content(m_Content,3));
    }

    @Override
    public void Zip(String i_SHA1FileName, Path i_PathOfTheFile, Path i_MagitDir) throws IOException
    {
        // 1. creating temp txt file in objects dir
        Path createdTempTxtPath = i_MagitDir.resolve("objects").resolve(i_SHA1FileName + ".txt");
        FileUtilities.createAndWriteTxtFile(createdTempTxtPath, m_Content);

        // 2. zipping the temp txt file
        super.Zip(i_SHA1FileName, createdTempTxtPath, i_MagitDir);

        // 3. remove the tmp txt file
        FileUtilities.deleteFile(createdTempTxtPath);
    }

    @Override
    public String toString()
    {
        String content = "";
        for (Item item : m_Items)
        {
            content = content.concat(item.toString()).concat(",").concat(System.lineSeparator());
        }

        content = content.substring(0, content.length() - 2);

        return content;
    }

    public void createItemListFromContent()
    {
        // this method creating an item list from the content of the node

        String[] lines = m_Content.split(System.lineSeparator());
        for (String line : lines)
        {
            String[] members = line.split(",");
            m_Items.add(new Item(members[0], members[1], members[2], members[3], DateUtils.FormatToDate(members[4])));
        }
    }

    public void removeItemFromList(Item i_ItemToDelete)
    {
        m_Items.remove(i_ItemToDelete);
    }

    public void sortItemList()
    {
        m_Items = m_Items.stream()
                .sorted(Comparator.comparing(Item::toString))
                .collect(Collectors.toList());
    }

    public Item getSpecificItem(String i_ItemName)
    {
        Item result = null;
        for(Item item : m_Items)
        {
            if(item.getName().equals(i_ItemName))
            {
                result = item;
            }
        }

        return result;
    }

    public int getNumberOfItems()
    {
        return m_Items.size();
    }

    public void setContentFromItemList()
    {
        m_Content = EMPTY_STRING;
        for(Item item : m_Items)
        {
            m_Content = m_Content.concat(item.toString() + System.lineSeparator());
        }
        m_Content = m_Content.substring(0, m_Content.length() - 2);
    }

    public void addItemToList(Item i_ToAddItem)
    {
        m_Items.add(i_ToAddItem);
    }
}
