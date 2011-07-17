package com.smartwerkz.bytecode.classfile;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;
import com.smartwerkz.bytecode.vm.VMLog;
import com.smartwerkz.bytecode.vm.VirtualMachine;

/**
 * A Java <tt>.class</tt> file.
 * 
 * u1=unsigned byte u2=unsigned short u4=int
 * 
 * <pre>
 * ClassFile {
 *     	u4 magic;
 *     	u2 minor_version;
 *     	u2 major_version;
 *     	u2 constant_pool_count;
 *     	cp_info constant_pool[constant_pool_count-1];
 *     	u2 access_flags;
 *     	u2 this_class;
 *     	u2 super_class;
 *     	u2 interfaces_count;
 *     	u2 interfaces[interfaces_count];
 *     	u2 fields_count;
 *     	field_info fields[fields_count];
 *     	u2 methods_count;
 *     	method_info methods[methods_count];
 *     	u2 attributes_count;
 *     	attribute_info attributes[attributes_count];
 *     }
 * </pre>
 * 
 * @author mhaller
 */
public class DefaultClassfile implements Closeable, Classfile {

	private final VMLog log = new VMLog(DefaultClassfile.class.getName(), false);

	private final byte[] magicNumber = new byte[4];
	private final List<String> interfaces = new ArrayList<String>();
	private int minorVersion;
	private int majorVersion;
	private final DataInputStream dis;
	private ConstantPool pool;
	private ClassAccessFlags accessFlags;
	private int thisClassIndex;
	private int superClassIndex;
	private Fields fields;
	private Methods methods;
	private Attributes attributes;

	private JavaClassReference javaClassReference;

	public DefaultClassfile(String classpathResource) {
		this(safeLoad(classpathResource));
	}

