package engine;

import static engine.StringFinals.EMPTY_STRING;

public class Branch
{
    private String m_Name;
    private String m_CommitSHA1 = null;
    private boolean m_IsRemote = false;
    private boolean m_IsTracking = false;
    private String m_TrackingAfter = null;

    public Branch(String i_Name, String i_CommitSHA1, boolean i_IsRemote, boolean i_IsTracking, String i_TrackingAfter)
    {
        m_Name = i_Name;
        m_CommitSHA1 = i_CommitSHA1;
        m_IsRemote = i_IsRemote;
        m_IsTracking = i_IsTracking;
        m_TrackingAfter = i_TrackingAfter;
    }

    public Branch(String i_Name, String i_CommitSHA1)
    {
        m_CommitSHA1 = i_CommitSHA1;
        m_Name = i_Name;
    }



    public String getTrackingAfter()
    {
        return m_TrackingAfter;
    }

    @Override
    public String toString()
    {
        return  m_CommitSHA1 + "," + m_IsRemote + "," + m_IsTracking + "," + m_TrackingAfter;
    }

    public void setTrackingAfter(String i_TrackingAfter)
    {
        this.m_TrackingAfter = i_TrackingAfter;
    }

    public boolean getIsTracking()
    {
        return m_IsTracking;
    }

    public void setIsTracking(boolean i_Tracking)
    {
        m_IsTracking = i_Tracking;
    }

    public boolean getIsRemote() { return m_IsRemote; }

    public void setIsRemote(boolean i_IsRemote) { m_IsRemote = i_IsRemote; }

    public void setName(String i_Name) { this.m_Name = i_Name; }

    public String getName()
    {
        return m_Name;
    }

    public String getCommitSHA1() { return m_CommitSHA1; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }
}
