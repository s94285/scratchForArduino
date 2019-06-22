package ece;

/**
 * This class is to replace {@link javafx.util.Pair} in JavaFX, which does not come with a default constructor for deserialize
 * @param <K> Key
 * @param <V> Value
 */
public class MyPair<K,V> {
    private K key;
    private V value;

    public MyPair(){
        key = null;
        value = null;
    }
    public MyPair(K k,V v){
        key = k;
        value = v;
    }
    public K getKey() {
        return key;
    }
    public void setKey(K key) {
        this.key = key;
    }
    public V getValue() {
        return value;
    }
    public void setValue(V value) {
        this.value = value;
    }
}
