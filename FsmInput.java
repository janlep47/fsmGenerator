import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;  // NEW
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.*;


public class FsmInput extends JPanel implements ItemListener {
    private static final Color bgcolor = new Color(236,236,236);
    private JLabel titlelabel;
    public JCheckBox ch;
    public  JButton enter;
    
    public static final int MAXIMUM_NUMBER_OF_STATES = 1000;
    public static final int MAXIMUM_PERCENTAGE = 80;
    public static final int DEFAULT_NUMBER_OF_STATES = 3;
    public static final int DEFAULT_NUMBER_OF_EVENTS = 3;
    public static final int DEFAULT_MAX_EVENTS_PER_STATE = 2;
    public static final int DEFAULT_MIN_EVENTS_PER_STATE = 0;

    private JLabel labNumStates, labNumEvents, labMaxEventsPerState, labMinEventsPerState,
        labPercentLoops, labPercentSelfLoops;
    private JFormattedTextField textNumStates, textNumEvents;
    private JFormattedTextField textMaxEventsPerState, textMinEventsPerState;
    private JFormattedTextField textPercentLoops, textPercentSelfLoops;
    private JPanel p1, p2, p3, p4, p5, p6, p7, p8, p9;
    
    private Font labelfont, titlefont;

    
    public static void main(String[] args){
        Frame f = new Frame();
        FsmInput p = new FsmInput();
        f.setLayout(null);
        p.setSize(460,579);
        f.add(p);
        p.initialize();
        f.setSize(460,579);
        f.setVisible(true);
        f.setLayout(null);
    }

    
    public FsmInput(){
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

        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        p6 = new JPanel();
        p7 = new JPanel();
        p8 = new JPanel();
        
        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        
        NumberFormatter numberFormatter = new NumberFormatter(intFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(true);  // so can backspace to an empty field!
        numberFormatter.setMinimum(0);
        numberFormatter.setMaximum(MAXIMUM_NUMBER_OF_STATES);
        
        NumberFormatter percentFormatter = new NumberFormatter(intFormat);
        percentFormatter.setValueClass(Integer.class);
        percentFormatter.setAllowsInvalid(true);
        percentFormatter.setMinimum(0);
        percentFormatter.setMaximum(MAXIMUM_PERCENTAGE);
        
        
	labNumStates = new JLabel("number of states: ",Label.RIGHT);
        labNumStates.setFont(labelfont);
        labNumStates.setForeground(Color.black);
        
        textNumStates = new JFormattedTextField(numberFormatter);
        textNumStates.setColumns(4);
        textNumStates.setFont(labelfont);
        textNumStates.setForeground(Color.black);
        textNumStates.setText("");
        
        p1.add(labNumStates);
        p1.add(textNumStates);
	labNumEvents = new JLabel("number of events: ",Label.RIGHT);
        labNumEvents.setFont(labelfont);
        labNumEvents.setForeground(Color.black);
        
        textNumEvents = new JFormattedTextField(numberFormatter);
        textNumEvents.setColumns(4);
        textNumEvents.setFont(labelfont);
        textNumEvents.setForeground(Color.black);
        textNumEvents.setText("");
        
        p2.add(labNumEvents);
        p2.add(textNumEvents);
    labMaxEventsPerState = new JLabel("maximum number of events per state: ",Label.RIGHT);
        labMaxEventsPerState.setFont(labelfont);
        labMaxEventsPerState.setForeground(Color.black);
        
        textMaxEventsPerState = new JFormattedTextField(numberFormatter);
        textMaxEventsPerState.setColumns(4);
        textMaxEventsPerState.setFont(labelfont);
        textMaxEventsPerState.setForeground(Color.black);
        textMaxEventsPerState.setText("");
        
        p3.add(labMaxEventsPerState);
        p3.add(textMaxEventsPerState);
    labMinEventsPerState = new JLabel("minimum number of events per state: ",Label.RIGHT);
        labMinEventsPerState.setFont(labelfont);
        labMinEventsPerState.setForeground(Color.black);
        
        textMinEventsPerState = new JFormattedTextField(numberFormatter);
        textMinEventsPerState.setColumns(4);
        textMinEventsPerState.setFont(labelfont);
        textMinEventsPerState.setForeground(Color.black);
        textMinEventsPerState.setText("");
        
        p4.add(labMinEventsPerState);
        p4.add(textMinEventsPerState);
        
        
    ch = new JCheckBox("  Any looping states",false);
        
    ch.setForeground(Color.black);
    ch.setFont(labelfont);
        p5.add(ch);
        
        
    labPercentLoops = new JLabel("percentage of transitions which are loops: ",Label.RIGHT);
        labPercentLoops.setFont(labelfont);
        labPercentLoops.setForeground(Color.black);
        
        textPercentLoops = new JFormattedTextField(percentFormatter);
        textPercentLoops.setColumns(4);
        textPercentLoops.setFont(labelfont);
        textPercentLoops.setForeground(Color.black);
        textPercentLoops.setText("");
        textPercentLoops.setEditable(false);
        
        p6.add(labPercentLoops);
        p6.add(textPercentLoops);
        p6.setVisible(false);
        
    labPercentSelfLoops = new JLabel("percentage of looping transitions which are self-loops: ",Label.RIGHT);
        labPercentSelfLoops.setFont(labelfont);
        labPercentSelfLoops.setForeground(Color.black);
        
        textPercentSelfLoops = new JFormattedTextField(percentFormatter);
        textPercentSelfLoops.setColumns(4);
        textPercentSelfLoops.setFont(labelfont);
        textPercentSelfLoops.setForeground(Color.black);
        textPercentSelfLoops.setText("");
        textPercentSelfLoops.setEditable(false);
        
        p7.add(labPercentSelfLoops);
        p7.add(textPercentSelfLoops);
        p7.setVisible(false);

        
        enter = new JButton("Create FSM");
        enter.setFont(labelfont);
        p8.add(enter);
        
        gbc.gridy = 1;
        add(p1,gbc);
        gbc.gridy = 2;
        add(p2,gbc);
        gbc.gridy = 3;
        add(p3,gbc);
        gbc.gridy = 4;
        add(p4,gbc);
        gbc.gridy = 5;
        add(p5,gbc);
        gbc.gridy = 6;
        add(p6,gbc);
        gbc.gridy = 7;
        add(p7,gbc);
        gbc.gridy = 8;
        add(p8,gbc);
        
        ch.addItemListener(this);
    }
    
    
    
    public String getOutputFSMfilname() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("newFSM.fsm"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("FSM files", "fsm");
        chooser.setFileFilter(filter);
        
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile().getPath();
        return null;
    }
    
    
    public int getNumStates() {
        int numStates;
        try {
            numStates = Integer.parseInt(textNumStates.getText());
        } catch (NumberFormatException e) {
            numStates = DEFAULT_NUMBER_OF_STATES;
        }
        return numStates;
    }
    
    
    public int getNumEvents() {
        int numEvents;
        try {
            numEvents = Integer.parseInt(textNumEvents.getText());
        } catch (NumberFormatException e) {
            numEvents = DEFAULT_NUMBER_OF_STATES;
        }
        return numEvents;
    }
    
    
    public int getMaxEventsPerState() {
        int maxEventsPerState;
        try {
            maxEventsPerState = Integer.parseInt(textMaxEventsPerState.getText());
        } catch (NumberFormatException e) {
            maxEventsPerState = DEFAULT_MAX_EVENTS_PER_STATE;
        }
        return maxEventsPerState;
    }
    
    
    public int getMinEventsPerState() {
        int minEventsPerState;
        try {
            minEventsPerState = Integer.parseInt(textMinEventsPerState.getText());
        } catch (NumberFormatException e) {
            minEventsPerState = DEFAULT_MIN_EVENTS_PER_STATE;
        }
        return minEventsPerState;
    }
    
    
    public double getPercentLoopingTransitions() {
        int prcntLoopingTransitions = 0;
        try {
            prcntLoopingTransitions = Integer.parseInt(textPercentLoops.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return (Double) (1.0/prcntLoopingTransitions);
    }
    
    
    public double getPercentSelfLooping() {
        int prcntSelfLooping = 0;
        try {
            prcntSelfLooping = Integer.parseInt(textPercentSelfLoops.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return (Double) (1.0/prcntSelfLooping);
    }
    
    
    
    public void itemStateChanged(ItemEvent evt){
        if (evt.getSource() == ch) {
            if (ch.isSelected()) {
                p6.setVisible(true);
                textPercentLoops.setEditable(true);
                p7.setVisible(true);
                textPercentSelfLoops.setEditable(true);
                validate();
            } else {
                p6.setVisible(false);
                textPercentLoops.setEditable(false);
                p7.setVisible(false);
                textPercentSelfLoops.setEditable(false);
                validate();
            }
        }
    }

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
        g.setFont(titlefont);
        g.drawString("Input",10,20);
    }
    
}

