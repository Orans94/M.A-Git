package engine;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Commit
{
    private String m_RootFolderSHA1;
    private List<String> m_ParentsSHA1 = new LinkedList<>();
    private String m_Message;
    private Date m_CommitDate;
    private String m_CommitAuthor;

    public Commit(String i_RootFolderSHA1, List<String> i_ParentsSHA1, String i_Message, Date i_CommitDate, String i_Author)
    {
        m_RootFolderSHA1 = i_RootFolderSHA1;
        for (String parentSHA1 : i_ParentsSHA1)
        {

            if (parentSHA1 != null && !parentSHA1.equals(""))
            {
                m_ParentsSHA1.add(parentSHA1);
            }
        }
        m_Message = i_Message;
        m_CommitDate = i_CommitDate;
        m_CommitAuthor = i_Author;
    }

    public List<String> getParentsSHA1() {return m_ParentsSHA1;}

    public Commit(String i_RootFolderSHA1, String i_ParentSHA1, String i_Message)
    {
        // ctor that takes the current time and current user name
        m_RootFolderSHA1 = i_RootFolderSHA1;
        if(i_ParentSHA1 != null && !i_ParentSHA1.equals(""))
        {
            m_ParentsSHA1.add(i_ParentSHA1);
        }
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitAuthor = EngineManager.getUserName();
    }

    public Commit (String i_RootFolderSHA1, String i_ParentSHA1, String i_Message, Date i_CommitDate, String i_Author)
    {
        //ctor that gets all his members as params
        m_RootFolderSHA1 = i_RootFolderSHA1;
        if(i_ParentSHA1 != null && !i_ParentSHA1.equals(""))
        {
            m_ParentsSHA1.add(i_ParentSHA1);
        }
        m_Message = i_Message;
        m_CommitDate = i_CommitDate;
        m_CommitAuthor = i_Author;
    }

    public void addParent(String i_ParentSHA1)
    {
        if(i_ParentSHA1 != null && !i_ParentSHA1.equals(""))
        {
            m_ParentsSHA1.add(i_ParentSHA1);
        }
    }

    public void Zip(String i_CommitSHA1FileName)
    {
        // 1. creating temp txt file in objects dir
        Path createTempTxtPath = Magit.getMagitDir().resolve("objects").resolve(i_CommitSHA1FileName + ".txt");
        FileUtilities.createAndWriteTxtFile(createTempTxtPath, this.toString());

        // 2. zipping the temp txt file
        FileUtilities.zip(i_CommitSHA1FileName, createTempTxtPath);

        // 3. remove the tmp txt file
        FileUtilities.deleteFile(createTempTxtPath);
    }

    @Override
    public String toString()
    {
        return
                "" + m_RootFolderSHA1 + ','
                + getParentsList() + ','
                + m_Message + ','
                + DateUtils.FormatToString(m_CommitDate) + ','
                + m_CommitAuthor;
    }

    private String getParentsList()
    {
        String parentsString;

        if (m_ParentsSHA1.size() == 0)
        {
            parentsString = ",";
        }
        else if (m_ParentsSHA1.size() == 1)
        {
            parentsString = m_ParentsSHA1.get(0) + ",";
        }
        else
        {
            parentsString = m_ParentsSHA1.get(0) + "," + m_ParentsSHA1.get(1);
        }

        return parentsString;
    }
    //TODO parent sha 1 will be list - the to string method will be different with extra ',' so dont forget to fix the makeSha1Content method
    public String SHA1() { return DigestUtils.sha1Hex(StringUtilities.makeSHA1Content(this.toString(),3)); }

    public String getRootFolderSHA1() { return m_RootFolderSHA1; }

    public Date getCommitDate() { return m_CommitDate; }

    public void setRootFolderSHA1(String i_RootFolderSHA1) { this.m_RootFolderSHA1 = i_RootFolderSHA1; }

    public String getCommitAuthor() { return m_CommitAuthor; }

    public String getMessage() { return m_Message; }
}
