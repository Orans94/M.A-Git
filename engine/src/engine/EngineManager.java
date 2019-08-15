package engine;

import mypackage.MagitRepository;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
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
    private Repository m_Repository;
    private static String m_UserName = "";

    public XMLManager getXMLManager() { return m_XMLManager; }

    private XMLManager m_XMLManager = new XMLManager();

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
        try
        {
            return Files.exists(i_Path);
        }
        catch (SecurityException ex)
        {
            throw new SecurityException("error: read access to file is denied");
        }
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

    public MagitRepository createXMLRepository(Path i_XMLFilePath) throws JAXBException, FileNotFoundException {
        SchemaBasedJAXB jaxb = new SchemaBasedJAXB();
        return jaxb.createRepositoryFromXML(i_XMLFilePath);
    }

    public void readRepositoryFromXMLFile(MagitRepository i_XMLRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException
    {
        // validation here
        m_Repository = new Repository(Paths.get(i_XMLRepository.getLocation()));
        m_Repository.loadXMLRepoToSystem(i_XMLRepository, i_XMLMagitMaps);
        m_Repository.checkout(m_Repository.getMagit().getHead().getActiveBranch().getName());
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

    public boolean isDirectoryEmpty(Path i_Path) throws IOException { return FileUtilities.getNumberOfSubNodes(i_Path) == 0;}

    public boolean isRepositoryNull() { return m_Repository == null;}

    public boolean isXMLRepositoryIsEmpty(MagitRepository i_XMLRepo) { return m_XMLManager.isXMLRepositoryIsEmpty(i_XMLRepo); }

    public boolean isBranchPointedCommitSHA1Empty(String i_BranchName)
    {
        return m_Repository.getMagit().getBranches().get(i_BranchName).getCommitSHA1() == null ||
                m_Repository.getMagit().getBranches().get(i_BranchName).getCommitSHA1().equals("");
    }

    public boolean isCommitSHA1Exists(String i_CommitSHA1) { return m_Repository.getMagit().getCommits().containsKey(i_CommitSHA1); }

    public void changeActiveBranchPointedCommit(String i_CommitSHA1) { m_Repository.changeActiveBranchPointedCommit(i_CommitSHA1); }

    public String getActiveBranchName() { return m_Repository.getMagit().getHead().getActiveBranch().getName();}
}
