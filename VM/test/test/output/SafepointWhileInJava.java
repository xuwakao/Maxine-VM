/*
 * Copyright (c) 2007 Sun Microsystems, Inc.  All rights reserved.
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
package test.output;


public class SafepointWhileInJava {
    private static final class Spinner implements Runnable {
        boolean done;
        int iterations;
        public void run() {
            System.out.println("Spinner: spinning...");
            final Object[] localRefs = new Object[1000];
            while (!stopRequested()) {
                frameWithReferences(localRefs);
                ++iterations;
            }
        }
        private Object frameWithReferences(final Object[] localRefs) {
            Object o = null;
            for (int i = 0; i != localRefs.length; ++i) {
                localRefs[i] = System.out;
                o = localRefs[i];
            }
            return o;
        }

        synchronized boolean stopRequested() {
            return done;
        }
        synchronized void requestStop() {
            done = true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Spinner spinner = new Spinner();
        System.gc();
        final Thread spinnerThread = new Thread(spinner, "Spinner");
        spinnerThread.start();

        // Poll 'spinner' until it starts spinning
        while (spinner.iterations < 1000) {
            Thread.sleep(1);
        }

        // GC while 'spinner' is spinning in Java code
        for (int i = 0; i < 5; ++i) {
            System.out.println("GC start " + i);
            System.gc();
            System.out.println("GC stop " + i);
        }

        // Stop 'spinner'
        spinner.requestStop();
        spinnerThread.join();
    }
}
