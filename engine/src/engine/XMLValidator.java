package engine;

import mypackage.*;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class XMLValidator
{
    //3.1
    public boolean isXMLFile(Path i_Path)
    {
        return FilenameUtils.getExtension(i_Path.getFileName().toString()).equals("xml");
    }

    private boolean areBlobsIDsValid(MagitBlobs i_Blobs)
    {
        //3.2
        // this method return true if there are no 2 identical ids in blobs

        Set<String> idSet = new TreeSet<>();
        for(MagitBlob blob : i_Blobs.getMagitBlob())
        {
            if(!idSet.contains(blob.getId()))
            {
                idSet.add(blob.getId());
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    private boolean areFoldersIDsValid(MagitFolders i_Folders)
    {
        //3.2
        // this method return true if there are no 2 identical ids in folders

        Set<String> idSet = new TreeSet<>();
        for(MagitSingleFolder folder : i_Folders.getMagitSingleFolder())
        {
            if(!idSet.contains(folder.getId()))
            {
                idSet.add(folder.getId());
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    private boolean areCommitsIDsValid(MagitCommits i_Commits)
    {
        //3.2
        // this method return true if there are no 2 identical ids in commits
        Set<String> idSet = new TreeSet<>();
        for(MagitSingleCommit commit : i_Commits.getMagitSingleCommit())
        {
            if(!idSet.contains(commit.getId()))
            {
                idSet.add(commit.getId());
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    public boolean areFoldersReferencesValid(MagitFolders i_Folders, MagitBlobs i_Blobs)
    {
        //3.3 , 3.4 , 3.5
        // this method return true if all references from all folders items exists and valid
        Set<String> folderIdSet = new TreeSet<>();
        Set<String> blobIdSet = new TreeSet<>();

        for(MagitBlob blob : i_Blobs.getMagitBlob())
        { // filling blobIdSet
            blobIdSet.add(blob.getId());
        }

        for(MagitSingleFolder folder : i_Folders.getMagitSingleFolder())
        { // filling folderIdSet
            folderIdSet.add(folder.getId());
        }

        for(MagitSingleFolder folder : i_Folders.getMagitSingleFolder())
        {
            for(mypackage.Item item : folder.getItems().getItem())
            {
                if(item.getType().equals("folder") && !folderIdSet.contains(item.getId()))
                { // reference doesnt exists
                    return false;
                }

                if(item.getType().equals("blob") && !blobIdSet.contains(item.getId()))
                { // reference does not exist
                    return false;
                }

                if(item.getType().equals("folder") && item.getId().equals(folder.getId()))
                { // self reference
                    return false;
                }
            }
        }

        return true;
    }

    public boolean areCommitsReferencesAreValid(MagitCommits i_Commits, Map<String, MagitSingleFolder> i_MagitSingleFolderByID)
    { // 3.6, 3.7
        for(MagitSingleCommit commit : i_Commits.getMagitSingleCommit())
        {
            if(!i_MagitSingleFolderByID.containsKey(commit.getRootFolder().getId()))
            {
                return false;
            }
            else
            {
                if(!i_MagitSingleFolderByID.get(commit.getRootFolder().getId()).isIsRoot())
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean areBranchesReferencesAreValid(MagitBranches i_Branches, MagitCommits i_Commits)
    { // 3.8
        Set<String> commitIdSet = new TreeSet<>();

        for(MagitSingleCommit commit : i_Commits.getMagitSingleCommit())
        { // filling commitIdSet
            commitIdSet.add(commit.getId());
        }

        for(MagitSingleBranch branch : i_Branches.getMagitSingleBranch())
        {
            if(branch.getPointedCommit() == null)
            {
                return false;
            }
            if(!commitIdSet.contains(branch.getPointedCommit().getId()))
            {
                return false;
            }
        }
        return true;
    }

    public boolean isHeadReferenceValid(MagitBranches i_Branches, String i_Head)
    { // 3.9
        Set<String> branchesSetName = new TreeSet<>();

        for(MagitSingleBranch branch : i_Branches.getMagitSingleBranch())
        {
            branchesSetName.add(branch.getName());
        }

        return branchesSetName.contains(i_Head);
    }

    public boolean areIDsValid(MagitRepository i_XmlRepo)
    {
        return areCommitsIDsValid(i_XmlRepo.getMagitCommits()) && areBlobsIDsValid(i_XmlRepo.getMagitBlobs()) && areFoldersIDsValid(i_XmlRepo.getMagitFolders());
    }

    public boolean isXMLRepositoryIsEmpty(MagitRepository i_XMLRepo)
    {
        boolean isMagitBlobsAreNull, isMagitFoldersAreNull, isMagitCommitsAreNull;

        for (MagitSingleBranch branch : i_XMLRepo.getMagitBranches().getMagitSingleBranch())
        {
            if (!branch.getPointedCommit().getId().equals(""))
            { // all branches not pointed to any commit.
                return false;
            }
        }

        isMagitBlobsAreNull = i_XMLRepo.getMagitBlobs().getMagitBlob().size() == 0;
        isMagitFoldersAreNull = i_XMLRepo.getMagitFolders().getMagitSingleFolder().size() == 0;
        isMagitCommitsAreNull = i_XMLRepo.getMagitCommits().getMagitSingleCommit().size() == 0;

        return isMagitBlobsAreNull && isMagitFoldersAreNull && isMagitCommitsAreNull;
    }

    public boolean isMagitReferenceValid(MagitRepository i_XmlRepo)
    {
        // this method return true if and only if getMagitRemoteReference.getLocation is a Magit repository
        if(i_XmlRepo.getMagitRemoteReference() != null)
        {
            // the element is appearing in xml
            if(i_XmlRepo.getMagitRemoteReference().getLocation() == null)
            {
                return true;
            }
            else
            {
                return FileUtilities.isExists(Paths.get(i_XmlRepo.getMagitRemoteReference().getLocation()).resolve(".magit"));
            }
        }
        else
        {
            // the element isn't appearing in xml
            return true;
        }
    }

    public boolean areBranchesTrackingAfterAreValid(MagitBranches i_MagitBranches)
    {
        for(MagitSingleBranch branch : i_MagitBranches.getMagitSingleBranch())
        {
            if(branch.isTracking())
            {
                if(!isBranchARemoteBranch(i_MagitBranches, branch.getTrackingAfter()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isBranchARemoteBranch(MagitBranches i_MagitBranches, String i_BranchToCheck)
    {
        for(MagitSingleBranch branch : i_MagitBranches.getMagitSingleBranch())
        {
            if(branch.getName().equals(i_BranchToCheck))
            {
                return branch.isIsRemote();
            }
        }

        return true;
    }
}
