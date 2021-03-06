package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.core.ExtrasImage;
import stencyl.ext.polydes.datastruct.data.core.Images;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class ExtrasImageType extends BuiltinType<ExtrasImage>
{
	public ExtrasImageType()
	{
		super(ExtrasImage.class, "nme.display.BitmapData", "IMAGE", "Image");
	}

	@Override
	public DataEditor<ExtrasImage> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new ExtrasImageEditor();
	}

	@Override
	public ExtrasImage decode(String s)
	{
		return Images.get().getImage(s);
	}

	@Override
	public String encode(ExtrasImage i)
	{
		if(i == null)
			return "";
		
		return i.name;
	}
	
	@Override
	public ExtrasImage copy(ExtrasImage t)
	{
		return t;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return null;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
	
	public static class ExtrasImageEditor extends DataEditor<ExtrasImage>
	{
		UpdatingCombo<ExtrasImage> editor;
		
		public ExtrasImageEditor()
		{
			editor = new UpdatingCombo<ExtrasImage>(Images.get().getList(), null);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public ExtrasImage getValue()
		{
			return editor.getSelected();
		}

		@Override
		public void set(ExtrasImage t)
		{
			editor.setSelectedItem(t);
		}

		@Override
		public JComponent[] getComponents()
		{
			return comps(editor);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
			editor = null;
		}
	}
}