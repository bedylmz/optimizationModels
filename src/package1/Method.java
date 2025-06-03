package package1;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Method 
{
	@SuppressWarnings("unused")
	private String name;
    private JCheckBox methodBox;
    private JLabel label;
    private JPanel mainPanel;
    private ArrayList<JComponent> components;
    
    public Method(String name)
    {
    	this.name = name;
    	methodBox = new JCheckBox(name);
    	methodBox.setSelected(true);
    	
    	label  = new JLabel(name+" Parameters: ");
        mainPanel = new JPanel(new FlowLayout());
        components = new ArrayList<>();
    }
    
    public void addParameter(String param, SpinnerNumberModel model)
    {
    	JLabel    paramLabel   = new JLabel(param);
        JSpinner  paramSpinner = new JSpinner(model);
        paramSpinner.setPreferredSize(new Dimension(70,20));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(paramSpinner, "0.00000");
        paramSpinner.setEditor(editor);
        mainPanel.add(paramLabel); mainPanel.add(paramSpinner);
        components.add(paramLabel); components.add(paramSpinner);
    }
    
    public void addParameter(String param)
    {
    	JLabel    paramLabel   = new JLabel(param);
        JSpinner  paramSpinner = new JSpinner();
        paramSpinner.setPreferredSize(new Dimension(70,20));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(paramSpinner, "0.00000");
        paramSpinner.setEditor(editor);
        mainPanel.add(paramLabel); mainPanel.add(paramSpinner);
        components.add(paramLabel); components.add(paramSpinner);
    }
    
    public void isEnable()
    {
    	methodBox.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				for(JComponent item : components)
		    	{
		    		item.setEnabled(methodBox.isSelected());
		    	}
			}
		});
    }
    
    public JCheckBox getBox() {return methodBox;}
    
    public ArrayList<JComponent> getComponents() {return components;}
    
    public JLabel getLabel() {return label;}
    
    public JPanel getPanel() {return mainPanel;}
    
    public String getParameters() 
    {
    	String result = "\n";
    	for(JComponent item : components)
    	{
    		if(item instanceof JSpinner)
    		{
    			result += (((JSpinner)item).getValue())+"\n";
    		}
    	}
    	return result;
    }


}
