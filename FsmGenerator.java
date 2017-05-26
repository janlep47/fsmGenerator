import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import java.util.SortedSet;
//
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// NOTE:  Should this create non-deterministic fsm's?  Meaning that a state can
//   have the same event lead to multiple different states.  For now, NO!

public class FsmGenerator {

    private HashMap<String, State> statesMap;

    private HashMap<String, State> unprocessedStates;
    private HashMap<String, State> processedStates;

    private HashMap<String, Event> eventList;

    

    private int numberStatesDesired;
    private int numberEventsDesired;
    private int maxNumberEventsPerState;
    // NOTE! if minNumberEventsPerState is NON-ZERO, then their must be some 
    //  looping, otherwise will have an INFINITE number of states
    private int minNumberEventsPerState;

    private double oneOverNumPossibleTrans;
    private double oneOverNumberEvents;

    // This will be a number from 0.0 to (slightly) less than 1.0
    private double percentLoopingTransitions;
    // This refers to which percentage of the looping transitions, should be 
    //   states whose next state is itself (self-looping); this will also be a
    //   number from 0.0 to 1.0
    private double percentSelfLooping;
    


    public FsmGenerator() {
        statesMap = new HashMap<String, State>();
        unprocessedStates = new HashMap<String, State>();
        processedStates = new HashMap<String, State>();
        eventList = new HashMap<String, Event>();
    }


    public void doit() {
        promptUserForParameters();
        generateStateList();
        generateEventList();
        // For now, start off with only 1 initial state, and make it be '0':
        State initialState = unprocessedStates.get("0");
        makeChildren(initialState);
        // For case when minNumberEventsPerState = 0, there may be 
        //  unprocessed states left; if so, these need to be tacked on 
        //  to some of the processed state(s), so that ALL the states are
        //  part of the fsm
        if (unprocessedStates.size() != 0) 
            fitInRemainingUnusedStates();
        statesMap = processedStates;
    }

    private void promptUserForParameters() {
        // FOR NOW!!
        numberStatesDesired = 5;
        numberEventsDesired = 3;
        //maxNumberEventsPerState = 2;
        //minNumberEventsPerState = 1;
        //  1/1 works    1/0 DOESN'T WORK    2/0 DOESN'T WORK
        maxNumberEventsPerState = 2;
        minNumberEventsPerState = 0;
        //
        //percentLoopingTransitions = 1.0/4;
        //percentSelfLooping = 1.0/3;
        percentLoopingTransitions = 0.0;
        percentSelfLooping = 0.0;

        oneOverNumPossibleTrans = 1.0 /
            (maxNumberEventsPerState - minNumberEventsPerState + 1);
        oneOverNumberEvents = 1.0 / numberEventsDesired;
    }

    private void generateStateList() {
        for (int i = 0; i < numberStatesDesired; i++) {
            String stateName = String.valueOf(i);
            unprocessedStates.put(stateName, new State(stateName));
        }
    }

    private void generateEventList() {
        for (int i = 0; i < numberEventsDesired; i++) {
            String eventName = getEventName(i);
            eventList.put(eventName, new Event(eventName));
        }
    }

    private String getEventName(int i) {
        int asciiChar = 'a';
        int newAsciiChar = asciiChar + (i % 26); // index modulus #chars in alphabet
        int asciiChar2 = i / 26;

        if (asciiChar2 == 0)
            return String.valueOf((char) newAsciiChar);
        else 
            return String.valueOf((char) newAsciiChar) +
                String.valueOf((char) (asciiChar + asciiChar2));
    }


    private void makeChildren(State s) {
        // Get a random number of transitions from this state:
        int numberTransitions = getRandomNumberOfTransitions();
        // Initialize local lists
        State[] nextStates = new State[numberTransitions];
        Event[] stateEvents = new Event[numberTransitions];
        State nextState;
        Event event;
        for (int i = 0; i < numberTransitions; i++) {
            // force deterministic for now
            do {
                // Get a random (child) event
                event = getRandomEvent();
            } while (eventAlreadyUsed(event, i-1, stateEvents));
            stateEvents[i] = event;
            // Now get a random next state that this event will lead to
            do {
                nextState = getRandomState(s);
                if (nextState == null) return; // OR do the createTransitions
                                              //  stuff, and THEN return!
            } while (stateAlreadyUsed(nextState, i-1, nextStates));
            nextStates[i] = nextState;
            s.createTransition(event, nextState);
        }
       // Now, add the (parent) state to the list of processed states
        processedStates.put(s.getName(), s);
        //  and remove this (parent) state from the list of unprocessed states:
        unprocessedStates.remove(s.getName());

        // Continue making children states for each of this states children states
        for (int i = 0; i < numberTransitions; i++) {
            nextState = nextStates[i];
            if (!aProcessedState(nextState))
                makeChildren(nextState);
        }
        return;
    }


