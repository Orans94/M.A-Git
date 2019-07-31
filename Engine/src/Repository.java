import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public Magit getMagit()
    {
        return m_Magit;
    }

    public void setMagit(Magit i_Magit)
    {
        this.m_Magit = i_Magit;
    }

    public void Commit(String i_CommitMessage) throws IOException
    {// TODO handle exception
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                if (dir.getFileName().toString().equals(".magit"))
                {
                    return FileVisitResult.SKIP_SUBTREE;
                } else
                {
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                //1. Sha-1
                String blobContent = new String(Files.readAllBytes(file));
                Blob blob = new Blob(blobContent);
                String blobSha1 = blob.SHA1();

                //2. Zip & save the zip in objects directory
                blob.Zip(blobSha1, file);

                //3. push blob to m_Nodes
                m_WorkingCopy.getNodeMaps().get.put(blobSha1, blob);

                //4. append my info to m_ChildrenInformation
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
                System.out.println(dir);

                //1. L <- Check how many kids I have.
                int numOfChildren = FileUtils.getNumberOfSubNodes(dir);
                numOfChildren += m_WorkingCopy.getWorkingCopyDir() == dir ? -1 : 0;

                //2. add the item information of my children to my content
                String folderContent = generateFolderContent(numOfChildren);

                //3.create a folder object from the details and put it in m_Nodes
                Folder folder = new Folder(folderContent);

                //4.add content details to item list of folder
                folder.createItemListFromContent();

                //add folder to map of nodes
                String folderSHA1 = m_WorkingCopy.addFolderToMap(folder);

                //5. update children information
                updateChildrenInformation(dir, numOfChildren, folder, folderSHA1);

                //6. zip the folder and save me in objects dir
                folder.Zip(folderSHA1, dir);

                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);
        String rootFolderItemString = m_ChildrenInformation.get(0);
        String rootFolderSha1 = Item.getSha1FromItemString(rootFolderItemString);
        m_ChildrenInformation.clear();

        m_Magit.handleNewCommit(rootFolderSha1
                , m_Magit.getHead().getActiveBranch().getCommitSHA1()
                , i_CommitMessage);
    }

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
        folderContent = folderContent.substring(0, folderContent.length() - 2);

        return folderContent;
    }

    private void handleNodeByStatus(WalkFileSystemResult i_Result, String i_SHA1,
                                    Node i_Node, Path i_Path, NodeMaps i_NodeMaps)
    {
        if (!i_NodeMaps.getSHA1ByPath().containsKey(i_Path))
        {// New File!
            i_Result.getOpenChanges().getNewNodes().add(i_Path);
            i_Result.getSHA1ByPath().put(i_Path, i_SHA1);
            i_Result.getNodeBySHA1().put(i_SHA1, i_Node);
        }
        else
        { // the path exists in the WC
            if(i_SHA1.equals(i_NodeMaps.getSHA1ByPath().get(i_Path)))
            { // the file has not been modified
                i_Result.getUnchangedNodes().put(i_Path, i_SHA1);
            }
            else
            {// the file has been modified - delete from temp and add to new maps
                i_Result.getOpenChanges().getModifiedNodes().add(i_Path);
                i_Result.getSHA1ByPath().put(i_Path, i_SHA1);
                i_Result.getNodeBySHA1().put(i_SHA1, i_Node);
            }

            i_NodeMaps.getNodeBySHA1().remove(i_SHA1);
            i_NodeMaps.getSHA1ByPath().remove(i_Path);
        }
    }

    public void Commit1(String i_CommitMessage) throws IOException
    {// TODO handle exception
        NodeMaps tempNodeMaps = new NodeMaps(MapUtilities.deepClone(m_WorkingCopy.getNodeMaps().getNodeBySHA1()),MapUtilities.deepClone(m_WorkingCopy.getNodeMaps().getSHA1ByPath()));

        WalkFileSystemResult result = new WalkFileSystemResult();
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>()
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
                //1. Sha-1
                String blobContent = new String(Files.readAllBytes(file));
                Blob blob = new Blob(blobContent);
                String blobSha1 = blob.SHA1();

                if (!tempSHA1ByPath.containsKey(file))
                {// New File!
                    result.getOpenChanges().getNewNodes().add(file);
                    result.getSHA1ByPath().put(file, blobSha1);
                    result.getNodeBySHA1().put(blobSha1, blob);
                }
                else
                { // the path exists in the WC
                    if(blobSha1.equals(tempSHA1ByPath.get(file)))
                    { // the file has not been modified
                        result.getUnchangedNodes().put(file, blobSha1);
                    }
                    else
                    {// the file has been modified - delete from temp and add to new maps
                        result.getOpenChanges().getModifiedNodes().add(file);
                        result.getSHA1ByPath().put(file, blobSha1);
                        result.getNodeBySHA1().put(blobSha1, blob);
                    }

                    tempNodeBySHA1.remove(blobSha1);
                    tempSHA1ByPath.remove(file);
                }

                //4. append my info to m_ChildrenInformation
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
                int numOfChildren = FileUtils.getNumberOfSubNodes(dir);
                numOfChildren += m_WorkingCopy.getWorkingCopyDir() == dir ? -1 : 0;

                //2. add the item information of my children to my content
                String folderContent = generateFolderContent(numOfChildren);

                //3.create a folder object from the details and put it in m_Nodes
                Folder folder = new Folder(folderContent);

                //4.add content details to item list of folder
                folder.createItemListFromContent();

                String folderSHA1 = folder.SHA1();

                //5. update children information
                updateChildrenInformation(dir, numOfChildren, folder, folderSHA1);

                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);
        //TODO zip all needed files and merge unchanged to newmaps.
        String rootFolderItemString = m_ChildrenInformation.get(0);
        String rootFolderSha1 = Item.getSha1FromItemString(rootFolderItemString);
        m_ChildrenInformation.clear();

        m_Magit.handleNewCommit(rootFolderSha1
                , m_Magit.getHead().getActiveBranch().getCommitSHA1()
                , i_CommitMessage);
    }
}
