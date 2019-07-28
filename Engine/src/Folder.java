import com.sun.deploy.Environment;

import java.util.LinkedList;
import java.util.List;

public class Folder extends Node
{
    private List<Item> m_Items;

    public Folder(String i_Content)
    {
        super(i_Content);
        m_Items = new LinkedList<>();
    }

    @Override
    public String toString()
    {
        String content = "";
        for (Item item : m_Items)
        {
            content = content.concat(item.toString()).concat(System.lineSeparator());
        }

        return content;
    }
}
