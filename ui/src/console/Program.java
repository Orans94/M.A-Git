package console;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class Program
{
    public static void main(String[] args) throws IOException, JAXBException
    {
        UIManager ui = new UIManager();
        ui.Run();
    }
}