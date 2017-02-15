package iv.main.history;

import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonGenerator;

public class SubstringsSet
{
	
	SubstringNode root = new SubstringNode();


	public void add(String string)
	{
		root.add(string, 0);
	}

	public void collectSubstrings(String string, Collection<String> linkedList) {
		root.collectSubstrings(string, linkedList, new StringBuilder());
	}

	public TreeSet<String> getHistory()
	{
		TreeSet<String> retVal = new TreeSet<>();
		root.collectSubstrings("", retVal, new StringBuilder());
		return retVal;
	}

	public void remove(String highlighted)
	{
		if (highlighted == null)
			return;
		root.remove(highlighted, 0);
	}

	public void clear()
	{
		root.children.clear();
	}

	public void addAll(Collection<String> collectAllLocations)
	{
		for (String str : collectAllLocations)
			add(str);
	}
	
	public int size()
	{
		return root.count();
	}

	class SubstringNode
	{
		boolean isLeaf;
		TreeMap<Character, SubstringNode> children = new TreeMap<>();
		
		private SubstringNode getChild(char c)
		{
			SubstringNode substringNode = children.get(c);
			if (substringNode == null)
				children.put(c, substringNode = new SubstringNode());
			return substringNode;
		}
		

		public void remove(String highlighted, int index)
		{
			if (index > highlighted.length())
				return;
			if (index == highlighted.length())
			{
				isLeaf = false;
				return;
			}
			
			char charAt = highlighted.charAt(index);
			SubstringNode substringNode = children.get(charAt);
			if (substringNode != null)
			{
				substringNode.remove(highlighted, index + 1);
				if (substringNode.count() == 0)
					children.remove(charAt);
			}
		}
		
		public int count()
		{
			int count = 0;
			for (SubstringNode child : children.values())
			{
				count += child.count();
			}
			if (isLeaf)
				count++;
			return count;
		}


		public void add(String string, int i) {
			if (i == string.length())
			{
				isLeaf = true;
				return;
			}
			getChild(string.charAt(i)).add(string, i + 1);
		}

		public void collectSubstrings(String string, Collection<String> linkedList, StringBuilder builder)
		{
			if (builder.length() >= string.length())
			{
				if (isLeaf)
				{
					linkedList.add(builder.toString());
				}

				int length = builder.length();
				for (Entry<Character, SubstringNode> node : children.entrySet())
				{
					builder.append(node.getKey());
					node.getValue().collectSubstrings(string, linkedList, builder);
					builder.setLength(length);
				}
			}
			else
			{
				char c = string.charAt(builder.length());
				SubstringNode substringNode = children.get(c);
				if (substringNode == null)
					return;
				builder.append(c);
				substringNode.collectSubstrings(string, linkedList, builder);
			}
		}
		
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeBooleanField("isLeaf", isLeaf);
			for (Entry<Character, SubstringNode> entry : children.entrySet())
			{
				generator.writeFieldName(String.valueOf(entry.getKey()));
				entry.getValue().write(generator);
			}
			generator.writeEndObject();
		}
	}
}