	protected static InputStream safeLoad(String classpathResource) {
		try {
			URL resource = Thread.currentThread().getContextClassLoader().getResource(classpathResource);
			if (resource == null) {
				throw new IllegalArgumentException("Unable to find classpath resource: " + classpathResource);
			}
			return resource.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public DefaultClassfile(InputStream resource) {
		try {
			dis = new DataInputStream(resource);
			readMagic();
			readVersion();
			readConstant();
			readAccess();
			readThis();
			readSuper();
			readInterfaces();
			readFields();
			readMethods();
			readAttributes();
			javaClassReference = new JavaClassReference(this);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private void readAttributes() throws IOException {
		attributes = new Attributes(pool, dis);
	}

	private void readMethods() throws IOException {
		methods = new Methods(getThisClassName(), pool, dis);
	}

	private void readFields() throws IOException {
		fields = new Fields(pool, dis);
	}

	private void readInterfaces() throws IOException {
		int interfaceCount = dis.readUnsignedShort();
		log.debug("Reading %d interfaces", interfaceCount);
		for (int i = 0; i < interfaceCount; i++) {
			int index = dis.readUnsignedShort();
			Object entry = pool.getEntry(index);
			if (entry instanceof ConstantStringInfo) {
				ConstantStringInfo constantStringInfo = (ConstantStringInfo) entry;
				ConstantUTF8Info utf8Info = pool.getUTF8Info(constantStringInfo.getStringIndex());
				interfaces.add(utf8Info.getValue());
			} else if (entry instanceof ConstantClassInfo) {
				ConstantClassInfo constantClassInfo = (ConstantClassInfo) entry;
				ConstantUTF8Info utf8Info = pool.getUTF8Info(constantClassInfo.getIndex());
				interfaces.add(utf8Info.getValue());
			} else if (entry instanceof ConstantUTF8Info) {
				ConstantUTF8Info constantUTF8Info = (ConstantUTF8Info) entry;
				interfaces.add(constantUTF8Info.getValue());

			} else
				throw new IllegalStateException("Entry " + index + " while reading class " + getThisClassName()
						+ " to interfaces list is strange: " + entry);
		}
	}

	private void readSuper() throws IOException {
		superClassIndex = dis.readUnsignedShort();
		log.debug("Reading name of super class: %s", getSuperClassName());
	}

	private void readThis() throws IOException {
		thisClassIndex = dis.readUnsignedShort();
		log.debug("Reading name of class at index: %d", thisClassIndex);
		log.debug("Reading name of class: %s", getThisClassName());
	}

	private void readAccess() throws IOException {
		accessFlags = new ClassAccessFlags(dis);
		log.debug("Reading access flags: %s", accessFlags);
	}

	private void readConstant() throws IOException {
		pool = new ConstantPool(dis);
		// log.debug("Reading constant pool: %s", pool);
	}

	protected void readMagic() throws IOException {
		int read = dis.read(magicNumber);
		log.debug("Reading magic bytes from class: %d bytes = %s", read, new BigInteger(magicNumber).toString(16));
	}

	private void readVersion() throws IOException {
		minorVersion = dis.readUnsignedShort();
		majorVersion = dis.readUnsignedShort();
		log.debug("Reading class file version %d.%d", majorVersion, minorVersion);
	}

	public byte[] getMagicNumber() throws IOException {
		byte[] result = new byte[4];
		System.arraycopy(magicNumber, 0, result, 0, 4);
		return result;
	}

	@Override
	public void close() throws IOException {
		dis.close();
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	@Override
	public ConstantPool getConstantPool() {
		return pool;
	}

	@Override
	public ClassAccessFlags getAccessFlags() {
		return accessFlags;
	}

	public int getThisClassIndex() {
		return thisClassIndex;
	}

	@Override
	public String getThisClassName() {
		ConstantClassInfo classInfoEntry = pool.getClassInfoEntry(thisClassIndex);
		ConstantUTF8Info utf8Info = pool.getUTF8Info(classInfoEntry.getIndex());
		return utf8Info.getValue();
	}

	@Override
	public boolean equals(Object obj) {
		Classfile other = (Classfile) obj;
		// TODO: Also add the 'classloader' to the comparison
		return getThisClassName().equals(other.getThisClassName());
	}

	@Override
	public String toString() {
		return String.format("%s[classname=%s]", super.toString(), getThisClassName());
	}

	public int getSuperClassIndex() {
		return superClassIndex;
	}

	@Override
	public String getSuperClassName() {
		ConstantClassInfo classInfoEntry = pool.getClassInfoEntry(superClassIndex);
		if (classInfoEntry == null)
			return null;
		ConstantUTF8Info utf8Info = pool.getUTF8Info(classInfoEntry.getIndex());
		return utf8Info.getValue();
	}

	public List<String> getInterfaces() {
		return Collections.unmodifiableList(interfaces);
	}

	@Override
	public Fields getFields() {
		return fields;
	}

	@Override
	public Methods getMethods(VirtualMachine vm) {
		return methods;
	}

	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	public String getMethodName(int index) {
		MethodInfo methodInfo = methods.getMethodInfo(index);
		ConstantUTF8Info utf8Info = pool.getUTF8Info(methodInfo.getNameIndex());
		return utf8Info.getValue();
	}

	// public String getAttributeName(int index) {
	// AttributeInfo attributeInfo = attributes.getAttributeInfo(index);
	// return pool.getUTF8Info(attributeInfo.getNameIndex()).getValue();
	// }

	public CodeAttribute getMethodCode(int index) throws IOException {
		return methods.getMethodInfo(index).getCode();
	}

	public MethodInfo getMethod(int i) {
		return methods.getMethodInfo(i);
	}

	@Override
	public JavaClassReference getAsJavaClassReference() {
		return javaClassReference;
	}

	@Override
	public boolean isInstanceOf(RuntimeDataArea rda, Frame frame, String className) {
		if (className.equals(getThisClassName())) {
			return true;
		}
		if (className.equals(getSuperClassName())) {
			return true;
		}
		if (getSuperClassName() == null) {
			return false;
		}
		
		// Interfaces
		if (getInterfaces().contains(className)) {
			return true;
		}

		// TODO: Tempfix: Any array can be cast down to object array
		if (getThisClassName().equals("java/lang/Class")
				&& className.equals("[Ljava/lang/Object;")) {
			return true;
		}
		
		// Super-Classes (Downcasting)
		Classfile cf = rda.loadClass(frame.getVirtualThread(), getSuperClassName());
		while (true) {
			if (cf.getSuperClassName() == null) {
				break;
			}
			cf = rda.loadClass(frame.getVirtualThread(), cf.getSuperClassName());
			if (className.equals(cf.getThisClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Classfile> getParentClasses(RuntimeDataArea rda, Frame frame) {
		List<Classfile> parentClasses = new ArrayList<Classfile>();
		Classfile cf = this;
		while (cf.getSuperClassName() != null) {
			cf = rda.loadClass(frame.getVirtualThread(), cf.getSuperClassName());
			parentClasses.add(cf);
		}
		return parentClasses;
	}
}
