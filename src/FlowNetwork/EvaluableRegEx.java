package FlowNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class EvaluableRegEx<T>{

    private interface Expression{
        double evaluate();
    }
    private class Constant implements Expression {
        T content;
        Function<T, Double> evaluate;
        private Constant(T content, Function<T, Double> f){
            this.content = content;
            this.evaluate = f;
        }
        @Override
        public double evaluate(){
            return evaluate.apply(content);
        }
        @Override
        public String toString() {
            return content.toString();
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constant constant = (Constant) o;
            return content == constant.content;
        }
        @Override
        public int hashCode() {
            return content.hashCode();
        }
    }
    private static class Sum implements Expression {
        List<Expression> sum;
        private Sum(List<Expression> sum){
            this.sum = sum;
        }
        private Sum(Expression expression){
            this.sum = new ArrayList<>();
            this.sum.add(expression);
        }
        private Sum(){
            this.sum = new ArrayList<>();
        }
        public void add(Expression v){
            this.sum.add(v);
        }
        @Override
        public double evaluate() {
            double sum = 0;
            for(Expression v : this.sum)
                sum += 1 / v.evaluate();
            return 1 / sum;
        }
        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("(");
            for(Expression v : this.sum) s.append(v.toString()).append("||");
            s.delete(s.length() - 2, s.length());
            s.append(")");
            return s.toString();
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sum sum = (Sum) o;
            for(Expression v : this.sum)
                if(!sum.sum.contains(v)) return false;
            return true;
        }
        @Override
        public int hashCode() {
            return sum.hashCode();
        }
    }
    private static class Product implements Expression{
        List<Expression> product;

        private Product(List<Expression> product){
            this.product = product;
        }

        private Product(Expression expression){
            this.product = new ArrayList<>();
            this.product.add(expression);
        }

        private Product(){
            this.product = new ArrayList<>();
        }

        private void add(Expression v){
            this.product.add(v);
        }

        @Override
        public double evaluate() {
            double product = 0;
            for(Expression v : this.product)
                product += v.evaluate();
            return product;
        }
        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("(");
            for(Expression v : this.product) s.append(v.toString()).append("+");
            s.deleteCharAt(s.length()-1);
            s.append(")");
            return s.toString();
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Product product1 = (Product) o;
            for(Expression v : this.product)
                if(!product1.product.contains(v)) return false;
            return true;
        }
        @Override
        public int hashCode() {
            return product.hashCode();
        }
    }

    private final Function<T, Double> getValue;
    private final Expression regEx;

    public EvaluableRegEx(LADFA<T> LADFA, Function<T, Double> getValue){
        this.getValue = getValue;
        regEx = build(LADFA);
    }

    public double evaluate(){
        return regEx.evaluate();
    }

    @Override
    public String toString(){
        return regEx.toString();
    }

    private Expression build(LADFA<T> LADFA){
        LADFA<Expression> c = reduceAndTransmute(LADFA);
        ArrayList<State<Expression>> states = c.getStates();
        while(c.getStates().size() > 2 || c.getStates().get(c.getStart()[0]).size() > 1){
            reduceParallel(states);
            reduceSeries(states);}
        return c.getStates().getFirst().transitionsBetween(c.getStates().getLast()).getFirst();
    }


    private Sum sum(ArrayList<T> ts){
        ArrayList<Expression> terms = new ArrayList<>();
        for(T t : ts) terms.add(new Constant(t, this.getValue));
        return new Sum(terms);
    }

    // Transmutes a CNFA of type T to one of type Expression and unifies all links between two nodes to one
    // <0,a,1>, <0,b,1>, <0,c,2> -> <0,a+b,1>, <0,c,2>
    private LADFA<Expression> reduceAndTransmute(LADFA<T> fa) {
        ArrayList<State<Expression>> newStates = new ArrayList<>();
        for(State<T> oldState : fa.getStates()) newStates.add(new State<Expression>(oldState.getStateNumber()));
        for(State<T> oldState : fa.getStates()){
            for(State<T> nextState : oldState.nextStates()){
                newStates.get(oldState.getStateNumber()).
                        addTransition(sum(oldState.transitionsBetween(nextState)), newStates.get(nextState.getStateNumber()));
            }
        }
        return new LADFA<Expression>(newStates, fa.getStart()[0], fa.getFinal()[0]);
    }

    // If a state has several transitions to the same state it combines them into a sum
    private static void reduceParallel(ArrayList<State<Expression>> states){
        for(State<Expression> state : states){
            HashMap<State<Expression>, HashSet<Expression>> tMap = new HashMap<>();
            HashMap<Expression, State<Expression>> stateTransitions = state.getTransitions();
            for(Expression expression : stateTransitions.keySet()){
                State<Expression> nextState = stateTransitions.get(expression);
                HashSet<Expression> transitions = tMap.getOrDefault(nextState, new HashSet<>());
                transitions.add(expression);
                tMap.put(nextState, transitions);}
            if(state.getTransitions().size() == tMap.size()) continue;
            state.clear();
            for(State<Expression> next : tMap.keySet()){
                Sum sum = new Sum();
                for(Expression expression : tMap.get(next)) sum.add(expression);
                state.addTransition(sum, next);}}
    }

    // if a State has one incoming and one outgoing, it removes that state and adds combined transition to previous state
    private static void reduceSeries(ArrayList<State<Expression>> states){
        ArrayList<State<Expression>> toRemove = new ArrayList<>();
        for(State<Expression> state : states){
            if(toRemove.contains(state)) continue;
            for(State<Expression> next : state.nextStates()){
                if(next.previousStates().size() == 1 && next.nextStates().size() == 1){
                    State<Expression> nextOfNext = next.nextStates().iterator().next();
                    Product newTransition = new Product();
                    newTransition.add(state.transitionsBetween(next).getFirst());
                    newTransition.add(next.transitionsBetween(nextOfNext).getFirst());
                    state.addTransition(newTransition, nextOfNext);
                    state.removeTransition(state.transitionsBetween(next).getFirst());
                    next.removeTransition(next.transitionsBetween(nextOfNext).getFirst());
                    toRemove.add(next);}}}
        states.removeAll(toRemove);
    }


}
