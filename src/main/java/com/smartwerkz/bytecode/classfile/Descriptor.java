package com.smartwerkz.bytecode.classfile;

import java.util.ArrayList;
import java.util.List;

import com.smartwerkz.bytecode.vm.VirtualMachine;

public class Descriptor {

	/**
	 * <ol>
	 * <li>()V == 0</li
	 * <li>([Ljava/lang/String;)V == 1</li>
	 * <li>()Ljava/lang/String; == 0</li>
	 * <li>((IF)V) == 2</li>
	 * </ol>
	 */
	public static int countParameters(String str) {
		int cnt = 0;
		int beginIndex = str.indexOf('(');
		int endIndex = str.indexOf(')');
		for (int i = beginIndex + 1; i < endIndex; i++) {
			char c = str.charAt(i);
			switch (c) {
			case 'I':
				cnt++;
				break;
			case 'S':
				cnt++;
				break;
			case 'B':
				cnt++;
				break;
			case 'F':
				cnt++;
				break;
			case 'D':
				cnt++;
				break;
			case 'J':
				cnt++;
				break;
			case 'C':
				cnt++;
				break;
			case 'Z':
				cnt++;
				break;
			case '[':
				// cnt++; Ignore Arrays and only count them once, by their type
				// def
				// i++;
				break;
			case 'L':
				cnt++;
				while (c != ';') {
					i++;
					if (i < str.length()) {
						c = str.charAt(i);
					}
				}
				break;
			default:
				throw new IllegalStateException("Unknown type: '" + c + "' in " + str);
			}
		}
		return cnt;
	}

	public static List<String> toClassNames(VirtualMachine vm, String str) {
		List<String> classnames = new ArrayList<String>();
		int cnt = 0;
		int beginIndex = str.indexOf('(');
		int endIndex = str.indexOf(')');
		for (int i = beginIndex + 1; i < endIndex; i++) {
			char c = str.charAt(i);
			switch (c) {
			case 'I':
				classnames.add(vm.classes().primitives().intClass().getThisClassName());
				break;
			case 'S':
				classnames.add(vm.classes().primitives().shortClass().getThisClassName());
				break;
			case 'B':
				classnames.add(vm.classes().primitives().byteClass().getThisClassName());
				break;
			case 'F':
				classnames.add(vm.classes().primitives().floatClass().getThisClassName());
				break;
			case 'D':
				classnames.add(vm.classes().primitives().doubleClass().getThisClassName());
				break;
			case 'J':
				classnames.add(vm.classes().primitives().longClass().getThisClassName());
				break;
			case 'C':
				classnames.add(vm.classes().primitives().charClass().getThisClassName());
				break;
			case 'Z':
				classnames.add(vm.classes().primitives().booleanClass().getThisClassName());
				break;
			case '[':
				// cnt++; Ignore Arrays and only count them once, by their type
				// def
				// i++;
				break;
			case 'L':
				cnt++;
				StringBuilder clazzName = new StringBuilder();
				while (c != ';') {
					i++;
					if (i < str.length()) {
						c = str.charAt(i);
						if (c != ';') {
							clazzName.append(c);
						}
					}
				}
				classnames.add(clazzName.toString());
				break;
			default:
				throw new IllegalStateException("Unknown type: '" + c + "' in " + str);
			}
		}
		return classnames;
	}

}
