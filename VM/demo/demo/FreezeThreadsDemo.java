/*
 * Copyright (c) 2010 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation, these intellectual property
 * rights may include one or more of the U.S. patents listed at http://www.sun.com/patents and one or
 * more additional patents or pending patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software. Government users are subject to the Sun
 * Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its
 * supplements.
 *
 * Use is subject to license terms. Sun, Sun Microsystems, the Sun logo, Java and Solaris are trademarks or
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All SPARC trademarks
 * are used under license and are trademarks or registered trademarks of SPARC International, Inc. in the
 * U.S. and other countries.
 *
 * UNIX is a registered trademark in the U.S. and other countries, exclusively licensed through X/Open
 * Company, Ltd.
 */
package demo;

import java.util.*;

import com.sun.max.unsafe.*;
import com.sun.max.vm.heap.*;
import com.sun.max.vm.jdk.*;
import com.sun.max.vm.runtime.*;
import com.sun.max.vm.stack.*;
import com.sun.max.vm.thread.*;

/**
 * Demonstrates usage of the {@link VmOperation} mechanism.
 * This demo starts a number of threads that spin in a loop
 * that allocates object arrays of length 1024, thus creating
 * garbage quickly. The main thread also spins in a loop for
 * a specified number of seconds, printing a stack trace of
 * all the other spinning threads.
 *
 * This demo also shows what happens if a FreezeThreads operation
 * triggers a GC.
 *
 * @author Doug Simon
 */
public class FreezeThreadsDemo extends VmOperation {

    private final HashSet<Thread> threads;

    public FreezeThreadsDemo(HashSet<Thread> threads) {
        super("FreezeThreadsDemo", null, Mode.Safepoint);
        this.threads = threads;
    }

    @Override
    protected void doIt() {
        super.doIt();
    }

    @Override
    protected boolean operateOnThread(VmThread thread) {
        return threads.contains(thread.javaThread());
    }

    @Override
    public void doThread(Pointer threadLocals, Pointer instructionPointer, Pointer stackPointer, Pointer framePointer) {
        VmThread vmThread = VmThread.fromVmThreadLocals(threadLocals);
        final List<StackFrame> frameList = new ArrayList<StackFrame>();
        new VmStackFrameWalker(threadLocals).frames(frameList, instructionPointer, stackPointer, framePointer);
        Thread thread = vmThread.javaThread();
        StackTraceElement[] trace = JDK_java_lang_Throwable.asStackTrace(frameList, null, Integer.MAX_VALUE);
        System.out.println(thread + " [stack depth: " + trace.length + "]");
        for (StackTraceElement e : trace) {
            System.out.println("\tat " + e);
        }
    }

    static boolean done;
    static int started;

    public static void main(String[] args) {
        int seconds = args.length == 0 ? 5 : Integer.parseInt(args[0]);
        Thread[] spinners = new Thread[10];
        for (int i = 0; i < spinners.length; i++) {
            Thread spinner = new Thread() {
                @Override
                public void run() {
                    synchronized (FreezeThreadsDemo.class) {
                        started++;
                    }
                    while (!done) {
                        Object[] o = new Object[1024];
                        o[0] = o;
                    }
                }
            };
            spinners[i] = spinner;
            spinner.start();
        }
        while (started != spinners.length) {
            Thread.yield();
        }

        FreezeThreadsDemo stackTraceDumper = new FreezeThreadsDemo(new HashSet<Thread>(Arrays.asList(spinners)));
        long start = System.currentTimeMillis();
        int time;
        try {
            do {
                time = (int) (System.currentTimeMillis() - start) / 1000;
                try {
                    System.out.println("---- Dumping stacks of spinning threads ----");
                    VmOperationThread.execute(stackTraceDumper);
                } catch (Heap.HoldsGCLockError e) {
                    System.out.println("GC triggered while dumping stack traces");
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (time < seconds);
        } finally {
            done = true;
        }
    }
}
