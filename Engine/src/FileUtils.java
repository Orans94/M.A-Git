import java.io.*;
import java.nio.file.Path;

public class FileUtils
{
    public static void CreateAndWriteTxtFile(Path i_Path, String i_Content, boolean i_ToZip)
    {
        try (Writer out1 = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(i_Path.toString()), "UTF-8")))
        {
            out1.write(i_Content);
        }
        catch (UnsupportedEncodingException e)
        {//TODO

        }
        catch (FileNotFoundException e)
        {//TODO

        }
        catch (IOException e)
        {//TODO

        }
    }

    public static void ModifyTxtFile(Path i_Path, String i_Content)
    { //TODO txt has been modifed

    }
}
