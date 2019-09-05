package engine;

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

public class Repository
{
    public static final String REPOSITORY_NAME_FILE = "RepositoryName.txt";
    private WC m_WorkingCopy;
    private Magit m_Magit;
    private static List<String> m_ChildrenInformation = new LinkedList<>();
    private String m_Name;

    public Repository(Path i_RepPath, String i_Name) throws IOException
    {
        createRepositoryDirectories(i_RepPath);
        m_Name = i_Name;
        m_WorkingCopy = new WC(i_RepPath);
        m_Magit = new Magit(i_RepPath.resolve(".magit"));
        createRepositoryNameFile();
    }

    private void createRepositoryNameFile() throws IOException
    {
        FileUtilities.createAndWriteTxtFile(Magit.getMagitDir().resolve(REPOSITORY_NAME_FILE), m_Name);
    }

    public Repository(Path i_RepPath) throws IOException
    {
        m_WorkingCopy = new WC(i_RepPath);
        m_Magit = new Magit();
        Magit.setMagitDir(i_RepPath.resolve(".magit"));
    }

    public WC getWorkingCopy() { return m_WorkingCopy; }

    public void loadNameFromFile() throws IOException
    {
        // this method is reading the repository name file and updating m_Name
        String repositoryName = "";
        Path fileNamePath = m_WorkingCopy.getWorkingCopyDir().resolve(".magit").resolve(REPOSITORY_NAME_FILE);
        if (FileUtilities.exists(fileNamePath))
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

    private void createRepositoryDirectories(Path i_RepPath) throws IOException
    {
        Files.createDirectory(i_RepPath.resolve(".magit"));
        Files.createDirectory(i_RepPath.resolve(".magit").resolve("branches"));
        Files.createDirectory(i_RepPath.resolve(".magit").resolve("objects"));
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
        String itemString = i_Folder.generateStringInformation(i_FolderSHA1, i_Dir.toFile().getName());
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

    public Commit commit(String i_CommitMessage, boolean i_SaveToFileSystem) throws IOException
    {
        NodeMaps tempNodeMaps = new NodeMaps(m_WorkingCopy.getNodeMaps());
        WalkFileSystemResult result = new WalkFileSystemResult();
        Commit commit = null;
        FileVisitor<Path> fileVisitor = getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(tempNodeMaps, result);
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
        if (isWCDirty)
        {
            if (i_SaveToFileSystem)
            {
                commit = manageDirtyWC(i_CommitMessage, rootFolderSha1);
            }
            else
            {
                commit = m_Magit.createCommit(rootFolderSha1, m_WorkingCopy.getCommitSHA1(), "");
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

    private Commit manageDirtyWC(String i_CommitMessage, String i_RootFolderSha1) throws IOException
    {
        String commitSHA1 = m_Magit.handleNewCommit(i_RootFolderSha1
                , m_Magit.getHead().getActiveBranch().getCommitSHA1()
                , i_CommitMessage);
        m_WorkingCopy.setCommitSHA1(commitSHA1);

        return m_Magit.getCommits().get(commitSHA1);
    }

    private FileVisitor<Path> getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(NodeMaps i_TempNodeMaps, WalkFileSystemResult i_WalkFileSystemResult)
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
            {

                //1. L <- Check how many kids I have.
                int numOfChildren = FileUtilities.getNumberOfSubNodes(dir);
                numOfChildren += m_WorkingCopy.getWorkingCopyDir() == dir ? -1 : 0;
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
        return engine.Item.getSha1FromItemString(rootFolderItemString);
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
            i_Result.getToZipNodes().getNodeBySHA1().get(entry.getValue()).Zip(entry.getValue(), entry.getKey());
        }
    }

    public void createNewBranch(String i_BranchName) throws IOException
    {
        Branch activeBranch = m_Magit.getHead().getActiveBranch();
        Branch newBranch = new Branch(i_BranchName, activeBranch.getCommitSHA1());
        FileUtilities.createAndWriteTxtFile(Magit.getMagitDir().resolve("branches").resolve(i_BranchName + ".txt"), activeBranch.getCommitSHA1());
        m_Magit.getBranches().put(i_BranchName, newBranch);
    }

    public void checkout(String i_BranchName) throws IOException
    {
        String commitSHA1 = m_Magit.getBranches().get(i_BranchName).getCommitSHA1();
        String rootFolderSHA1 = m_Magit.getCommits().get(commitSHA1).getRootFolderSHA1();
        // clear repository- clear file system and clear nodes map
        workingCopyFileSystemClear();
        m_WorkingCopy.clear();

        // change head to point on new checkedout branch
        m_Magit.getHead().setActiveBranch(m_Magit.getBranches().get(i_BranchName));
        FileUtilities.modifyTxtFile(Magit.getMagitDir().resolve("branches").resolve("HEAD.txt"), i_BranchName);

        m_WorkingCopy.getNodeMaps().getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), rootFolderSHA1);
        setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), m_WorkingCopy.getNodeMaps(), true);
    }

