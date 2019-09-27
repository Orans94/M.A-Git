package engine.xml;

import mypackage.*;

import java.util.Map;

public class XMLManager
{
    private XMLValidator m_XMLValidator = new XMLValidator();
    private XMLMagitMaps m_XMLMagitMaps = new XMLMagitMaps();

    public void loadXMLRepoToMagitMaps(MagitRepository i_XMLRepository)
    {
        m_XMLMagitMaps.clear();
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

    public boolean isXMLRepositoryIsEmpty(MagitRepository i_XMLRepo) { return m_XMLValidator.isXMLRepositoryIsEmpty(i_XMLRepo);}

    public Map<String, MagitSingleFolder> getMagitSingleFolderByID()
    {
        return m_XMLMagitMaps.getMagitSingleFolderByID();
    }

    public boolean areIDsValid(MagitRepository i_XMLRepo)
    {
        return m_XMLValidator.areIDsValid(i_XMLRepo);
    }

    public boolean areFoldersReferencesValid(MagitFolders magitFolders, MagitBlobs magitBlobs)
    {
        return m_XMLValidator.areFoldersReferencesValid(magitFolders, magitBlobs);
    }

    public boolean areCommitsReferencesAreValid(MagitCommits magitCommits, Map<String, MagitSingleFolder> i_magitFolderByID)
    {
        return m_XMLValidator.areCommitsReferencesAreValid(magitCommits,i_magitFolderByID);
    }

    public boolean isHeadReferenceValid(MagitBranches magitBranches, String head)
    {
        return m_XMLValidator.isHeadReferenceValid(magitBranches, head);
    }

    public boolean areBranchesReferencesAreValid(MagitBranches magitBranches, MagitCommits magitCommits)
    {
        return m_XMLValidator.areBranchesReferencesAreValid(magitBranches, magitCommits);
    }

    public boolean isMagitRemoteReferenceValid(MagitRepository i_XmlRepo)
    {
        return m_XMLValidator.isMagitReferenceValid(i_XmlRepo);
    }

    public boolean areBranchesTrackingAfterAreValid(MagitBranches i_MagitBranches)
    {
        return m_XMLValidator.areBranchesTrackingAfterAreValid(i_MagitBranches);
    }
}
