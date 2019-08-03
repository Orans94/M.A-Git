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
    private OpenChanges m_OpenChanges;

    public OpenChanges getOpenChanges() { return m_OpenChanges; }

    public void setOpenChanges(OpenChanges i_OpenChanges) { m_OpenChanges = i_OpenChanges; }

    public String getParentSHA1() {return m_ParentSHA1;}

    public void setParentSHA1(String i_ParentSHA1) { m_ParentSHA1 = i_ParentSHA1; }

    public Commit(String i_RootFolderSHA1, String i_ParentSHA1, String i_Message)
    {
        m_RootFolderSHA1 = i_RootFolderSHA1;
        m_ParentSHA1 = i_ParentSHA1;
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitAuthor = EngineManager.getUserName();
        m_OpenChanges = new OpenChanges();
    }

    public void Zip(String i_CommitSHA1FileName)
    {
        // 1. creating temp txt file in objects dir
        Path createTempTxtPath = Magit.getMagitDir().resolve("objects").resolve(i_CommitSHA1FileName + ".txt");
        FileUtilities.CreateAndWriteTxtFile(createTempTxtPath, this.toString());

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

    public void setRootFolderSHA1(String i_RootFolderSHA1) { this.m_RootFolderSHA1 = i_RootFolderSHA1; }
}
