import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WC
{
    private Path m_WorkingCopyDir;
    private Head m_Head;
    private List<Node> m_Nodes;

    public WC(Path i_Path)
    {
        m_WorkingCopyDir = i_Path;
        m_Head = new Head(m_WorkingCopyDir.resolve(".magit"));
        m_Nodes = new LinkedList<>();
    }

    public String SHA1() throws IOException
    {// TODO handle exception
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
                //**IGNORE .magit FOLDER**
                //Files.walk(dir,1).forEach();
                List<String> blobsList = Files.walk(dir, 1)
                        .filter(file -> !Files.isDirectory(file))
                        .map(Path::toString)
                        .collect(Collectors.toList());


                List<String> foldersList = Files.walk(dir, 1)
                        .filter(file -> Files.isDirectory(file))
                        .map(Path::toString)
                        .collect(Collectors.toList());

                for (String blobName : blobsList)
                {
                    try
                    {
                        String blobContent= new String(Files.readAllBytes(Paths.get(blobName)));
                        m_Nodes.add(new Blob(blobContent));

                    }
                    catch (NoSuchFileException e)
                    {
                        //TODO handle exception
                    }


                }

                return FileVisitResult.CONTINUE;
            }
        };


        Files.walkFileTree(Paths.get("C:\\Users\\orans\\Desktop\\Testing"), fileVisitor);
        return null;
    }


}
