package engine.core;

import engine.branches.Branch;
import engine.dataobjects.MergeNodeMaps;
import engine.dataobjects.NodeMaps;
import engine.dataobjects.OpenChanges;
import engine.dataobjects.WalkFileSystemResult;
import engine.dataobjects.eConflictCases;
import engine.objects.Blob;
import engine.objects.Commit;
import engine.objects.Folder;
import engine.objects.Node;
import engine.utils.DateUtils;
import engine.utils.FileUtilities;
import engine.utils.StringUtilities;
import engine.xml.XMLMagitMaps;
import mypackage.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static engine.utils.StringFinals.EMPTY_STRING;

public class Repository
{
    private transient String m_Username;
    private static final String REPOSITORY_REMOTE_FILE = "Remote.txt";
    private static final String REPOSITORY_NAME_FILE = "RepositoryName.txt";
    private WC m_WorkingCopy;
    private Magit m_Magit;
    private transient List<String> m_ChildrenInformation = new LinkedList<>();
    private String m_Name;
    private Path m_RemoteRepositoryPath = null;

    public Repository(Path i_RepPath, String i_Name) throws IOException
    {
        createRepositoryDirectories(i_RepPath);
        m_Name = i_Name;
        m_WorkingCopy = new WC(i_RepPath);
        m_Magit = new Magit(i_RepPath.resolve(".magit"));
        createRepositoryNameFile();
        writeRemoteRepositoryPathToFileSystem();
    }

    public Repository(Path i_Source, Path i_Dest, String i_Name) throws IOException, ParseException
    {
        // clone c'tor
        Path destMagitPath = i_Dest.resolve(".magit");
        createRepositoryDirectories(i_Dest);
        FileUtilities.copyDirectory(i_Source.resolve(".magit"), destMagitPath);
        m_Name = i_Name;
        m_WorkingCopy = new WC(i_Dest);
        m_Magit = new Magit();
        m_Magit.setMagitDir(destMagitPath);
        m_RemoteRepositoryPath = i_Source;
        FileUtilities.deleteFile(destMagitPath.resolve(REPOSITORY_REMOTE_FILE));
        writeRemoteRepositoryPathToFileSystem();
        m_Magit.load(m_Magit.getMagitDir());
        moveBranchesToRemoteBranchesDirectory();
        configureRBs();
        String activeBranchRemoteName = getRemoteActiveBranchName();
        String remoteRepositoryName = getRemoteRepositoryName();
        m_Magit.createNewRTB(remoteRepositoryName, activeBranchRemoteName);
        m_Magit.setActiveBranch(activeBranchRemoteName, true);
    }

    public void setUsername(String i_Username) { m_Username = i_Username; }

    public String getUsername() { return m_Username; }

    public void writeRemoteRepositoryPathToFileSystem() throws IOException
    {
        String fileContent = m_RemoteRepositoryPath == null ? EMPTY_STRING : m_RemoteRepositoryPath.toString();
        FileUtilities.createAndWriteTxtFile(m_Magit.getMagitDir().resolve("Remote.txt"), fileContent);
    }

    private String getRemoteActiveBranchName() throws IOException
    {
        Path headRemotePath = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve("HEAD.txt");

        return new String(Files.readAllBytes(headRemotePath));
    }

    public void deleteRepositoryNameFile() throws IOException
    {
        Path repositoryNameFilePath = m_Magit.getMagitDir().resolve("RepositoryName.txt");
        if (FileUtilities.isExists(repositoryNameFilePath))
        {
            FileUtilities.deleteFile(repositoryNameFilePath);
        }
    }

    public void configureRBs() throws IOException
    {
        // this method is changing the remote branches names (concating '\' to the name with the RR namename)
        // it also
        String RRName = getRemoteRepositoryName();
        Path destination = m_Magit.getMagitDir().resolve("branches").resolve(RRName);
        String branchName, chainedBranchName;
        try (Stream<Path> walk = Files.walk(destination))
        {
            List<Path> result = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            for (Path path : result)
            {
                branchName = FilenameUtils.removeExtension(path.toFile().getName());
                m_Magit.setIsRemoteBranch(branchName, true);
                chainedBranchName = m_Magit.changeBranchName(RRName, branchName);
                FileUtilities.modifyTxtFile(path, m_Magit.getBranches().get(chainedBranchName).toString());
            }
        }
    }

    public void moveBranchesToRemoteBranchesDirectory() throws IOException
    {
        String RRName = getRemoteRepositoryName();
        Path destination = m_Magit.getMagitDir().resolve("branches").resolve(RRName);
        createRemoteBranchesDirectory(RRName);
        try (Stream<Path> walk = Files.walk(m_Magit.getMagitDir().resolve("branches")))
        {
            List<Path> result = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            for (Path path : result)
            {
                if (!path.getFileName().toString().equals("HEAD.txt"))
                {
                    FileUtilities.moveFile(path, destination.resolve(path.getFileName()));
                }
            }
        }
    }

    private void createRemoteBranchesDirectory(String i_RRName) throws IOException
    {
        Files.createDirectories(m_Magit.getMagitDir().resolve("branches").resolve(i_RRName));
    }

    private String getRemoteRepositoryName() throws IOException
    {
        if (m_RemoteRepositoryPath != null && !m_RemoteRepositoryPath.toString().equals(""))
        {
            Path RRNameFile = m_RemoteRepositoryPath.resolve(".magit").resolve(REPOSITORY_NAME_FILE);
            return FileUtilities.getFileContent(RRNameFile);
        }
        else
        {
            return null;
        }
    }

    public void createRepositoryNameFile() throws IOException
    {
        FileUtilities.createAndWriteTxtFile(m_Magit.getMagitDir().resolve(REPOSITORY_NAME_FILE), m_Name);
    }

    public Repository(Path i_RepPath) throws IOException
    {
        m_WorkingCopy = new WC(i_RepPath);
        m_Magit = new Magit();
        m_Magit.setMagitDir(i_RepPath.resolve(".magit"));
    }

    public WC getWorkingCopy()
    {
        return m_WorkingCopy;
    }

    public void loadNameFromFile() throws IOException
    {
        // this method is reading the repository name file and updating m_Name
        String repositoryName = "";
        Path fileNamePath = m_WorkingCopy.getWorkingCopyDir().resolve(".magit").resolve(REPOSITORY_NAME_FILE);
        if (FileUtilities.isExists(fileNamePath))
        {
            repositoryName = new String(Files.readAllBytes(fileNamePath));
        }

        m_Name = repositoryName;
    }

    public void clear() throws IOException
    {
        m_WorkingCopy.clear();
        m_Magit.clear();
        m_ChildrenInformation.clear();
        m_Name = null;
    }

    public Path getRemoteRepositoryPath()
    {
        return m_RemoteRepositoryPath;
    }

    private void createRepositoryDirectories(Path i_RepPath) throws IOException
    {
        Files.createDirectories(i_RepPath.resolve(".magit"));
        Files.createDirectories(i_RepPath.resolve(".magit").resolve("branches"));
        Files.createDirectories(i_RepPath.resolve(".magit").resolve("objects"));
    }

    public String getName()
    {
        return m_Name;
    }

    public void setName(String i_Name)
    {
        m_Name = i_Name;
    }

    public Magit getMagit()
    {
        return m_Magit;
    }

    private void updateChildrenInformation(Path i_Dir, int i_NumOfChildren, Folder i_Folder, String i_FolderSHA1)
    {
        m_ChildrenInformation = m_ChildrenInformation.stream()
                .limit(m_ChildrenInformation.size() - i_NumOfChildren)
                .collect(Collectors.toList());

        //make an item string from my content and add it to m_ChildrenInformation
        String itemString = i_Folder.generateStringInformation(i_FolderSHA1, i_Dir.toFile().getName(), m_Username);
        m_ChildrenInformation.add(itemString);
    }


