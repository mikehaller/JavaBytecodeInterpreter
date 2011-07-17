package com.smartwerkz.bytecode.vm;

import java.util.concurrent.atomic.AtomicLong;

public class VMLog {

	private static final boolean OVERRIDE = true;

	private static final AtomicLong id = new AtomicLong(0);
	private final String name;
	private boolean enabled;

	public VMLog(String name) {
		this(name, true);
	}

	public VMLog(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	public void debug(String msg, Object... parameters) {
		if (!OVERRIDE)
			return;
		if (!enabled)
			return;
		System.out.println(format(resolveMsg(msg, parameters)));
	}

	public void warn(String msg, Object... parameters) {
		System.out.println(format(resolveMsg(msg, parameters)));
	}

	public void error(String msg, Object... parameters) {
		if (!OVERRIDE)
			return;
		System.err.println(format(resolveMsg(msg, parameters)));
	}

	private String format(String resolveMsg) {
		return String.format("%tT %08d %s %s", System.currentTimeMillis(), id.incrementAndGet(), name, resolveMsg);
	}

	protected String resolveMsg(String msg, Object... parameters) {
		return String.format(msg, parameters);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void disable() {
		enabled=false;
	}


}
