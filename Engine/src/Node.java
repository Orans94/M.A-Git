import java.nio.file.Path;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;

abstract public class Node
{
    protected String m_Content = "";

    public Node(String i_Content) { this.m_Content = i_Content; }

    protected String SHA1() { return DigestUtils.sha1Hex(m_Content); }

    protected void Zip(String i_SHA1FileName, Path i_PathOfTheFile) { FileUtils.zip(i_SHA1FileName, i_PathOfTheFile); }

    public String generateStringInformation(String i_Sha1, String i_FileName)
    {
        return "" + i_FileName + "," + i_Sha1 + "," +
                this.getClass().getSimpleName() + "," +
                EngineManager.getUserName() + "," +
                DateUtils.FormatToString(new Date());
    }
}
