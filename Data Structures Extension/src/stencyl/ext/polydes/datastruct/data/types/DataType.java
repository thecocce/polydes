package stencyl.ext.polydes.datastruct.data.types;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.sw.editors.snippet.designer.Definition;

public abstract class DataType<T> implements Comparable<DataType<?>>
{
	public static String DEFAULT_VALUE = "default";
	public static String EDITOR = "editor";
	
	public String xml;
	public String stencylType;
	public String haxeType;
	public Class<T> javaType;
	
	public String haxeClassname;
	public String haxePackage;
	
	public DataType(Class<T> javaType, String haxeType, String stencylType, String xml)
	{
		this.javaType = javaType;
		this.haxeType = haxeType;
		this.stencylType = stencylType;
		this.xml = xml;
		
		int lastDot = haxeType.lastIndexOf('.');
		if(lastDot == -1)
		{
			haxePackage = "";
			haxeClassname = haxeType;
		}
		else
		{
			haxePackage = haxeType.substring(0, lastDot);
			haxeClassname = haxeType.substring(lastDot + 1);
		}
	}
	
	public abstract ExtrasMap saveExtras(ExtraProperties extras);
	public abstract ExtraProperties loadExtras(ExtrasMap extras);
	/*{
		if(extraParameters != null)
			for(StructureField f : extraParameters.getFields())
			{
				if(field.optionalArgs.containsKey(f.name))
				{
					DataType<?> converter = Types.fromXML(f.type);
					field.optionalArgs.put(f.name, converter.decode("" + field.optionalArgs.get(f.name)));
				}
				else if(f.optionalArgs.containsKey("default"))
				{
					DataType<?> converter = Types.fromXML(f.type);
					field.optionalArgs.put(f.name, converter.decode("" + f.optionalArgs.get("default")));
				}
			}
	}*/
	
	protected JComponent[] comps(JComponent... c)
	{
		return c;
	}
	
	//return null for classes that already exist
	public abstract List<String> generateHaxeClass();
	public abstract List<String> generateHaxeReader();
	public abstract JComponent[] getEditor(DataUpdater<T> updater, ExtraProperties extras, PropertiesSheetStyle style);
	public JComponent[] getEditor(DataUpdater<T> updater, ExtrasMap extras, PropertiesSheetStyle style)
	{
		return getEditor(updater, loadExtras(extras), style);
	}
	//public abstract void applyToFieldPanel(StructureFieldPanel panel);
	public void applyToFieldPanel(StructureFieldPanel panel){};
	
	public abstract T decode(String s);
	public abstract String toDisplayString(T data);
	
	protected <S> S or(S item, S defaultValue)
	{
		return item == null ? defaultValue: item;
	}
	
	@SuppressWarnings("unchecked")
	public String checkToDisplayString(Object o)
	{
		if(o == null)
			return "null";
		
		if(javaType.isAssignableFrom(o.getClass()))
			return toDisplayString((T) o);
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public String checkEncode(Object o)
	{
		if(o == null)
			return "";
		
		if(javaType.isAssignableFrom(o.getClass()))
			return encode((T) o);
		
		System.out.println("Failed to encode " + o);
		
		return "";
	}
	
	public abstract String encode(T t);

	@Override
	public int compareTo(DataType<?> dt)
	{
		return xml.compareTo(dt.xml);
	}
	
	@SuppressWarnings("unchecked")
	public T checkCopy(Object o)
	{
		if(o == null)
			return null;
		
		if(javaType.isAssignableFrom(o.getClass()))
			return copy((T) o);
		
		System.out.println("Failed to copy " + o);
		
		return null;
	}
	
	public abstract T copy(T t);
	
	public String toString()
	{
		return xml;
	}

	public ArrayList<Definition> getBlocks()
	{
		return null;
	}
}