import java.nio.file.Path;

public class Head
{
    private String m_Content;

    public Head(Path i_Path)
    {
        m_Content = "master";
        FileUtils.CreateAndWriteTxtFile(i_Path.resolve("HEAD".concat(".txt")), m_Content);
    }
}
