import java.util.Map;
import java.util.stream.Collectors;

public class MapUtilities
{
    public static<K, V> Map<K,V> deepClone (Map<K,V> i_MapToDeepCopy)
    {
        return i_MapToDeepCopy.entrySet().stream()
                .collect(Collectors
                .toMap(Map.Entry::getKey,Map.Entry::getValue));
    }
}