    private String generateFolderContent(int i_NumOfChildren)
    {
        List<String> folderContentList = m_ChildrenInformation.stream()
                .skip(m_ChildrenInformation.size() - i_NumOfChildren)
                .sorted(Comparator.comparing(String::toString))
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
                                    Node i_Node, Path i_Path, NodeMaps i_TempNodeMaps)
    {
        if (!i_TempNodeMaps.getSHA1ByPath().containsKey(i_Path))
        {// New File!
            i_Result.getOpenChanges().getNewNodes().add(i_Path);
            i_Result.getToZipNodes().getSHA1ByPath().put(i_Path, i_SHA1);
            i_Result.getToZipNodes().getNodeBySHA1().put(i_SHA1, i_Node);
        }
        else
        { // the path exists in the WC
            if (i_SHA1.equals(i_TempNodeMaps.getSHA1ByPath().get(i_Path)))
            { // the file has not been modified
                i_Result.getUnchangedNodes().getSHA1ByPath().put(i_Path, i_SHA1);
                i_Result.getUnchangedNodes().getNodeBySHA1().put(i_SHA1, i_TempNodeMaps.getNodeBySHA1().get(i_SHA1));
            }
            else
            {// the file has been modified - delete from temp and add to new maps
                i_Result.getOpenChanges().getModifiedNodes().add(i_Path);
                i_Result.getToZipNodes().getSHA1ByPath().put(i_Path, i_SHA1);
                i_Result.getToZipNodes().getNodeBySHA1().put(i_SHA1, i_Node);
            }

            i_TempNodeMaps.getNodeBySHA1().remove(i_SHA1);
            i_TempNodeMaps.getSHA1ByPath().remove(i_Path);
        }
    }

    public Commit commit(String i_CommitMessage, boolean i_SaveToFileSystem, String i_SecondParentSHA1) throws IOException
    {
        List<String> parents = new LinkedList<>();
        parents.add(m_WorkingCopy.getCommitSHA1());
        if (i_SecondParentSHA1 != null)
        {
            parents.add(i_SecondParentSHA1);
        }

        NodeMaps tempNodeMaps = new NodeMaps(m_WorkingCopy.getNodeMaps());
        WalkFileSystemResult result = new WalkFileSystemResult();
        Commit commit = null;
        FileVisitor<Path> fileVisitor = getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(m_WorkingCopy.getWorkingCopyDir(), tempNodeMaps, result);
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);
        if (i_SaveToFileSystem)
        {
            zipNewAndModifiedNodes(result);
            updateDeletedNodeList(tempNodeMaps, result);
            m_WorkingCopy.setNodeMaps(result.getToZipNodes());
        }

        String rootFolderSha1 = getRootFolderSHA1();
        m_ChildrenInformation.clear();
        boolean isWCDirty = isWCDirty(rootFolderSha1);
        if (isWCDirty || i_SecondParentSHA1 != null)
        {
            if (i_SaveToFileSystem)
            {
                commit = manageDirtyWC(i_CommitMessage, rootFolderSha1, i_SecondParentSHA1);
            }
            else
            {
                commit = m_Magit.createCommit(rootFolderSha1, parents, "", m_Username);
            }
        }

