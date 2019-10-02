package engine.dataobjects;

import engine.utils.DateUtils;

import java.nio.file.Path;
import java.util.Date;

public class PullRequest
{
    private Path m_LRPath;
    private Path m_RRPath;
    private String m_RRUserName;
    private String m_LRUserName;
    private String m_TargetBranchName;
    private String m_BaseBranchName;
    private String m_Status;
    private String m_Message;
    private Date m_DateOfRequest;
    private int m_ID;

    public PullRequest(Path i_LRPath, Path i_RRPath, String i_RRUserName, String i_LRUserName, String i_TargetBranchName, String i_BaseBranchName, String i_Message)
    {
        m_LRPath = i_LRPath;
        m_RRPath = i_RRPath;
        m_RRUserName = i_RRUserName;
        m_LRUserName = i_LRUserName;
        m_TargetBranchName = i_TargetBranchName;
        m_BaseBranchName = i_BaseBranchName;
        m_Message = i_Message;
        m_DateOfRequest = DateUtils.FormatToDate(new Date().toString());
        m_Status = "Waiting";
    }


    public String getMessage() { return m_Message; }

    public void setMessage(String i_Message) { m_Message = i_Message; }

    public String getRRUserName() { return m_RRUserName; }

    public void setRRUserName(String i_RRUserName) { m_RRUserName = i_RRUserName; }

    public String getLRUserName() { return m_LRUserName; }

    public void setLRUserName(String i_LRUserName) { m_LRUserName = i_LRUserName; }

    public String getTargetBranchName() { return m_TargetBranchName; }

    public void setTargetBranchName(String i_TargetBranchName) { m_TargetBranchName = i_TargetBranchName; }

    public String getBaseBranchName() { return m_BaseBranchName; }

    public void setBaseBranchName(String i_BaseBranchName) { m_BaseBranchName = i_BaseBranchName; }

    public String getStatus() { return m_Status; }

    public void setStatus(String i_Status) { m_Status = i_Status; }

    public Date getDateOfRequest() { return m_DateOfRequest; }

    public void setDateOfRequest(Date i_DateOfRequest) { m_DateOfRequest = i_DateOfRequest; }

    public int getID() { return m_ID; }

    public void setID(int i_ID) { m_ID = i_ID; }

    public Path getLRPath() { return m_LRPath; }

    public void setLRPath(Path i_LRPath) { m_LRPath = i_LRPath; }

    public Path getRRPath() { return m_RRPath; }

    public void setRRPath(Path i_RRPath) { m_RRPath = i_RRPath; }

}
