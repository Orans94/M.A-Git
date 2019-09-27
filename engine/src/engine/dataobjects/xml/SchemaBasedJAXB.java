package engine.xml;

import mypackage.MagitRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

public class SchemaBasedJAXB
{
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "mypackage";

    public MagitRepository createRepositoryFromXML(Path i_XMLFilePath) throws JAXBException, FileNotFoundException
    {
        File file = new File(i_XMLFilePath.toString());
        InputStream inputStream = new FileInputStream(file);
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();

        return (MagitRepository) u.unmarshal(inputStream);
    }
}