    private boolean aProcessedState(State s) {
        String name = s.getName();
        State state = processedStates.get(name);
        if (state != null) return true;
        return false;
    }


    private boolean eventAlreadyUsed(Event event, int index, Event[] stateEvents) {
        for (int i = 0; i <= index; i++) {
            Event e = stateEvents[i];
            if (e == null) continue;
            if (e == event) return true;
        }
        return false;
    }

    private boolean stateAlreadyUsed(State state, int index, State[] nextStates) {
        for (int i = 0; i <= index; i++) {
            State s = nextStates[i];
            if (s == null) continue;
            if (s == state) return true;
        }
        return false;
    }
        


    private int getRandomNumberOfTransitions() {
        int randValue = (int) (Math.random() / oneOverNumPossibleTrans);
        randValue += minNumberEventsPerState;
        return randValue;
    }

    private Event getRandomEvent() {
        int randValue = (int) (Math.random() / oneOverNumberEvents);
        int asciiChar = 'a';
        int newAsciiChar = asciiChar + (randValue % 26);
        int asciiChar2 = randValue / 26;
        String eventName;
        if (asciiChar2 == 0)
            eventName = String.valueOf((char) newAsciiChar);
        else 
            eventName = String.valueOf((char) newAsciiChar) +
                String.valueOf((char) (asciiChar + asciiChar2));
        Event event = eventList.get(eventName);
        return event;
    }


    private State getRandomState(State parentState) {
        double randVal = Math.random();
        State nextState;
        // Handle looping next-state:
        if (randVal <= percentLoopingTransitions) {
            // OK, we'll do a looping next-state.  NOW, see if it should be 
            //   a self-looping transition, or looping to an already processed
            //   state:
            double randWhichLooping = Math.random();
            // test for case where we'll do a self-loop (state loops to itself)
            if (randWhichLooping <= percentSelfLooping &&
                !(alreadySelfLooping(parentState))) {
                nextState = parentState;
                // Otherwise, do a loop to a different, already processed state:
            } else {
                nextState = getRandomAlreadyProcessedState();
                if (nextState == null)
                    nextState = getRandomUnprocessedState(parentState);
            }
            // handle non-looping next-state:
        } else {
            nextState = getRandomUnprocessedState(parentState);
        }
        return nextState;
    }


    private boolean alreadySelfLooping(State s) {
        HashMap<Event, ArrayList<State>> transitions = 
            s.getAllTransitions();

        if (transitions == null) return false;
        for (Map.Entry<Event, ArrayList<State>> eventEntry : 
                 transitions.entrySet()) {
            ArrayList<State> nextStates = eventEntry.getValue();
            if (nextStates == null) continue;
            for (int j = 0; j < nextStates.size(); j++) {
                State nextState = nextStates.get(j);
                if (nextState == s) return true;
            }
        }
        return false;
    }
    

    private State getRandomAlreadyProcessedState() {
        // First, get the number of processed states:
        int size = processedStates.size();

        if (size == 0) return null;

        // Now get the list of processStates, so that we can index into it,
        //  rather than using a get:
        Set<String> keys = processedStates.keySet();
        String[] stateNames = keys.toArray(new String[0]);
        // Now, get a random number to index into this list of (processed)
        //  state names:
        
        int randIndex = (int) (Math.random() * size);
        String stateName = stateNames[randIndex];
        State nextState = processedStates.get(stateName);
        return nextState;
    }


