import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils
{
    private String m_FormatedDate;

    public static String Format(Date i_Date)
    {
        DateFormat parseFormat = new SimpleDateFormat(
                "dd.mm.yyyy-hh:mm:ss:sss");

        return parseFormat.format(i_Date);
    }
}

