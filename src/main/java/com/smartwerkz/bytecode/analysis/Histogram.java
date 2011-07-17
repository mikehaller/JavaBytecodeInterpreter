package com.smartwerkz.bytecode.analysis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Histogram {

	private final Map<Object, InputVector> counter = new HashMap<Object, InputVector>();

	public synchronized void count(Object key, Object... parameters) {
		if (!counter.containsKey(key)) {
			InputVector keys = new InputVector(key);
			keys.attach(parameters);
			counter.put(key, keys);
			return;
		}
		counter.get(key).attach(parameters);
	}

	private static class InputVector {
		private final Object keyObject;
		private final List<Object> attachments = new ArrayList<Object>();
		private BigInteger counter = BigInteger.ONE;

		public InputVector(Object keyObject) {
			this.keyObject = keyObject;
		}

		public void attach(Object[] parameters) {
			Collections.addAll(attachments, parameters);
			counter = counter.add(BigInteger.ONE);
		}

		public BigInteger getCounter() {
			return counter;
		}

		public Object getKey() {
			return keyObject;
		}

		public List<Object> getAttachments() {
			return attachments;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((keyObject == null) ? 0 : keyObject.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InputVector other = (InputVector) obj;
			if (keyObject == null) {
				if (other.keyObject != null)
					return false;
			} else if (!keyObject.equals(other.keyObject))
				return false;
			return true;
		}

	}

	public String dump() {
		StringBuffer sb = new StringBuffer();
		Set<Entry<Object, InputVector>> entrySet = counter.entrySet();
		for (Entry<Object, InputVector> entry : entrySet) {
			Object key = entry.getKey();
			InputVector value = entry.getValue();
			String format = String.format("%20s %20s %50s", value.getCounter(), key, value.getAttachments());
			sb.append(format);
			sb.append("\n");
		}
		return sb.toString();
	}

}
