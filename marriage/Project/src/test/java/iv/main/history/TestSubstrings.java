package iv.main.history;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

public class TestSubstrings
{
	@Test
	public void test0()
	{
		SubstringsSet set = new SubstringsSet();
		
		set.add("foobar");
		set.add("foobar");
		set.add("foobar");
		set.add("foobar");
		
		Assert.assertEquals("Wrong size.", 1, set.size());
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("foo", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 1, linkedList.size());
		Assert.assertTrue("Missing match.", linkedList.contains("foobar"));
	}

	@Test
	public void test1()
	{
		SubstringsSet set = new SubstringsSet();
		
		set.add("foobar");
		set.add("foorab");
		set.add("raboof");
		set.add("rabfoo");
		
		Assert.assertEquals("Wrong size.", 4, set.size());
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("fo", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 2, linkedList.size());
		Assert.assertTrue("Missing match.", linkedList.contains("foobar"));
		Assert.assertTrue("Missing match.", linkedList.contains("foorab"));
		Assert.assertFalse("False match.", linkedList.contains("raboof"));
		Assert.assertFalse("False match.", linkedList.contains("rabfoo"));
	}
	@Test
	public void test2()
	{
		SubstringsSet set = new SubstringsSet();
		
		set.add("foobar");
		set.add("foorab");
		set.add("raboof");
		set.add("rabfoo");
		
		Assert.assertEquals("Wrong size.", 4, set.size());
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("foo", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 2, linkedList.size());
		Assert.assertTrue("Missing match.", linkedList.contains("foobar"));
		Assert.assertTrue("Missing match.", linkedList.contains("foorab"));
		Assert.assertFalse("False match.", linkedList.contains("raboof"));
		Assert.assertFalse("False match.", linkedList.contains("rabfoo"));
	}

	@Test
	public void test3()
	{
		SubstringsSet set = new SubstringsSet();
		
		set.add("foobar");
		set.add("foorab");
		set.add("raboof");
		set.add("rabfoo");
		set.add("foo");
		
		Assert.assertEquals("Wrong size.", 5, set.size());
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("foo", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 3, linkedList.size());
		Assert.assertTrue("Missing match.", linkedList.contains("foo"));
		Assert.assertTrue("Missing match.", linkedList.contains("foobar"));
		Assert.assertTrue("Missing match.", linkedList.contains("foorab"));
		Assert.assertFalse("False match.", linkedList.contains("raboof"));
		Assert.assertFalse("False match.", linkedList.contains("rabfoo"));
	}
	
	@Test
	public void testEmpty()
	{
		SubstringsSet set = new SubstringsSet();
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("fo", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 0, linkedList.size());
	}
	
	@Test
	public void testNone()
	{
		SubstringsSet set = new SubstringsSet();
		
		set.add("foobar");
		set.add("foorab");
		set.add("raboof");
		set.add("rabfoo");
		
		Assert.assertEquals("Wrong size.", 4, set.size());
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("of", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 0, linkedList.size());
	}

	@Test
	public void testRemove()
	{
		SubstringsSet set = new SubstringsSet();
		
		set.add("foobar");
		set.add("foorab");
		set.add("raboof");
		set.add("rabfoo");
		
		Assert.assertEquals("Wrong size.", 4, set.size());
		
		LinkedList<String> linkedList = new LinkedList<>();
		set.collectSubstrings("foo", linkedList);
		
		Assert.assertEquals("Wrong number of matches.", 2, linkedList.size());
		Assert.assertTrue("Missing match.", linkedList.contains("foobar"));
		Assert.assertTrue("Missing match.", linkedList.contains("foorab"));
		
		set.remove("foorab");
		
		Assert.assertEquals("Wrong size.", 3, set.size());
		
		linkedList.clear();
		set.collectSubstrings("foo", linkedList);

		Assert.assertEquals("Wrong number of matches.", 1, linkedList.size());
		Assert.assertTrue("Missing match.", linkedList.contains("foobar"));
	}
}
