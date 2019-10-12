package magithub.utils;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;

public class OnShutdown implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        System.out.println("Server up");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        // delete C:\magit-ex3
        try
        {
            System.out.println("Server down");
            FileUtils.deleteDirectory(new File("C:\\magit-ex3"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
