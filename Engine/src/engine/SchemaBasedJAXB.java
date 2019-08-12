package engine;

import mypackage.MagitRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.nio.file.Path;

public class SchemaBasedJAXB
{
    private MagitRepository xmlRepo;
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "mypackage";

    public MagitRepository createRepositoryFromXML(Path i_XMLFilePath) throws JAXBException
    {
        InputStream inputStream = SchemaBasedJAXB.class.getResourceAsStream("/xml/ex1-small.xml");//TODO change to i_XMLFILEPATH
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        xmlRepo = (MagitRepository)u.unmarshal(inputStream);

        return xmlRepo;
    }
}
