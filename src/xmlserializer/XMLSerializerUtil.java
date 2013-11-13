package xmlserializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * This class can be used to serialize objects as XML to files or OutputStreams and deserialize objects from those XML
 * files or InputStreams, in a manner similar to how java serialization works. It works for almost all objects, but
 * there are a few objects that cannot be instantiated due to java's internal security. I included a work around for
 * instantiating Class objects, but there may be others that can not be deserialized properly. I would advise adding
 * a work around for these specific cases, not serializing objects with those components, or using the @XMLIgnore
 * annotation.
 * <p>
 * Furthermore, while I have tried to test this these methods on a variety of classes, I cannot guarantee that it
 * will work in all cases. If bugs come up, you can try to fix them or let me know, and I will try to fix them. As
 * a further note, I am aware that this class does not contain elegant code. The read and write algorithms are complicated
 * and functionality is the priority.
 * <p>
 * This class also relies on the SilentObjectCreator to get the SerializationConstructor that is used to construct
 * the read objects without invoking their constructors. This means that the SilentObjectCreator must use the sun.reflect
 * package and, therefore, could be broken in future Java versions. It also means that each object you wish to serialize
 * to XML using this class should implement the Serializable interface or inherit it from a superclass. Any objects it
 * contains and its superclasses should also implement Serializable whenever possible. Output may be unpredictable 
 * if this is not the case.
 * <p>
 * Finally, several objects are constructed using special cases, this is either to prevent security exceptions, as is
 * the case for the Class class, or to make their outputs less verbose, as is the case for Strings and Maps. I have
 * included test cases in TestXMLObjectSerializer.java that should help illustrate the usage of this class.
 * {@author Tristan Bepler}
 * <p>
 * The method formatXML has been added to create indentations for better readability, and also to omit the XML declaration
 * at the beginning of the file. {@author Alex Song}
 * <p>
 * Happy serializing.
 * 
 * @author Tristan Bepler
 * @author Alex Song
 *
 */

public class XMLSerializerUtil {

	public static final Map<Class<?>, Class<?>> WRAPPER_TYPES = getWrapperTypes();

	public static final Map<Character, Character> REPLACEMENT_MAP = replacementMap();
	public static final Map<String, Class<?>> PRIMITIVE_CLASSES = primitiveClasses();

	public static boolean isWrapperType(Class<?> clazz){
		return WRAPPER_TYPES.values().contains(clazz);
	}

	public static Class<?> getWrapper(Class<?> clazz){
		return WRAPPER_TYPES.get(clazz);
	}

	private static Map<Character, Character> replacementMap(){
		Map<Character, Character> map = new HashMap<Character, Character>();
		map.put('$', '.');
		map.put('.', '$');
		return map;
	}

	private static Map<String, Class<?>> primitiveClasses(){
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("boolean", boolean.class);
		map.put("byte", byte.class);
		map.put("short", short.class);
		map.put("int", int.class);
		map.put("long", long.class);
		map.put("char", char.class);
		map.put("float", float.class);
		map.put("double", double.class);
		map.put("void", void.class);
		return map;
	}

	private static Map<Class<?>, Class<?>> getWrapperTypes(){
		Map<Class<?>, Class<?>> ret = new HashMap<Class<?>, Class<?>>();
		ret.put(Boolean.TYPE, Boolean.class);
		ret.put(Character.TYPE, Character.class);
		ret.put(Byte.TYPE, Byte.class);
		ret.put(Short.TYPE, Short.class);
		ret.put(Integer.TYPE, Integer.class);
		ret.put(Long.TYPE, Long.class);
		ret.put(Float.TYPE, Float.class);
		ret.put(Double.TYPE, Double.class);
		ret.put(Void.TYPE, Void.class);
		return ret;
	}

