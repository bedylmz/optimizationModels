package package1;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class MainTab 
{
	private JPanel tab;
	private JLabel label;
	private SpringLayout layout;
	
	public MainTab(JFrame frame)
	{
        layout = new SpringLayout();
		label = new JLabel(new ImageIcon("src\\icons\\home.png"));
		
        tab = new JPanel();
        tab.setLayout(layout);
        
        JLabel initialL = new JLabel("Please enter your Interval(inclusive) for initial point (? for random): ");
        
        JLabel startL = new JLabel("From: ");
        JTextField start = new JTextField("00000");
        start.setPreferredSize(new Dimension(50,20));
        JLabel endL = new JLabel("To: ");
        JTextField end = new JTextField("00000");
        end.setPreferredSize(new Dimension(50,20));
        JPanel interval = new JPanel(new FlowLayout());
        interval.add(startL);
        interval.add(start);
        interval.add(endL);
        interval.add(end);
        
        
        JLabel algorithmsL =  new JLabel("Please select algorithms to work: ");
        
        JPanel algorithmsP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        
        Method GD = new Method("Gradient Descent");
        GD.addParameter("Step Size: ", new SpinnerNumberModel(0.0001, 0.0, 1.0, 0.00001));
        GD.addParameter("Threshold: ", new SpinnerNumberModel(0.00005, 0, 0.5, 0.00001));
        GD.isEnable();
    	
        
        Method SGD = new Method("Stochastic Gradient Descent");
        SGD.addParameter("Step Size: ", new SpinnerNumberModel(0.0001, 0, 1, 0.00001));
        SGD.addParameter("Threshold: ", new SpinnerNumberModel(0.00005, 0, 0.5, 0.00001));
        SGD.isEnable();

        Method ADAM = new Method("ADAM");
        ADAM.addParameter("Step Size: ", new SpinnerNumberModel(0.001, 0, 1, 0.0001));
        ADAM.addParameter("ß1: ",        new SpinnerNumberModel(0.9, 0, 1, 0.01));
        ADAM.addParameter("ß2: ",        new SpinnerNumberModel(0.999, 0, 1, 0.01));
        ADAM.addParameter("Threshold: ", new SpinnerNumberModel(0.00005, 0, 0.5, 0.00001));
        ADAM.isEnable();

        algorithmsP.add(GD.getBox());
        algorithmsP.add(SGD.getBox());
        algorithmsP.add(ADAM.getBox());
        
        JPanel graphsInfo = new JPanel();
        GridLayout layoutGraph = new GridLayout(5, 1);
        graphsInfo.setLayout(layoutGraph);
        layoutGraph.setHgap(10000);
        
        JLabel graphsLabel = new JLabel("Graph Plotter Settings: ");
   
        JPanel timeOrIterationPanel = new JPanel(layout);     
        JLabel timeOrIterationLabel = new JLabel("Please Select Time or Iteration for x axis: ");
        ButtonGroup timeOrIteration = new ButtonGroup();
        JRadioButton time = new JRadioButton("Time");
        time.setSelected(true);
        JRadioButton iteration = new JRadioButton("Iteration");
        timeOrIteration.add(time);
        timeOrIteration.add(iteration);
        
        layout.putConstraint(SpringLayout.WEST, time, 0, SpringLayout.WEST, initialL);
        layout.putConstraint(SpringLayout.WEST, iteration, 5, SpringLayout.EAST, time);
        
        timeOrIterationPanel.add(time);
        timeOrIterationPanel.add(iteration);
        
        JLabel yAxisLabel = new JLabel("Please Select your y axis term or terms: ");
        
        JPanel yAxisPanel = new JPanel(new FlowLayout());
        
        JCheckBox trainLoss  = new JCheckBox("Train Loss");
        JCheckBox testLoss   = new JCheckBox("Test Loss");
        JCheckBox trainGuess = new JCheckBox("Correct Guess in Train");
        JCheckBox testGuess  = new JCheckBox("Correct Guess in Test");
        
        yAxisPanel.add(trainLoss);
        yAxisPanel.add(testLoss);
        yAxisPanel.add(trainGuess);
        yAxisPanel.add(testGuess);
        
        graphsInfo.add(graphsLabel);
        graphsInfo.add(timeOrIterationLabel);
        graphsInfo.add(timeOrIterationPanel);
        graphsInfo.add(yAxisLabel);
        graphsInfo.add(yAxisPanel);
        
        
        
        JButton run = new JButton(new ImageIcon("src\\icons\\run.png"));
        run.addActionListener(new ActionListener()
        	{
        		public void actionPerformed(ActionEvent e)
				{
					deleteFilesInDirectory("src\\exec\\data");
					try
					{
						if(GD.getBox().isSelected())
						{
							saveData("src\\exec\\data\\gd.dat", GD.getParameters());
						}
						if(SGD.getBox().isSelected())
						{
							saveData("src\\exec\\data\\sgd.dat", SGD.getParameters());
						}
						if(ADAM.getBox().isSelected())
						{
							saveData("src\\exec\\data\\adam.dat", ADAM.getParameters());
						}
						
						
						String info = "\n"+ time.isSelected()       +
									  "\n"+ iteration.isSelected()  +
									  "\n"+ trainLoss.isSelected()  +
									  "\n"+	testLoss.isSelected()   +  
									  "\n"+ trainGuess.isSelected() +
									  "\n"+ testGuess.isSelected() ;
						
						saveData("src\\exec\\data\\graphs.dat", info);
						
						ArrayList<String> photoData = loadData("src\\exec\\img\\photo.dat");
						
						//6 in this code where line we save size of image
						int imageSize = Integer.parseInt(photoData.get(6));
						
						if(start.getText().equals("?") || end.getText().equals("?"))
						{
							double startPoint = Math.random()*10000000;
							double endPoint = Math.random()*10000000;
							if (startPoint > endPoint)
							{
								double dummy = startPoint;
								startPoint = endPoint;
								endPoint = dummy;
							}
							double middlePoint = (startPoint + endPoint)/2;
							double distance = (endPoint - startPoint)/2;
							String initialList = "\n";
							for(int i  = 0; i <  imageSize*imageSize; i++)
							{
								initialList += Double.toString(middlePoint+(Math.random()*distance)) + "\n";
							}
							
							saveData("src\\exec\\data\\w.dat", initialList);
						}
						else if(start.getText().equals(end.getText()))
						{
							throw new IncorrectIntervalError("");
						}
						else
						{
							double startPoint = Double.parseDouble(start.getText());
							double endPoint = Double.parseDouble(end.getText());
							if (startPoint > endPoint)
							{
								throw new IncorrectIntervalError("");
							}
							double distance = (endPoint - startPoint);
							String initialList = "\n";
							for(int i  = 0; i <  imageSize*imageSize; i++)
							{
								initialList += Double.toString(startPoint+(Math.random()*distance)) + "\n";
							}
							
							saveData("src\\exec\\data\\w.dat", initialList);
						}
						
						SwingUtilities.invokeLater(() -> 
						{
							JFrame loadingDialog = loadingScene(frame);
						
						// Start a background task
							SwingWorker<Void, Void> worker = new SwingWorker<Void,Void>() 
						    {
						        protected Void doInBackground() 
						        {
						            try 
						            {
						                // Simulate a long task
						            	runProgram("src\\exec\\main.exe");
										runProgram("src\\exec\\Graphs.exe");
						            } 
						            catch (InterruptedException | IOException e) 
						            {
						            	JOptionPane.showMessageDialog(frame, "Train.exe doesn't work correctly."); 
						            }
						            return null;
						        }
						        protected void done() 
						        {
						            // Close the dialog when the task is complete
						            loadingDialog.dispose();
						        }
						    };
						
						    // Start the worker and show the dialog
						    worker.execute();
						    loadingDialog.setVisible(true);
						 });

//						loading.setVisible(true);
//						int exitcode = runProgram("src\\exec\\main.exe");
//						exitcode = runProgram("src\\exec\\Graphs.exe");
//						loading.setVisible(false);
						
//    						JOptionPane.showMessageDialog(frame, "Finished!\nExit code: " + exitcode );  
					}
					catch(IOException err)
					{
						JOptionPane.showMessageDialog(frame, "Cannot find some file or cannot write");  
					}
//					catch(InterruptedException err)
//					{
//						JOptionPane.showMessageDialog(frame, "Train.exe doesn't work correctly.");  
//					}
					catch(NumberFormatException err)
					{
						JOptionPane.showMessageDialog(frame, "Please Enter Number in \"From\" and \"To\"!");  
					}
					catch(IncorrectIntervalError err)
					{
						JOptionPane.showMessageDialog(frame, "Please enter valid interval!");
					}
//						catch(Exception err)
//						{
//							JOptionPane.showMessageDialog(frame, "Please recheck some settings or ensure not incorrupt files!");
//						}
				}
        	});
        
        //Setting UI
        layout.putConstraint(SpringLayout.WEST,	initialL,3,SpringLayout.WEST, 	tab); 
        layout.putConstraint(SpringLayout.NORTH,initialL,10,SpringLayout.NORTH, tab);
        
        layout.putConstraint(SpringLayout.WEST,	interval,0,SpringLayout.WEST, 	initialL); 
        layout.putConstraint(SpringLayout.NORTH,interval,10,SpringLayout.SOUTH, initialL); 
        
        layout.putConstraint(SpringLayout.WEST,	algorithmsL	,0,SpringLayout.WEST,   initialL); 
        layout.putConstraint(SpringLayout.NORTH,algorithmsL	,10,SpringLayout.SOUTH, interval); 
        
        layout.putConstraint(SpringLayout.WEST,	algorithmsP	,0,SpringLayout.WEST,   initialL); 
        layout.putConstraint(SpringLayout.NORTH,algorithmsP	,5,SpringLayout.SOUTH, algorithmsL);
        
//  	Gradient Descent--------------------------------------------------------------------
        arrangeMethod(initialL, algorithmsP, GD.getLabel(), GD.getPanel());
        
//  	Stochastic Gradient Descent--------------------------------------------------------
        arrangeMethod(initialL, GD.getPanel(), SGD.getLabel(), SGD.getPanel());

//  	ADAM--------------------------------------------------------------------
        arrangeMethod(initialL, SGD.getPanel(), ADAM.getLabel(), ADAM.getPanel());
        
        run.setPreferredSize(new Dimension(100,40));
        layout.putConstraint(SpringLayout.SOUTH, run,-10, SpringLayout.SOUTH, tab);
        layout.putConstraint(SpringLayout.EAST, run,-10, SpringLayout.EAST, tab);
        
        layout.putConstraint(SpringLayout.WEST,	graphsInfo ,0,SpringLayout.WEST,  initialL); 
        layout.putConstraint(SpringLayout.NORTH,graphsInfo ,15,SpringLayout.SOUTH, ADAM.getPanel());
        
        tab.add(initialL);
        tab.add(algorithmsL);
        tab.add(interval);
        tab.add(algorithmsP);
        
        tab.add(GD.getLabel());
        tab.add(GD.getPanel());
        
        tab.add(SGD.getLabel());
        tab.add(SGD.getPanel());
        
        tab.add(ADAM.getLabel());
        tab.add(ADAM.getPanel());
        
        tab.add(graphsInfo);
        
        tab.add(run);
	}
	
	public static JFrame loadingScene(JFrame frame)
	{
		JFrame loading = new JFrame("Loading");
		loading.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loading.setSize(256, 256);
        loading.setLocationRelativeTo(frame); // Center the frame on the screen
		loading.setResizable(false);
		loading.setUndecorated(true);
		
		JLabel iconLabel = new JLabel(new ImageIcon("src\\icons\\loading.gif"));
//		JLabel iconLabel = new JLabel("Loading");
//	    imageIcon.setImageObserver(iconLabel);
	    //iconLabel.setPreferredSize(new Dimension(256,256));
		
        loading.add(iconLabel);
//        loading.setVisible(false);
        
       return loading;
		
	}
	
	private void arrangeMethod(JLabel initialL, JPanel previousPanel, JLabel currentLabel, JPanel currentPanel)
	{
		layout.putConstraint(SpringLayout.WEST,	currentLabel, 0,SpringLayout.WEST,   initialL); 
        layout.putConstraint(SpringLayout.NORTH,currentLabel, 10,SpringLayout.SOUTH, previousPanel);
        
        layout.putConstraint(SpringLayout.WEST,  currentPanel, 0, SpringLayout.WEST,  initialL);
        layout.putConstraint(SpringLayout.NORTH, currentPanel, 0, SpringLayout.SOUTH, currentLabel);
		
	}
	
	public static ArrayList<String> loadData(String path) throws IOException
	{
		ArrayList<String> result = new ArrayList<>();
		
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) 
        {
        	result.add(line);
        }
        
        return result;
	}
	
	public static int runProgram(String path) throws IOException, InterruptedException
	{
		ProcessBuilder Builder = new ProcessBuilder(path);
		Builder.directory(new File("src\\exec"));
		Process train = Builder.start();
		int exitcode = train.waitFor();
		
		return exitcode;
	}
	
	private void saveData(String path, Object value) throws IOException
	{
		ObjectOutputStream outputObject = new ObjectOutputStream(
				new FileOutputStream(path));
		outputObject.writeObject(value);
		outputObject.close(); 
	}

	public JPanel getPanel() {return tab;}
	
	public JLabel getLabel() {return label;}
	
	public static void deleteFilesInDirectory(String path)
	{
		File directory = new File(path);

        // Ensure it's a valid directory
        if (!directory.isDirectory()) 
        {
            System.out.println("The provided path is not a directory.");
        }

        File[] files = directory.listFiles();
		try
		{
			for (File file : files)
			{
				file.delete();
			}
		}
		catch(SecurityException e1)
		{
			JOptionPane.showMessageDialog(null, e1, path, 0);
		}
	}
	
}
