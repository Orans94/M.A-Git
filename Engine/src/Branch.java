public class Branch
{
    private String m_Name;
    private String m_CommitSHA1 = null;

    public Branch(String i_Name)
    {
        m_Name = i_Name;
    }

    public String getName()
    {
        return m_Name;
    }

    public void setName(String m_Name)
    {
        this.m_Name = m_Name;
    }
}