        return commit;
    }

    private void updateDeletedNodeList(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_WalkFileSystemResult)
    {
        addUnchangedNodesToNewNodeMaps(i_WalkFileSystemResult);
        i_WalkFileSystemResult.getUnchangedNodes().clear();
        addDeletedNodesToDeletedList(i_TempNodeMaps, i_WalkFileSystemResult);
    }

    private Commit manageDirtyWC(String i_CommitMessage, String i_RootFolderSha1, String i_SecondParentSHA1) throws IOException
    {
        List<String> parents = new LinkedList<>();
        parents.add(m_Magit.getHead().getActiveBranch().getCommitSHA1());
        if (i_SecondParentSHA1 != null)
        {
            parents.add(i_SecondParentSHA1);
        }

        String commitSHA1 = m_Magit.handleNewCommit(i_RootFolderSha1, parents, i_CommitMessage, m_Username);
        m_WorkingCopy.setCommitSHA1(commitSHA1);

        return m_Magit.getCommits().get(commitSHA1);
    }

    private FileVisitor<Path> getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(Path i_RootFolderPath, NodeMaps i_TempNodeMaps, WalkFileSystemResult i_WalkFileSystemResult)
    {
        /*
        this method is setting i_WalkFileSystemResult to contain all the OpenChanges, unchanged nodes, toZipNodes
        between the file system and the current commit.
        at the end of the method - the deleted nodes will be all the nodes remained at i_TempNodeMaps.
        If a user of this method want to have an updated deletedNodeList - call addDeletedNodesToDeletedList right after.
         */
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
                m_ChildrenInformation.add(blob.generateStringInformation(blobSha1, file.toFile().getName(), m_Username));

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {

                //1. L <- Check how many kids I have.
                int numOfChildren = FileUtilities.getNumberOfSubNodes(dir);
                numOfChildren += i_RootFolderPath == dir ? -1 : 0;
                if (numOfChildren != 0)
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

    private boolean isWCDirty(String i_RootFolderSha1)
    {
        return m_WorkingCopy.getCommitSHA1().equals("") ||
                !i_RootFolderSha1.equals(m_Magit.getCommits().get(m_WorkingCopy.getCommitSHA1()).getRootFolderSHA1());
    }


    private String getRootFolderSHA1()
    {
        String rootFolderItemString = m_ChildrenInformation.get(0);
        return engine.objects.Item.getSha1FromItemString(rootFolderItemString);
    }

    private void addDeletedNodesToDeletedList(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_Result)
    {
        for (Map.Entry<Path, String> entry : i_TempNodeMaps.getSHA1ByPath().entrySet())
        {
            i_Result.getOpenChanges().getDeletedNodes().add(entry.getKey());
        }
    }

    private void addUnchangedNodesToNewNodeMaps(WalkFileSystemResult i_Result)
    {
        for (Map.Entry<Path, String> entry : i_Result.getUnchangedNodes().getSHA1ByPath().entrySet())
        {
            i_Result.getToZipNodes().getSHA1ByPath().put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Node> entry : i_Result.getUnchangedNodes().getNodeBySHA1().entrySet())
        {
            i_Result.getToZipNodes().getNodeBySHA1().put(entry.getKey(), entry.getValue());
        }
    }

    private void zipNewAndModifiedNodes(WalkFileSystemResult i_Result) throws IOException
    {
        for (Map.Entry<Path, String> entry : i_Result.getToZipNodes().getSHA1ByPath().entrySet())
        {
            i_Result.getToZipNodes().getNodeBySHA1().get(entry.getValue()).Zip(entry.getValue(), entry.getKey(), m_Magit.getMagitDir());
        }
    }

    public void createNewBranch(String i_BranchName, String i_CommitSHA1) throws IOException
    {
        Branch newBranch = new Branch(i_BranchName, i_CommitSHA1);
        FileUtilities.createAndWriteTxtFile(m_Magit.getMagitDir().resolve("branches").resolve(i_BranchName + ".txt"), newBranch.toString());
        m_Magit.getBranches().put(i_BranchName, newBranch);
    }

    public void checkout(String i_BranchName) throws IOException
    {
        String commitSHA1 = m_Magit.getBranches().get(i_BranchName).getCommitSHA1();
        String rootFolderSHA1 = m_Magit.getCommits().get(commitSHA1).getRootFolderSHA1();
        Branch branchToCheckout = m_Magit.getBranches().get(i_BranchName);
        // clear repository- clear file system and clear nodes map
        workingCopyFileSystemClear();
        m_WorkingCopy.clear();

        // change head to point on new checkedout branch
        m_Magit.getHead().setActiveBranch(m_Magit.getBranches().get(i_BranchName));
        FileUtilities.modifyTxtFile(m_Magit.getMagitDir().resolve("branches").resolve("HEAD.txt"), i_BranchName);

        m_WorkingCopy.getNodeMaps().getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), rootFolderSHA1);
        setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), m_WorkingCopy.getWorkingCopyDir(), m_WorkingCopy.getNodeMaps(), true);

        m_WorkingCopy.setCommitSHA1(branchToCheckout.getCommitSHA1());
    }

    public void setNodeMapsByRootFolder(Path i_StartPath, Path i_WorkingCopyDir, NodeMaps i_NodeMapsToUpdate, boolean i_LoadToFileSystem) throws IOException
    {
        /*
        this method is setting i_NodeMapsToUpdate by the rootFolder given in startPath.
        if i_LoadToFileSystem is true - it also set the nodes to the file system working copy
        Assumptions:
                 1. the object folder includes all the zipped nodes which are the root folder siblings
                 2. startPath is the path of the root folder.
                 3. i_NodeMapsToUpdate already contains the <Path, SHA1> entry represents the root folder.

        At the end of the method, i_NodeMapsToUpdate will contain all the nodes that are the root folder siblings
        and the root folder itself.
         */
        String myBlobContent;

        // getting the SHA1 of the folder by it path
        String zipName = i_NodeMapsToUpdate.getSHA1ByPath().get(i_StartPath);

        // creating folder
        Path objectsMagitDir = i_WorkingCopyDir.resolve(".magit").resolve("objects");
        String myDirContent = FileUtilities.getTxtFromZip(objectsMagitDir.resolve(zipName + ".zip").toString(), zipName.concat(".txt"));
        Folder folder = new Folder(myDirContent);

        // add to map
        i_NodeMapsToUpdate.getNodeBySHA1().put(zipName, folder);

        folder.createItemListFromContent();

        for (engine.objects.Item item : folder.getItems())
        {
            i_NodeMapsToUpdate.getSHA1ByPath().put(i_StartPath.resolve(item.getName()), item.getSHA1());
            if (item.getType().equals("folder"))
            {
                if (i_LoadToFileSystem)
                {
                    Files.createDirectory(i_StartPath.resolve(item.getName()));
                }
                setNodeMapsByRootFolder(i_StartPath.resolve(item.getName()), i_WorkingCopyDir, i_NodeMapsToUpdate, i_LoadToFileSystem);
            }
            else
            {
                // getting the blob SHA1 by it path
                zipName = i_NodeMapsToUpdate.getSHA1ByPath().get(i_StartPath.resolve(item.getName()));
                myBlobContent = FileUtilities.getTxtFromZip(objectsMagitDir.resolve(zipName + ".zip").toString(), item.getName());
                if (i_LoadToFileSystem)
                {
                    FileUtilities.createAndWriteTxtFile(i_StartPath.resolve(item.getName()), myBlobContent);
                }
                Blob blob = new Blob(myBlobContent);
                i_NodeMapsToUpdate.getNodeBySHA1().put(zipName, blob);
            }
        }
    }

    private void workingCopyFileSystemClear() throws IOException
    {
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), getRemoveFileVisitor(m_WorkingCopy.getWorkingCopyDir()));
    }

    private SimpleFileVisitor<Path> getRemoveFileVisitor(Path i_PathToDeleteFrom)
    {
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

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc != null)
                {
                    throw exc;
                }
                if (!dir.equals(i_PathToDeleteFrom))
                {
                    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir))
                    {
                        if (dirStream.iterator().hasNext())
                        {
                            FileUtils.cleanDirectory(dir.toFile());
                        }
                    }
                    Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        };
    }

    public NodeMaps getNodeMaps()
    {
        return m_WorkingCopy.getNodeMaps();
    }

    public void deleteBranch(String i_branchName) throws IOException
    {
        m_Magit.getBranches().remove(i_branchName);
        FileUtilities.deleteFile(m_Magit.getMagitDir().resolve("branches").resolve(i_branchName + ".txt"));
    }

    public void loadRepository(Path i_RepPath) throws IOException, ParseException
    {
        clear();
        loadNameFromFile();
        loadRemoteFromFile();
        m_WorkingCopy.setWorkingCopyDir(i_RepPath);
        loadRemoteBranches();
        m_Magit.load(i_RepPath);
        m_WorkingCopy.setCommitSHA1(m_Magit.getHead().getActiveBranch().getCommitSHA1());
        String rootFolderSHA1 = m_Magit.getCommits().get(m_Magit.getHead().getActiveBranch().getCommitSHA1()).getRootFolderSHA1();
        m_WorkingCopy.getNodeMaps().getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), rootFolderSHA1);
        setNodeMapsByRootFolder(i_RepPath, i_RepPath, m_WorkingCopy.getNodeMaps(), false);
    }

    private void loadRemoteBranches() throws IOException
    {
        Path magitBranchesDir = m_Magit.getMagitDir().resolve("branches");
        List<Path> remoteBranches = Files.walk(magitBranchesDir, 1)
                .filter(d -> !d.equals(magitBranchesDir))
                .filter(d -> d.toFile().isDirectory())
                .collect(Collectors.toList());
        for (Path path : remoteBranches)
        {
            m_Magit.loadBranches(path, getRemoteRepositoryName());
        }
    }

    private void loadRemoteFromFile() throws IOException
    {
        Path remoteFilePath = m_Magit.getMagitDir().resolve(REPOSITORY_REMOTE_FILE);
        String remoteFileContent = new String(Files.readAllBytes(remoteFilePath));
        m_RemoteRepositoryPath = remoteFileContent.equals(EMPTY_STRING) ? null : Paths.get(remoteFileContent);
    }

    public OpenChanges delta(Commit i_FirstCommit, Commit i_SecondCommit) throws IOException
    {
        /*
        this method recieves 2 Commits and return OpenChanges object represent the delta between the commits.
         */
        String myBlobContent;
        Path WCPath = m_WorkingCopy.getWorkingCopyDir();
        OpenChanges delta;
        NodeMaps firstCommitNodeMaps = new NodeMaps();
        NodeMaps secondCommitNodeMaps = new NodeMaps();
        addRootFolderToPathMap(firstCommitNodeMaps, i_FirstCommit);
        addRootFolderToPathMap(secondCommitNodeMaps, i_SecondCommit);
        setNodeMapsByRootFolder(WCPath, WCPath, firstCommitNodeMaps, false);
        setNodeMapsByRootFolder(WCPath, WCPath, secondCommitNodeMaps, false);
        Date firstCommitDate = i_FirstCommit.getCommitDate();
        Date secondCommitDate = i_SecondCommit.getCommitDate();
        if (firstCommitDate.after(secondCommitDate))
        {
            delta = computeDelta(secondCommitNodeMaps, firstCommitNodeMaps);
        }
        else
        {
            delta = computeDelta(firstCommitNodeMaps, secondCommitNodeMaps);
        }

        return delta;
    }

    private OpenChanges computeDelta(NodeMaps i_OlderCommitNodeMaps, NodeMaps i_NewerCommitNodeMaps)
    {
        OpenChanges delta = new OpenChanges();

        for (Map.Entry<Path, String> entry : i_OlderCommitNodeMaps.getSHA1ByPath().entrySet())
        {
            if (isContainsKey(i_NewerCommitNodeMaps, entry))
            {
                if (isDifferentSHA1(i_NewerCommitNodeMaps, entry))
                {// Modified node
                    delta.getModifiedNodes().add(entry.getKey());
                }
            }
            else
            {// Deleted node
                delta.getDeletedNodes().add(entry.getKey());
            }
        }

        for (Map.Entry<Path, String> entry : i_NewerCommitNodeMaps.getSHA1ByPath().entrySet())
        {
            if (!isContainsKey(i_OlderCommitNodeMaps, entry))
            {
                delta.getNewNodes().add(entry.getKey());
            }
        }

        return delta;
    }

    private boolean isContainsKey(NodeMaps i_NodeMap, Map.Entry<Path, String> i_Entry)
    {
        return i_NodeMap.getSHA1ByPath().containsKey(i_Entry.getKey());
    }

    private boolean isDifferentSHA1(NodeMaps i_NodeMap, Map.Entry<Path, String> i_Entry)
    {
        return !i_NodeMap.getSHA1ByPath().get(i_Entry.getKey()).equals(i_Entry.getValue());
    }

    private void addRootFolderToPathMap(NodeMaps i_NodeMap, Commit i_Commit)
    {
        i_NodeMap.getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), i_Commit.getRootFolderSHA1());
    }

    public OpenChanges getFileSystemStatus() throws IOException
    {
        // this method return the openChanges objects represents the status of the WC.

        if (isRootFolderEmpty())
        {
            OpenChanges openChanges = new OpenChanges();;
            openChanges.getDeletedNodes().addAll(m_WorkingCopy.getNodeMaps().getSHA1ByPath().keySet());

            return openChanges;
        }
        else
        {
            Commit fileSystemFictiveCommit = commit("", false, null);
            if (fileSystemFictiveCommit == null)
            {// wc is clean
                return new OpenChanges();
            }
            else
            { // there are changes - compute delta
                NodeMaps tempNodeMaps = new NodeMaps(m_WorkingCopy.getNodeMaps());
                WalkFileSystemResult result = new WalkFileSystemResult();
                FileVisitor<Path> fileVisitor = getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(m_WorkingCopy.getWorkingCopyDir(), tempNodeMaps, result);
                Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);
                addDeletedNodesToDeletedList(tempNodeMaps, result);

                return result.getOpenChanges();
            }
        }
    }

    public void loadXMLRepoToSystem(MagitRepository i_XmlRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException
    {
        Path XMLRepositoryPath = Paths.get(i_XmlRepository.getLocation());
        createRepositoryDirectories(XMLRepositoryPath);
        if (i_XmlRepository.getMagitRemoteReference() == null || i_XmlRepository.getMagitRemoteReference().getLocation() == null)
        {
            m_RemoteRepositoryPath = null;
        }
        else
        {
            m_RemoteRepositoryPath = Paths.get(i_XmlRepository.getMagitRemoteReference().getLocation());
        }

        m_Name = i_XmlRepository.getName();
        writeRemoteRepositoryPathToFileSystem();
        createRepositoryNameFile();
        writeXMLObjectsToFileSystemAtObjectsDir(i_XmlRepository, i_XMLMagitMaps);
        loadHead(i_XmlRepository);
        writeHeadToFileSystem();
        writeBranchesToFileSystem();
        writeCommitsToFileSystem();
    }

    private void writeHeadToFileSystem() throws IOException
    {
        Path destination = m_Magit.getMagitDir().resolve("branches").resolve("HEAD.txt");
        FileUtilities.createAndWriteTxtFile(destination, m_Magit.getHead().getActiveBranch().getName());
    }

    private void writeCommitsToFileSystem() throws IOException
    {
        for (Map.Entry<String, Commit> entry : m_Magit.getCommits().entrySet())
        {
            FileUtilities.createZipFileFromContent(entry.getKey(), entry.getValue().toString(), entry.getKey(), m_Magit.getMagitDir());
        }
    }

    private void writeBranchesToFileSystem() throws IOException
    {
        String fileName, fileContent;
        Path destination = m_Magit.getMagitDir().resolve("branches");
        for (Map.Entry<String, Branch> entry : m_Magit.getBranches().entrySet())
        {
            fileContent = entry.getValue().toString();
            if (entry.getKey().contains("\\"))
            {
                fileName = Paths.get(entry.getKey()).getFileName().toString();
                if (!FileUtilities.isExists(destination.resolve(getRemoteRepositoryName())))
                {
                    Files.createDirectories(destination.resolve(getRemoteRepositoryName()));
                }

                FileUtilities.createAndWriteTxtFile(destination.resolve(getRemoteRepositoryName()).resolve(fileName + ".txt"), fileContent);
            }
            else
            {
                fileName = entry.getKey();
                FileUtilities.createAndWriteTxtFile(destination.resolve(fileName + ".txt"), fileContent);
            }
        }
    }

    private void loadHead(MagitRepository i_XmlRepository)
    {
        m_Magit.getHead().setActiveBranch(m_Magit.getBranches().get(i_XmlRepository.getMagitBranches().getHead()));
    }

    private void writeXMLObjectsToFileSystemAtObjectsDir(MagitRepository i_XMLRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException
    {
        String rootFolderSHA1, rootFolderContent, id;
        String commitDate, commitAuthor, commitMessage, commitSHA1;
        List<String> parentsSHA1 = new LinkedList<>();
        Stack<Integer> commitStack = new Stack<>();
        MagitSingleCommit currentXMLCommit;
        Map<Integer, String> commitSHA1ByID = new HashMap<>();
        SortedSet<MagitSingleCommit> sortedXMLCommitsByDateOfCreation = new TreeSet<>((Comparator<MagitSingleCommit>) (commit1, commit2) ->
        {
            if (DateUtils.FormatToDate(commit1.getDateOfCreation()).compareTo(DateUtils.FormatToDate(commit2.getDateOfCreation())) > 0)
            {
                return 1;
            }
            else if (DateUtils.FormatToDate(commit1.getDateOfCreation()).compareTo(DateUtils.FormatToDate(commit2.getDateOfCreation())) < 0)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        });
        sortedXMLCommitsByDateOfCreation.addAll(i_XMLMagitMaps.getMagitSingleCommitByID().values().stream().collect(Collectors.toList()));
        for (MagitSingleCommit XMLCommit : sortedXMLCommitsByDateOfCreation)
        {
            id = XMLCommit.getRootFolder().getId();
            writeCommitNodesToObjectsDir("folder", id, i_XMLMagitMaps);
            rootFolderContent = generateFolderContent(m_ChildrenInformation.size());
            rootFolderSHA1 = DigestUtils.sha1Hex(StringUtilities.makeSHA1Content(rootFolderContent, 3));
            if (XMLCommit.getPrecedingCommits() != null)
            {
                for (PrecedingCommits.PrecedingCommit parent : XMLCommit.getPrecedingCommits().getPrecedingCommit())
                {
                    parentsSHA1.add(commitSHA1ByID.get(Integer.parseInt(parent.getId())));
                }
            }

            Commit commit = new Commit(rootFolderSHA1, parentsSHA1, XMLCommit.getMessage(), DateUtils.FormatToDate(XMLCommit.getDateOfCreation()), XMLCommit.getAuthor());
            commitSHA1 = commit.getSHA1();
            m_Magit.getCommits().put(commitSHA1, commit);
            commitSHA1ByID.put(Integer.parseInt(XMLCommit.getId()), commitSHA1);
            m_ChildrenInformation.clear();
            parentsSHA1.clear();
        }

        loadBranchesFromXML(i_XMLRepository.getMagitBranches(), commitSHA1ByID);
    }

    private void loadBranchesFromXML(MagitBranches i_XMLBranches, Map<Integer, String> i_CommitSHA1ByID)
    {
        String branchContent;
        String branchName;
        for (MagitSingleBranch XMLBranch : i_XMLBranches.getMagitSingleBranch())
        {
            branchName = XMLBranch.getName();
            if (XMLBranch.getPointedCommit().getId().equals(""))
            {
                branchContent = "";
            }
            else
            {
                branchContent = i_CommitSHA1ByID.get(Integer.parseInt(XMLBranch.getPointedCommit().getId()));
            }

            Branch branch = new Branch(branchName, branchContent);
            branch.setIsRemote(XMLBranch.isIsRemote());
            if (XMLBranch.isTracking())
            {
                branch.setIsTracking(true);
                branch.setTrackingAfter(XMLBranch.getTrackingAfter());
            }
            m_Magit.getBranches().put(branchName, branch);
        }
    }

    private void writeCommitNodesToObjectsDir(String i_Type, String i_ID, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException
    {
        if (i_Type.equals("blob"))
        {

            MagitBlob magitBlobObj = i_XMLMagitMaps.getMagitSingleBlobByID().get(i_ID);
            String blobSHA1 = DigestUtils.sha1Hex(magitBlobObj.getContent());
            engine.objects.Item realItem = new engine.objects.Item(magitBlobObj.getName(), blobSHA1, "blob",
                    magitBlobObj.getLastUpdater(), DateUtils.FormatToDate(magitBlobObj.getLastUpdateDate()));
            m_ChildrenInformation.add(realItem.toString());
            FileUtilities.createZipFileFromContent(blobSHA1, magitBlobObj.getContent(), realItem.getName(), m_Magit.getMagitDir());
        }
        else // type.equals("folder")
        {
            MagitSingleFolder magitFolderObj = i_XMLMagitMaps.getMagitSingleFolderByID().get(i_ID);
            for (mypackage.Item XMLItem : magitFolderObj.getItems().getItem())
            {
                String XMLItemID = XMLItem.getId();
                writeCommitNodesToObjectsDir(XMLItem.getType(), XMLItemID, i_XMLMagitMaps);
            }
            int numOfChildren = magitFolderObj.getItems().getItem().size();
            //2. add the item information of my children to my content
            String folderContent = generateFolderContent(numOfChildren);
            String folderSHA1 = DigestUtils.sha1Hex(StringUtilities.makeSHA1Content(folderContent, 3));
            FileUtilities.createZipFileFromContent(folderSHA1, folderContent, folderSHA1, m_Magit.getMagitDir());
            engine.objects.Item realItem = new engine.objects.Item(magitFolderObj.getName(), folderSHA1, i_Type,
                    magitFolderObj.getLastUpdater(), DateUtils.FormatToDate(magitFolderObj.getLastUpdateDate()));

            if (!i_XMLMagitMaps.getMagitSingleFolderByID().get(i_ID).isIsRoot())
            {
                m_ChildrenInformation = m_ChildrenInformation.stream()
                        .limit(m_ChildrenInformation.size() - numOfChildren)
                        .collect(Collectors.toList());
                m_ChildrenInformation.add(realItem.toString());
            }
        }
    }

    public SortedSet<String> getActiveBranchHistory()
    {
        SortedSet<String> commitsHistory = new TreeSet<>(new Comparator<String>()
        {
            @Override
            public int compare(String i_FirstSHA1, String i_SecondSHA1)
            {
                if (m_Magit.getCommits().get(i_FirstSHA1).getCommitDate().compareTo(m_Magit.getCommits().get(i_SecondSHA1).getCommitDate()) > 0)
                {
                    return -1;
                }
                else if (m_Magit.getCommits().get(i_FirstSHA1).getCommitDate().compareTo(m_Magit.getCommits().get(i_SecondSHA1).getCommitDate()) < 0)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        });
        Branch activeBranch = m_Magit.getHead().getActiveBranch();
        String commitSHA1 = activeBranch.getCommitSHA1();
        createCommitsHistoryRecursive(commitsHistory, commitSHA1);

        return commitsHistory;
    }

    private void createCommitsHistoryRecursive(SortedSet<String> i_CommitsHistory, String i_CommitSHA1)
    {
        i_CommitsHistory.add(i_CommitSHA1);
        Commit currentCommit = m_Magit.getCommits().get(i_CommitSHA1);
        for (String SHA1 : currentCommit.getParentsSHA1())
        {
            createCommitsHistoryRecursive(i_CommitsHistory, SHA1);
        }
    }

    public void setActiveBranchPointedCommitByCommitSHA1(String i_CommitSHA1) throws IOException
    {
        String activeBranchName = m_Magit.getHead().getActiveBranch().getName();
        Path destination = m_Magit.getMagitDir().resolve("branches").resolve(activeBranchName + ".txt");
        m_Magit.getBranches().get(activeBranchName).setCommitSHA1(i_CommitSHA1);
        FileUtilities.modifyTxtFile(destination, m_Magit.getBranches().get(activeBranchName).toString());
    }

    public boolean isRootFolderEmpty() throws IOException
    {
        // assuming only magit folder inside root folder.
        return FileUtilities.getNumberOfSubNodes(m_WorkingCopy.getWorkingCopyDir()) == 1;
    }

    public Commit getNewestCommitByItDate()
    {
        return m_Magit.getNewestCommitByItDate();
    }

    public List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        return m_Magit.getContainedBranches(i_CommitSHA1);
    }

    public Folder getFolderBySHA1(String i_FolderSHA1)
    {
        if (m_WorkingCopy.getNodeMaps().getNodeBySHA1().containsKey(i_FolderSHA1))
        {
            return (Folder) m_WorkingCopy.getNodeMaps().getNodeBySHA1().get(i_FolderSHA1);
        }
        else
        {
            return null;
        }
    }

    public Node getNodeBySHA1(String i_ItemSHA1)
    {
        return m_WorkingCopy.getNodeMaps().getNodeBySHA1().get(i_ItemSHA1);
    }


    public MergeNodeMaps merge(String i_TheirBranchName) throws IOException
    {
        MergeNodeMaps mergeNodeMaps = new MergeNodeMaps();
        Branch theirBranch = m_Magit.getBranches().get(i_TheirBranchName);
        Commit theirCommit = m_Magit.getCommits().get(theirBranch.getCommitSHA1());
        generateCommitsNodeMaps(mergeNodeMaps, theirCommit);
        unionCommitsNodeMaps(mergeNodeMaps);
        mergeUnconflictedNodesAndGetConflictedNodeMaps(mergeNodeMaps);

        return mergeNodeMaps;
    }

    private void mergeUnconflictedNodesAndGetConflictedNodeMaps(MergeNodeMaps i_MergeNodeMaps) throws IOException
    {
        NodeMaps unionNodeMaps = i_MergeNodeMaps.getUnionNodeMaps();
        for (Path path : unionNodeMaps.getSHA1ByPath().keySet())
        {
            if (!FileUtilities.isFolder(path))
            {
                //handle blob - conflict(add to list) or not conflict(solve)
                String decision;
                eConflictCases conflictCases = generateConflictCases(path, i_MergeNodeMaps);
                if (conflictCases.isConflict())
                {
                    i_MergeNodeMaps.getConflicts().add(path);
                }
                else
                {
                    decision = conflictCases.getFileVersionToTake();
                    if (decision.equals("their"))
                    {
                        executeDecision(path, i_MergeNodeMaps.getTheirNodeMaps());
                    }
                }
            }
        }

        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), getRemoveEmptyDirectoriesFileVisitor());
    }

    private SimpleFileVisitor<Path> getRemoveEmptyDirectoriesFileVisitor()
    {
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
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (FileUtilities.getNumberOfSubNodes(dir) == 0)
                {
                    FileUtilities.deleteFile(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        };
    }


    private void executeDecision(Path i_Path, NodeMaps i_ToTakeFromNodeMaps) throws IOException
    {
        Path pathToCreate = i_Path.getParent();
        Files.createDirectories(pathToCreate);
        if (FileUtilities.isExists(i_Path))
        {
            FileUtilities.deleteFile(i_Path);
        }
        if (i_ToTakeFromNodeMaps.getSHA1ByPath().containsKey(i_Path))
        { // get the file version from their maps
            String nodeSHA1 = i_ToTakeFromNodeMaps.getSHA1ByPath().get(i_Path);
            String fileContent = i_ToTakeFromNodeMaps.getNodeBySHA1().get(nodeSHA1).getContent();
            FileUtilities.createAndWriteTxtFile(i_Path, fileContent);
        }
    }

    private eConflictCases generateConflictCases(Path i_CheckingPath, MergeNodeMaps i_MergeNodeMaps)
    {
        boolean isExistInAncestor, isExistInOurs, isExistInTheir, isAncestorEqualsOurs, isAncestorEqualsTheirs, isTheirsEqualsOurs;

        isExistInAncestor = isBlobExistsInAncestorNodeMap(i_CheckingPath, i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath());
        isExistInOurs = isBlobExistsInOursNodeMap(i_CheckingPath, i_MergeNodeMaps.getOursNodeMaps().getSHA1ByPath());
        isExistInTheir = isBlobExistsInTheirNodeMap(i_CheckingPath, i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath());
        isAncestorEqualsOurs = isExistInAncestor && isExistInOurs && isBlobInAncestorEqualBlobInOurs(i_CheckingPath, i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath(), i_MergeNodeMaps.getOursNodeMaps().getSHA1ByPath());
        isAncestorEqualsTheirs = isExistInAncestor && isExistInTheir && isBlobInAncestorEqualsBlobInTheirs(i_CheckingPath, i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath(), i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath());
        isTheirsEqualsOurs = isExistInTheir && isExistInOurs && isBlobInTheirsEqualsBlobInOurs(i_CheckingPath, i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath(), i_MergeNodeMaps.getOursNodeMaps().getSHA1ByPath());

        return eConflictCases.getItem(isExistInAncestor, isExistInOurs, isExistInTheir, isAncestorEqualsOurs, isAncestorEqualsTheirs, isTheirsEqualsOurs).get();
    }

    private void removeFromNodeMaps(Path i_PathToRemove, NodeMaps i_NodeMapsToDeleteFrom)
    {
        String srcSHA1 = i_NodeMapsToDeleteFrom.getSHA1ByPath().get(i_PathToRemove);
        i_NodeMapsToDeleteFrom.getSHA1ByPath().remove(i_PathToRemove);
        i_NodeMapsToDeleteFrom.getNodeBySHA1().remove(srcSHA1);
    }

    private boolean isBlobInTheirsEqualsBlobInOurs(Path i_Path, Map<Path, String> i_TheirSha1ByPath, Map<Path, String> i_OurSha1ByPath1)
    {
        return i_TheirSha1ByPath.get(i_Path).equals(i_OurSha1ByPath1.get(i_Path));
    }

    private boolean isBlobInAncestorEqualsBlobInTheirs(Path i_Path, Map<Path, String> i_AncestorSha1ByPath, Map<Path, String> i_TheirSha1ByPath1)
    {
        return i_AncestorSha1ByPath.get(i_Path).equals(i_TheirSha1ByPath1.get(i_Path));
    }

    private boolean isBlobInAncestorEqualBlobInOurs(Path i_Path, Map<Path, String> i_AncestorSha1ByPath, Map<Path, String> i_OurSha1ByPath1)
    {
        return i_AncestorSha1ByPath.get(i_Path).equals(i_OurSha1ByPath1.get(i_Path));
    }

    private boolean isBlobExistsInTheirNodeMap(Path i_Path, Map<Path, String> i_TheirSha1ByPath)
    {
        return i_TheirSha1ByPath.containsKey(i_Path);
    }

    private boolean isBlobExistsInOursNodeMap(Path i_Path, Map<Path, String> i_OursSha1ByPath)
    {
        return i_OursSha1ByPath.containsKey(i_Path);
    }

    private boolean isBlobExistsInAncestorNodeMap(Path i_Path, Map<Path, String> i_AncestorSHA1ByPath)
    {
        return i_AncestorSHA1ByPath.containsKey(i_Path);
    }

    private void unionCommitsNodeMaps(MergeNodeMaps i_MergeNodeMaps)
    {
        NodeMaps unionNodeMaps = i_MergeNodeMaps.getUnionNodeMaps();
        unionNodeMaps.putAll(i_MergeNodeMaps.getOursNodeMaps());

        for (Path path : i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath().keySet())
        {
            unionNodeMaps.putIfPathDoesntExists(path, i_MergeNodeMaps.getTheirNodeMaps());
        }

        for (Path path : i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath().keySet())
        {
            unionNodeMaps.putIfPathDoesntExists(path, i_MergeNodeMaps.getAncestorNodeMaps());
        }
    }

    private void generateCommitsNodeMaps(MergeNodeMaps i_MergeNodeMaps, Commit i_TheirCommit) throws IOException
    {
        generateAncestorCommitNodeMaps(i_MergeNodeMaps, i_TheirCommit);
        generateTheirCommitNodeMaps(i_MergeNodeMaps, i_TheirCommit);
        generateOursCommitNodeMaps(i_MergeNodeMaps);
    }

    private void generateAncestorCommitNodeMaps(MergeNodeMaps i_MergeNodeMaps, Commit i_TheirCommit) throws IOException
    {
        String ourCommitSHA1, ancestorSHA1;
        Commit ourCommit, ancestorCommit;

        ourCommitSHA1 = m_Magit.getHead().getActiveBranch().getCommitSHA1();
        ourCommit = m_Magit.getCommits().get(ourCommitSHA1);
        AncestorFinder finder = new AncestorFinder(SHA1 -> m_Magit.getCommits().get(SHA1));
        ancestorSHA1 = finder.traceAncestor(ourCommit.getSHA1(), i_TheirCommit.getSHA1());
        ancestorCommit = m_Magit.getCommits().get(ancestorSHA1);

        NodeMaps ancestorNodeMaps = i_MergeNodeMaps.getAncestorNodeMaps();
        Path rootFolderPath = m_WorkingCopy.getWorkingCopyDir();
        ancestorNodeMaps.getSHA1ByPath().put(rootFolderPath, ancestorCommit.getRootFolderSHA1());
        setNodeMapsByRootFolder(rootFolderPath, rootFolderPath, ancestorNodeMaps, false);
    }

    private void generateTheirCommitNodeMaps(MergeNodeMaps i_MergeNodeMaps, Commit i_TheirCommit) throws IOException
    {
        NodeMaps theirNodeMaps = i_MergeNodeMaps.getTheirNodeMaps();
        Path rootFolderPath = m_WorkingCopy.getWorkingCopyDir();
        theirNodeMaps.getSHA1ByPath().put(rootFolderPath, i_TheirCommit.getRootFolderSHA1());
        setNodeMapsByRootFolder(rootFolderPath, rootFolderPath, theirNodeMaps, false);
    }

    private void generateOursCommitNodeMaps(MergeNodeMaps i_MergeNodeMaps)
    {
        i_MergeNodeMaps.setOursNodeMaps(m_WorkingCopy.getNodeMaps());
    }

    public String getPointedCommitSHA1(String i_PointedBranch)
    {
        return m_Magit.getBranches().get(i_PointedBranch).getCommitSHA1();
    }

    public void removeEmptyDirectories() throws IOException
    {
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), getRemoveEmptyDirectoriesFileVisitor());
    }

    public Path getRootFolderPath()
    {
        return m_WorkingCopy.getWorkingCopyDir();
    }

    public void setActiveBranchPointedCommitByBranchName(String i_BranchNameToCopyPointedCommit) throws IOException
    {
        Branch branchToCopy = m_Magit.getBranches().get(i_BranchNameToCopyPointedCommit);
        Branch activeBranch = m_Magit.getHead().getActiveBranch();
        activeBranch.setCommitSHA1(branchToCopy.getCommitSHA1());
        Path pathToActiveBranch = m_Magit.getMagitDir().resolve("branches").resolve(activeBranch.getName() + ".txt");
        FileUtilities.modifyTxtFile(pathToActiveBranch, activeBranch.toString());
    }

    public boolean isFastForwardMerge(String i_TheirBranchName)
    {
        String ourCommitSHA1 = m_Magit.getHead().getActiveBranch().getCommitSHA1();
        String theirCommitSHA1 = m_Magit.getBranches().get(i_TheirBranchName).getCommitSHA1();

        AncestorFinder finder = new AncestorFinder(SHA1 -> m_Magit.getCommits().get(SHA1));
        String ancestorSHA1 = finder.traceAncestor(ourCommitSHA1, theirCommitSHA1);

        return ancestorSHA1.equals(ourCommitSHA1) || ancestorSHA1.equals(theirCommitSHA1);
    }

    public boolean isOursContainsTheir(String i_TheirBranchName)
    {
        String ourCommitSHA1 = m_Magit.getHead().getActiveBranch().getCommitSHA1();
        String theirCommitSHA1 = m_Magit.getBranches().get(i_TheirBranchName).getCommitSHA1();

        AncestorFinder finder = new AncestorFinder(SHA1 -> m_Magit.getCommits().get(SHA1));
        String ancestorSHA1 = finder.traceAncestor(ourCommitSHA1, theirCommitSHA1);

        return ancestorSHA1.equals(ourCommitSHA1);
    }

    public List<Commit> getOrderedCommitsByDate()
    {
        return m_Magit.getCommits().values()
                .stream()
                .sorted(Comparator.comparing(Commit::getCommitDate).reversed())
                .collect(Collectors.toList());
    }

    public boolean isCommitFather(String i_FatherSHA1, String i_ChildSHA1)
    {
        boolean isFather;

        List<String> parentsSHA1 = m_Magit.getCommits().get(i_ChildSHA1).getParentsSHA1();
        for (String parent : parentsSHA1)
        {
            if (parent.equals(i_FatherSHA1))
            {
                return true;
            }
        }

        return false;
    }

    public List<Commit> getAllCommitsWithTwoParents()
    {
        return m_Magit.getCommits().values().stream()
                .filter(c -> c.getParentsSHA1().size() == 2)
                .collect(Collectors.toList());
    }

    public Commit getCommit(String i_CommitSHA1)
    {
        return m_Magit.getCommits().get(i_CommitSHA1);
    }

    public boolean isRBBranch(String i_BranchName)
    {
        return m_Magit.getBranches().get(i_BranchName).getIsRemote();
    }

    public void createNewRTB(String i_RemoteBranchName) throws IOException
    {
        m_Magit.createNewRTB(getRemoteRepositoryName(), i_RemoteBranchName);
    }

    public void fetch() throws IOException, ParseException
    {
        // ASSUMPTION: before we invoke this method, we checked if LR has a RR
        String remoteRepositoryName = getRemoteRepositoryName();
        Path remoteBranchesDirPath = m_Magit.getMagitDir().resolve("branches").resolve(remoteRepositoryName);
        FileUtilities.copyDirectoryContent(m_RemoteRepositoryPath.resolve(".magit").resolve("branches"), remoteBranchesDirPath);
        m_Magit.loadBranches(remoteBranchesDirPath, remoteRepositoryName);
        fixRBsIsRemote(remoteBranchesDirPath);
        FileUtilities.copyDirectoryContent(m_RemoteRepositoryPath.resolve(".magit").resolve("objects"), m_Magit.getMagitDir().resolve("objects"));
        m_Magit.loadCommits();
    }

    private void fixRBsIsRemote(Path i_Path) throws IOException
    {
        String branchName;

        try (Stream<Path> walk = Files.walk(i_Path))
        {
            List<Path> result = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            for (Path path : result)
            {
                branchName = FilenameUtils.removeExtension(path.toFile().getName());
                m_Magit.setIsRemoteBranch(getRemoteRepositoryName() + "\\" + branchName, true);
                FileUtilities.modifyTxtFile(path, m_Magit.getBranches().get(getRemoteRepositoryName() + "\\" + branchName).toString());
            }
        }
    }

    public boolean isRRExists()
    {
        return m_RemoteRepositoryPath != null;
    }

    public String getRBNameFromCommitSHA1(String i_CommitSHA1Selected)
    {
        String rbName = null;
        List<Branch> pointingBranches = m_Magit.getContainedBranches(i_CommitSHA1Selected);
        for (Branch branch : pointingBranches)
        {
            if (branch.getIsRemote())
            {
                rbName = branch.getName();
                break;
            }
        }

        return rbName;
    }

    public boolean isPushRequired() throws IOException
    {
        // this method return true if rtb pointing commit is newer than rb pointing commit
        if (m_RemoteRepositoryPath == null)
        {
            return false;
        }

        String headBranchPointedCommitSHA1 = m_Magit.getHead().getActiveBranch().getCommitSHA1();
        Path rrCommitPath = m_RemoteRepositoryPath.resolve(".magit").resolve("objects").resolve(headBranchPointedCommitSHA1 + ".zip");

        return !FileUtilities.isExists(rrCommitPath);
    }

    public void pull() throws IOException, ParseException
    {
        Branch LRActiveBranch = m_Magit.getHead().getActiveBranch();
        Path RRActiveBranchPath = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve(LRActiveBranch.getName() + ".txt");
        String RRPointedCommitSHA1 = StringUtilities.getContentInformation(new String(Files.readAllBytes(RRActiveBranchPath)), 0);
        pullCommitsObjectsRecursive(RRPointedCommitSHA1);
        // changing RB and RTB to point on the new pointed commit
        setActiveBranchPointedCommitByCommitSHA1(RRPointedCommitSHA1);
        changeRBToPointOnRTBCommit(LRActiveBranch);
        //TODO change m_WOrkingcopy commit sha 1 to the new sha 1 pulled
        //TODO check if checkout needed at the end of pull
    }

    private void changeRBToPointOnRTBCommit(Branch i_RTB) throws IOException
    {
        // this method gets an RTB and changing the RB he is tracking after to point on same commit
        String RBName = i_RTB.getTrackingAfter();
        Branch RBBranch = m_Magit.getBranches().get(RBName);
        Path RBBranchPath = m_Magit.getMagitDir().resolve("branches").resolve(RBName + ".txt");
        RBBranch.setCommitSHA1(i_RTB.getCommitSHA1());
        FileUtilities.modifyTxtFile(RBBranchPath, RBBranch.toString());
    }

    private void pullCommitsObjectsRecursive(String i_RRPointedCommitSHA1) throws IOException, ParseException
    {
        NodeMaps currentCommitNodeMaps = new NodeMaps();
        Path RRCommitPath = m_RemoteRepositoryPath.resolve(".magit").resolve("objects").resolve(i_RRPointedCommitSHA1 + ".zip");
        Path LRCommitPath = m_Magit.getMagitDir().resolve("objects").resolve(i_RRPointedCommitSHA1 + ".zip");
        Commit commit;

        if (!m_Magit.getCommits().containsKey(i_RRPointedCommitSHA1))
        {
            // copying the commit zipped file from RR objects dir to LR objects dir
            FileUtilities.copyFile(RRCommitPath, LRCommitPath);

            // create commit object and add it to m_Magit commit map
            commit = m_Magit.createCommitByObjectsDir(i_RRPointedCommitSHA1, RRCommitPath.getParent().toString());
            m_Magit.getCommits().put(commit.getSHA1(), commit);

            // create NodeMaps object from current commit
            String rootFolderSHA1 = commit.getRootFolderSHA1();
            currentCommitNodeMaps.getSHA1ByPath().put(m_RemoteRepositoryPath, rootFolderSHA1);
            setNodeMapsByRootFolder(m_RemoteRepositoryPath, m_RemoteRepositoryPath, currentCommitNodeMaps, false);
            writeNodeMapsToFileSystem(m_RemoteRepositoryPath.resolve(".magit").resolve("objects"), m_Magit.getMagitDir().resolve("objects"), currentCommitNodeMaps);

            // call recursive with parents
            for (String parentSHA1 : commit.getParentsSHA1())
            {
                pullCommitsObjectsRecursive(parentSHA1);
            }

        }
    }

    private void writeNodeMapsToFileSystem(Path i_SourcePath, Path i_DestPath, NodeMaps i_NodeMapsToWrite) throws IOException
    {
        Path nodePath;
        String nodeSHA1;

        for (Map.Entry<Path, String> entry : i_NodeMapsToWrite.getSHA1ByPath().entrySet())
        {
            nodeSHA1 = entry.getValue();
            nodePath = i_DestPath.resolve(nodeSHA1 + ".zip");
            // copy every node from node map to ObjectMagitDir
            if (!FileUtilities.isExists(nodePath))
            {
                FileUtilities.copyFile(i_SourcePath.resolve(nodeSHA1 + ".zip"), i_DestPath.resolve(nodePath.getFileName()));
            }
        }
    }

    public boolean isRRWcIsClean() throws IOException, ParseException
    {
        WalkFileSystemResult result = new WalkFileSystemResult();
        NodeMaps commitPointedByHeadInRRNodeMaps = new NodeMaps();
        Commit commit;
        String rrPointedCommitSHA1, rootFolderSHA1;
        // get RR head branch pointed commit sha1
        rrPointedCommitSHA1 = getRRHeadBranchPointedCommitSHA1();

        // create commit object from RR pointed commit
        commit = m_Magit.createCommitByObjectsDir(rrPointedCommitSHA1, m_RemoteRepositoryPath.resolve(".magit").resolve("objects").toString());
        rootFolderSHA1 = commit.getRootFolderSHA1();

        // get remote branch pointed commit node maps from RR
        commitPointedByHeadInRRNodeMaps.getSHA1ByPath().put(m_RemoteRepositoryPath, rootFolderSHA1);
        setNodeMapsByRootFolder(m_RemoteRepositoryPath, m_RemoteRepositoryPath, commitPointedByHeadInRRNodeMaps, false);

        //  get open changes from RR
        FileVisitor<Path> fileVisitor = getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(m_RemoteRepositoryPath, commitPointedByHeadInRRNodeMaps, result);
        Files.walkFileTree(m_RemoteRepositoryPath, fileVisitor);
        addDeletedNodesToDeletedList(commitPointedByHeadInRRNodeMaps, result);

        return result.getOpenChanges().isFileSystemClean();
    }

    private String getRRHeadBranchPointedCommitSHA1() throws IOException
    {
        Path pathToHead = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve("HEAD.txt");
        String activeBranchName = new String(Files.readAllBytes(pathToHead));
        Path pathToBranch = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve(activeBranchName + ".txt");

        return StringUtilities.getContentInformation(new String(Files.readAllBytes(pathToBranch)), 0);
    }

    public boolean isHeadRTB()
    {
        Branch headBranch = m_Magit.getHead().getActiveBranch();

        return headBranch.getIsTracking();
    }

    public boolean isRBEqualInRRAndLR(String i_TrackingAfter) throws IOException
    {
        Branch rbLR;
        String rbName, rbRRCommitSHA1;
        Path RRRBPath;

        rbLR = m_Magit.getBranches().get(i_TrackingAfter);
        rbName = m_Magit.getTrackingBranchName(rbLR.getName());

        RRRBPath = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve(rbName + ".txt");
        rbRRCommitSHA1 = StringUtilities.getContentInformation(new String(Files.readAllBytes(RRRBPath)), 0);

        return rbLR.getCommitSHA1().equals(rbRRCommitSHA1);
    }

    public boolean isRBAndRTBAlreadyTracking(Branch i_Branch)
    {
        return m_Magit.isRBAndRTBAlreadyTracking(i_Branch);
    }

    private void createPRTargetBranch(Branch i_BranchToCreate) throws IOException
    {
        Path pathToBranchInRR = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve(i_BranchToCreate.getName() + ".txt");
        FileUtilities.createAndWriteTxtFile(pathToBranchInRR, i_BranchToCreate.getCommitSHA1());
    }

    public void push() throws IOException, ParseException
    {
        Branch activeBranch = m_Magit.getHead().getActiveBranch();
        Path pathToRBInRR = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve(activeBranch.getName() + ".txt");
        String LRCommitSHA1 = activeBranch.getCommitSHA1();
        pushCommitsObjectsRecursive(LRCommitSHA1);
        changeRBToPointOnRTBCommit(activeBranch);
        changeRRBranchToPointOnNewCommit(pathToRBInRR, m_Magit.getHead().getActiveBranch().getCommitSHA1());
        checkoutRRWcIfNeeded();
    }

    private void checkoutRRWcIfNeeded() throws IOException
    {
        // if the rb is head in RR - than delete RR WC and checkout to pointed head commit

        if(isActiveBranchInRREqualsLRActiveBranch())
        {
            deleteRRWC();
            checkoutRR(m_Magit.getHead().getActiveBranch().getCommitSHA1());
        }
    }

    private void checkoutRR(String i_CommitSHA1) throws IOException
    {
        NodeMaps nodeMaps = new NodeMaps();
        String rootFolderSHA1 = m_Magit.getCommits().get(i_CommitSHA1).getRootFolderSHA1();
        nodeMaps.getSHA1ByPath().put(m_RemoteRepositoryPath, rootFolderSHA1);
        setNodeMapsByRootFolder(m_RemoteRepositoryPath, m_RemoteRepositoryPath, nodeMaps, true);
    }

    private void deleteRRWC() throws IOException
    {
        SimpleFileVisitor<Path> removeFileVisitor = getRemoveFileVisitor(m_RemoteRepositoryPath);
        Files.walkFileTree(m_RemoteRepositoryPath, removeFileVisitor);
    }

    private boolean isActiveBranchInRREqualsLRActiveBranch() throws IOException
    {
        return m_Magit.getHead().getActiveBranch().getName().equals(getRemoteActiveBranchName());
    }

    private void changeRRBranchToPointOnNewCommit(Path i_PathToBranch, String i_CommitSHA1) throws IOException
    {
        String RRBranchContent, isRemoteString, isTrackingString, trackingAfter;
        boolean isRemote, isTracking;
        Branch tempBranch = new Branch("temp", i_CommitSHA1);

        RRBranchContent = new String(Files.readAllBytes(i_PathToBranch));
        isRemoteString = StringUtilities.getContentInformation(RRBranchContent, 1);
        isTrackingString = StringUtilities.getContentInformation(RRBranchContent, 2);
        trackingAfter = StringUtilities.getContentInformation(RRBranchContent, 3);
        isRemote = isRemoteString.equals("true");
        isTracking = isTrackingString.equals("true");
        tempBranch.setIsRemote(isRemote);
        tempBranch.setIsTracking(isTracking);
        tempBranch.setTrackingAfter(trackingAfter);
        FileUtilities.modifyTxtFile(i_PathToBranch, tempBranch.toString());
    }

    private void pushCommitsObjectsRecursive(String i_LRCommitSHA1) throws IOException, ParseException
    {
        NodeMaps currentCommitNodeMaps = new NodeMaps();
        Path RRCommitPath = m_RemoteRepositoryPath.resolve(".magit").resolve("objects").resolve(i_LRCommitSHA1 + ".zip");
        Path LRCommitPath = m_Magit.getMagitDir().resolve("objects").resolve(i_LRCommitSHA1 + ".zip");
        Commit commit;

        // copying the commit zipped file from RR objects dir to LR objects dir
        if (!FileUtilities.isExists(m_RemoteRepositoryPath.resolve(".magit").resolve("objects").resolve(i_LRCommitSHA1 + ".zip")))
        {
            FileUtilities.copyFile(LRCommitPath, RRCommitPath);

            // create commit object and add it to m_Magit commit map
            commit = m_Magit.createCommitByObjectsDir(i_LRCommitSHA1, LRCommitPath.getParent().toString());
            m_Magit.getCommits().put(commit.getSHA1(), commit);

            // create NodeMaps object from current commit
            String rootFolderSHA1 = commit.getRootFolderSHA1();
            currentCommitNodeMaps.getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), rootFolderSHA1);
            setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), m_WorkingCopy.getWorkingCopyDir(), currentCommitNodeMaps, false);
            setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), m_WorkingCopy.getWorkingCopyDir(), currentCommitNodeMaps, false);
            writeNodeMapsToFileSystem(m_Magit.getMagitDir().resolve("objects"), m_RemoteRepositoryPath.resolve(".magit").resolve("objects"), currentCommitNodeMaps);

            // call recursive with parents
            for (String parentSHA1 : commit.getParentsSHA1())
            {
                pushCommitsObjectsRecursive(parentSHA1);
            }
        }
    }

    public void updateRTBToBeRegularBranch(String i_RBName) throws IOException
    {
        String rtbName;
        Branch rtb;
        Path rtbPath;

        rtbName = m_Magit.getTrackingBranchName(i_RBName);
        rtb = m_Magit.getBranches().get(rtbName);

        rtb.setIsTracking(false);
        rtb.setTrackingAfter(null);

        rtbPath = m_Magit.getMagitDir().resolve("branches").resolve(rtbName + ".txt");

        // update it tracking on file system
        FileUtilities.modifyTxtFile(rtbPath, rtb.toString());
    }

    public void pushNotRTB() throws IOException, ParseException
    {
        String activeBranchName = m_Magit.getHead().getActiveBranch().getName();
        String RBname = createRBForActiveBranch(activeBranchName);
        setActiveBranchTrackingAfter(RBname);
        String activeBranchCommit = m_Magit.getHead().getActiveBranch().getCommitSHA1();
        createRBInRR(activeBranchName, activeBranchCommit);
        pushCommitsObjectsRecursive(activeBranchCommit);
        checkoutRRWcIfNeeded();
    }

    private String getFirstCommitThatExistsInRR(String i_CommitSHA1)
    {
        String SHA1 = i_CommitSHA1;
        Commit commit;
        Path Objpath = m_RemoteRepositoryPath.resolve(".magit").resolve("objects");
        Path commitPath = Objpath.resolve(SHA1 + ".zip");
        while(!FileUtilities.isExists(commitPath))
        {
            commit = m_Magit.getCommits().get(i_CommitSHA1);
            SHA1 = commit.getParentsSHA1().get(0);
            commitPath = Objpath.resolve(SHA1 + ".zip");
        }

        return SHA1;
    }

    private void createRBInRR(String i_BranchName, String i_CommitSHA1) throws IOException
    {
        Branch RRBranch = new Branch(i_BranchName, i_CommitSHA1, false, false, null);
        Path pathToBranch = m_RemoteRepositoryPath.resolve(".magit").resolve("branches").resolve(i_BranchName + ".txt");
        FileUtilities.createAndWriteTxtFile(pathToBranch, RRBranch.toString());
    }

    private void setActiveBranchTrackingAfter(String i_RBName) throws IOException
    {
        Branch activeBranch = m_Magit.getHead().getActiveBranch();

        activeBranch.setIsTracking(true);
        activeBranch.setTrackingAfter(i_RBName);
        FileUtilities.modifyTxtFile(m_Magit.getMagitDir().resolve("branches").resolve(activeBranch.getName() + ".txt"), activeBranch.toString());
    }

    private String createRBForActiveBranch(String i_ActiveBranchName) throws IOException
    {
        String branchName = getRemoteRepositoryName() + "\\" + i_ActiveBranchName;
        Branch RB = new Branch(branchName, m_Magit.getHead().getActiveBranch().getCommitSHA1(), true, false, null);
        m_Magit.getBranches().put(branchName, RB);
        Path pathToBranch = m_Magit.getMagitDir().resolve("branches").resolve(getRemoteRepositoryName()).resolve(i_ActiveBranchName + ".txt");
        FileUtilities.createAndWriteTxtFile(pathToBranch, RB.toString());

        return branchName;
    }

    public boolean isHeadTrackingAfterRB()
    {
        Branch activeBranch = m_Magit.getHead().getActiveBranch();
        return m_Magit.getBranches().containsKey(activeBranch.getTrackingAfter()) && m_Magit.getBranches().get(activeBranch.getTrackingAfter()).getIsRemote();
    }

    public List<Commit> getConnectedCommitsByCommitSHA1(String i_CommitSHA1)
    {
        return m_Magit.getConnectedCommitsByCommitSHA1(i_CommitSHA1);
    }

    public NodeMaps getNodeMapsByCommitSHA1(String i_CommitSHA1) throws IOException
    {
        NodeMaps temp = new NodeMaps();

        temp.getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), m_Magit.getCommits().get(i_CommitSHA1).getRootFolderSHA1());
        setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), m_WorkingCopy.getWorkingCopyDir(), temp, false);

        return temp;
    }

    public Set<Path> getWCFilePaths() throws IOException
    {
        Set<Path> pathSet = new HashSet<>();
        SimpleFileVisitor<Path> fileVisitor = getWCFilePathsFileVisitor(pathSet);
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);

        return pathSet;
    }

    private SimpleFileVisitor<Path> getWCFilePathsFileVisitor(Set<Path> pathSet)
    {
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
                pathSet.add(file);

                return FileVisitResult.CONTINUE;
            }
        };
    }

    public void deleteRTB(String i_BranchName) throws IOException
    {
        Branch RTB = m_Magit.getBranches().get(i_BranchName);

        //delete RB
        String RBName = RTB.getTrackingAfter();
        deleteBranch(RBName);

        //delete RTB
        deleteBranch(RTB.getName());
    }
}
