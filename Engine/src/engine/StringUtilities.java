package engine;

public class StringUtilities
{
    public static String makeSHA1Content(String i_Content)
    {
        String newContent = "";
        String[] lines = i_Content.split(System.lineSeparator());
        for (String line : lines)
        {
            String[] members = line.split(",");
            newContent = newContent.concat(members[0])
                    .concat(",").concat(members[1])
                    .concat(",").concat(members[2])
                    .concat(System.lineSeparator());
        }

        return newContent.substring(0, newContent.length() - 2);
    }

    public static String getCommitInformation(String i_Content, int i_Index)
    { // TODO add safe nullPtrExpection
        String[] split = i_Content.split(",");
        return split[i_Index];
    }
}