	/**
	 * This method parses the given value into the specified primitive or wrapper class.
	 * @param clazz - primitive or wrapper class used to parse
	 * @param value - string to be parsed
	 * @return object of type clazz parsed from the string
	 * @author Trisan Bepler
	 */
	public static Object toObject( Class<?> clazz, String value ) {
		if( Boolean.TYPE == clazz ) return Boolean.parseBoolean( value );
		if( Byte.TYPE == clazz ) return Byte.parseByte( value );
		if( Short.TYPE == clazz ) return Short.parseShort( value );
		if( Integer.TYPE == clazz ) return Integer.parseInt( value );
		if( Long.TYPE == clazz ) return Long.parseLong( value );
		if( Float.TYPE == clazz ) return Float.parseFloat( value );
		if( Double.TYPE == clazz ) return Double.parseDouble( value );
		if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
		if( Byte.class == clazz ) return Byte.parseByte( value );
		if( Short.class == clazz ) return Short.parseShort( value );
		if( Integer.class == clazz ) return Integer.parseInt( value );
		if( Long.class == clazz ) return Long.parseLong( value );
		if( Float.class == clazz ) return Float.parseFloat( value );
		if( Double.class == clazz ) return Double.parseDouble( value );
		if( Character.class == clazz) return value.charAt(0);
		if( Character.TYPE == clazz) return value.charAt(0);
		return value;
	}
	
