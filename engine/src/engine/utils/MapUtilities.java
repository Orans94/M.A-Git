package engine.utils;

import java.util.*;
import java.util.stream.Stream;

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

    public static  <K, V> K getKeyFromMap(Map<K, V> map, V value)
    {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findAny()
                .get();
    }
}