    private State getRandomUnprocessedState(State s) {
        // First, get the number of unprocessed states:
        int size = unprocessedStates.size();

        if (size == 0) return null;

        // Now get the list of unprocessStates, so that we can index into it,
        //  rather than using a get:
        Set<String> keys = unprocessedStates.keySet();
        String[] stateNames = keys.toArray(new String[0]);
        if (size == 1) return unprocessedStates.get(stateNames[0]);
        
        double oneOverSize = 1.0 / size;

        State nextState;
        // Now, get a random number to index into this list of (unprocessed)
        //  state names:
        do {
            int randIndex = (int) (Math.random() / oneOverSize);
            String stateName = stateNames[randIndex];
            nextState = unprocessedStates.get(stateName);
            // Make sure the random state we get is NOT == s
        } while (nextState == s);
        return nextState;
    }

    
    // For the case of minNumberEventsPerState = 0, and still some 
    //  unprocessed states left, after creating random children from 
    //  the initial state:
    private void fitInRemainingUnusedStates() {
        // While any unprocessedStates left in list:
        while (unprocessedStates.size() > 0) {
            // First, pick a random PROCESSED state
            State processedState = getRandomAlreadyProcessedState();
            // Now, see if its number-of-children-states = maxNumberEventsPerState
            // If so, loop back up to pick a (new) random PROCESSED state
            if (processedState.getNumberOfTransitions() == maxNumberEventsPerState)
                continue;
            // Otherwise, pick a random UNPROCESSED state
            State unprocessedState = getRandomUnprocessedState(processedState);
            // Pick a random event
            Event event = getRandomEvent();
            // Now, create a transition from the random processed state to
            //  the random unprocessed state; NOTE that this child state will
            //  NOT have any children - ok, b/c minNumberEventsPerState = 0
            processedState.createTransition(event, unprocessedState);
            // Now, remove this child state from the list of unprocessed states:
            unprocessedStates.remove(unprocessedState.getName());
            processedStates.put(unprocessedState.getName(), unprocessedState);
        }
        return;
    }

// *****************************************



    public void printIt() {
        System.out.println("Here are the randomly generated states with events and next states:");
        System.out.println();
        SortedSet<String> keys = new TreeSet<String>(statesMap.keySet());
        for (String key : keys) { 
            State state = statesMap.get(key);
            System.out.println(state.getName());
            HashMap<Event, ArrayList<State>> transitions =
                state.getAllTransitions();
            for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
                Event event = transition.getKey();
                ArrayList<State> nextStates = transition.getValue();
                if (nextStates == null) continue;
                for (int i = 0; i < nextStates.size(); i++) {
                    State nextState = nextStates.get(i);
                    System.out.println("   "+event.getName()+"   "+nextState.getName());
                }
            }
        }
    }


// ****************************************


    private String getFsmFilename() {
        return "fsmExample1.fsm"; // FOR NOW ...
    }


    public void writeOutputFsmfile() {
        String outFileName = getFsmFilename();
        PrintWriter fileWriter;
        try {
            fileWriter = new PrintWriter(outFileName);
        } catch(FileNotFoundException e) {
            System.out.println("File '"+outFileName+"' not found ...");
            return;
        }
        fileWriter.println(statesMap.size());
        
        SortedSet<String> keys = new TreeSet<String>(statesMap.keySet());
        for (String key : keys) { 
            fileWriter.println();
            State state = statesMap.get(key);
            HashMap<Event, ArrayList<State>> transitions =
                state.getAllTransitions();
            fileWriter.println(state.getName() + "\t" + "0" +
                               "\t" + String.valueOf(getTotalNumberOfTransitions(transitions)));
            for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
                Event event = transition.getKey();
                ArrayList<State> nextStates = transition.getValue();
                if (nextStates == null) continue;
                for (int i = 0; i < nextStates.size(); i++) {
                    State nextState = nextStates.get(i);
                    fileWriter.println(event.getName() + "\t" +
                                       nextState.getName() + "\t" +
                                       "c" + "\t" + "o");
                }
            }
        }
        fileWriter.close();
    }



    private int getTotalNumberOfTransitions(HashMap<Event, ArrayList<State>> transitions) {
        int numberTransitions = 0;
        for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
                ArrayList<State> nextStates = transition.getValue();
                if (nextStates == null) continue;
                numberTransitions += nextStates.size();
        }
        return numberTransitions;
    }

// *****************************************

    public static void main(String[] args) {
        FsmGenerator fsmGenerator = new FsmGenerator();
        fsmGenerator.doit();
        fsmGenerator.printIt();
        fsmGenerator.writeOutputFsmfile();
    }
}
