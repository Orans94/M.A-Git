import org.apache.commons.codec.binary.StringUtils;

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
    private static StringBuilder m_ChildrenInformation;

    public WC(Path i_Path)
    {
        m_ChildrenInformation = new StringBuilder("");
        m_WorkingCopyDir = i_Path;
        m_Head = new Head(m_WorkingCopyDir.resolve(".magit"));
        m_Nodes = new HashMap<>();
    }

    public String SHA1() throws IOException {// TODO handle exception
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                return FileVisitResult.CONTINUE;
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
                blob.Zip(blobSha1, m_WorkingCopyDir.resolve(".magit").resolve("objects"));
                //3. save the zip in objects directory
                //4. push Blob to m_Nodes
                m_Nodes.put(blobSha1, blob);
                //5. append my info to m_ChildrenInformation
                m_ChildrenInformation.append(blob.generateStringInformation(blobSha1,file.toFile().getName()));
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
                //TODO**IGNORE .magit FOLDER**
                //
                //1. L <- Check how many kids I have.
                int numOfChildren = (int)Files.walk(dir,1).count();

                //2. add the item information of my children to my content
                String[] lines = m_ChildrenInformation.toString().split(System.lineSeparator());
                List<String> list = Arrays.stream(lines).skip(lines.length-1-numOfChildren)
                    .limit(numOfChildren).collect(Collectors.toList());
                String folderContent = "";
                for(String s: list)
                {
                    folderContent = folderContent.concat(s).concat(System.lineSeparator());
                }                //TODO maybe delete the last line concated to this string

                //3.create a folder object from the details and put it in m_Nodes
                Folder folder = new Folder(folderContent);
                String folderSHA1 = folder.SHA1();
                m_Nodes.put(folderSHA1,folder);

                //4.add content details to item list of folder
                folder.toItem();

                //5. delete this line from m_ChildrenInformation
                list = Arrays.stream(lines).limit(lines.length - numOfChildren).collect(Collectors.toList());

                //6. make an item string from my content and add it to m_ChildrenInformation
                String itemString = folder.generateStringInformation(folderSHA1,dir.toFile().getName());

                //7. zip the folder
                //8. save me in objects.


                //ORAN'S VERSION:
//                List<String> blobsList = Files.walk(dir, 1)
//                        .filter(file -> !Files.isDirectory(file))
//                        .map(Path::toString)
//                        .collect(Collectors.toList());
//
//
//                List<String> foldersList = Files.walk(dir, 1)
//                        .filter(file -> Files.isDirectory(file))
//                        .map(Path::toString)
//                        .collect(Collectors.toList());
//
//                for (String blobName : blobsList) {
//                    try {
//                        String blobContent = new String(Files.readAllBytes(Paths.get(blobName)));
//                        m_Nodes.add(new Blob(blobContent));
//                        //sha 1 zip.
//
//                    } catch (NoSuchFileException e) {
//                        //TODO handle exception
//                    }
//
//
//                }

                return FileVisitResult.CONTINUE;
            }
        };


        Files.walkFileTree(Paths.get("C:\\Users\\orans\\Desktop\\Testing"), fileVisitor);
        return null;
    }


}
