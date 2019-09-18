package engine;

public class StringUtilities
{
    public static String makeSHA1Content(String i_Content, int i_NumberOfInfoToSHA1)
    {
        String newContent = "";
        String[] lines = i_Content.split(System.lineSeparator());
        for (String line : lines)
        {
            String[] members = line.split(",");
            for (int i = 0; i < i_NumberOfInfoToSHA1; i++)
            {
                newContent = newContent.concat(members[i]);
            }
            newContent.concat(System.lineSeparator());
        }

        return newContent.substring(0, newContent.length() - 2);
    }


    public static String getContentInformation(String i_Content, int i_Index)
    {
        String[] split = i_Content.split(",");

        return split[i_Index];
    }
}
