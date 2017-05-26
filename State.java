
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class State {

    public State() {
        transitions = new HashMap<Event, ArrayList<State>>();
    };
    
    public State(String stateName, boolean isInitial, boolean isNonsecret) {
        name = stateName;
        transitions = new HashMap<Event, ArrayList<State>>();
    }
    public State(String stateName) {
        name = stateName;
        transitions = new HashMap<Event, ArrayList<State>>();
    }
    
    public void createTransition(Event event, State nextState) {
        if (!transitions.containsKey(event))
            transitions.put(event, new ArrayList<State>());
        (transitions.get(event)).add(nextState);
        updateNumberOfTransitions();
    }
    /*
     * This function does not update number of transitions
     * It fill out the transition table
     */
    public void addTransition(Event event, State nextState) {
        if (!transitions.containsKey(event))
            transitions.put(event, new ArrayList<State>());
        (transitions.get(event)).add(nextState);
    }
    
    /*
     * This function does not update number of transitions
     * It fill out the transition table
     */
    public void removeTransition(Event event, State nextState) {
        if (transitions.containsKey(event)) {
            ArrayList<State> toStates = transitions.get(event);
            if (toStates != null) {
                for (int i = 0; i < toStates.size(); i++) {
                    State toState = toStates.get(i);
                    if (toState.getName().equals(nextState.getName())) {
                        toStates.remove(i);
                        return;
                    }
                }
            }
        }
    }

    public void setTransitions(HashMap<Event, ArrayList<State>> newTransitions) {
        transitions = newTransitions;
    }
    
    
    public int getNumberOfTransitions() {
        return numberOfTransitions;
    }
    
    public void updateNumberOfTransitions() {
        numberOfTransitions = 0;
        for (Map.Entry<Event, ArrayList<State>> transitionEntry : transitions
                 .entrySet())
            numberOfTransitions += transitionEntry.getValue().size();
    }
    
    public HashMap<Event, ArrayList<State>> getAllTransitions() {
        return transitions;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
        name = newName;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public ArrayList<State> getNextStateList(Event event) {
        if ((transitions.containsKey(event)))
            return transitions.get(event);
        else
            return null;
    }
    
    public static class StateComparator implements Comparator<State> {
        @Override
            public int compare(State s1, State s2) {
            return (s1.getName().compareTo(s2.getName()));
        }
    }

    protected int index = -1;
    protected String name;
    protected int numberOfTransitions = 0;
    protected HashMap<Event, ArrayList<State>> transitions;

}
