import com.sun.deploy.Environment;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.nio.file.Path;
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
    protected String SHA1()
    {
        String contentToSHA1 = updateContent();
        return DigestUtils.sha1Hex(contentToSHA1);
    }

    @Override
    protected void Zip(String i_SHA1FileName, Path i_PathOfTheFile)
    {
        // 1. creating temp txt file in objects dir
        Path createTempTxtPath = Magit.getMagitDir().resolve("objects").resolve(i_SHA1FileName).resolve(".txt");
        FileUtils.CreateAndWriteTxtFile(createTempTxtPath, m_Content);

        // 2. zipping the temp txt file
        super.Zip(i_SHA1FileName, createTempTxtPath);

        // 3. remove the tmp txt file
        FileUtils.deleteFile(createTempTxtPath);
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

    public void createItemListFromContent()
    {
        String[] lines = m_Content.split(System.lineSeparator());
        for (String line : lines)
        {
            String[] members = line.split(",");
            m_Items.add(new Item(members[0], members[1], members[2], members[3], DateUtils.FormatToDate(members[4])));
        }
    }


    public String updateContent()
    {
        String newContent = "";
        String[] lines = m_Content.split(System.lineSeparator());
        for (String line : lines)
        {
            String[] members = line.split(",");
            newContent = newContent.concat(members[0])
                    .concat(",").concat(members[1])
                    .concat(",").concat(members[2])
                    .concat(System.lineSeparator());
        }
        newContent = newContent.substring(0, newContent.length() - 2);

        return newContent;
    }
}
