package Model.BoundNetwork;

import java.util.ArrayList;
import java.util.function.Function;

public class BoundNetwork<T extends Comparable<T>>{

    private class Bound {

        private final Function<T, Integer> lowerBound;
        private final Function<T, Integer> upperBound;

        Bound(Function<T, Integer> lowerBound, Function<T, Integer> upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
        boolean contains(T t){
            return 0 <= lowerBound.apply(t) && upperBound.apply(t) <= 0;
        }

        boolean isLowerBound(T t){
            return lowerBound.apply(t) == 0;
        }

        boolean isUpperBound(T t){
            return upperBound.apply(t) == 0;
        }
    }

    private final ArrayList<Bound> bounds;

    public BoundNetwork(){
        bounds = new ArrayList<>();
    }


    public void addBound(T lowerBound, T upperBound){
        Function<T, Integer> largerOrEqualTo = lowerBound::compareTo;
        Function<T, Integer> smallerOrEqualTo = upperBound::compareTo;
        Bound newBound = new Bound(largerOrEqualTo, smallerOrEqualTo);
        bounds.add(newBound);
    }

    public boolean contains(T t){
        return bounds.stream().anyMatch(b -> b.contains(t));
    }

    public boolean isBound(T t){
        return bounds.stream().anyMatch(b -> b.isLowerBound(t)) || bounds.stream().anyMatch(b -> b.isUpperBound(t));
    }

}
