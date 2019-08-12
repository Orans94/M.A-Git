package engine;

import mypackage.MagitSingleCommit;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.util.Date;

public class Commit
{
    private String m_RootFolderSHA1;
    private String m_ParentSHA1;
    private String m_Message;
    private Date m_CommitDate;
    private String m_CommitAuthor;

    public String getParentSHA1() {return m_ParentSHA1;}

    public void setParentSHA1(String i_ParentSHA1) { m_ParentSHA1 = i_ParentSHA1; }

    public Commit(String i_RootFolderSHA1, String i_ParentSHA1, String i_Message)
    {
        // ctor that takes the current time and current user name
        m_RootFolderSHA1 = i_RootFolderSHA1;
        m_ParentSHA1 = i_ParentSHA1;
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitAuthor = EngineManager.getUserName();
    }

    public Commit (String i_RootFolderSHA1, String i_ParentSHA1, String i_Message, Date i_CommitDate, String i_Author)
    {
        //ctor that gets all his members as params
        m_RootFolderSHA1 = i_RootFolderSHA1;
        m_ParentSHA1 = i_ParentSHA1;
        m_Message = i_Message;
        m_CommitDate = i_CommitDate;
        m_CommitAuthor = i_Author;
    }

    public Commit(MagitSingleCommit i_XMLCommit)
    {
        // ctor that gets an XML commit and creates a regular commit from it.
        m_RootFolderSHA1 = i_XMLCommit.getRootFolder().getId();
        m_ParentSHA1 = i_XMLCommit.getPrecedingCommits().getPrecedingCommit().get(0).getId();// TODO - could have 2 parents - fix that.
        m_Message = i_XMLCommit.getMessage();
        m_CommitDate = DateUtils.FormatToDate(i_XMLCommit.getDateOfCreation());
        m_CommitAuthor = i_XMLCommit.getAuthor();
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
                + m_ParentSHA1 + ','
                + m_Message + ','
                + DateUtils.FormatToString(m_CommitDate) + ','
                + m_CommitAuthor;
    }

    public String SHA1() { return DigestUtils.sha1Hex(StringUtilities.makeSHA1Content(this.toString())); }

    public String getRootFolderSHA1() { return m_RootFolderSHA1; }

    public Date getCommitDate() { return m_CommitDate; }

    public void setRootFolderSHA1(String i_RootFolderSHA1) { this.m_RootFolderSHA1 = i_RootFolderSHA1; }

    public String getCommitAuthor() { return m_CommitAuthor; }

    public String getMessage() { return m_Message; }
}
