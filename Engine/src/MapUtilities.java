import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapUtilities
{
    public static<K, V> Map<K,V> deepClone (Map<K,V> i_MapToDeepCopy)
    {
        HashMap<K,V> newHashMap = new HashMap<>();
        for (Map.Entry<K,V> entry : i_MapToDeepCopy.entrySet())
        {
            newHashMap.put(entry.getKey(), entry.getValue());
        }
        return newHashMap;
    }
}
