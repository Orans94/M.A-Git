import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//ENGINE ASSUMPTIONS:
//1.  m_Engine.ReadRepositoryFromXMLFile(XMLFilePath);
//    maybe returning a value if something is wrong and if so what is wrong(Exception or string?)
//2.  m_Engine.ChangeRepository(repoPath);
//    no need to check if path is legal and if it represents a magit repo, just change
//3.  m_Engine.GetWorkingCopy();
//    maybe create a class which wraps all details of the WC
//4.  m_Engine.CreateNewBranch(branchName);
//    no need to check if branchName already exists
//5.  m_Engine.DeleteBranch(branchName);
//    no need to check if branchName is Head or if does not exists
//6.  m_Engine.Checkout(branchName);
//    no need to check if status is clean, just checkout

public class EngineManager
{


    public void CreateRepository(Path i_RepPath) throws IOException // TODO catch this expection
    {
        Files.createDirectory(i_RepPath);
    }

    public boolean isPathExists(Path i_Path)
    {
        return Files.exists(i_Path);
    }

    public boolean IsRepository(Path i_Path)
    {
        return Files.exists(i_Path.resolve(".magit"));
    }
}