//ModInput.java

/*
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
*/

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;  // NEW






public class ModInput extends Panel implements ItemListener, KeyListener {
    private static final Color bgcolor = new Color(236,236,236);
    private Label titlelabel;
    public Checkbox ch;
    public  Button enter;

    private Label labNumStates, labNumEvents, labMaxEventsPerState, labMinEventsPerState,
        labPercentLoops, labPercentSelfLoops;
    private TextField textNumStates, textNumEvents;
    private TextField textMaxEventsPerState, textMinEventsPerState;
    private TextField textPercentLoops, textPercentSelfLoops;
    private Panel p1, p2, p3, p4, p5, p6, p7, p8, p9;
    private boolean digitEntered = false;
    
    private Font labelfont, titlefont;

    
    public static void main(String[] args){
        Frame f = new Frame();
        ModInput p = new ModInput();
        f.setLayout(null);
        p.setSize(420,579);
        f.add(p);
        p.initialize();
        //f.setSize(991,660);
        f.setSize(420,579);
        f.setVisible(true);
        f.setLayout(null);
    }

    
    public ModInput(){
        super();
        setLayout(null);
        setBackground(bgcolor);
    }


    public void initialize() {
        
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        titlefont = new Font("SanSerif",Font.BOLD,16);
        labelfont = new Font("SanSerif",Font.BOLD,12);

        titlelabel = new Label("Input",Label.CENTER);
        titlelabel.setFont(new Font("SanSerif",Font.BOLD,16));
        
        p1 = new Panel();
        p1.add(titlelabel);


        p2 = new Panel();
        p3 = new Panel();
        p4 = new Panel();
        p5 = new Panel();
        p6 = new Panel();
        p7 = new Panel();
        p8 = new Panel();
        p9 = new Panel();
        
        
        
	labNumStates = new Label("number of states: ",Label.RIGHT);
        labNumStates.setFont(labelfont);
        labNumStates.setForeground(Color.black);
        
        textNumStates = new TextField(5);
        textNumStates.setFont(labelfont);
        textNumStates.setForeground(Color.black);
        textNumStates.setText("");
        
        p2.add(labNumStates);
        p2.add(textNumStates);
	labNumEvents = new Label("number of events: ",Label.RIGHT);
        labNumEvents.setFont(labelfont);
        labNumEvents.setForeground(Color.black);
        
        textNumEvents = new TextField(5);
        textNumEvents.setFont(labelfont);
        textNumEvents.setForeground(Color.black);
        textNumEvents.setText("");
        
        p3.add(labNumEvents);
        p3.add(textNumEvents);
    labMaxEventsPerState = new Label("maximum number of events per state: ",Label.RIGHT);
        labMaxEventsPerState.setFont(labelfont);
        labMaxEventsPerState.setForeground(Color.black);
        
        textMaxEventsPerState = new TextField(5);
        textMaxEventsPerState.setFont(labelfont);
        textMaxEventsPerState.setForeground(Color.black);
        textMaxEventsPerState.setText("");
        
        p4.add(labMaxEventsPerState);
        p4.add(textMaxEventsPerState);
    labMinEventsPerState = new Label("minimum number of events per state: ",Label.RIGHT);
        labMinEventsPerState.setFont(labelfont);
        labMinEventsPerState.setForeground(Color.black);
        
        textMinEventsPerState = new TextField(5);
        textMinEventsPerState.setFont(labelfont);
        textMinEventsPerState.setForeground(Color.black);
        textMinEventsPerState.setText("");
        
        p5.add(labMinEventsPerState);
        p5.add(textMinEventsPerState);
        
        
    ch = new Checkbox("  Any looping states",false);
        
    ch.setForeground(Color.black);
    ch.setFont(labelfont);
        p6.add(ch);
        
        
    labPercentLoops = new Label("percentage of transitions which are loops: ",Label.RIGHT);
        labPercentLoops.setFont(labelfont);
        labPercentLoops.setForeground(Color.black);
        
        textPercentLoops = new TextField(5);
        textPercentLoops.setFont(labelfont);
        textPercentLoops.setForeground(Color.black);
        textPercentLoops.setText("");
        textPercentLoops.setEditable(false);
        
        p7.add(labPercentLoops);
        p7.add(textPercentLoops);
        
    labPercentSelfLoops = new Label("percentage of looping transitions which are self-loops: ",Label.RIGHT);
        labPercentSelfLoops.setFont(labelfont);
        labPercentSelfLoops.setForeground(Color.black);
        
        textPercentSelfLoops = new TextField(5);
        textPercentSelfLoops.setFont(labelfont);
        textPercentSelfLoops.setForeground(Color.black);
        textPercentSelfLoops.setText("");
        textPercentSelfLoops.setEditable(false);
        
        p8.add(labPercentSelfLoops);
        p8.add(textPercentSelfLoops);

        
        enter = new Button("Create FSM");
        enter.setFont(labelfont);
        p9.add(enter);
        
        
        gbc.gridy = 1;
        add(p2,gbc);
        gbc.gridy = 2;
        add(p3,gbc);
        gbc.gridy = 3;
        add(p4,gbc);
        gbc.gridy = 4;
        add(p5,gbc);
        gbc.gridy = 5;
        add(p6,gbc);
        gbc.gridy = 6;
        add(p7,gbc);
        gbc.gridy = 7;
        add(p8,gbc);
        gbc.gridy = 8;
        add(p9,gbc);
        
        p7.setVisible(false);
        p8.setVisible(false);


        ch.addItemListener(this);
        textNumStates.addKeyListener(this);
        textNumEvents.addKeyListener(this);
        textMaxEventsPerState.addKeyListener(this);
        textMinEventsPerState.addKeyListener(this);
        textPercentLoops.addKeyListener(this);
        textPercentSelfLoops.addKeyListener(this);
    }
    
    
    
