//ModInput.java
/*
        // Define the panel to hold the components
        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
 
        // Put constraints on different buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JButton("Button1"), gbc);
 
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new JButton("Button 2"), gbc);
 
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        panel.add(new JButton("Button 3"), abc);
*/

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
//import java.awt.font.*;
import java.text.*;
import java.io.*;  // NEW
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.*;

//import java.util.Map;
//import java.util.Hashtable;
//import java.util.jar.Attributes;






public class ModInput extends JPanel implements ItemListener, KeyListener {
    private static final Color bgcolor = new Color(236,236,236);
    private JLabel titlelabel;
    public JCheckBox ch;
    public  JButton enter;

    private JLabel labNumStates, labNumEvents, labMaxEventsPerState, labMinEventsPerState,
        labPercentLoops, labPercentSelfLoops;
    private JFormattedTextField textNumStates, textNumEvents;
    private JFormattedTextField textMaxEventsPerState, textMinEventsPerState;
    private JFormattedTextField textPercentLoops, textPercentSelfLoops;
    private JPanel p1, p2, p3, p4, p5, p6, p7, p8, p9;
    //public Scrollbar sliderPercentLoops;
    //public Scrollbar sliderPercentSelfLoops;
    
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
        
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        //panel.add(new JButton("Button1"), gbc);

        titlefont = new Font("SanSerif",Font.BOLD,16);
        labelfont = new Font("SanSerif",Font.BOLD,12);

        //titlelabel = new JLabel("Input",Label.CENTER);
        titlelabel = new JLabel("Input",JLabel.CENTER);
        titlelabel.setFont(new Font("SanSerif",Font.BOLD,16));
        
        p1 = new JPanel();
        p1.add(titlelabel);



        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        p6 = new JPanel();
        p7 = new JPanel();
        p8 = new JPanel();
        p9 = new JPanel();
        
        
        
        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        
        NumberFormatter numberFormatter = new NumberFormatter(intFormat);
        numberFormatter.setValueClass(Integer.class); //optional, ensures you will always get an Int value
        numberFormatter.setAllowsInvalid(false); //this is the key!!
        numberFormatter.setMinimum(0); //Optional
        numberFormatter.setMaximum(5000); //Optional

        
        
	labNumStates = new JLabel("number of states: ",Label.RIGHT);
        labNumStates.setFont(labelfont);
        labNumStates.setForeground(Color.black);
        
        textNumStates = new JFormattedTextField(numberFormatter);
        textNumStates.setColumns(4);
        textNumStates.setFont(labelfont);
        textNumStates.setForeground(Color.black);
        textNumStates.setText("");
        
        p2.add(labNumStates);
        p2.add(textNumStates);
	labNumEvents = new JLabel("number of events: ",Label.RIGHT);
        labNumEvents.setFont(labelfont);
        labNumEvents.setForeground(Color.black);
        
        textNumEvents = new JFormattedTextField(5);
        textNumEvents.setColumns(4);
        textNumEvents.setFont(labelfont);
        textNumEvents.setForeground(Color.black);
        textNumEvents.setText("");
        
        p3.add(labNumEvents);
        p3.add(textNumEvents);
    labMaxEventsPerState = new JLabel("maximum number of events per state: ",Label.RIGHT);
        labMaxEventsPerState.setFont(labelfont);
        labMaxEventsPerState.setForeground(Color.black);
        
        textMaxEventsPerState = new JFormattedTextField(5);
        textMaxEventsPerState.setColumns(4);
        textMaxEventsPerState.setFont(labelfont);
        textMaxEventsPerState.setForeground(Color.black);
        textMaxEventsPerState.setText("");
        //textMaxEventsPerState.setText("3");
        
        p4.add(labMaxEventsPerState);
        p4.add(textMaxEventsPerState);
    labMinEventsPerState = new JLabel("minimum number of events per state: ",Label.RIGHT);
        labMinEventsPerState.setFont(labelfont);
        labMinEventsPerState.setForeground(Color.black);
        
        textMinEventsPerState = new JFormattedTextField(5);
        textMinEventsPerState.setColumns(4);
        textMinEventsPerState.setFont(labelfont);
        textMinEventsPerState.setForeground(Color.black);
        textMinEventsPerState.setText("");
        //textMinEventsPerState("3");
        
        p5.add(labMinEventsPerState);
        p5.add(textMinEventsPerState);
        
        
    ch = new JCheckBox("  Any looping states",false);
        
    ch.setForeground(Color.black);
    ch.setFont(labelfont);
        p6.add(ch);
        
        
    labPercentLoops = new JLabel("percentage of transitions which are loops: ",Label.RIGHT);
        labPercentLoops.setFont(labelfont);
        labPercentLoops.setForeground(Color.black);
        //labPercentLoops.setVisible(false);
        
