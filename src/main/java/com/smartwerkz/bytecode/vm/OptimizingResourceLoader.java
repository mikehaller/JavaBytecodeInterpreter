package com.smartwerkz.bytecode.vm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import proguard.Configuration;
import proguard.ConfigurationParser;
import proguard.ParseException;
import proguard.classfile.ClassPool;
import proguard.classfile.ProgramClass;
import proguard.classfile.io.ProgramClassReader;
import proguard.classfile.io.ProgramClassWriter;
import proguard.optimize.Optimizer;

public class OptimizingResourceLoader implements ResourceLoader {

	private final VMLog log = new VMLog(OptimizingResourceLoader.class.getSimpleName());

	private final ResourceLoader original;

	public OptimizingResourceLoader(ResourceLoader original) {
		this.original = original;
	}

	@Override
	public InputStream open(String resourceName) {
		// Step 1: load from original
		// Step 2: optimize
		// Step 3: return
		InputStream open = original.open(resourceName);
		if (open == null)
			return null;

		try {

			String clazzName = resourceName.replaceAll(Pattern.quote(".class"), "").replace('/', '.');
			log.debug("Optimizing: %s", clazzName);
			
			String[] args = new String[] {
					"-keepclasseswithmembers public class " + clazzName,
					"-keepclassmembers class *",
					"-dontshrink",
					"-dontoptimize",
					"-dontobfuscate",
					"-dump",
					"-verbose",
			};
			ConfigurationParser parser = new ConfigurationParser(args);
			Configuration config = new Configuration();
			parser.parse(config);
			
			Optimizer optimizer = new Optimizer(config);
			ClassPool programCP = new ClassPool();
			ProgramClass clazz = new ProgramClass();
			clazz.accept(new ProgramClassReader(new DataInputStream(open)));
			programCP.addClass(clazz);
			ClassPool libraryCP = new ClassPool();

			optimizer.execute(programCP, libraryCP);

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			clazz.accept(new ProgramClassWriter(new DataOutputStream(bytes)));
			return new ByteArrayInputStream(bytes.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

}
