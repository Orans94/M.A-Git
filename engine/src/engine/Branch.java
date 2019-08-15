package engine;

public class Branch
{
    private String m_Name;
    private String m_CommitSHA1 = null;

    public Branch(String i_Name, String i_CommitSHA1)
    {
        m_CommitSHA1 = i_CommitSHA1;
        m_Name = i_Name;
    }

    public String getName()
    {
        return m_Name;
    }

    public String getCommitSHA1() { return m_CommitSHA1; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }
}
