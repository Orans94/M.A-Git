import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class Repository
{
    private WC m_WorkingCopy;
    private Magit m_Magit;
    private static List<String> m_ChildrenInformation;

    public Repository(Path i_RepPath) throws IOException // TODO catch
    {
        m_ChildrenInformation = new LinkedList<>();
        createRepositoryDirectories(i_RepPath);
        m_WorkingCopy = new WC(i_RepPath);
        m_Magit = new Magit(i_RepPath.resolve(".magit"));
    }

    private void createRepositoryDirectories(Path i_RepPath) throws IOException //TODO catch exception
    {
        Files.createDirectory(i_RepPath.resolve(".magit"));
        Files.createDirectory(i_RepPath.resolve(".magit").resolve("branches"));
        Files.createDirectory(i_RepPath.resolve(".magit").resolve("objects"));
    }

    public WC getWorkingCopy()
    {
        return m_WorkingCopy;
    }

    public void setWorkingCopy(WC i_WorkingCopy)
    {
        this.m_WorkingCopy = i_WorkingCopy;
    }

    public Magit getMagit() { return m_Magit; }

    public void setMagit(Magit i_Magit) { this.m_Magit = i_Magit; }

    private void updateChildrenInformation(Path i_Dir, int i_NumOfChildren, Folder i_Folder, String i_FolderSHA1)
    {
        m_ChildrenInformation = m_ChildrenInformation.stream()
                .limit(m_ChildrenInformation.size() - i_NumOfChildren)
                .collect(Collectors.toList());

        //make an item string from my content and add it to m_ChildrenInformation
        String itemString = i_Folder.generateStringInformation(i_FolderSHA1, i_Dir.toFile().getName());
        m_ChildrenInformation.add(itemString);
    }


    private String generateFolderContent(int i_NumOfChildren)
    {
        List<String> folderContentList = m_ChildrenInformation.stream()
                .skip(m_ChildrenInformation.size() - i_NumOfChildren)
                .collect(Collectors.toList());
        String folderContent = "";
        for (String s : folderContentList)
        {
            folderContent = folderContent.concat(s).concat(System.lineSeparator());
        }

        //delete last line from the string
        //TODO handle situation => folder is empty and expection has been thrown
        folderContent = folderContent.substring(0, folderContent.length() - 2);

        return folderContent;
    }

    private void handleNodeByStatus(WalkFileSystemResult i_Result, String i_SHA1,
                                    Node i_Node, Path i_Path, NodeMaps i_TempNodeMaps)
    {
        if (!i_TempNodeMaps.getSHA1ByPath().containsKey(i_Path))
        {// New File!
            i_Result.getOpenChanges().getNewNodes().add(i_Path);
            i_Result.getNewLoadedNodes().getSHA1ByPath().put(i_Path, i_SHA1);
            i_Result.getNewLoadedNodes().getNodeBySHA1().put(i_SHA1, i_Node);
        }
        else
        { // the path exists in the WC
            if(i_SHA1.equals(i_TempNodeMaps.getSHA1ByPath().get(i_Path)))
            { // the file has not been modified
                i_Result.getUnchangedNodes().getSHA1ByPath().put(i_Path, i_SHA1);
                i_Result.getUnchangedNodes().getNodeBySHA1().put(i_SHA1, i_TempNodeMaps.getNodeBySHA1().get(i_SHA1));
            }
            else
            {// the file has been modified - delete from temp and add to new maps
                i_Result.getOpenChanges().getModifiedNodes().add(i_Path);
                i_Result.getNewLoadedNodes().getSHA1ByPath().put(i_Path, i_SHA1);
                i_Result.getNewLoadedNodes().getNodeBySHA1().put(i_SHA1, i_Node);
            }

            i_TempNodeMaps.getNodeBySHA1().remove(i_SHA1);
            i_TempNodeMaps.getSHA1ByPath().remove(i_Path);
        }
    }

    public boolean commit(String i_CommitMessage) throws IOException
    {// TODO handle exception
        //TODO if a folder is empty, dont make a folder obj from it and lo lehityahes
        NodeMaps tempNodeMaps = new NodeMaps(m_WorkingCopy.getNodeMaps());
        WalkFileSystemResult result = new WalkFileSystemResult();

        FileVisitor<Path> fileVisitor = getOpenChangesFileVisitor(tempNodeMaps, result);
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);
        zipNewAndModifiedNodes(result);
        updateDeletedNodeList(tempNodeMaps, result);
        m_WorkingCopy.setNodeMaps(result.getNewLoadedNodes());
        String rootFolderSha1 = getRootFolderSHA1();
        m_ChildrenInformation.clear();
        boolean isWCDirty = isWCDirty(rootFolderSha1);
        if(isWCDirty)
        {
            manageDirtyWC(i_CommitMessage, result, rootFolderSha1);
        }

        return isWCDirty;
    }

    private void updateDeletedNodeList(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_WalkFileSystemResult) {
        addUnchangedNodesToNewNodeMaps(i_TempNodeMaps, i_WalkFileSystemResult);
        i_WalkFileSystemResult.getUnchangedNodes().clear();
        addDeletedNodesToDeletedList(i_TempNodeMaps, i_WalkFileSystemResult);
    }

    private void manageDirtyWC(String i_CommitMessage, WalkFileSystemResult i_WalkFileSystemResult, String i_RootFolderSha1) {
        String commitSHA1 = m_Magit.handleNewCommit(i_RootFolderSha1
                , m_Magit.getHead().getActiveBranch().getCommitSHA1()
                , i_CommitMessage);
        configureNewCommit(i_WalkFileSystemResult, commitSHA1);
        m_WorkingCopy.setCommitSHA1(commitSHA1);
    }

    private FileVisitor<Path> getOpenChangesFileVisitor(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_WalkFileSystemResult) {
        return new FileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
                {
                    if (dir.getFileName().toString().equals(".magit"))
                    {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    else
                    {
                        return FileVisitResult.CONTINUE;
                    }
                }



                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    // read txt file content , make a blob from it and SHA1 it
                    String blobContent = new String(Files.readAllBytes(file));
                    Blob blob = new Blob(blobContent);
                    String blobSha1 = blob.SHA1();

                    // filter the blob by his status - new modified or deleted
                    handleNodeByStatus(i_WalkFileSystemResult, blobSha1, blob, file, i_TempNodeMaps);

                    // append my info to m_ChildrenInformation
                    m_ChildrenInformation.add(blob.generateStringInformation(blobSha1, file.toFile().getName()));

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
                {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                { //TODO handle exception

                    //1. L <- Check how many kids I have.
                    int numOfChildren = FileUtilities.getNumberOfSubNodes(dir);
                    numOfChildren += m_WorkingCopy.getWorkingCopyDir() == dir ? -1 : 0;
                    if(numOfChildren != 0)
                    {
                        //2. add the item information of my children to my content
                        String folderContent = generateFolderContent(numOfChildren);

                        //3.create a folder object from the details and put it in m_Nodes
                        Folder folder = new Folder(folderContent);

                        //4.add content details to item list of folder
                        folder.createItemListFromContent();

                        //5. SHA1 the folder
                        String folderSHA1 = folder.SHA1();

                        //6. filter the node by status - new modified or deleted.
                        handleNodeByStatus(i_WalkFileSystemResult, folderSHA1, folder, dir, i_TempNodeMaps);

                        //7. update children information
                        updateChildrenInformation(dir, numOfChildren, folder, folderSHA1);
                    }

                    return FileVisitResult.CONTINUE;
                }
            };
    }

    private void configureNewCommit(WalkFileSystemResult i_Result, String i_CommitSHA1) {
        m_Magit.getCommits().get(i_CommitSHA1).setParentSHA1(m_WorkingCopy.getCommitSHA1());
        m_Magit.getCommits().get(i_CommitSHA1).setOpenChanges(i_Result.getOpenChanges());
    }

    private boolean isWCDirty(String i_RootFolderSha1) {
        return m_WorkingCopy.getCommitSHA1().equals("") ||
                !i_RootFolderSha1.equals(m_Magit.getCommits().get(m_WorkingCopy.getCommitSHA1()).getRootFolderSHA1());
    }


    private String getRootFolderSHA1()
    {
        String rootFolderItemString = m_ChildrenInformation.get(0);
        return Item.getSha1FromItemString(rootFolderItemString);
    }

    private void addDeletedNodesToDeletedList(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_Result)
    {
        for(Map.Entry<Path,String> entry : i_TempNodeMaps.getSHA1ByPath().entrySet())
        {
            i_Result.getOpenChanges().getDeletedNodes().add(entry.getKey());
        }
    }

    private void addUnchangedNodesToNewNodeMaps(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_Result) {
        for(Map.Entry<Path,String> entry : i_Result.getUnchangedNodes().getSHA1ByPath().entrySet())
        {
            i_Result.getNewLoadedNodes().getSHA1ByPath().put(entry.getKey(),entry.getValue());
            i_Result.getNewLoadedNodes().getNodeBySHA1().put(entry.getValue(), i_TempNodeMaps.getNodeBySHA1().get(entry.getValue()));
        }
    }

    private void zipNewAndModifiedNodes(WalkFileSystemResult i_Result) {
        for(Map.Entry<Path,String> entry: i_Result.getNewLoadedNodes().getSHA1ByPath().entrySet())
        {
            i_Result.getNewLoadedNodes().getNodeBySHA1().get(entry.getValue()).Zip(entry.getValue(), entry.getKey());
        }
    }

    public OpenChanges getOpenChanges() throws IOException
    {
        NodeMaps tempNodeMaps = new NodeMaps(m_WorkingCopy.getNodeMaps());
        WalkFileSystemResult result = new WalkFileSystemResult();

        FileVisitor<Path> fileVisitor = getOpenChangesFileVisitor(tempNodeMaps, result);
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);

        m_ChildrenInformation.clear();
        return result.getOpenChanges();
    }

    public void createNewBranch(String i_BranchName)
    {
        Branch activeBranch = m_Magit.getHead().getActiveBranch();
        Branch newBranch = new Branch(Magit.getMagitDir(), i_BranchName, activeBranch.getCommitSHA1());
        m_Magit.getBranches().put(i_BranchName, newBranch);
    }

    public void checkout(String i_BranchName) throws IOException
    {
        String commitSHA1 = m_Magit.getBranches().get(i_BranchName).getCommitSHA1();
        String rootFolderSHA1 = m_Magit.getCommits().get(commitSHA1).getRootFolderSHA1();
        // clear repository- clear file system and clear nodes map
        clear();

        // change head to point on new checkedout branch
        m_Magit.getHead().setActiveBranch(m_Magit.getBranches().get(i_BranchName));

        m_WorkingCopy.getNodeMaps().getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), rootFolderSHA1);
        checkoutFileVisit(m_WorkingCopy.getWorkingCopyDir());
    }

    private void checkoutFileVisit(Path startPath) throws IOException {
        String myBlobContent;

        // getting the SHA1 of the folder by it path
        String zipName = m_WorkingCopy.getNodeMaps().getSHA1ByPath().get(startPath);

        // creating folder
        String myDirContent = FileUtilities.getTxtFromZip(zipName.concat(".zip"), zipName.concat(".txt"));
        Folder folder = new Folder(myDirContent);

        // add to map
        m_WorkingCopy.getNodeMaps().getNodeBySHA1().put(zipName, folder);

        folder.createItemListFromContent();

        for (Item item: folder.getItems())
        {
            m_WorkingCopy.getNodeMaps().getSHA1ByPath().put(startPath.resolve(item.getName()),item.getSHA1());
            if (item.getType().equals("Folder"))
            {
                Files.createDirectory(startPath.resolve(item.getName()));
                checkoutFileVisit(startPath.resolve(item.getName()));
            }
            else
            {
                // getting the blob SHA1 by it path
                zipName = m_WorkingCopy.getNodeMaps().getSHA1ByPath().get(startPath.resolve(item.getName()));
                myBlobContent = FileUtilities.getTxtFromZip(zipName.concat(".zip"),item.getName());
                FileUtilities.CreateAndWriteTxtFile(startPath.resolve(item.getName()),myBlobContent);
                Blob blob = new Blob(myBlobContent);
                m_WorkingCopy.getNodeMaps().getNodeBySHA1().put(zipName, blob);
            }
        }
    }

    private void clear() throws IOException
    {

        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), getRemoveFileVisitor());
        m_WorkingCopy.clear();

    }

    private SimpleFileVisitor<Path> getRemoveFileVisitor() {
        return new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
                {
                    if (dir.getFileName().toString().equals(".magit"))
                    {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    else
                    {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Files.delete(file);
                    System.out.println(file+" has been deleted");

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    if (exc != null) {
                        throw exc;
                    }
                    if (!dir.equals(m_WorkingCopy.getWorkingCopyDir()))
                    {
                        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                            if(dirStream.iterator().hasNext())
                            {
                                FileUtils.cleanDirectory(dir.toFile());
                            }
                        }
                        Files.delete(dir);
                        System.out.println(dir+" has been deleted");
                    }
                    return FileVisitResult.CONTINUE;
                }
            };
    }
}
