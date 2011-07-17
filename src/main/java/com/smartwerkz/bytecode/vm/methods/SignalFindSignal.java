package com.smartwerkz.bytecode.vm.methods;

import com.smartwerkz.bytecode.primitives.JavaInteger;
import com.smartwerkz.bytecode.primitives.JavaObject;
import com.smartwerkz.bytecode.primitives.JavaString;
import com.smartwerkz.bytecode.vm.Frame;
import com.smartwerkz.bytecode.vm.OperandStack;
import com.smartwerkz.bytecode.vm.RuntimeDataArea;

public class SignalFindSignal implements NativeMethod {

	// SIGABRT Process aborted 6
	// SIGALRM Signal raised by alarm 14
	// SIGBUS Bus error: "access to undefined portion of memory object" 7
	// SIGCHLD Child process terminated, stopped (or continued*) 17
	// SIGCONT Continue if stopped 18
	// SIGFPE Floating point exception: "erroneous arithmetic operation" 8
	// SIGHUP Hangup 1
	// SIGILL Illegal instruction 4
	// SIGINT Interrupt 2
	// SIGKILL Kill (terminate immediately) 9
	// SIGPIPE Write to pipe with no one reading 13
	// SIGQUIT Quit and dump core 3
	// SIGSEGV Segmentation violation 11
	// SIGSTOP Stop executing temporarily 19
	// SIGTERM Termination (request to terminate) 15
	// SIGTSTP Terminal stop signal 20
	// SIGTTIN Background process attempting to read from tty ("in") 21
	// SIGTTOU Background process attempting to write to tty ("out") 22
	// SIGUSR1 User-defined 1 10
	// SIGUSR2 User-defined 2 12
	// SIGPOLL Pollable event 29
	// SIGPROF Profiling timer expired 27
	// SIGSYS Bad syscall 31
	// SIGTRAP Trace/breakpoint trap 5
	// SIGURG Urgent data available on socket 23
	// SIGVTALRM Signal raised by timer counting virtual time:
	// "virtual timer expired" 26
	// SIGXCPU CPU time limit exceeded 24
	// SIGXFSZ File size limit exceeded 25

	@Override
	public void execute(RuntimeDataArea rda, Frame frame, OperandStack otherOperandStack) {
		JavaObject signalName = frame.getLocalVariables().getLocalVariable(0);
		if (signalName instanceof JavaString) {
			JavaString javaString = (JavaString) signalName;
			if (javaString.asStringValue().equals("INT")) {
				otherOperandStack.push(new JavaInteger(2));
				return;
			}
			if (javaString.asStringValue().equals("TERM")) {
				otherOperandStack.push(new JavaInteger(15));
				return;
			}
		}
		
		// Unknown signal name
		otherOperandStack.push(new JavaInteger(-1));
	}

}
