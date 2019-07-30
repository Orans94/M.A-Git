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

    public Commit(String i_RootFolderSHA1, String i_ParentSHA1, String i_Message)
    {
        m_RootFolderSHA1 = i_RootFolderSHA1;
        m_ParentSHA1 = i_ParentSHA1;
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitAuthor = EngineManager.getUserName();
    }

    public void Zip(String i_CommitSHA1FileName)
    {
        // 1. creating temp txt file in objects dir
        Path createTempTxtPath = Magit.getMagitDir().resolve("objects").resolve(i_CommitSHA1FileName + ".txt");
        FileUtils.CreateAndWriteTxtFile(createTempTxtPath, this.toString());

        // 2. zipping the temp txt file
        FileUtils.Zip(i_CommitSHA1FileName, createTempTxtPath);

        // 3. remove the tmp txt file
        FileUtils.deleteFile(createTempTxtPath);
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

    public void setRootFolderSHA1(String i_RootFolderSHA1) { this.m_RootFolderSHA1 = i_RootFolderSHA1; }
}
