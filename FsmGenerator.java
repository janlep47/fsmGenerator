import java.awt.*;
import java.awt.event.*;
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
import java.util.Vector;
//
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;
import java.util.List;
import java.util.Set;

import javax.swing.*;


// NOTE:  Should this create non-deterministic fsm's?  Meaning that a state can
//   have the same event lead to multiple different states.  For now, NO!

public class FsmGenerator extends JFrame implements ActionListener,
                                                    WindowListener {
    FsmInput inputPanel;

    private HashMap<String, State> statesMap;

    private HashMap<String, State> unprocessedStates;
    private HashMap<String, State> processedStates;

    private HashMap<String, Event> eventList;
                                                        
    private Vector<String> stateNames;
    private Vector<String> eventNames;
    

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
                                                        
    private String fsmOutputFilename;
                                                        
                                                        
                                                        
                                                        
                                                        
    public static void main(String[] args){
        FsmGenerator f = new FsmGenerator();
        f.setSize(480,412);
        f.setVisible(true);
        f.setLayout(null);
    }

    public FsmGenerator() {
        setTitle("FsmGenerator");
        setLayout(null);
        inputPanel = new FsmInput();
        inputPanel.setBounds(10,10,460,369);
        inputPanel.setVisible(true);
        
        inputPanel.initialize();
        
        Container cp;
        cp = this.getContentPane();
        cp.add(inputPanel);
        
        //Listeners
        this.addWindowListener(this);
        inputPanel.enter.addActionListener(this);

        // Initialize data structures
        statesMap = new HashMap<String, State>();
        unprocessedStates = new HashMap<String, State>();
        processedStates = new HashMap<String, State>();
        eventList = new HashMap<String, Event>();
        stateNames = new Vector<String>();
        eventNames = new Vector<String>();
    }


    public void doit() {
        // Create states and events:
        generateStateList();
        generateEventList();
        
        // For now, start off with only 1 initial state, and make it be '0':
        State initialState = unprocessedStates.get("0");
        makeChildren(initialState, true);
        // For case when minNumberEventsPerState = 0, there may be 
        //  unprocessed states left; if so, these need to be tacked on 
        //  to some of the processed state(s), so that ALL the states are
        //  part of the fsm
        if (unprocessedStates.size() != 0) 
            fitInRemainingUnusedStates();
        statesMap = processedStates;
    }

    private void generateStateList() {
        for (int i = 0; i < numberStatesDesired; i++) {
            String stateName = String.valueOf(i);
            unprocessedStates.put(stateName, new State(stateName));
            stateNames.add(stateName);
        }
    }

    private void generateEventList() {
        for (int i = 0; i < numberEventsDesired; i++) {
            String eventName = getEventName(i);
            eventList.put(eventName, new Event(eventName));
            eventNames.add(eventName);
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


    private void makeChildren(State s, boolean isInitialState) {
        // ONLY create children for this state, if there isn't any already:
        if (s.getNumberOfTransitions() > 0) return;
        // ONLY create children for this state, it not already done:
        if (processedStates.get(s.getName()) != null) {
            if (unprocessedStates.get(s.getName()) != null)
                unprocessedStates.remove(s.getName());
            return;
        }
        System.out.println(s.getName());
        // Get a random number of transitions from this state:
        int numberTransitions = getRandomNumberOfTransitions();
        
        if (isInitialState && numberTransitions == 0)
            numberTransitions = 1;
        
        State nextState;
        Event event;
        Vector<State> nextStates = new Vector<State>();
        for (int i = 0; i < numberTransitions; i++) {
            // force deterministic for now
            do {
                // Get a random (child) event
                event = getRandomEvent();
            } while (eventAlreadyUsed(s, event));
            // Now get a random next state that this event will lead to
            do {
                nextState = getRandomState(s);
                if (nextState == null) break;
            } while (stateAlreadyUsed(s, nextState));
            if (nextState != null) {
                s.createTransition(event, nextState);
                nextStates.add(nextState);
            }
        }
       // Now, add the (parent) state to the list of processed states
        processedStates.put(s.getName(), s);
        //  and remove this (parent) state from the list of unprocessed states:
        unprocessedStates.remove(s.getName());

        // Continue making children states for each of this states children states
        for (int i = 0; i < nextStates.size(); i++) {
            nextState = nextStates.get(i);
            if (!aProcessedState(nextState))
                makeChildren(nextState, false);
        }
        return;
    }


    private boolean aProcessedState(State s) {
        String name = s.getName();
        State state = processedStates.get(name);
        if (state != null) return true;
        return false;
    }


    private boolean eventAlreadyUsed(State s, Event e) {
        HashMap<Event, ArrayList<State>> transitions = s.getAllTransitions();
        for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
            Event event = transition.getKey();
            if (event == e) return true;
        }
        return false;
    }

    private boolean stateAlreadyUsed(State s, State nextS) {
        HashMap<Event, ArrayList<State>> transitions = s.getAllTransitions();
        for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
            Event event = transition.getKey();
            ArrayList<State> nextStates = transition.getValue();
            for (int i = 0; i < nextStates.size(); i++) {
                State nextState = nextStates.get(i);
                if (nextState == nextS) return true;
            }
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
        
        
        //int newAsciiChar = 'a';
        //String eventName = String.valueOf((char) newAsciiChar);
        
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
        String[] stateNames = keys.toArray(new String[keys.size()]);
        
        // if only one unprocessed state, return it IF it's not the same as the parent state
        //  (because this routine is for non-self looping transitions)
        if (size == 1) {
            if (s != unprocessedStates.get(stateNames[0]))
                return unprocessedStates.get(stateNames[0]);
            else return null;
        }
        
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

    //  File format:
    //
    //  <total number of states> <number of initial states ... 1 for now>
    //
    //  <state name1>  <# of transitions from this state>
    //  <event name>  <next state>
    //  .              .
    //  .              .
    //  .              .
    //  <event name>  <next state>
    //
    //  <state name2>  <# of transitions from this state>
    //  <event name2>  <next state>
    //  .
    //  .
    //  .
    public void writeOutputFsmfile() {
        String outFileName = fsmOutputFilename;
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
            fileWriter.println(state.getName() + "\t" +
                               String.valueOf(getTotalNumberOfTransitions(transitions)));
            for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
                Event event = transition.getKey();
                ArrayList<State> nextStates = transition.getValue();
                if (nextStates == null) continue;
                for (int i = 0; i < nextStates.size(); i++) {
                    State nextState = nextStates.get(i);
                    fileWriter.println(event.getName() + "\t" +
                                       nextState.getName() );
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

    public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
    }
    
    public void windowOpened(WindowEvent evt){}
    
    public void windowIconified(WindowEvent evt){}
    
    public void windowClosed(WindowEvent evt){}
    
    public void windowDeiconified(WindowEvent evt){}
    
    public void windowActivated(WindowEvent evt){}
    
    public void windowDeactivated(WindowEvent evt){}



    public void actionPerformed(ActionEvent evt){
        
        if (evt.getSource()==inputPanel.enter){
            numberStatesDesired = inputPanel.getNumStates();
            numberEventsDesired = inputPanel.getNumEvents();
            maxNumberEventsPerState = inputPanel.getMaxEventsPerState();
            minNumberEventsPerState = inputPanel.getMinEventsPerState();
            percentLoopingTransitions = inputPanel.getPercentLoopingTransitions();
            percentSelfLooping = inputPanel.getPercentSelfLooping();
            
            oneOverNumPossibleTrans = 1.0 /
                (maxNumberEventsPerState - minNumberEventsPerState + 1);
            oneOverNumberEvents = 1.0 / numberEventsDesired;

            fsmOutputFilename = inputPanel.getOutputFSMfilname();
            
            // Now can create the random fsm with the user-requested parameters:
            doit();
            //printIt();  // write stuff to terminal
            writeOutputFsmfile();
            System.exit(0);
        }
    }
                                                        
}
