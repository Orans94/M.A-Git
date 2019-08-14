package engine;

import mypackage.MagitSingleBranch;

import java.nio.file.Path;

public class Branch
{
    private String m_Name;
    private String m_CommitSHA1 = null;

    public Branch(String i_Name, String i_CommitSHA1)
    {
        m_CommitSHA1 = i_CommitSHA1;
        m_Name = i_Name;
    }

    public Branch(MagitSingleBranch i_XMLBranch)
    {
        // this ctor recieves an XML branch and create a regular branch from it.
        m_Name = i_XMLBranch.getName();
        m_CommitSHA1 = i_XMLBranch.getPointedCommit().getId();
    }


    public String getName()
    {
        return m_Name;
    }

    public String getCommitSHA1() { return m_CommitSHA1; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }
}
