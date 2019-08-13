package engine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtilities
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


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void createAndWriteTxtFile(Path i_Path, String i_Content)
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

    public static void createZipFileFromContent(String i_ZipName, String i_Content , String i_NameOfTxtFileInsideZip)
    {
        Path createdTempTxtPath = Magit.getMagitDir().resolve("objects").resolve(i_NameOfTxtFileInsideZip + ".txt");
        createAndWriteTxtFile(createdTempTxtPath, i_Content);
        zip(i_ZipName, createdTempTxtPath);
        deleteFile(createdTempTxtPath);
    }

    public static void zip(String i_ZipName, Path i_FilePath)
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
        }
        catch (IOException e)
        {

        }

    }

    public static void unzip(String i_ZipName, Path i_DestToUnZip) throws IOException
    {
        String fileZip = Magit.getMagitDir().resolve("objects").resolve(i_ZipName).toString();
        File destDir = new File(i_DestToUnZip.toString());
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null)
        {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0)
            {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static int getNumberOfSubNodes(Path i_Path) throws IOException
    { // return number of sub non empty nodes
        List<Path> ListOfSubFolders = Files.walk(i_Path, 1)
                .filter(d -> d.toFile().isDirectory())
                .filter(d->d!= i_Path)
                .collect(Collectors.toList());
        int numOfSubBlobs = (int) Files.walk(i_Path, 1)
                .filter(d -> !d.toFile().isDirectory())
                .count();
        int numOfSubFolders = 0;
        for (Path path : ListOfSubFolders)
        {
            if (Files.walk(path, 1).count() - 1 != 0)
            {
                numOfSubFolders++;
            }
        }
        return numOfSubFolders + numOfSubBlobs;
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

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException
    {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator))
        {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static String getTxtFromZip(String i_ZipFileName, String i_TxtFileNameInZip) throws IOException
    {
        //read File From Zip Without extract here
        ZipFile zipFile = new ZipFile(Magit.getMagitDir().resolve("objects").resolve(i_ZipFileName).toString());
        ZipEntry zipEntry = zipFile.getEntry(i_TxtFileNameInZip);

        InputStream inputStream = zipFile.getInputStream(zipEntry);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1)
        {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }

    public static boolean isDirectory(Path i_dirToCheck)
    {
        return Files.isDirectory(i_dirToCheck);
    }
}
