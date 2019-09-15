package engine;

public class Branch
{
    private String m_Name;
    private String m_CommitSHA1 = null;
    private boolean isRemote = false;
    private boolean isTracking = false;
    private String m_TrackingAfter = null;

    public Branch(String i_Name, String i_CommitSHA1)
    {
        m_CommitSHA1 = i_CommitSHA1;
        m_Name = i_Name;
    }

    public String getTrackingAfter()
    {
        return m_TrackingAfter;
    }

    public void setTrackingAfter(String i_TrackingAfter)
    {
        this.m_TrackingAfter = i_TrackingAfter;
    }

    public boolean getIsTracking()
    {
        return isTracking;
    }

    public void setIsTracking(boolean i_Tracking)
    {
        isTracking = i_Tracking;
    }

    public boolean getIsRemote() { return isRemote; }

    public void setIsRemote(boolean i_IsRemote) { isRemote = i_IsRemote; }

    public void setName(String i_Name) { this.m_Name = i_Name; }

    public String getName()
    {
        return m_Name;
    }

    public String getCommitSHA1() { return m_CommitSHA1; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }
}
