package stencyl.ext.polydes.datastruct.data.structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.folder.FolderPolicy;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.general.StructureType;
import stencyl.ext.polydes.datastruct.io.Text;
import stencyl.ext.polydes.datastruct.io.XML;
import stencyl.ext.polydes.datastruct.utils.DelayedInitialize;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class StructureDefinitions
{
	private static StructureDefinitions _instance;
	public static Folder root;
	private static HashMap<Folder, File> baseFolders;
	public static HashMap<String, StructureDefinition> defMap = new HashMap<String, StructureDefinition>();
	
	private StructureDefinitions()
	{
		root = new Folder("Structure Definitions");
		baseFolders = new HashMap<Folder, File>();
		
		FolderPolicy policy = new FolderPolicy();
		policy.folderCreationEnabled = false;
		policy.itemCreationEnabled = false;
		policy.itemEditingEnabled = false;
		policy.itemRemovalEnabled = false;
		root.setPolicy(policy);
	}
	
	public static StructureDefinitions get()
	{
		if(_instance == null)
			_instance = new StructureDefinitions();
		
		return _instance;
	}
	
	public void addFolder(File fsfolder, String name)
	{
		//TODO: Disallow name editing of Base Folders.
		Folder newFolder = new Folder(name);
		newFolder.setPolicy(new UniqueRootPolicy());
		baseFolders.put(newFolder, fsfolder);
		for(File f : fsfolder.listFiles())
			load(f, newFolder);
		root.addItem(newFolder);
		root.setClean();
	}
	
	public void load(File fsfile, Folder dsfolder)
	{
		if(fsfile.isDirectory())
		{
			Folder newFolder = new Folder(fsfile.getName());
			for(File f : fsfile.listFiles())
				load(f, newFolder);
		}
		else
		{
			if(fsfile.getName().endsWith(".xml"))
			{
				dsfolder.addItem(loadDefinition(fsfile).dref);
			}
		}
	}
	
	public static StructureDefinition loadDefinition(File fsfile)
	{
		String fname = fsfile.getName();
		
		if(!fname.endsWith(".xml"))
			return null;
		
		String defname = fname.substring(0, fname.length() - 4);
		
		Element structure = XML.getFile(fsfile.getAbsolutePath());
		StructureDefinition def = new StructureDefinition(defname, structure.getAttribute("classname"));
		XML.readDefinition(structure, def);
		
		File parent = fsfile.getParentFile();
		
		File haxeFile = new File(parent, defname + ".hx");
		if(haxeFile.exists())
			def.customCode = Text.readString(haxeFile);
		else
			def.customCode = "";
		
		try
		{
			def.setImage(ImageIO.read(new File(parent, defname + ".png")));
		}
		catch (IOException e)
		{
			System.out.println("Couldn't load icon for Structure Definition " + def.name);
		}
		
		addDefinition(def);
		
		return def;
	}
	
	public static void addDefinition(StructureDefinition def)
	{
		defMap.put(def.name, def);
		Structures.structures.put(def.name, new ArrayList<Structure>());
		Types.addType(new StructureType(def));
		DelayedInitialize.initProp(def.name, Types.fromXML(def.name));
	}
	
	/*
	public static void addDesignModeBlocks(StructureDefinition def)
	{
		for(StructureField field : def.getFields())
		{
			String spec = String.format("set %s for %s %%0 to %%1", field.label, def.label);
			
			Definition blockDef = new Definition
			(
				Definition.Category.CUSTOM,
				String.format("ds-%s-set-%s", def.name, field.name),
				new Type[] { Definition.Type.OBJECT, Definition.getTypeFromString(Types.fromXML(field.type).stencylType) },
				new BasicCodeMap().setCode(CodeMap.HX, String.format("~.%s = ~;", field.name)),
				spec,
				Block.BlockType.ACTION,
				Definition.Type.VOID
			);
			
			blockDef.guiTemplate = spec;
			blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
			
			Definitions.get().put(blockDef.tag, blockDef);
			tagCache.add(blockDef.tag);
			
			spec = String.format("get %s for %s %%0", field.label, def.label);
			
			blockDef = new Definition
			(
				Definition.Category.CUSTOM,
				String.format("ds-%s-get-%s", def.name, field.name),
				new Type[] { Definition.Type.OBJECT },
				new BasicCodeMap().setCode(CodeMap.HX, String.format("~.%s", field.name)),
				spec,
				Block.BlockType.NORMAL,
				Definition.getTypeFromString(Types.fromXML(field.type).stencylType)
			);
			
			blockDef.guiTemplate = spec;
			blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
			
			Definitions.get().put(blockDef.tag, blockDef);
			tagCache.add(blockDef.tag);
		}
	}
	*/
	
	public void saveChanges() throws IOException
	{
		for(Folder dsfolder : baseFolders.keySet())
		{
			if(dsfolder.isDirty())
			{
				File fsfolder = baseFolders.get(dsfolder);
				File temp = new File(Locations.getTemporaryDirectory() + File.separator + "data structure defs save");
				temp.mkdirs();
				
				FileUtils.deleteDirectory(temp);
				temp.mkdirs();
				
				for(DataItem d : dsfolder.getItems())
					save(d, temp);
				
				FileUtils.deleteDirectory(fsfolder);
				fsfolder.mkdirs();
				FileUtils.copyDirectory(temp, fsfolder);
			}
		}
		root.setClean();
	}
	
	public void save(DataItem item, File file) throws IOException
	{
		if(item instanceof Folder)
		{
			File saveDir = new File(file, item.getName());
			if(!saveDir.exists())
				saveDir.mkdirs();
			
			for(DataItem d : ((Folder) item).getItems())
				save(d, saveDir);
		}
		else
		{
			StructureDefinition def = (StructureDefinition) item.getObject();
			
			Document doc = FileHelper.newDocument();
			Element e = doc.createElement("structure");
			XML.writeDefinition(doc, e, def);
			doc.appendChild(e);
			FileHelper.writeXMLToFile(doc, new File(file, def.name + ".xml").getAbsolutePath());
			if(def.iconImg != null)
				ImageIO.write(def.iconImg, "png", new File(file, def.name + ".png"));
			if(!def.customCode.isEmpty())
				FileUtils.writeStringToFile(new File(file, def.name + ".hx"), def.customCode);
		}
	}
	
	public static void dispose()
	{
		defMap.clear();
		baseFolders.clear();
		_instance = null;
		root = null;
	}
	
	class UniqueRootPolicy extends FolderPolicy
	{
		public UniqueRootPolicy()
		{
			duplicateItemNamesAllowed = false;
			folderCreationEnabled = false;
			itemCreationEnabled = true;
			itemEditingEnabled = true;
			itemRemovalEnabled = true;
		}
		
		@Override
		public boolean canAcceptItem(Folder folder, DataItem item)
		{
			Folder fromFolder = (item instanceof Folder) ?
						(Folder) item :
						(Folder) item.getParent();
			
			boolean sameRoot = (fromFolder.getPolicy() == this);
			
			return super.canAcceptItem(folder, item) && sameRoot;
		}
	}
}