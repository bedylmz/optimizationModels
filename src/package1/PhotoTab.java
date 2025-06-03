package package1;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class PhotoTab 
{
	private JLabel label;
	private JPanel tab;
	private SpringLayout layout;
	private JCheckBox cacheImages;
	
	public PhotoTab(JFrame frame)
	{
		label = new JLabel(new ImageIcon("src\\icons\\photos.png"));
        label.setPreferredSize(new Dimension(25,25)); 
        layout = new SpringLayout();
        tab = new JPanel(layout);
        
    	JPanel       pickerPanel  = new JPanel(new FlowLayout());
    	JFileChooser photo1Picker  = new JFileChooser();
    	photo1Picker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
    	JFileChooser photo2Picker  = new JFileChooser();
    	photo2Picker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
    	JLabel       pickerLabel  = new JLabel("Select Images' Folder: ");
    	JButton      pickerButton = new JButton(new ImageIcon("src\\icons\\folder.png"));
    	pickerButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				photo1Picker.showDialog(frame, "Select Class 1");
				photo2Picker.showDialog(frame, "Select Class 2");
			}
    	});
    	pickerButton.setPreferredSize(new Dimension(25,20));
    	pickerPanel.add(pickerLabel);
    	pickerPanel.add(pickerButton);
    	
    	JPanel percentagePanel = new JPanel(new FlowLayout());
    	JLabel percentageLabel = new JLabel("Please Select Percentage(%) of Images for Train Set: ");//tooltip "both classes affect by this situation"
    	JSpinner percentageSpinner = new JSpinner(new SpinnerNumberModel(80,10,90,10));
    	percentagePanel.add(percentageLabel);
    	percentagePanel.add(percentageSpinner);
    	
    	JPanel sizeInfoPanel = new JPanel(new FlowLayout());
    	JLabel widthHeightLabel = new JLabel("Select Width & Height Size: ");
    	JSpinner widthHeight = new JSpinner(new SpinnerNumberModel(32,20,128,1));
    	sizeInfoPanel.add(widthHeightLabel);
    	sizeInfoPanel.add(widthHeight);
    	
    	
    	cacheImages = new JCheckBox("Save Manipulated Images?");
    	cacheImages.setSelected(true);
    	
    	
    	JButton save = new JButton(new ImageIcon("src\\icons\\create.png"));
    	save.setPreferredSize(new Dimension(100,40));
    	save.addActionListener(new ActionListener() 
    	{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					if(photo1Picker.getSelectedFile() == null || photo2Picker.getSelectedFile() == null)
					{
						throw new IOException();
					}
					ObjectOutputStream photoData = new ObjectOutputStream(new FileOutputStream("src\\exec\\img\\photo.dat"));
					photoData.writeObject("\n" + photo1Picker.getSelectedFile() + 
										  "\n" + countPhotosInDirectory(photo1Picker.getSelectedFile().toString()) + 
										  "\n" + photo2Picker.getSelectedFile() + 
										  "\n" + countPhotosInDirectory(photo2Picker.getSelectedFile().toString()) + 
										  "\n" + percentageSpinner.getValue()   +
										  "\n" + widthHeight.getValue()         +
										  "\n" + cacheImages.isSelected());
					photoData.close();
					
					SwingUtilities.invokeLater(() -> 
					{
						JFrame loadingDialog = MainTab.loadingScene(frame);
					
					// Start a background task
						SwingWorker<Void, Void> worker = new SwingWorker<Void,Void>() 
					    {
					        protected Void doInBackground() 
					        {
					            try 
					            {
					            	MainTab.runProgram("src\\exec\\imageManipulation.exe");
					            	JOptionPane.showMessageDialog(frame, "Saved!");
					            } 
					            catch (InterruptedException | IOException e) 
					            {
					            	JOptionPane.showMessageDialog(frame, "Please ensure photos is correct.");
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
					
					
				} 
				catch (IOException e1) 
				{
					JOptionPane.showMessageDialog(frame, "Please Select Both Folder.");
				} 
			}
    	});
    	
    	//Setting UI
        layout.putConstraint(SpringLayout.WEST,	pickerPanel,3,SpringLayout.WEST,    tab); 
        layout.putConstraint(SpringLayout.NORTH,pickerPanel,10,SpringLayout.NORTH,  tab);
    	
        layout.putConstraint(SpringLayout.WEST,	percentagePanel,0,SpringLayout.WEST, pickerPanel); 
        layout.putConstraint(SpringLayout.NORTH,percentagePanel,10,SpringLayout.SOUTH, pickerPanel);
    	
        layout.putConstraint(SpringLayout.WEST,	sizeInfoPanel,0,SpringLayout.WEST,  pickerPanel); 
        layout.putConstraint(SpringLayout.NORTH,sizeInfoPanel,10,SpringLayout.SOUTH, percentagePanel);

        layout.putConstraint(SpringLayout.WEST,	cacheImages,0,SpringLayout.WEST,  pickerPanel); 
        layout.putConstraint(SpringLayout.NORTH,cacheImages,10,SpringLayout.SOUTH, sizeInfoPanel);
    	
    	layout.putConstraint(SpringLayout.SOUTH, save,-10, SpringLayout.SOUTH, tab);
        layout.putConstraint(SpringLayout.EAST,  save,-10, SpringLayout.EAST,  tab);
    	
    	tab.add(pickerPanel);
    	tab.add(percentagePanel);
    	tab.add(sizeInfoPanel);
    	tab.add(cacheImages);
    	tab.add(save);
	}
	
	private int countPhotosInDirectory(String path) 
	{
        File directory = new File(path);

        // Ensure it's a valid directory
        if (!directory.isDirectory()) 
        {
            System.out.println("The provided path is not a directory.");
            return 0;
        }

        // List files and filter by photo extensions
        String[] photoExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        File[] files = directory.listFiles(
        		(dir, name) -> 
		        {
		            for (String ext : photoExtensions) 
		            {
		                if (name.toLowerCase().endsWith(ext)) 
		                {
		                    return true;
		                }
		            }
		            return false;
		        });

        // Return the count of photo files
        return files != null ? files.length : 0;
    }

	public JPanel getPanel() {return tab;}
	
	public JLabel getLabel() {return label;}
	
	public boolean IsSaveManipulatedImages() {return cacheImages.isSelected();}
}
