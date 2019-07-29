import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.List;

public class Commit
{
    private String m_Content;
    private Commit m_Parent;
    private String m_Message;
    private Date m_CommitDate;
    private String m_CommitAuthor;

    public Commit(String i_Content, Commit i_Parent, String i_Message)
    {
        m_Content = i_Content;
        m_Parent = i_Parent;
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitDate = DateUtils.FormatToDate(m_CommitDate.toString());
        m_CommitAuthor = EngineManager.getUserName();
    }

    @Override
    public String toString()
    {
        return
                "" + m_Content + ','
                + m_Parent + ','
                + m_Message + ','
                + m_CommitDate.toString() + ','
                + m_CommitAuthor;
    }

    public String SHA1()
    {
        return DigestUtils.sha1Hex(this.toString());
    }
}
