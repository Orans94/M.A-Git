package engine;

import mypackage.MagitBlob;
import mypackage.MagitSingleCommit;
import mypackage.MagitSingleFolder;

import java.util.HashMap;
import java.util.Map;

public class XMLMagitMaps
{
    private Map<String, MagitSingleFolder> m_MagitSingleFolderByID = new HashMap<>();
    private Map<String, MagitSingleCommit> m_MagitSingleCommitByID = new HashMap<>();
    private Map<String, MagitBlob> m_MagitSingleBlobByID = new HashMap<>();

    public Map<String, MagitSingleFolder> getMagitSingleFolderByID(){ return m_MagitSingleFolderByID; }

    public Map<String, MagitSingleCommit> getMagitSingleCommitByID() { return m_MagitSingleCommitByID; }

    public Map<String, MagitBlob> getMagitSingleBlobByID(){ return m_MagitSingleBlobByID; }
}
