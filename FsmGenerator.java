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
        
        Stack<State> unprocessedChildren = new Stack<State>();
        Stack<State> processedChildren = new Stack<State>();
        
        // Put all children of the initial state into the unprocessedChildren stack:
        saveUnprocessedChildren(initialState, unprocessedChildren);
        State s;
        do {
            while (unprocessedChildren.size() != 0) {
                s = unprocessedChildren.pop();
                makeChildren(s, false);
                processedChildren.push(s);
            }
            
            // If all the states have been assigned children (min to max - possibly 0),
            //  no need to find any more children states:
            if (unprocessedStates.size() == 0) break;
            
            // Now, all child states have been processed (new children created for each of the previous
            //  child states).  For each processed child state, find all of their child states and add
            //  to the unprocessedChildren stack.
            while (processedChildren.size() != 0) {
                s = processedChildren.pop();
                saveUnprocessedChildren(s, unprocessedChildren);
            }
        } while (unprocessedChildren.size() != 0  &&  processedStates.size() < stateNames.size());
        
        //
        // ************  Do some final processing  ************
        //
        // if unprocessedStates is NOT empty we need to tie these into the fsm:
        if (unprocessedStates.size() != 0) {
            
            //SortedSet<String> keys = new TreeSet<String>(unprocessedStates.keySet());
            HashSet<String> keys = new HashSet<String>(unprocessedStates.keySet());
            //HashSet<String> keys = (HashSet<String>) skeys.clone();
            for (String key : keys) {
                s = unprocessedStates.get(key);
                
                // If this unprocessed state is NOT a child of some other state, it needs to be added into
                //  the FSM as a child of some state already in the FSM:
                if (!stateIsChildStateSomewhere(s)) {
                    State childState = s;
                    Vector<String> fsmStates = (Vector<String>) stateNames.clone();
                    
                    State okParent;
                    State goodParent = null;
                    
                    // Find a potential parent state which doesn't already a number of transitions
                    //  equal to the maxNumberEventsPerState:
                    do {
                        int randValue = (int) (Math.random() * fsmStates.size());
                        String stateName = fsmStates.elementAt(randValue);
                        okParent = processedStates.get(stateName);
                        if (okParent == null)
                            okParent = unprocessedStates.get(stateName);
                        fsmStates.remove(randValue);
                        if (okParent.getNumberOfTransitions() < maxNumberEventsPerState) {
                            goodParent = okParent;
                            Vector<String> fsmEvents = findUnusedEvents(goodParent);
                            randValue = (int) (Math.random() * fsmEvents.size());
                            String eventName = fsmEvents.elementAt(randValue);
                            Event e = eventList.get(eventName);
                            goodParent.createTransition(e,childState);
                            break;
                        }
                    } while (fsmStates.size() > 0);
                    if (goodParent == null) {
                        Vector<String> fsmEvents = findUnusedEvents(okParent);
                        int randValue = (int) (Math.random() * fsmEvents.size());
                        String eventName = fsmEvents.elementAt(randValue);
                        Event e = eventList.get(eventName);
                        okParent.createTransition(e,childState); // oh well, too many transitions ...
                    }
                }
                processedStates.put(s.getName(),s);
                unprocessedStates.remove(s.getName());
            }
        }
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
                                                        
                                                                 
    private Vector<String> findUnusedEvents(State s) {
        Vector<String> fsmEvents = (Vector<String>) eventNames.clone();
        HashMap<Event, ArrayList<State>> transitions = s.getAllTransitions();
        for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
            Event event = transition.getKey();
            for (int i = 0; i < fsmEvents.size(); i++) {
                if ( ((String) fsmEvents.elementAt(i)).equals(event.getName()))
                    fsmEvents.remove(i);
            }
        }
        return fsmEvents;
    }
        
                                                        
                                                        
    private void saveUnprocessedChildren(State s, Stack<State> unprocessedChildren) {
        HashMap<Event, ArrayList<State>> transitions = s.getAllTransitions();
        for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
            Event event = transition.getKey();
            ArrayList<State> nextStates = transition.getValue();
            if (nextStates == null) continue;
            for (int i = 0; i < nextStates.size(); i++) {
                State nextState = nextStates.get(i);
                // Important: if state s has a transition to itself, don't add it to the unprocessed
                //  children stack; also don't add the transition state, if it has already been processed
                if (nextState != s && processedStates.get(nextState.getName()) == null)
                    unprocessedChildren.push(nextState);
            }
        }
    }

 
            
    private boolean stateIsChildStateSomewhere(State s) {
        SortedSet<String> keys = new TreeSet<String>(processedStates.keySet());
        for (String key : keys) {
            State state = processedStates.get(key);
            HashMap<Event, ArrayList<State>> transitions =
            state.getAllTransitions();
            for (Map.Entry<Event, ArrayList<State>> transition : transitions.entrySet()) {
                Event event = transition.getKey();
                ArrayList<State> nextStates = transition.getValue();
                if (nextStates == null) continue;
                for (int i = 0; i < nextStates.size(); i++) {
                    State nextState = nextStates.get(i);
                    if (nextState == s) return true;
                }
            }
        }
        return false;
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

        // Get a random number of transitions from this state:
        int numberTransitions = getRandomNumberOfTransitions();
        
        if (isInitialState && numberTransitions == 0)
            numberTransitions = 1;
        
        // Initialize local lists
        
        //Vector<String> availStates = new Vector<String>();
        //availStates.clone(stateNames);
        Vector<String> availStates = (Vector<String>) stateNames.clone();
        
        //Vector<String> availEvents = new Vector<String>();
        //availEvents.clone(eventNames);
        Vector<String> availEvents = (Vector<String>) eventNames.clone();
        
        State nextState;
        Event event;
        
        for (int i = 0; i < numberTransitions; i++) {
            event = null;
            nextState = null;
            // force deterministic for now; get a random (child) event
            if (availEvents.size() > 0) {
                int randValue = (int) (Math.random() * availEvents.size());
                String eventName = availEvents.elementAt(randValue);
                event = eventList.get(eventName);
                availEvents.remove(randValue);
            }
            if (event == null) break;

            // Now get a random next state that this event will lead to
            if (availStates.size() > 0){
                int randValue = (int) (Math.random() * availStates.size());
                String stateName = availStates.elementAt(randValue);
                nextState = unprocessedStates.get(stateName);
                if (nextState == null)
                    nextState = processedStates.get(stateName);
                availStates.remove(randValue);
            }
            if (nextState == null) break;
            s.createTransition(event, nextState);
        }
       // Now, add the (parent) state to the list of processed states
        processedStates.put(s.getName(), s);
        //  and remove this (parent) state from the list of unprocessed states:
        unprocessedStates.remove(s.getName());
        return;
    }



    private int getRandomNumberOfTransitions() {
        int randValue = (int) (Math.random() * (maxNumberEventsPerState - minNumberEventsPerState));
        randValue += minNumberEventsPerState;
        return randValue;
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
            
            fsmOutputFilename = inputPanel.getOutputFSMfilname();
            
            // Now can create the random fsm with the user-requested parameters:
            doit();
            //printIt();  // write stuff to terminal
            writeOutputFsmfile();
            System.exit(0);
        }
    }
                                                        
}
