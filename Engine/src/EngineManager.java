import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
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
    private Repository m_Repository;
    private static String m_UserName = "";

    public static String getUserName()
    {
        return m_UserName;
    }

    public static void setUserName(String i_UserName)
    {
        EngineManager.m_UserName = i_UserName;
    }

    public void createRepository(Path i_RepPath, String i_Name) throws IOException
    {
        m_Repository = new Repository(i_RepPath, i_Name);
    }

    public boolean commit(String i_CommitMessage) throws IOException
    {
        return m_Repository.commit(i_CommitMessage, true) != null;
    }

    public boolean isPathExists(Path i_Path)
    {
        return Files.exists(i_Path);
    }

    public boolean isRepository(Path i_Path)
    {
        return Files.exists(i_Path.resolve(".magit"));
    }

    public void CreateDirectory(Path i_RepPath, String i_DirectoryName) throws IOException
    {
        Files.createDirectory(i_RepPath.resolve(i_DirectoryName));
    }

    public boolean isDirectoryNameValid(String i_RepositoryName)
    {//TODO implement this method
        return true;
    } //TODO

    public boolean isBranchExists(String i_BranchName)
    {
        return m_Repository.getMagit().getBranches().containsKey(i_BranchName);
    }

    public void createNewBranch(String i_BranchName)
    {
        m_Repository.createNewBranch(i_BranchName);
    }

    public boolean isBranchNameEqualsHead(String i_BranchName)
    {
        return i_BranchName.toUpperCase().equals("HEAD");
    }

    public OpenChanges getFileSystemStatus() throws IOException { return m_Repository.getFileSystemStatus(); }

    public boolean isFileSystemDirty(OpenChanges i_OpenChanges) { return !i_OpenChanges.isFileSystemClean(); }

    public void checkout(String branchName) throws IOException { m_Repository.checkout(branchName); }

    public Repository getRepository() { return m_Repository; }

    public void changeRepository(Path i_RepoPath) throws IOException
    {
        m_Repository = new Repository(i_RepoPath);
        m_Repository.loadRepository(i_RepoPath);
    }

    public boolean isDirectory(Path i_dirToCheck) { return FileUtilities.isDirectory(i_dirToCheck); }

    public boolean isBranchNameRepresentsHead(String i_BranchName)
    {
        Head head = m_Repository.getMagit().getHead();

        return head.getActiveBranch().getName().equals(i_BranchName);
    }

    public void deleteBranch(String i_BranchName)
    {
        m_Repository.deleteBranch(i_BranchName);
    }

    public void stashRepository(Path i_repositoryToStash) throws IOException
    {
        Path pathToDelete = i_repositoryToStash.resolve(".magit");
        FileUtils.cleanDirectory(pathToDelete.toFile());
        Files.delete(pathToDelete);
    }
}