    public void setNodeMapsByRootFolder(Path i_StartPath, NodeMaps i_NodeMapsToUpdate, boolean i_LoadToFileSystem) throws IOException
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
        String myDirContent = FileUtilities.getTxtFromZip(zipName.concat(".zip"), zipName.concat(".txt"));
        Folder folder = new Folder(myDirContent);

        // add to map
        i_NodeMapsToUpdate.getNodeBySHA1().put(zipName, folder);

        folder.createItemListFromContent();

        for (engine.Item item : folder.getItems())
        {
            i_NodeMapsToUpdate.getSHA1ByPath().put(i_StartPath.resolve(item.getName()), item.getSHA1());
            if (item.getType().equals("folder"))
            {
                if (i_LoadToFileSystem)
                {
                    Files.createDirectory(i_StartPath.resolve(item.getName()));
                }
                setNodeMapsByRootFolder(i_StartPath.resolve(item.getName()), i_NodeMapsToUpdate, i_LoadToFileSystem);
            }
            else
            {
                // getting the blob SHA1 by it path
                zipName = i_NodeMapsToUpdate.getSHA1ByPath().get(i_StartPath.resolve(item.getName()));
                myBlobContent = FileUtilities.getTxtFromZip(zipName.concat(".zip"), item.getName());
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
        Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), getRemoveFileVisitor());
    }

    private SimpleFileVisitor<Path> getRemoveFileVisitor()
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
                if (!dir.equals(m_WorkingCopy.getWorkingCopyDir()))
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
        FileUtilities.deleteFile(Magit.getMagitDir().resolve("branches").resolve(i_branchName + ".txt"));
    }

    public void loadRepository(Path i_RepPath) throws IOException, ParseException {
        clear();
        loadNameFromFile();
        m_WorkingCopy.setWorkingCopyDir(i_RepPath);
        m_Magit.load(i_RepPath);
        m_WorkingCopy.setCommitSHA1(m_Magit.getHead().getActiveBranch().getCommitSHA1());
        String rootFolderSHA1 = m_Magit.getCommits().get(m_Magit.getHead().getActiveBranch().getCommitSHA1()).getRootFolderSHA1();
        m_WorkingCopy.getNodeMaps().getSHA1ByPath().put(m_WorkingCopy.getWorkingCopyDir(), rootFolderSHA1);
        setNodeMapsByRootFolder(i_RepPath, m_WorkingCopy.getNodeMaps(), false);
    }

    public OpenChanges delta(Commit i_FirstCommit, Commit i_SecondCommit) throws IOException
    {
        /*
        this method recieves 2 Commits and return OpenChanges object represent the delta between the commits.
         */
        String myBlobContent;
        OpenChanges delta;
        NodeMaps firstCommitNodeMaps = new NodeMaps();
        NodeMaps secondCommitNodeMaps = new NodeMaps();
        addRootFolderToPathMap(firstCommitNodeMaps, i_FirstCommit);
        addRootFolderToPathMap(secondCommitNodeMaps, i_SecondCommit);
        setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), firstCommitNodeMaps, false);
        setNodeMapsByRootFolder(m_WorkingCopy.getWorkingCopyDir(), secondCommitNodeMaps, false);
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

        if(isRootFolderEmpty())
        {
            OpenChanges openChanges = new OpenChanges();
            openChanges.getDeletedNodes().addAll(m_WorkingCopy.getNodeMaps().getSHA1ByPath().keySet());

            return openChanges;
        }
        else
        {
            Commit fileSystemFictiveCommit = commit("", false);
            if (fileSystemFictiveCommit == null)
            {// wc is clean
                return new OpenChanges();
            }
            else
            { // there are changes - compute delta
                NodeMaps tempNodeMaps = new NodeMaps(m_WorkingCopy.getNodeMaps());
                WalkFileSystemResult result = new WalkFileSystemResult();
                FileVisitor<Path> fileVisitor = getOpenChangesBetweenFileSystemAndCurrentCommitFileVisitor(tempNodeMaps, result);
                Files.walkFileTree(m_WorkingCopy.getWorkingCopyDir(), fileVisitor);
                addDeletedNodesToDeletedList(tempNodeMaps, result);

                return result.getOpenChanges();
            }
        }
    }

    public void loadXMLRepoToSystem(MagitRepository i_XmlRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException {
        Path XMLRepositoryPath = Paths.get(i_XmlRepository.getLocation());
        createRepositoryDirectories(XMLRepositoryPath);
        m_Name = i_XmlRepository.getName();
        createRepositoryNameFile();
        writeXMLObjectsToFileSystemAtObjectsDir(i_XmlRepository, i_XMLMagitMaps);
        loadHead(i_XmlRepository);
        writeHeadToFileSystem();
        writeBranchesToFileSystem();
        writeCommitsToFileSystem();
    }

    private void writeHeadToFileSystem() throws IOException
    {
        Path destination = Magit.getMagitDir().resolve("branches").resolve("HEAD.txt");
        FileUtilities.createAndWriteTxtFile(destination, m_Magit.getHead().getActiveBranch().getName());
    }

    private void writeCommitsToFileSystem() throws IOException
    {
        for (Map.Entry<String, Commit> entry : m_Magit.getCommits().entrySet())
        {
            FileUtilities.createZipFileFromContent(entry.getKey(), entry.getValue().toString(), entry.getKey());
        }
    }

    private void writeBranchesToFileSystem() throws IOException
    {
        Path destination = Magit.getMagitDir().resolve("branches");
        for (Map.Entry<String, Branch> entry : m_Magit.getBranches().entrySet())
        {
            FileUtilities.createAndWriteTxtFile(destination.resolve(entry.getKey() + ".txt"), entry.getValue().getCommitSHA1());
        }
    }

    private void loadHead(MagitRepository i_XmlRepository)
    {
        m_Magit.getHead().setActiveBranch(m_Magit.getBranches().get(i_XmlRepository.getMagitBranches().getHead()));
    }

    private void writeXMLObjectsToFileSystemAtObjectsDir(MagitRepository i_XMLRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException {
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
            m_Magit.getBranches().put(branchName, new Branch(branchName, branchContent));
        }
    }

    private void writeCommitNodesToObjectsDir(String i_Type, String i_ID, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException {
        if (i_Type.equals("blob"))
        {

            MagitBlob magitBlobObj = i_XMLMagitMaps.getMagitSingleBlobByID().get(i_ID);
            String blobSHA1 = DigestUtils.sha1Hex(magitBlobObj.getContent());
            engine.Item realItem = new engine.Item(magitBlobObj.getName(), blobSHA1, "blob",
                    magitBlobObj.getLastUpdater(), DateUtils.FormatToDate(magitBlobObj.getLastUpdateDate()));
            m_ChildrenInformation.add(realItem.toString());
            FileUtilities.createZipFileFromContent(blobSHA1, magitBlobObj.getContent(), realItem.getName());
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
            FileUtilities.createZipFileFromContent(folderSHA1, folderContent, folderSHA1);
            engine.Item realItem = new engine.Item(magitFolderObj.getName(), folderSHA1, i_Type,
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

    public void changeActiveBranchPointedCommit(String i_CommitSHA1) throws IOException
    {
        String activeBranchName = m_Magit.getHead().getActiveBranch().getName();
        Path destination = Magit.getMagitDir().resolve("branches").resolve(activeBranchName + ".txt");
        m_Magit.getBranches().get(activeBranchName).setCommitSHA1(i_CommitSHA1);
        FileUtilities.modifyTxtFile(destination, i_CommitSHA1);
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
        if(m_WorkingCopy.getNodeMaps().getNodeBySHA1().containsKey(i_FolderSHA1))
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


    public List<Path> merge(String i_TheirBranchName) throws IOException
    {
        MergeNodeMaps mergeNodeMaps = new MergeNodeMaps();
        Branch theirBranch = m_Magit.getBranches().get(i_TheirBranchName);
        Commit theirCommit = m_Magit.getCommits().get(theirBranch.getCommitSHA1());
        generateCommitsNodeMaps(mergeNodeMaps, theirCommit);
        unionCommitsNodeMaps(mergeNodeMaps);
        mergeUnconflictedNodesAndGetConflictedNodeMaps(mergeNodeMaps);

        //TODO
        return null;
    }

    private void mergeUnconflictedNodesAndGetConflictedNodeMaps(MergeNodeMaps i_MergeNodeMaps)
    {
        Path rootFolderPath = m_WorkingCopy.getWorkingCopyDir();
        mergeFromNodeMapsRecursive(rootFolderPath, i_MergeNodeMaps);
        Folder rootFolder = i_MergeNodeMaps.getUnionNodeMaps().getFolderByPath(rootFolderPath);
        String rootFolderSHA1 = rootFolder.SHA1();
        updateFolderInNodeMapsAfterVisitedChilds(rootFolderPath, rootFolderSHA1, rootFolder, i_MergeNodeMaps.getUnionNodeMaps());
    }

    private void mergeFromNodeMapsRecursive(Path i_CurrentPath, MergeNodeMaps i_MergeNodeMaps)
    {
        Map<Path, String> unionSHA1ByPath = i_MergeNodeMaps.getUnionNodeMaps().getSHA1ByPath();
        NodeMaps unionNodeMaps = i_MergeNodeMaps.getUnionNodeMaps();
        // union all path from three map to one
        Set<Path> childernPaths = generateSubNodesPathsSet(i_CurrentPath, unionSHA1ByPath.keySet());
        Folder currentPathFolder = unionNodeMaps.getFolderByPath(i_CurrentPath);
        
        for(Path childPath : childernPaths)
        {
            String itemName = childPath.getFileName().toString();
            Item toUpdateItem =currentPathFolder.getSpecificItem(itemName);
            if (isPathRepresentsAFolder(childPath))
            {
                mergeFromNodeMapsRecursive(childPath, i_MergeNodeMaps);
                Folder childPathFolder = unionNodeMaps.getFolderByPath(i_CurrentPath);
                if(isFolderHasNoItems(childPathFolder))
                {
                    currentPathFolder.removeItemFromList(toUpdateItem);
                    removeFromNodeMaps(childPath, i_MergeNodeMaps.getUnionNodeMaps());
                }
                else
                {
                    childPathFolder.sortItemList();
                    childPathFolder.setContentFromItemList();
                    String childFolderSHA1 = childPathFolder.SHA1();
                    toUpdateItem.setSHA1(childFolderSHA1);
                    updateFolderInNodeMapsAfterVisitedChilds(childPath, childFolderSHA1, childPathFolder, unionNodeMaps);
                }
            }
            else
            {
                //handle blob - conflict(add to list) or not conflict(solve)
                boolean isExistInAncestor, isExistInOurs, isExistInTheir, isAncestorEqualsOurs, isAncestorEqualsTheirs, isTheirsEqualsOurs;
                String decision;
                isExistInAncestor = isBlobExistsInAncestorNodeMap(childPath, i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath());
                isExistInOurs = isBlobExistsInOursNodeMap(childPath, i_MergeNodeMaps.getOursNodeMaps().getSHA1ByPath());
                isExistInTheir = isBlobExistsInTheirNodeMap(childPath, i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath());
                isAncestorEqualsOurs = isExistInAncestor && isExistInOurs && isBlobInAncestorEqualBlobInOurs(childPath, i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath(), i_MergeNodeMaps.getOursNodeMaps().getSHA1ByPath());
                isAncestorEqualsTheirs = isExistInAncestor && isExistInTheir && isBlobInAncestorEqualsBlobInTheirs(childPath, i_MergeNodeMaps.getAncestorNodeMaps().getSHA1ByPath(), i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath());
                isTheirsEqualsOurs = isExistInTheir && isExistInOurs && isBlobInTheirsEqualsBlobInOurs(childPath, i_MergeNodeMaps.getTheirNodeMaps().getSHA1ByPath(), i_MergeNodeMaps.getOursNodeMaps().getSHA1ByPath());
                eConflictCases conflictCases = eConflictCases.getItem(isExistInAncestor, isExistInOurs, isExistInTheir, isAncestorEqualsOurs, isAncestorEqualsTheirs, isTheirsEqualsOurs).get();
                if (conflictCases.isConflict())
                {
                    addToNodeMaps(childPath, i_MergeNodeMaps.getUnionNodeMaps(), i_MergeNodeMaps.getConflictsNodeMaps());
                    removeFromNodeMaps(childPath, unionNodeMaps);
                }
                else // not conflict
                {
                    decision = conflictCases.getFileVersionToTake();
                    handleDecision(childPath, i_MergeNodeMaps, decision);

                    //update me as item in my father's item list
                    if(unionSHA1ByPath.containsKey(childPath))
                    {
                        //not deleted
                        if(decision.equals("their"))
                        {
                            Folder fatherFolderTheir = i_MergeNodeMaps.getTheirNodeMaps().getFolderByPath(i_CurrentPath);
                            Item updatedItemTheir = fatherFolderTheir.getSpecificItem(itemName);
                            toUpdateItem.copyItemData(updatedItemTheir);
                        }
                    }
                    else
                    {
                        //deleted
                        currentPathFolder.removeItemFromList(toUpdateItem);
                    }
                }

            }
        }
    }
    

    private void updateFolderInNodeMapsAfterVisitedChilds(Path i_ChildPath, String i_ChildFolderSHA1, Folder i_ChildPathFolder, NodeMaps i_DestNodeMap)
    {
        removeFromNodeMaps(i_ChildPath, i_ChildFolderSHA1, i_DestNodeMap);
        addToNodeMaps(i_ChildPath, i_ChildFolderSHA1, i_ChildPathFolder, i_DestNodeMap);
    }

    private void addToNodeMaps(Path i_Path, String i_SHA1, Folder i_Folder, NodeMaps i_DestNodeMap)
    {
        i_DestNodeMap.getSHA1ByPath().put(i_Path, i_SHA1);
        i_DestNodeMap.getNodeBySHA1().put(i_SHA1, i_Folder);
    }

    private void removeFromNodeMaps(Path i_Path, String i_SHA1, NodeMaps i_DestNodeMap)
    {
        i_DestNodeMap.getNodeBySHA1().remove(i_SHA1);
        i_DestNodeMap.getSHA1ByPath().remove(i_Path);
    }

    private boolean isFolderHasNoItems(Folder i_ChildPathFolder)
    {
        return i_ChildPathFolder.getNumberOfItems() == 0;
    }

    private Set<Path> generateSubNodesPathsSet(Path i_CurrentPath, Set<Path> i_Paths)
    {
        Set<Path> result = new HashSet<>();

        // getting all sub blob and folders path
        List<Path> subNodesList = i_Paths.stream()
                .filter(p -> p.startsWith(i_CurrentPath))
                .filter(p-> !p.equals(i_CurrentPath))
                .collect(Collectors.toList());
        Path subNodePath;

        for (Path path : subNodesList)
        {
            subNodePath = i_CurrentPath.getRoot().resolve(path.subpath(0, i_CurrentPath.getNameCount() + 1));
            result.add(subNodePath);
        }

        return result;
    }

    private void handleDecision(Path i_Path, MergeNodeMaps i_MergeNodeMaps, String i_Decision)
    {
        if(i_Decision.equals("ours"))
        {
            changeUnionNodeMapsAccordingToDecision(i_Path, i_MergeNodeMaps.getOursNodeMaps(), i_MergeNodeMaps.getUnionNodeMaps());
        }
        else // theirs
        {
            changeUnionNodeMapsAccordingToDecision(i_Path, i_MergeNodeMaps.getTheirNodeMaps(), i_MergeNodeMaps.getUnionNodeMaps());
        }
    }

    private void changeUnionNodeMapsAccordingToDecision(Path i_Path, NodeMaps i_DecidedVersionNodeMaps, NodeMaps i_UnionNodeMaps)
    {
        String decidedVersionSHA1 = i_DecidedVersionNodeMaps.getSHA1ByPath().get(i_Path);

        removeFromNodeMaps(i_Path, i_UnionNodeMaps);
        if(i_DecidedVersionNodeMaps.getSHA1ByPath().containsKey(i_Path))
        {
            i_UnionNodeMaps.getSHA1ByPath().put(i_Path, decidedVersionSHA1);
            i_UnionNodeMaps.getNodeBySHA1().put(decidedVersionSHA1, i_DecidedVersionNodeMaps.getNodeBySHA1().get(decidedVersionSHA1));
        }
    }

    private void removeFromNodeMaps(Path i_PathToRemove, NodeMaps i_NodeMapsToDeleteFrom)
    {
        String srcSHA1 = i_NodeMapsToDeleteFrom.getSHA1ByPath().get(i_PathToRemove);
        i_NodeMapsToDeleteFrom.getSHA1ByPath().remove(i_PathToRemove);
        i_NodeMapsToDeleteFrom.getNodeBySHA1().remove(srcSHA1);
    }

    private void addToNodeMaps(Path i_PathFileToAdd, NodeMaps i_SourceNodeMaps, NodeMaps i_DestinationNodeMaps)
    {
        String srcSHA1 = i_SourceNodeMaps.getSHA1ByPath().get(i_PathFileToAdd);
        i_DestinationNodeMaps.getSHA1ByPath().put(i_PathFileToAdd, srcSHA1);
        i_DestinationNodeMaps.getNodeBySHA1().put(srcSHA1, i_SourceNodeMaps.getNodeBySHA1().get(srcSHA1));
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

    private boolean isPathRepresentsAFolder(Path i_Path)
    {
        return FilenameUtils.getExtension(i_Path.toString()).equals("");
    }

    private void unionCommitsNodeMaps(MergeNodeMaps i_MergeNodeMaps)
    {
        NodeMaps unionNodeMaps = i_MergeNodeMaps.getUnionNodeMaps();
        unionNodeMaps.putAll(i_MergeNodeMaps.getTheirNodeMaps());
        unionNodeMaps.putAll(i_MergeNodeMaps.getAncestorNodeMaps());
        unionNodeMaps.putAll(i_MergeNodeMaps.getOursNodeMaps());
    }

    private void generateCommitsNodeMaps(MergeNodeMaps i_MergeNodeMaps, Commit i_TheirCommit) throws IOException
    {
        generateAncestorCommitNodeMaps(i_MergeNodeMaps, i_TheirCommit);
        generateTheirCommitNodeMaps(i_MergeNodeMaps,i_TheirCommit);
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
        setNodeMapsByRootFolder(rootFolderPath, ancestorNodeMaps, false);
    }

    private void generateTheirCommitNodeMaps(MergeNodeMaps i_MergeNodeMaps, Commit i_TheirCommit) throws IOException
    {
        NodeMaps theirNodeMaps = i_MergeNodeMaps.getTheirNodeMaps();
        Path rootFolderPath = m_WorkingCopy.getWorkingCopyDir();
        theirNodeMaps.getSHA1ByPath().put(rootFolderPath, i_TheirCommit.getRootFolderSHA1());
        setNodeMapsByRootFolder(rootFolderPath, theirNodeMaps, false);
    }

    private void generateOursCommitNodeMaps(MergeNodeMaps i_MergeNodeMaps)
    {
        i_MergeNodeMaps.setOursNodeMaps(m_WorkingCopy.getNodeMaps());
    }

}
