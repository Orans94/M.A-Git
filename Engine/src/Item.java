import java.util.Date;

public class Item
{
    private String m_Name;
    private String m_SHA1;
    private String m_Type;
    private String m_Author;
    private Date m_ModificationDate;

    public Item(String i_FileName, String i_SHA1, String i_Type, String i_AuthorName, Date i_ModificationDate)
    {
        m_Name = i_FileName;
        m_SHA1 = i_SHA1;
        m_Type = i_Type;
        m_Author = i_AuthorName;
        m_ModificationDate = i_ModificationDate;
    }

    @Override
    public String toString()
    {
        return m_Name + ',' + m_SHA1 + ',' + m_Type + ','
                + m_Author + ',' + DateUtils.FormatToString(m_ModificationDate);
    }

    public static String getSha1FromItemString(String i_ItemString)
    {
        String[] members = i_ItemString.split(",");
        return members[1];
    }

}
