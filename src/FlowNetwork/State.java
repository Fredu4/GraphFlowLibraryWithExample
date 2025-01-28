package FlowNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class State<T> {

    private class Transition{
        T t;
        State<T> transitionsTo;
        public Transition(T t, State<T> transitionsTo){
            this.t = t;
            this.transitionsTo = transitionsTo;
        }
        @Override
        public boolean equals(Object o){
            Transition other = (Transition) o;
            if(this.transitionsTo == null && other.transitionsTo == null) return true;
            if(this.transitionsTo == null || other.transitionsTo == null) return false;
            return this.t.equals(other.t) && this.transitionsTo.equals(other.transitionsTo);
        }
        @Override
        public int hashCode(){
            return t.hashCode() + transitionsTo.hashCode();
        }
    }

    private int stateNumber;
    private final ArrayList<Transition> transitions;
    private final ArrayList<Transition> previous;

    public State(int stateNumber){
        this.transitions = new ArrayList<>();
        this.previous = new ArrayList<>();
        this.stateNumber = stateNumber;
    }
    public State<T> next(T transition){
        for(Transition t : this.transitions)
            if(t.t.equals(transition)) return t.transitionsTo;
        throw new RuntimeException("No transitionsBetween found for " + transition + " in state " + stateNumber + this.toString());
    }

    public boolean contains(T transition){
        return this.transitions.contains(transition);
    }

    protected HashMap<T, State<T>> getTransitions(){
        HashMap<T, State<T>> nextStates = new HashMap<>();
        for(Transition t : transitions)
            nextStates.put(t.t, t.transitionsTo);
        return nextStates;
    }

    protected HashSet<State<T>> nextStates(){
        HashSet<State<T>> nextStates = new HashSet<>();
        for(Transition t : transitions) nextStates.add(t.transitionsTo);
        return nextStates;
    }

    protected ArrayList<T> transitionsBetween(State<T> state){
        ArrayList<T> transitionsBetween = new ArrayList<>();
        for(Transition t : transitions) if(t.transitionsTo == state) transitionsBetween.add(t.t);
        if(transitionsBetween.isEmpty()) throw new RuntimeException("No transitionsBetween found between " + getStateNumber() + " and " + state.getStateNumber());
        return transitionsBetween;
    }

    public int size(){
        return transitions.size();
    }

    protected void addTransition(T t, State<T> transitionsTo){
        Transition transition = new Transition(t, transitionsTo);
        if(transitions.contains(transition)) return;
        transitions.add(transition);
        transitionsTo.addPrevious(t, this);
    }

    protected void addPrevious(T t, State<T> transitionsFrom){
        Transition transition = new Transition(t, transitionsFrom);
        if(!previous.contains(transition)) previous.add(transition);
    }

    protected HashMap<T, State<T>> previousStates(){
        HashMap<T, State<T>> previousStates = new HashMap<>();
        for(Transition t : previous)
            previousStates.put(t.t, t.transitionsTo);
        return previousStates;
    }

    private void removePrevious(T t){
        for(Transition transition : previous)
            if(transition.t.equals(t)) {
                previous.remove(transition);
                return;
            }
        throw new RuntimeException("No transitionsBetween found for " + t + " in state " + stateNumber + this);
    }

    protected void removeTransition(T t){
        for(Transition transition : transitions)
            if(transition.t.equals(t)){
                transition.transitionsTo.removePrevious(t);
                transitions.remove(transition);
                return;
            }
        throw new RuntimeException("No transition " + t + " to remove from" + this);
    }

    private void clearPreviousOfState(State<T> state){
        ArrayList<Transition> toRemove = new ArrayList<>();
        for(Transition t : previous)
            if(t.transitionsTo == state) toRemove.add(t);
        previous.removeAll(toRemove);
    }

    protected void clear(){
        for(Transition t : transitions) t.transitionsTo.clearPreviousOfState(this);
        transitions.clear();
    }

    public int getStateNumber(){
        return stateNumber;
    }
    public void setStateNumber(int stateNumber){
        this.stateNumber = stateNumber;
    }

    @Override
    public String toString(){
        if(transitions.isEmpty()) return "<"+stateNumber+">";
        StringBuilder str = new StringBuilder();
        for(Transition t : transitions)
            str.append("<" + stateNumber+","+t.t +","+t.transitionsTo.stateNumber+">,");
        if(str.length() > 0) str.deleteCharAt(str.length()-1);
        return str.toString();
    }

}