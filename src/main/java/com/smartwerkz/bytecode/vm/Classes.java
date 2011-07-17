package com.smartwerkz.bytecode.vm;

import java.util.HashMap;
import java.util.Map;

import com.smartwerkz.bytecode.classfile.Classfile;
import com.smartwerkz.bytecode.classfile.LazyClassfile;
import com.smartwerkz.bytecode.classfile.PrimitiveClassfile;
import com.smartwerkz.bytecode.primitives.JavaArray;
import com.smartwerkz.bytecode.primitives.JavaClassReference;
import com.smartwerkz.bytecode.primitives.JavaInteger;

public class Classes {

	private final VirtualMachine vm;

	private final Classfile stringClass = new LazyClassfile("java/lang/String");
	private final Classfile systemClass = new LazyClassfile("java/lang/System");
	private final Classfile objectClass = new LazyClassfile("java/lang/Object");
	private final Classfile classClass = new LazyClassfile("java/lang/Class");

	private final Primitives primitives = new Primitives();
	private final PrimitiveArrays primitiveArrays = new PrimitiveArrays();

	public class Primitives {
		private final Classfile voidClass = new PrimitiveClassfile("void");
		private final Classfile floatClass = new PrimitiveClassfile("float");
		private final Classfile doubleClass = new PrimitiveClassfile("double");
		private final Classfile charClass = new PrimitiveClassfile("char");
		private final Classfile intClass = new PrimitiveClassfile("int");
		private final Classfile byteClass = new PrimitiveClassfile("byte");
		private final Classfile longClass = new PrimitiveClassfile("long");
		private final Classfile booleanClass = new PrimitiveClassfile("boolean");
		private final Classfile shortClass = new PrimitiveClassfile("short");

		public Classfile voidClass() {
			return voidClass;
		}

		public Classfile floatClass() {
			return floatClass;
		}

		public Classfile doubleClass() {
			return doubleClass;
		}

		public Classfile charClass() {
			return charClass;
		}

		public Classfile intClass() {
			return intClass;
		}

		public Classfile byteClass() {
			return byteClass;
		}

		public Classfile longClass() {
			return longClass;
		}

		public Classfile booleanClass() {
			return booleanClass;
		}

		public Classfile shortClass() {
			return shortClass;
		}

	}

	public class PrimitiveArrays {
		private final Classfile floatArrayClass = new PrimitiveClassfile("[F");
		private final Classfile doubleArrayClass = new PrimitiveClassfile("[D");
		private final Classfile charArrayClass = new PrimitiveClassfile("[C");
		private final Classfile intArrayClass = new PrimitiveClassfile("[I");
		private final Classfile byteArrayClass = new PrimitiveClassfile("[B");
		private final Classfile longArrayClass = new PrimitiveClassfile("[J");
		private final Classfile booleanArrayClass = new PrimitiveClassfile("[Z");
		private final Classfile shortArrayClass = new PrimitiveClassfile("[S");

		public Classfile floatArrayClass() {
			return floatArrayClass;
		}

		public Classfile doubleArrayClass() {
			return doubleArrayClass;
		}

		public Classfile charArrayClass() {
			return charArrayClass;
		}

		public Classfile intArrayClass() {
			return intArrayClass;
		}

		public Classfile byteArrayClass() {
			return byteArrayClass;
		}

		public Classfile longArrayClass() {
			return longArrayClass;
		}

		public Classfile booleanArrayClass() {
			return booleanArrayClass;
		}

		public Classfile shortArrayClass() {
			return shortArrayClass;
		}

		public JavaArray convert(byte[] value) {
			JavaArray array = new JavaArray(vm, vm.classes().primitives().byteClass(), value.length);
			for (int i = 0; i < value.length; i++) {
				byte b = value[i];
				array.set(i, new JavaInteger(b));
			}
			return array;
		}

	}

	private final Map<String, JavaClassReference> mapping = new HashMap<String, JavaClassReference>();

	public Classes(VirtualMachine vm) {
		this.vm = vm;

		mapping.put("C", primitives.charClass().getAsJavaClassReference());
		mapping.put("B", primitives.byteClass().getAsJavaClassReference());
		mapping.put("I", primitives.intClass().getAsJavaClassReference());
		mapping.put("J", primitives.longClass().getAsJavaClassReference());
		mapping.put("Z", primitives.booleanClass().getAsJavaClassReference());
		mapping.put("F", primitives.floatClass().getAsJavaClassReference());
		mapping.put("D", primitives.doubleClass().getAsJavaClassReference());
		mapping.put("V", primitives.voidClass().getAsJavaClassReference());

		mapping.put("[C", primitiveArrays.charArrayClass().getAsJavaClassReference());
		mapping.put("[B", primitiveArrays.byteArrayClass().getAsJavaClassReference());
		mapping.put("[I", primitiveArrays.intArrayClass().getAsJavaClassReference());
		mapping.put("[J", primitiveArrays.longArrayClass().getAsJavaClassReference());
		mapping.put("[Z", primitiveArrays.booleanArrayClass().getAsJavaClassReference());
		mapping.put("[F", primitiveArrays.floatArrayClass().getAsJavaClassReference());
		mapping.put("[D", primitiveArrays.doubleArrayClass().getAsJavaClassReference());
	}

	public Map<String, JavaClassReference> getMapping() {
		return mapping;
	}

	public Classfile stringClass() {
		return stringClass;
	};

	public Classfile classClass() {
		return classClass;
	};

	public Classfile systemClass() {
		return systemClass;
	};

	public Classfile objectClass() {
		return objectClass;
	};

	public Primitives primitives() {
		return primitives;
	};

	public PrimitiveArrays arrays() {
		return primitiveArrays;
	};

}
