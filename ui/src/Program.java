import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Program
{
    public static void main(String[] args) throws IOException, JAXBException {
        UIManager ui = new UIManager();
        ui.Run();
    }
}