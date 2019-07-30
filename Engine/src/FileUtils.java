import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils
{
    public static void modifyTxtFile(Path i_Path, String i_Content)
    {

        FileOutputStream writer = null;
        try
        {
            writer = new FileOutputStream(String.valueOf(i_Path.toString()));
            writer.write(("").getBytes());
            writer.write(i_Content.getBytes());
            writer.close();


        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void CreateAndWriteTxtFile(Path i_Path, String i_Content)
    {
        try (Writer out1 = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(i_Path.toString()), "UTF-8")))
        {
            out1.write(i_Content);
        } catch (UnsupportedEncodingException e)
        {//TODO

        } catch (FileNotFoundException e)
        {//TODO

        } catch (IOException e)
        {//TODO

        }
    }

    public static void Zip(String i_ZipName, Path i_FilePath)
    { //TODO handle exepction
        Path zippingPath = Magit.getMagitDir().resolve("objects").resolve(i_ZipName + ".zip");
        try
        {
            String sourceFile = i_FilePath.toString();
            FileOutputStream fos = new FileOutputStream(zippingPath.toString());
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0)
            {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
        } catch (IOException e)
        {

        }

    }

    public static int getNumberOfSubNodes(Path i_Path) throws IOException
    {
        return (int) Files.walk(i_Path, 1).count() - 1;
    }

    public static void deleteFile(Path i_FileToDelete)
    {
        // using NIO API
        // TODO handle exception
        try
        {

            Files.delete(i_FileToDelete);
        }
        catch (IOException e)
        {

        }
    }

}
