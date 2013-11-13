package xmlserializer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

public class TestXMLObjectSerializer extends junit.framework.TestCase{
	
	
	public static class TestParent implements Serializable{
		public String foo = "helloworld";
	}
	
	public static class TestChild extends TestParent{
		public int number = 10;
	}
	
	public static class TestCycle implements Serializable{
		public String contents;
		public TestCycle next;
	}
	
	public static class TestIgnore implements Serializable{
		public String stuff = "stuff";
		public int num = 150;
		@XMLIgnore
		public Method method = TestIgnore.class.getMethods()[0];
		public String[] test = new String[]{"did", "ignore", "work"};
		
		public void iHaveAMethod(){
			System.out.println("method here.");
		}
	}
	
	
	public void testCollection() throws ObjectWriteException, ObjectReadException{
		
		Collection<Integer> write = new ArrayList<Integer>();
		write.add(1);
		write.add(2);
		
		XMLSerializerUtil.write(write, "test.xml");
		
		Collection<Integer> read = XMLSerializerUtil.read(write.getClass(), "test.xml");
		assertEquals(write.size(), read.size());
		assertTrue(write.containsAll(read));
		assertTrue(read.containsAll(write));
	}
	
	public void testHashMap() throws ObjectReadException, ObjectWriteException{
		
		Map<String, Integer> write2 = new HashMap<String, Integer>();
		write2.put("test", 1);
		write2.put("huh", 500);
		write2.put("foo", 35235);
		write2.put("hello", 2);
		write2.put("null", null);
		write2.put(null, 1000);
		
		XMLSerializerUtil.write(write2, "test2.xml");
		
		Map<String, Integer> read2 = XMLSerializerUtil.read(write2.getClass(), "test2.xml");
		
		assertEquals(write2.size(), read2.size());
		assertEquals(write2.get("test"), read2.get("test"));
		assertEquals(write2.get("huh"), read2.get("huh"));
		assertEquals(write2.get("foo"), read2.get("foo"));
		assertEquals(write2.get("hello"), read2.get("hello"));
		assertEquals(write2.get("null"), read2.get("null"));
		assertEquals(write2.get(null), read2.get(null));
	}
	
	public void testParentChild() throws ObjectReadException, ObjectWriteException{
		
		TestChild t = new TestXMLObjectSerializer.TestChild();
		XMLSerializerUtil.write(t, "test3.xml");
		TestChild r = XMLSerializerUtil.read(t.getClass(), "test3.xml");
		assertEquals(t.foo, r.foo);
		assertEquals(t.number, r.number);
		
	}
	
	public void testCycles() throws ObjectReadException, ObjectWriteException{
		TestCycle cyc = new TestXMLObjectSerializer.TestCycle();
		cyc.contents = "one";
		TestCycle cyc2 = new TestXMLObjectSerializer.TestCycle();
		cyc2.contents = "two";
		cyc.next = cyc2;
		cyc2.next = cyc;
		
		XMLSerializerUtil.write(cyc, "test4.xml");
		TestCycle cycRead = XMLSerializerUtil.read(TestCycle.class, "test4.xml");
		assertEquals(cyc.contents, cycRead.contents);
		assertEquals(cyc2.contents, cycRead.next.contents);
		assertEquals(cycRead.next.next.contents, cyc.contents);
	}
	
	public void testIgnore() throws ObjectReadException, ObjectWriteException{
		
		TestIgnore ig = new TestIgnore();
		XMLSerializerUtil.write(ig, "test5.xml");
		TestIgnore igRead = XMLSerializerUtil.read(TestIgnore.class, "test5.xml");
		assertEquals(ig.num, igRead.num);
		assertEquals(ig.stuff, igRead.stuff);
		for(int i=0; i<ig.test.length; i++){
			assertEquals(ig.test[i], igRead.test[i]);
		}
		assertNull(igRead.method);
	}
	
	public void testComplexMap() throws ObjectReadException, ObjectWriteException{
		
		Map<String, Object> testTree = new TreeMap<String, Object>();
		String[] value1 = new String[]{"hi", "there"};
		String key1 = "asaadsfad";
		testTree.put(key1, value1);
		String key2 = "1.67";
		TestCycle value2 = new TestCycle();
		value2.contents = "value2";
		testTree.put(key2, value2);
		String key3 = "25550";
		Map<String, Object> value3 = testTree;
		testTree.put(key3, value3);
		String key4 = "key4";
		TestCycle value4 = new TestCycle();
		value4.contents = "asaadsfad";
		value2.next = value4;
		value4.next = value2;
		testTree.put(key4, value4);
		
		XMLSerializerUtil.write(testTree, "test6.xml");
		
		Map<Object, Object> readTree = XMLSerializerUtil.read(Map.class, "test6.xml");
		assertEquals(testTree.getClass(), readTree.getClass());
		assertEquals(testTree.size(), readTree.size());
		assertEquals(Array.get(testTree.get(key1), 0), Array.get(readTree.get(key1), 0));
		assertEquals(Array.get(testTree.get(key1), 1), Array.get(readTree.get(key1), 1));
		assertEquals(((TestCycle) testTree.get(key2)).next, testTree.get(key4));
		assertEquals(((TestCycle) readTree.get(key2)).next, readTree.get(key4));
		assertTrue(testTree.keySet().containsAll(readTree.keySet()));
		assertTrue(readTree.keySet().containsAll(testTree.keySet()));
		assertEquals(readTree, readTree.get(key3));
	}
	
	
	
}
