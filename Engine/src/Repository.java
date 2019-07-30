import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
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

    public WC getWorkingCopy() {
        return m_WorkingCopy;
    }

    public void setWorkingCopy(WC i_WorkingCopy) {
        this.m_WorkingCopy = i_WorkingCopy;
    }

    public Magit getMagit() { return m_Magit; }

    public void setMagit(Magit i_Magit) { this.m_Magit = i_Magit; }

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

                //2. Zip & save the zip in objects directory
                blob.Zip(blobSha1, file);

                //3. push blob to m_Nodes
                m_WorkingCopy.getNodes().put(blobSha1, blob);

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
                ,m_Magit.getHead().getActiveBranch().getCommitSHA1()
                ,i_CommitMessage);
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
}
