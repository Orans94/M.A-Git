package engine.objects;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import engine.managers.EngineManager;
import engine.utils.DateUtils;
import engine.utils.FileUtilities;
import org.apache.commons.codec.digest.DigestUtils;

abstract public class Node
{
    protected String m_Content = "";

    public Node(String i_Content) { this.m_Content = i_Content; }

    public String SHA1() { return DigestUtils.sha1Hex(m_Content); }

    public void Zip(String i_SHA1FileName, Path i_PathOfTheFile) throws IOException { FileUtilities.zip(i_SHA1FileName, i_PathOfTheFile); }

    public String generateStringInformation(String i_Sha1, String i_FileName)
    {
        return "" + i_FileName + "," + i_Sha1 + "," +
                this.getClass().getSimpleName().toLowerCase() + "," +
                EngineManager.getUserName() + "," +
                DateUtils.FormatToString(new Date());
    }

    public String getContent(){return m_Content;}

    public void setContent(String i_Content){ m_Content = i_Content;}
}
