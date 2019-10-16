package engine.dataobjects;

import engine.utils.DateUtils;

import java.nio.file.Path;
import java.util.Date;

public class PullRequest
{
    private String m_LRName;
    private String m_RRName;
    private String m_RRUsername;
    private String m_LRUsername;
    private String m_BaseBranchName;
    private String m_TargetBranchName;
    private String m_Message;
    private ePullRequestState m_Status;
    private Date m_DateOfCreation;
    private int m_RequestID;

    public PullRequest(String i_LRName, String i_RRName, String i_RRUsername, String i_LRUsername, String i_TargetBranchName, String i_BaseBranchName, String i_Message)
    {
        m_LRName = i_LRName;
        m_RRName = i_RRName;
        m_RRUsername = i_RRUsername;
        m_LRUsername = i_LRUsername;
        m_TargetBranchName = i_TargetBranchName;
        m_BaseBranchName = i_BaseBranchName;
        m_Message = i_Message;
        m_DateOfCreation = DateUtils.FormatToDate(new Date().toString());
        m_Status = ePullRequestState.Open;
    }


    public String getMessage() { return m_Message; }

    public void setMessage(String i_Message) { m_Message = i_Message; }

    public String getRRUsername() { return m_RRUsername; }

    public void setRRUsername(String i_RRUsername) { m_RRUsername = i_RRUsername; }

    public String getLRUsername() { return m_LRUsername; }

    public void setLRUsername(String i_LRUsername) { m_LRUsername = i_LRUsername; }

    public String getTargetBranchName() { return m_TargetBranchName; }

    public void setTargetBranchName(String i_TargetBranchName) { m_TargetBranchName = i_TargetBranchName; }

    public String getBaseBranchName() { return m_BaseBranchName; }

    public void setBaseBranchName(String i_BaseBranchName) { m_BaseBranchName = i_BaseBranchName; }

    public ePullRequestState getStatus() { return m_Status; }

    public void setStatus(ePullRequestState i_Status) { m_Status = i_Status; }

    public Date getDateOfRequest() { return m_DateOfCreation; }

    public void setDateOfRequest(Date i_DateOfRequest) { m_DateOfCreation = i_DateOfRequest; }

    public int getID() { return m_RequestID; }

    public void setID(int i_ID) { m_RequestID = i_ID; }

    public String getLRName() { return m_LRName; }

    public void setLRName(String i_LRName) { m_LRName = i_LRName; }

    public String getRRName() { return m_RRName; }

    public void setRRName(String i_RRName) { m_RRName = i_RRName; }

}
