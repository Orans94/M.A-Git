package engine;

import mypackage.MagitBlob;
import mypackage.MagitRepository;
import mypackage.MagitSingleCommit;
import mypackage.MagitSingleFolder;

import java.util.HashMap;
import java.util.Map;

public class XMLManager
{
    private XMLValidator m_XMLValidator = new XMLValidator();
    private XMLMagitMaps m_XMLMagitMaps = new XMLMagitMaps();

    public void loadXMLRepoToMagitMaps(MagitRepository i_XMLRepository)
    {
        for(MagitSingleCommit commit : i_XMLRepository.getMagitCommits().getMagitSingleCommit())
        {
            m_XMLMagitMaps.getMagitSingleCommitByID().put(commit.getId(), commit);
        }

        for(MagitSingleFolder folder : i_XMLRepository.getMagitFolders().getMagitSingleFolder())
        {
            m_XMLMagitMaps.getMagitSingleFolderByID().put(folder.getId(), folder);
        }

        for(MagitBlob blob : i_XMLRepository.getMagitBlobs().getMagitBlob())
        {
            m_XMLMagitMaps.getMagitSingleBlobByID().put(blob.getId(), blob);
        }
    }

    public XMLValidator getXMLValidator() { return m_XMLValidator; }

    public XMLMagitMaps getXMLMagitMaps() { return m_XMLMagitMaps; }
}
