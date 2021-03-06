package stencyl.ext.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import stencyl.ext.polydes.common.nodes.HierarchyModel;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.Layout;

public class StructureEditor extends JPanel
{
	public JLabel label;
	public PropertiesSheet properties;
	public Structure structure;
	
	public StructureEditor(Structure structure)
	{
		this(structure, null);
	}
	
	public StructureEditor(Structure structure, HierarchyModel<DataItem> model)
	{
		super(new BorderLayout());
		
		this.structure = structure;
		
		label = new JLabel(structure.dref.getName());
		label.setBackground(PropertiesSheetStyle.DARK.pageBg);
		label.setForeground(new Color(0x717171));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setFont(UIConsts.displayNameFont);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		label.setOpaque(true);
		label.setIcon(structure.getMediumIcon());
		
		properties = new PropertiesSheet(structure, model, PropertiesSheetStyle.DARK);
		
		add(label, BorderLayout.NORTH);
		add(Layout.aligned(properties, SwingConstants.LEFT, SwingConstants.TOP), BorderLayout.CENTER);
		
		revalidate();
	}
	
	public void nameChanged(String name)
	{
		if(label != null)
			label.setText(name);
	}

	public void highlightElement(DataItem n)
	{
		properties.highlightElement(n);
	}

	public void dispose()
	{
		removeAll();
		
		label = null;
		if(properties != null)
			properties.dispose();
		properties = null;
		structure = null;
	}
}
