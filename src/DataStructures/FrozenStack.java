package DataStructures;


import java.util.Collection;
import java.util.Stack;

public class FrozenStack<T>{

    private final Stack<T> stack;
    private boolean isFrozen;

    public FrozenStack(Collection<? extends T> collection) {
        stack = new Stack<>();
        stack.addAll(collection);
        isFrozen = true;
    }

    public FrozenStack(){
        stack = new Stack<>();
        isFrozen = false;
    }

    public void freeze(){
        isFrozen = true;
    }

    public boolean push(T t){
        if(isFrozen) return false;
        stack.push(t);
        return true;
    }

    public T pop(){
        return stack.pop();
    }

    public boolean isEmpty(){
        return stack.isEmpty();
    }
}
