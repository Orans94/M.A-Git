package engine;

import mypackage.*;

import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

public class XMLValidator
{
    //3.1
    public boolean isXMLFile(Path i_Path) { return i_Path.endsWith(".xml"); }

    public boolean areBlobsIDsValid(MagitBlobs i_Blobs)
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

    public boolean areFoldersIDsValid(MagitFolders i_Folders)
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

    public boolean areCommitsIDsValid(MagitCommits i_Commits)
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
                {
                    return false;
                }

                if(item.getId().equals(folder.getId()))
                { // self reference
                    return false;
                }
            }
        }

        return true;
    }

    public boolean areCommitsReferencesAreValid(MagitCommits i_Commits, MagitFolders i_Folders)
    { // 3.6, 3.7

        Set<String> folderIdSet = new TreeSet<>();

        for(MagitSingleFolder folder : i_Folders.getMagitSingleFolder())
        { // filling folderIdSet
            folderIdSet.add(folder.getId());
        }

        for(MagitSingleCommit commit : i_Commits.getMagitSingleCommit())
        {
            if(!folderIdSet.contains(commit.getRootFolder().getId()))
            {
                return false;
            }
            else
            {
                if(!i_Folders.getMagitSingleFolder().get(Integer.parseInt(commit.getRootFolder().getId())).isIsRoot())
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
}
