package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericTreeNode<K,V> {

	private K key;
    private V value;
    private List<GenericTreeNode<K,V>> children;

    public GenericTreeNode() {
        super();
        children = new ArrayList<GenericTreeNode<K,V>>();
    }

    public GenericTreeNode(K key,V value) {
        this();
        setKey(key);
        setValue(value);
    }

    public List<GenericTreeNode<K,V>> getChildren() {
        return this.children;
    }

    public int getNumberOfChildren() {
        return getChildren().size();
    }

    public boolean hasChildren() {
        return (getNumberOfChildren() > 0);
    }

    public void setChildren(List<GenericTreeNode<K,V>> children) {
        this.children = children;
    }

    public void addChild(GenericTreeNode<K,V> child) {
        children.add(child);
    }

    public void removeChildren() {
        this.children = new ArrayList<GenericTreeNode<K,V>>();
    }

    public GenericTreeNode<K,V> getChildAt(int index) throws IndexOutOfBoundsException {
        return children.get(index);
    }

    public K getKey() {
        return this.key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }
    
    public String toString() {
        return getKey().toString() +" - "+ getValue().toString();
    }

    public String toStringVerbose() {
        String stringRepresentation = this + ":[";
        
        for (GenericTreeNode<K,V> node : getChildren()) {
        	if(node.hasChildren()){
        		stringRepresentation += node.toStringVerbose();
        	}
        	else{
        		stringRepresentation += node.toString();
        	}
        	stringRepresentation += ", ";
        }

        //Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
        Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(stringRepresentation);

        stringRepresentation = matcher.replaceFirst("");
        stringRepresentation += "]";

        return stringRepresentation;
    }
}