    public int getNumStates() {
        int numStates;
        try {
            numStates = Integer.parseInt(textNumStates.getText());
        } catch (NumberFormatException e) {
            numStates = 3;
        }
        return numStates;
    }
    
    public int getNumEvents() {
        int numEvents;
        try {
            numEvents = Integer.parseInt(textNumEvents.getText());
        } catch (NumberFormatException e) {
            numEvents = 3;
        }
        return numEvents;
    }

    public boolean getAnyLoops() {
        boolean anyLoops = ch.getState();
        return anyLoops;
    }
    
    
    public int getMaxEventsPerState() {
        int maxEventsPerState;
        try {
            maxEventsPerState = Integer.parseInt(textMaxEventsPerState.getText());
        } catch (NumberFormatException e) {
            maxEventsPerState = 3;
        }
        return maxEventsPerState;
    }
    
    
    public int getMinEventsPerState() {
        int minEventsPerState;
        try {
            minEventsPerState = Integer.parseInt(textMinEventsPerState.getText());
        } catch (NumberFormatException e) {
            minEventsPerState = 1;
        }
        return minEventsPerState;
    }
    
    
    public int getPercentLoops() {
        int percentLoops;
        try {
            percentLoops = Integer.parseInt(textPercentLoops.getText());
        } catch (NumberFormatException e) {
            percentLoops = 0;
        }
        return percentLoops;
    }
    
    
    public int getPercentSelfLoops() {
        int percentSelfLoops;
        try {
            percentSelfLoops = Integer.parseInt(textPercentSelfLoops.getText());
        } catch (NumberFormatException e) {
            percentSelfLoops = 0;
        }
        return percentSelfLoops;
    }

    

    
    
    
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() >= KeyEvent.VK_0  && evt.getKeyCode() <= KeyEvent.VK_9) ||
            evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            digitEntered = true;
        } else {
            digitEntered = false;
            evt.consume();
        }
        return;
    }
    
    public void keyReleased(KeyEvent evt) {
        if (!digitEntered) {
            evt.consume();
        }
    }
    
    public void keyTyped(KeyEvent evt) {
        if (!digitEntered) {
            evt.consume();
        }
    }
    
    
    public void itemStateChanged(ItemEvent evt){
        if (evt.getSource() == ch) {
            if (ch.getState()) {
                p7.setVisible(true);
                textPercentLoops.setEditable(true);
                p8.setVisible(true);
                textPercentSelfLoops.setEditable(true);
                validate();
            } else {
                p7.setVisible(false);
                textPercentLoops.setEditable(false);
                p8.setVisible(false);
                textPercentSelfLoops.setEditable(false);
                validate();
            }
        }
    }


    
    public void paint(Graphics g){
	
	g.setColor(bgcolor.darker());
	g.fillRect(0,getSize().height-2,getSize().width,2);
	g.fillRect(getSize().width-2,0,2,getSize().height);
	g.setColor(bgcolor.brighter());
	g.fillRect(0,0,2,getSize().height-1);
	g.fillRect(0,0,getSize().width-2,2);

	Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.black);
        g.setFont(titlefont);
        g.drawString("Input",10,20);
    }
    
}

