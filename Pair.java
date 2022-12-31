 
public class Pair
{
    private final String key;
    private final GeometryValue value;

    public Pair(String aKey, GeometryValue aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public String key()   { return key; }
    public GeometryValue value() { return value; }
}