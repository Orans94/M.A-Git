import org.apache.commons.codec.digest.DigestUtils;
import java.util.Date;

public class Commit
{
    private String m_RootFolderSHA1;
    private String m_ParentSHA1;
    private String m_Message;
    private Date m_CommitDate;
    private String m_CommitAuthor;

    public Commit(String i_RootFolderSHA1, String i_ParentSHA1, String i_Message)
    {
        m_RootFolderSHA1 = i_RootFolderSHA1;
        m_ParentSHA1 = i_ParentSHA1;
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitDate = DateUtils.FormatToDate(m_CommitDate.toString());
        m_CommitAuthor = EngineManager.getUserName();
    }

    @Override
    public String toString()
    {
        return
                "" + m_RootFolderSHA1 + ','
                + m_ParentSHA1 + ','
                + m_Message + ','
                + m_CommitDate.toString() + ','
                + m_CommitAuthor;
    }

    public String SHA1()
    {
        return DigestUtils.sha1Hex(StringUtilities.makeSHA1Content(this.toString()));
    }
}
