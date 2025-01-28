package FlowNetwork;

import java.util.ArrayList;
import java.util.Arrays;

// Linear Acyclic Determinate Finite Automata
// M = < Q, A, D, s, f >

public class LADFA<T> {

    private final int[] Q;
    private final ArrayList<State<T>> states;
    private final int s;
    private final int f;

    public LADFA(ArrayList<State<T>> states, int s, int f) {
        this.states = states;
        this.s = s;
        this.f = f;
        Q = new int[states.size()];
        for(int i = 0; i < Q.length; i++) Q[i] = states.get(i).getStateNumber();
    }

    public int[] getStart() {
        return new int[]{s};
    }
    public int[] getFinal() {
        return new int[]{f};
    }

    protected ArrayList<State<T>> getStates(){
        return states;
    }

    public boolean parse(ArrayList<T> string){
        State<T> currentState = this.states.get(this.s);
        for(T t : string)
            try{ currentState = currentState.next(t); }
            catch(Exception e){ return false; }
        return states.get(f).equals(currentState);
    }

    public void print(){
        System.out.println("CNFA:");
        System.out.println("Alphabet = " + Arrays.toString(Q));
        System.out.println("States:");
        for(int i = 0; i < states.size(); i++)
            System.out.println(states.get(i));
        System.out.print("Start = " + s);
        System.out.println("Final = " + f);
        System.out.println();
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("<{");
        for(int i : Q) str.append(i).append(",");   //states
        str.delete(str.length()-1, str.length());
        str.append("}, {{");
        for(State<T> s : states) str.append(s.toString()).append("}, ");    //transitionsBetween functions
        str.delete(str.length()-1, str.length());
        str.append("}, ");
        str.append(s).append(", {");
        str.append(f);
        str.append("}>");
        return str.toString();
    }

}