        textPercentLoops = new JFormattedTextField(5);
        textPercentLoops.setColumns(4);
        textPercentLoops.setFont(labelfont);
        textPercentLoops.setForeground(Color.black);
        textPercentLoops.setText("");
        //textPercentLoops.setVisible(false);
        textPercentLoops.setEditable(false);
        
        p7.add(labPercentLoops);
        p7.add(textPercentLoops);
        //p7.setVisible(false);
        
    labPercentSelfLoops = new JLabel("percentage of looping transitions which are self-loops: ",Label.RIGHT);
        labPercentSelfLoops.setFont(labelfont);
        labPercentSelfLoops.setForeground(Color.black);
        //labPercentSelfLoops.setVisible(false);
        
        textPercentSelfLoops = new JFormattedTextField(5);
        textPercentSelfLoops.setColumns(4);
        textPercentSelfLoops.setFont(labelfont);
        textPercentSelfLoops.setForeground(Color.black);
        textPercentSelfLoops.setText("");
        //textPercentSelfLoops.setVisible(false);
        textPercentSelfLoops.setEditable(false);
        
        p8.add(labPercentSelfLoops);
        p8.add(textPercentSelfLoops);
        //p8.setVisible(false);

        
        enter = new JButton("Create FSM");
        enter.setFont(labelfont);
        p9.add(enter);

        // ADD the panels:

        //add(p1,gbc);
        
        //add(p1);
        //p1.setBounds(20,5,50,18);
        
        
        
        /*
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField field = new JFormattedTextField(formatter);
*/
        
        
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
    }
    
    
    
    
    
    
    public String getOutputFSMfilname() {
        JFileChooser chooser = new JFileChooser();
        
        chooser.setSelectedFile(new File("newFSM.fsm"));
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                                     "FSM files", "fsm");
        chooser.setFileFilter(filter);
        //JFrame parent = (JFrame) getParent();
        //int returnVal = chooser.showOpenDialog(parent);
        
        int returnVal = chooser.showSaveDialog(this);
        
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                               chooser.getSelectedFile().getName());
            System.out.println(" ... this is the current directory: " +
                               chooser.getCurrentDirectory().getName());
            System.out.println(" ... path: " +
                               chooser.getSelectedFile().getPath());
            System.out.println(" ... 2 path: " +
                               chooser.getSelectedFile().getAbsolutePath());
            //System.out.println(" ... 3 path: " +
            //                   chooser.getSelectedFile().getCanonicalPath());
        }
        if(returnVal == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile().getPath();
        return null;
    }

    
    
    
    //private TextField textNumStates, textNumEvents;
    //private TextField textMaxEventsPerState, textMinEventsPerState;
    //private TextField textPercentLoops, textPercentSelfLoops;
    public int getNumStates() {
        int numStates;
        try {
            numStates = Integer.parseInt(textNumStates.getText());
        } catch (NumberFormatException e) {
            numStates = 3;
        }
        return numStates;
    }
    
    
    public void keyPressed(KeyEvent evt) {
        //if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
    }
    
    public void keyReleased(KeyEvent evt) {
    }
    
    public void keyTyped(KeyEvent evt) {
    }
    
    
    public void itemStateChanged(ItemEvent evt){
        if (evt.getSource() == ch) {
            if (ch.isSelected()) {
                p7.setVisible(true);
                textPercentLoops.setEditable(true);
                p8.setVisible(true);
                textPercentSelfLoops.setEditable(true);
                //p7.invalidate();
                //p8.invalidate();
                validate();
            } else {
                p7.setVisible(false);
                textPercentLoops.setEditable(false);
                p8.setVisible(false);
                textPercentSelfLoops.setEditable(false);
                //p7.invalidate();
                //p8.invalidate();
                validate();
            }
        }
    }


    
    

/*
    public void reset() {
        ch2.setState(true);   // set to "Place Charge"
        ch01.setState(false);
        ch02.setState(false);
        ch03.setState(false);
        state.displayPotentialField = false;
        state.displayEquiPotentialLines = false;
        state.displayElectricField = false;        
        state.voltageAtCursor = 0.0;
        state.efieldAtCursor = 0.0; 
        textv.setText("0");
        texte.setText("0");
        state.chargeToAdd = state.chargeMax;
	textc.setText(""+.format(state.chargeToAdd));
	sliderc.setValue((int)((state.chargeToAdd-state.chargeMin)*
                               ScrollMax/(state.chargeMax-state.chargeMin)));
        sliderdv.setValue(7);
        sa.reset();
    }
*/

/*
    public void clearShowVoltageStuff() {
        textv.setText("0");
        texte.setText("0");
        sa.reset();
    }
*/

    
    //public void paint(Graphics g){
    protected void paintComponent(Graphics g) {
	
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
        //g.setFont(TheFonts.bold16);
        //g.drawLine(5,yLine,455,yLine);
        //g.setFont(TheFonts.bold14);
        g.setFont(titlefont);
        g.drawString("Input",10,20);
    }
    
}

