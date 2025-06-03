package package1;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame 
{
	private JFrame frame;
	private MainTab mainTab;
	private PhotoTab photoTab;
	
	public MainFrame()
	{
        frame = new JFrame("Optimization Techniques Graph Plotter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 550);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        
        // Create panels for tabs
        mainTab  = new MainTab(frame);
		photoTab = new PhotoTab(frame);
		
		JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.LEFT); 
        
		tabbedPanel.setBounds(50,50,200,200);
		tabbedPanel.add(mainTab.getPanel());
		tabbedPanel.setTabComponentAt(0, mainTab.getLabel());
		tabbedPanel.setBackgroundAt(0, new Color(255,204,69));
		
		tabbedPanel.add(photoTab.getPanel());
		tabbedPanel.setTabComponentAt(1, photoTab.getLabel());
		tabbedPanel.setBackgroundAt(1, new Color(69,204,255));

		frame.add(tabbedPanel);

        // Add the content panel to the frame
        frame.setContentPane(tabbedPanel);

        // Make the frame visible
        frame.setVisible(true);
	}



	public void quit()
	{
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) 
            {
            	MainTab.deleteFilesInDirectory("src\\exec\\data");
                if(!photoTab.IsSaveManipulatedImages()) 
                {
                	MainTab.deleteFilesInDirectory("src\\exec\\img\\class1");
                	MainTab.deleteFilesInDirectory("src\\exec\\img\\class2");
                }
            }
        });
		
	}
	 
}
