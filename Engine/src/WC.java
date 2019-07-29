import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WC
{
    private Path m_WorkingCopyDir;
    private Head m_Head;
    private Map<String, Node> m_Nodes;
    private static List<String> m_ChildrenInformation;

    public WC(Path i_Path)
    {
        m_ChildrenInformation = new LinkedList<>();
        m_WorkingCopyDir = i_Path;
        m_Nodes = new HashMap<>();
    }

    public String Commit() throws IOException
    {// TODO handle exception
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                if (dir.getFileName().toString().equals(".magit"))
                {
                    return FileVisitResult.TERMINATE;
                }
                else
                {
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                //System.out.println(file.getFileName());
                //1. Sha-1
                String blobContent = new String(Files.readAllBytes(file));
                Blob blob = new Blob(blobContent);
                String blobSha1 = blob.SHA1();
                //2. Zip //TODO
                blob.Zip(blobSha1, file);
                //3. save the zip in objects directory
                //4. push Blob to m_Nodes
                m_Nodes.put(blobSha1, blob);
                //5. append my info to m_ChildrenInformation
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
                int numOfChildren = getNumOfChildren(dir);

                //2. add the item information of my children to my content
                String folderContent = generateFolderContent(numOfChildren);

                //3.create a folder object from the details and put it in m_Nodes
                Folder folder = new Folder(folderContent);
                //4.add content details to item list of folder
                folder.createItemListFromContent();
                //update content before making sha1 (remove author name and date)
                String contentToSHA1 = folder.updateContent();
                String folderSHA1 = addFolderToMap(folder, contentToSHA1);

                //5. update children information
                updateChildrenInformation(dir, numOfChildren, folder, folderSHA1);

                //6. zip the folder and save me in objects dir
                folder.Zip(folderSHA1, dir);

                return FileVisitResult.CONTINUE;
            }
        };


        Files.walkFileTree(Paths.get("C:\\Users\\orans\\Desktop\\Testing"), fileVisitor);
        String rootFolderItemString = m_ChildrenInformation.get(0);
        String rootFolderSha1 = getSha1FromItemString(rootFolderItemString);
        m_ChildrenInformation.clear();

        return rootFolderSha1;
    }

    private String getSha1FromItemString(String i_ItemString)
    {
        String[] members = i_ItemString.split(",");
        return members[1];
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

    private String addFolderToMap(Folder i_Folder, String i_ContentToSHA1) {
        String folderSHA1 = DigestUtils.sha1Hex(i_ContentToSHA1);
        m_Nodes.put(folderSHA1, i_Folder);
        return folderSHA1;
    }

    private String generateFolderContent(int i_NumOfChildren) {
        List<String> folderContentList = m_ChildrenInformation.stream()
                .skip(m_ChildrenInformation.size() - i_NumOfChildren)
                .collect(Collectors.toList());
        String folderContent = "";
        for (String s : folderContentList)
        {
            folderContent = folderContent.concat(s).concat(System.lineSeparator());
        }                //TODO maybe delete the last line concated to this string

        //delete last line from the string
        folderContent = folderContent.substring(0, folderContent.length() - 2);
        return folderContent;
    }

    private int getNumOfChildren(Path i_Path) throws IOException {
        return (int) Files.walk(i_Path, 1).count();
    }


}