	/**
	 * This method is used to serialize the given object as XML to the given OutputStream. Objects should implement
	 * the Serializable interface or inherit it from a superclass. Furthermore, as many superclasses and contained
	 * classes should implement Serializable as possible. If they do not, behavior may be unpredictable. See this
	 * classes description for more information. {@link XMLSerializerUtil}
	 * @param o - object to be serialized to XML
	 * @param out - OutputStream this object will be written to
	 * @throws ObjectWriteException
	 * @author Tristan Bepler
	 */
	public static void serialize(Object o, OutputStream out) throws ObjectWriteException{
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			fillTreeRecurse(o, null, doc, new ArrayList<Object>());
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 2);
			Transformer transformer = formatXML(transformerFactory.newTransformer());
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(out);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
		}catch(Exception e){
			throw new ObjectWriteException(e);
		}
	}


	/**
	 * This method is used to serialize the given object to XML. The object XML will be written to a file at the path
	 * specified by the given string. Objects should implement the Serializable interface or inherit it from a superclass.
	 * Furthermore, as many superclasses and contained classes should implement Serializable as possible. If they do not,
	 * behavior may be unpredictable. See this classes description for more information. {@link XMLSerializerUtil}
	 * @param o - object to be serialized to XML
	 * @param file - file the object XML should be written to
	 * @throws ObjectWriteException
	 * @author Tristan Bepler
	 */
	public static void write(Object o, String file) throws ObjectWriteException{
		try{
			serialize(o, new BufferedOutputStream(new FileOutputStream(file)));
		} catch (Exception e){
			throw new ObjectWriteException(e);
		}
	}

	/**
	 * This method recursively serializes objects.
	 * @author Tristan Bepler
	 */
	private static void fillTreeRecurse(Object o, Element parent, Document doc, ArrayList<Object> written) throws IllegalArgumentException, IllegalAccessException{
		// write nothing if o is null
		if(o == null){
			return;
		}
		//initialize parent node if uninitialized
		if(parent == null){
			parent = doc.createElement(o.getClass().getSimpleName());
			doc.appendChild(parent);
		}
		//set the classpath of this object as an attribute of the node
		parent.setAttribute("classpath", o.getClass().getName());
		//simply write the objects vale if it is a primitive wrapper
		if(XMLSerializerUtil.isWrapperType(o.getClass())){
			parent.setTextContent(String.valueOf(o));
			return;
		}
		//if the object references an object that has already been written, tag it's refId
		if(written.contains(o)){
			parent.setAttribute("seeRefId", String.valueOf(written.indexOf(o)));
			return;
		}
		//add this object to the written object array and set its refId
		written.add(o);
		parent.setAttribute("refId", String.valueOf(written.indexOf(o)));
		//special cases
		if(writeSpecialCaseObjectIsString(o, parent, doc, written)){
			return;
		}
		if(writeSpecialCaseObjectIsClass(o, parent, doc, written)){
			return;
		}
		if(writeSpecialCaseObjectIsMap(o, parent, doc, written)){
			return;
		}
		//if object is an array, have to handle it specially, recursively write array elements
		if(o.getClass().isArray()){
			for(int i=0; i<Array.getLength(o); i++){
				Element entry = doc.createElement("entry");
				parent.appendChild(entry);
				entry.setAttribute("index", String.valueOf(i));
				fillTreeRecurse(Array.get(o, i), entry, doc, written);
			}
			return;
		}
		//object is a proper object, check and write its superclass and fill all fields
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> clazz = o.getClass();
		while(clazz != null){
			parent.setAttribute("superclass", clazz.getName());
			classes.add(clazz);
			clazz = clazz.getSuperclass();
		}
		classes.add(o.getClass());
		//now write the object's fields recursively
		for(Field f : getAllFields(classes)){
			f.setAccessible(true);
			Object value = f.get(o);
			//ignore this field if it is null or static and final or annotated by @XMLIgnore
			if(value==null){
				continue;
			}
			if(f.isAnnotationPresent(XMLIgnore.class)){
				continue;
			}
			if(Modifier.isStatic(f.getModifiers())&&Modifier.isFinal(f.getModifiers())){
				continue;
			}
			String name = f.getName();
			name = name.replace('$', REPLACEMENT_MAP.get('$'));
			Element field = doc.createElement(name);
			parent.appendChild(field);
			fillTreeRecurse(value, field, doc, written);
		}
	}

	/**
	 * Returns all the unique fields of the classes in the given list.
	 * @author Tristan Bepler
	 */
	private static Field[] getAllFields(List<Class<?>> classes){
		Set<Field> fields = new HashSet<Field>();
		for(Class<?> clazz : classes){
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Method for handling serialization of String objects.
	 * @author Tristan Bepler
	 */
	private static boolean writeSpecialCaseObjectIsString(Object o, Element cur, Document doc, ArrayList<Object> written){
		if(o instanceof String){
			cur.setTextContent((String) o);
			return true;
		}
		return false;
	}

	/**
	 * Method for handling deserialization of String objects.
	 * @author Tristan Bepler
	 */
	private static Object readSpecialCaseObjectIsString(Class<?> clazz, Element cur, Map<Integer, Object> references){
		if(String.class == clazz){
			return cur.getTextContent();
		}
		return null;
	}

	/**
	 * Method for handling serialization of class objects.
	 * @author Tristan Bepler
	 */
	private static boolean writeSpecialCaseObjectIsClass(Object o, Element cur, Document doc, ArrayList<Object> written){
		if(o instanceof Class){
			cur.setTextContent(((Class)o).getName());
			return true;
		}
		return false;
	}

	/**
	 * Method for handling deserialization of class objects.
	 * @author Tristan Bepler
	 */
	private static Object readSpecialCaseObjectIsClass(Class<?> clazz, Element cur, ClassLoader loader, Map<Integer, Object>references) throws ClassNotFoundException{
		if(Class.class == clazz){
			if(PRIMITIVE_CLASSES.containsKey(cur.getTextContent())){
				return PRIMITIVE_CLASSES.get(cur.getTextContent());
			}
			try{
				return loader.loadClass(cur.getTextContent());
			} catch (Exception e){
				return Class.forName(cur.getTextContent());
			}
		}
		return null;
	}

	/**
	 * Method for handling serialization of map objects. Maps were very verbose before.
	 * @author Tristan Bepler
	 */
	private static boolean writeSpecialCaseObjectIsMap(Object o, Element cur, Document doc, ArrayList<Object> written) throws IllegalArgumentException, IllegalAccessException {
		if(o instanceof Map){
			Map map = (Map) o;
			for(Object key : map.keySet()){
				Element entry = doc.createElement("entry");
				cur.appendChild(entry);
				Element k = doc.createElement("key");
				entry.appendChild(k);
				fillTreeRecurse(key, k, doc, written);
				Element v = doc.createElement("value");
				entry.appendChild(v);
				fillTreeRecurse(map.get(key), v, doc, written);
			}
			return true;
		}
		return false;
	}

	/**
	 * This method handled deserialization of map objects.
	 * @author Tristan Bepler
	 */
	private static Object readSpecialCaseObjectIsMap(Class<?> clazz, Element cur, ClassLoader loader, Map<Integer, Object> references) throws Exception{
		if(Map.class.isAssignableFrom(clazz)){
			try{
				Class<? extends Map> c = (Class<? extends Map>) loader.loadClass(cur.getAttribute("classpath"));
				Constructor con = c.getConstructor();
				con.setAccessible(true);
				Map map = (Map) con.newInstance();
				if(cur.hasAttribute("refId")){
					int id = Integer.parseInt(cur.getAttribute("refId").trim());
					references.put(id, map);
				}
				List<Element> entries = getDirectChildElementsByTag(cur, "entry");
				for(Element entry: entries){
					Element k = getDirectChildElementsByTag(entry, "key").get(0);
					Object key;
					if(!k.hasAttribute("classpath")){
						key = null;
					}else{
						Class<?> keyClass;
						try{
							keyClass = loader.loadClass(k.getAttribute("classpath"));
						}catch(Exception e){
							keyClass = Class.forName(k.getAttribute("classpath"));
						}
						key = readTreeRecurse(keyClass, k, loader, references);
					}
					Element v = getDirectChildElementsByTag(entry, "value").get(0);
					Object value;
					if(!v.hasAttribute("classpath")){
						value = null;
					}else{
						Class<?> valueClass;
						try{
							valueClass = loader.loadClass(v.getAttribute("classpath"));
						}catch(Exception e){
							valueClass = Class.forName(v.getAttribute("classpath"));
						}
						value = readTreeRecurse(valueClass, v, loader, references);
					}
					map.put(key, value);
				}
				return map;
			}catch (Exception e){
				throw new Exception(e);
			}
		}
		return null;
	}
	
	/**
	 * This method is used to deserialize a previously XML serialized object from the given InputStream. The class
	 * of the object that should be returned is specified by the clazz parameter. The ClassLoader will be used to
	 * retrieve the classes required for object instantiation. This is to allow loading of classes from external sources
	 * if necessary. See this classes description for more information on object serialization and deserialization.
	 * {@link XMLSerializerUtil}
	 * @param <T> - the type of the object that will be returned
	 * @param clazz - the class of the object to be deserialized
	 * @param in - the InputStream the object will be deserialized from
	 * @param loader - a ClassLoader that will be used to load Class objects
	 * @return - the deserialized object
	 * @throws ObjectReadException
	 * @author Tristan Bepler
	 */
	public static <T> T deserialize(Class<T> clazz, InputStream in, ClassLoader loader) throws ObjectReadException{
		try {
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(in));
			Document doc = parser.getDocument();
			doc.getDocumentElement().normalize();
			T readObject = readTreeRecurse(clazz, doc.getDocumentElement(), loader, new HashMap<Integer, Object>());
			return readObject;
		} catch (Exception e) {
			throw new ObjectReadException(e);
		}
	}
	
	/**
	 * This method is used to deserialize a previously XML serialized object from the given InputStream. The class
	 * of the object that should be returned is specified by the clazz parameter. See this classes description for
	 * more information on object serialization and deserialization.{@link XMLSerializerUtil}
	 * @param <T> - the type of the object that will be returned
	 * @param clazz - the class of the object to be deserialized
	 * @param in - the InputStream the object will be deserialized from
	 * @return - the deserialized object
	 * @throws ObjectReadException
	 * @author Trisan Bepler
	 */
	public static <T> T deserialize(Class<T> clazz, InputStream in) throws ObjectReadException{
		return deserialize(clazz, in, ClassLoader.getSystemClassLoader());
	}

	/**
	 * This method is used to deserialize a previously serialized object from the XML file it was written to. The class
	 * of the object that should be returned is specified by the clazz parameter. The ClassLoader will be used to
	 * retrieve the classes required for object instantiation. This is to allow loading of classes from external sources
	 * if necessary. See this classes description for more information on object serialization and deserialization.
	 * {@link XMLSerializerUtil}
	 * @param <T> - the type of the object that will be returned
	 * @param clazz - the class of the object to be deserialized
	 * @param file - the file containing the object XML
	 * @param loader - a ClassLoader that will be used to load Class objects.
	 * @return - the deserialized object
	 * @throws ObjectReadException
	 * @author Tristan Bepler
	 */
	public static <T> T read(Class<T> clazz, String file, ClassLoader loader) throws ObjectReadException{
		try {
			return deserialize(clazz, new BufferedInputStream(new FileInputStream(file)), loader);
		} catch (Exception e) {
			throw new ObjectReadException(e);
		}

	}

	/**
	 * This method is used to deserialize and object from its serialized object XML. The class of the object is specified
	 * by the clazz parameter. The path of the file the object should be deserialized from is specified by the file 
	 * parameter. See this classes description for more information about object serialization and deserialization.
	 * {@link XMLSerializerUtil}
	 * @param <T> - the type of the object that will be returned
	 * @param clazz - the class of the object to be deserialized
	 * @param file - the file containing the object XML
	 * @return - the deserialized object
	 * @throws ObjectReadException
	 * @author Tristan Bepler
	 */
	public static <T> T read(Class<T> clazz, String file) throws ObjectReadException{
		return read(clazz, file, ClassLoader.getSystemClassLoader());
	}

	/**
	 * This method recursively deserializes objects
	 * @author Tristan Bepler
	 */
	private static <T,S extends T> S readTreeRecurse(Class<T> clazz, Element cur, ClassLoader loader, Map<Integer, Object> references) throws ObjectReadException{
		//if cur is null return null
		if(cur == null){
			return null;
		}
		//if cur is already written elswhere, read it from it's reference
		if(cur.hasAttribute("seeRefId")){
			int refId = Integer.parseInt(cur.getAttribute("seeRefId"));
			try{
				Object reference = references.get(refId);
				return (S) reference;
			} catch(Exception e){
				throw new ObjectReadException("Error: could find reference object");
			}
		}
		//if cur represents a primitive type, parse and return it
		if(clazz.isPrimitive()){
			try {
				return (S) toObject(clazz, cur.getTextContent().trim());
			}catch(Exception e){
				throw new ObjectReadException(e);
			}
		}
		//if cur has no classpath specified, return null
		if(!cur.hasAttribute("classpath")){
			return null;
		}
		try {
			//get the class of cur
			Class<S> c;
			try{
				c = (Class<S>) loader.loadClass(cur.getAttribute("classpath"));
			} catch(Exception e){
				c = (Class<S>) Class.forName(cur.getAttribute("classpath"));
			}
			//if class of cur does not extend desired class, throw error
			if(!clazz.isAssignableFrom(c)){
				throw new ObjectReadException("Error: class \""+clazz.getName()+"\" cannot be assigned from class \""+c.getName()+"\"");
			}
			//if cur is a wrapper type, parse and return it
			if(isWrapperType(c)){
				return (S) toObject(c, cur.getTextContent().trim());
			}
			//secial case, if cur is a string return it
			Object string = readSpecialCaseObjectIsString(c, cur, references);
			if(string != null){
				if(cur.hasAttribute("refId")){
					int id = Integer.parseInt(cur.getAttribute("refId"));
					references.put(id, string);
				}
				return (S) string;
			}
			Object isClass = readSpecialCaseObjectIsClass(c, cur, loader, references);
			if(isClass != null){
				if(cur.hasAttribute("refId")){
					int id = Integer.parseInt(cur.getAttribute("refId"));
					references.put(id, string);
				}
				return (S) isClass;
			}
			Object isMap = readSpecialCaseObjectIsMap(c, cur, loader, references);
			if(isMap != null){
				return (S) isMap;
			}
			//if cur is an array, instantiate it and recursively build its elements
			if(c.isArray()){
				Class<?> type = c.getComponentType();
				List<Element> children = getDirectChildElementsByTag(cur, "entry");
				S readArray = (S) Array.newInstance(type, children.size());
				if(cur.hasAttribute("refId")){
					int id = Integer.parseInt(cur.getAttribute("refId"));
					references.put(id, readArray);
				}
				for(int i=0; i<children.size(); i++){
					Element entry = (Element) children.get(i);
					int index = Integer.parseInt(entry.getAttribute("index"));
					Array.set(readArray, index, readTreeRecurse(type, entry, loader, references));
				}
				return readArray;
			}
			//check what cur's superclass is and get all parent classes
			Class<? super S> sup = (Class<? super S>) Class.forName(cur.getAttribute("superclass"));
			List<Class<?>> classes = new ArrayList<Class<?>>();
			Class<?> curClass = c;
			while(curClass != sup){
				classes.add(curClass);
				curClass = curClass.getSuperclass();
			}
			classes.add(sup);
			//create object using constructor of its highest level superclass not implementing serializable
			S readObject = SilentObjectCreator.create(c, sup);
			if(cur.hasAttribute("refId")){
				int id = Integer.parseInt(cur.getAttribute("refId"));
				references.put(id, readObject);
			}
			//fill all fields recursively
			for(Field f : getAllFields(classes)){
				f.setAccessible(true);
				//if field is final static or transient, ignore
				if(Modifier.isStatic(f.getModifiers())&&Modifier.isFinal(f.getModifiers())){
					continue;
				}
				//if field is annoted with @XMLIgnore, ignore
				if(f.isAnnotationPresent(XMLIgnore.class)){
					continue;
				}
				//if field is final, remove final modifier
				if(Modifier.isFinal(f.getModifiers())){
					Field mods = Field.class.getDeclaredField("modifiers");
					mods.setAccessible(true);
					mods.setInt(f, f.getModifiers() & ~Modifier.FINAL);
				}
				String name = f.getName();
				name = name.replace('$', REPLACEMENT_MAP.get('$'));
				List<Element> field = getDirectChildElementsByTag(cur, name);
				if(field.isEmpty()){
					f.set(readObject, null);
				}else{
					Element child = getDirectChildElementsByTag(cur, name).get(0);
					Class<?> type = f.getType();
					f.set(readObject, readTreeRecurse(type, child, loader, references));
				}
			}
			return readObject;

		} catch (Exception e) {
			throw new ObjectReadException(e);
		}


	}

	/**
	 * This method returns a list of the direct element node children of this element node with the specified tag.
	 * @param node - parent node
	 * @param tag - tag of direct children to be returned
	 * @return a list containing the direct element children with the given tag
	 * @author Tristan Bepler
	 */
	public static List<Element> getDirectChildElementsByTag(Element node, String tag){
		List<Element> children = new ArrayList<Element>();
		Node child = node.getFirstChild();
		while(child!=null){
			if(child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(tag)){
				children.add((Element) child);
			}
			child = child.getNextSibling();
		}
		return children;
	}

	/**
	 * This method formats the XML file by omitting the XML Declaration and 
	 * creating indentations 
	 * @param transformer - transformer that is used to process XML
	 * @return a transformer that omits the XML declaration and performs indentations  
	 * @author Alex Song
	 */
	private static Transformer formatXML(Transformer transformer) {
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		return transformer;
	}






}
