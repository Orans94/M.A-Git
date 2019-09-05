package javafx.primary.top.popup.loadrepositorybyxml;

import javafx.AlertFactory;
import javafx.BrowseManager;
import javafx.StageUtilities;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import mypackage.MagitRepository;
import mypackage.MagitSingleFolder;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;

public class LoadRepositoryByXMLController implements PopupController
{

    private TopController m_TopController;

    @FXML private VBox createNewRepositoryComponent;
    @FXML private TextField xmlPathTextField;
    @FXML private Button browseButton;
    @FXML private Button loadButton;

    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }


    @FXML
    public void initialize()
    {
        // binding Create button to directory text field
        BooleanBinding isTextFieldEmpty = Bindings.isEmpty(xmlPathTextField.textProperty());
        loadButton.disableProperty().bind(isTextFieldEmpty);
    }

    @FXML
    void browseButtonAction(ActionEvent event)
    {

        BrowseManager browseManager = new BrowseManager();
        File selectedDirectory = browseManager.openFileChooser(event, "*.xml");

        // if user chose a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            xmlPathTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void loadButtonAction(ActionEvent event) throws IOException, JAXBException, ParseException
    {
        //TODO when creating repository from XML we assumed that the directory already exists
        //TODO ask aviad if thats ok or we should create the directory if it doesnt exists(C:/repo1)
        Path XMLFilePath;
        Path XMLRepositoryLocation;
        boolean isRepositoryAlreadyExistsInPath, toStash;

        XMLFilePath = Paths.get(xmlPathTextField.getText());
        MagitRepository XMLRepo = m_TopController.createXMLRepository(XMLFilePath);
        m_TopController.loadXMLRepoToMagitMaps(XMLRepo);
        XMLRepositoryLocation = Paths.get(XMLRepo.getLocation());
        if(!m_TopController.isPathExists(XMLRepositoryLocation))
        {
            m_TopController.createRepositoryPathDirectories(XMLRepositoryLocation);
        }

        isRepositoryAlreadyExistsInPath = m_TopController.isRepository(XMLRepositoryLocation);
        String repositoryName = XMLRepo.getName();

        if (m_TopController.isXMLRepositoryEmpty(XMLRepo))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                toStash = doesUserWantToStashExistingRepository();
                if (toStash)
                {
                    m_TopController.stashRepository(XMLRepositoryLocation);
                    m_TopController.createEmptyRepository(XMLRepositoryLocation, repositoryName);
                    m_TopController.clearCommitTableView();
                    m_TopController.addCommitsToTableView();
                    notifyRepositoryHasBeenLoaded();
                }
            }
            else
            {
                createNewRepository(XMLRepositoryLocation, repositoryName);
                m_TopController.clearCommitTableView();
                m_TopController.addCommitsToTableView();
                notifyRepositoryHasBeenCreated();
            }
        }
        else if (validateXMLRepository(XMLRepo, XMLFilePath, m_TopController.getMagitSingleFolderByID()))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                toStash = doesUserWantToStashExistingRepository();
                if (toStash)
                {
                    m_TopController.stashRepository(XMLRepositoryLocation);
                    m_TopController.readRepositoryFromXMLFile(XMLRepo, m_TopController.getXMLMagitMaps());
                    m_TopController.clearCommitTableView();
                    m_TopController.addCommitsToTableView();
                    notifyRepositoryHasBeenLoaded();
                }
            }
            else
            {
                if (m_TopController.isDirectoryEmpty(XMLRepositoryLocation))
                {
                    m_TopController.readRepositoryFromXMLFile(XMLRepo, m_TopController.getXMLMagitMaps());
                    m_TopController.clearCommitTableView();
                    m_TopController.addCommitsToTableView();
                    notifyRepositoryLoadedSuccessfullyFromXML(XMLRepo.getName());
                }
                else
                {
                    notifyTheLoadingWasCanceledFolderHasContent();
                }
            }
        }

        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    private void createNewRepository(Path i_RepositoryPath, String i_RepositoryName) throws IOException
    {
        m_TopController.createRepository(i_RepositoryPath, i_RepositoryName);
    }

    private boolean validateXMLRepository(MagitRepository i_XmlRepo, Path i_XMLFilePath, Map<String, MagitSingleFolder> i_MagitFolderByID)
    {
        boolean isXMLTotallyValid = true;

        if (!m_TopController.areIDsValid(i_XmlRepo))
        {
            // 3.2
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "There are 2 identical IDs")
                    .showAndWait();
        }

        if (!m_TopController.areFoldersReferencesValid(i_XmlRepo.getMagitFolders(), i_XmlRepo.getMagitBlobs()))
        {
            // 3.3, 3.4, 3.5
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "Folders references are not valid")
                    .showAndWait();
        }

        if (!m_TopController.areCommitsReferencesAreValid(i_XmlRepo.getMagitCommits(), i_MagitFolderByID))
        {
            // 3.6, 3.7
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "Commits references are not valid")
                    .showAndWait();
        }

        if (!m_TopController.areBranchesReferencesAreValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitCommits()))
        {
            // 3.8
            isXMLTotallyValid = false;

            AlertFactory.createErrorAlert("Load repository by XML", "Branches references are not valid")
                    .showAndWait();
        }

        if (!m_TopController.isHeadReferenceValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitBranches().getHead()))
        {
            isXMLTotallyValid = false;
            System.out.println("Head reference is not valid");
            AlertFactory.createErrorAlert("Load repository by XML", "Head reference is not valid")
                    .showAndWait();
        }

        return isXMLTotallyValid;
    }

    private void notifyRepositoryHasBeenCreated()
    {
        String repositoryName = m_TopController.getRepositoryName();
        String message = "A new repository " + repositoryName + " has been created successfully";

        AlertFactory.createInformationAlert("Load repository", message)
                .showAndWait();
    }


    private void notifyRepositoryHasBeenLoaded()
    {
        String repositoryName = m_TopController.getRepositoryName();
        String message = "Repository " + repositoryName + " has been loaded";

        AlertFactory.createInformationAlert("Load repository", message)
                .showAndWait();
    }


    private boolean doesUserWantToStashExistingRepository()
    {
        // ask user if he want to stash the existing repository
        Alert alert = AlertFactory.createYesNoAlert("Create new repository", "Would you like to stash the existing repository?");

        return alert.showAndWait().get().getText().equals("Yes");
    }

    private void notifyTheLoadingWasCanceledFolderHasContent()
    {
        AlertFactory.createErrorAlert("Load repository by XML", "The directory already has content in it")
        .showAndWait();
    }

    private void notifyRepositoryLoadedSuccessfullyFromXML(String i_RepoName)
    {
        AlertFactory.createInformationAlert("Load repository by XML", "Repository " + i_RepoName + " loaded successfully from xml file")
        .showAndWait();
    }

}

