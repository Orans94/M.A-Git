package engine.objects;

import engine.utils.DateUtils;

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

    public Item(String i_ItemString)
    {
        String[] members = i_ItemString.split(",");
        m_Name = members[0];
        m_SHA1 = members[1];
        m_Type = members[2];
        m_Author = members[3];
        m_ModificationDate = DateUtils.FormatToDate(members[4]);
    }

    public void setSHA1(String i_SHA1)
    {
        this.m_SHA1 = m_SHA1;
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

    public String getSHA1() {return m_SHA1; }

    public String getName() { return m_Name; }

    public String getType() { return m_Type; }

    public String getAuthor() { return m_Author; }

    public Date getModificationDate() { return m_ModificationDate; }

    public void copyItemData(Item i_ItemToCopy)
    {
        m_Author = i_ItemToCopy.getAuthor();
        m_ModificationDate = i_ItemToCopy.getModificationDate();
        m_Name = i_ItemToCopy.getName();
        m_SHA1 = i_ItemToCopy.getSHA1();
        m_Type = i_ItemToCopy.getType();
    }
}
