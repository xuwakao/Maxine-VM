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
package com.sun.max.vm.heap;

import com.sun.max.annotate.*;
import com.sun.max.lang.*;
import com.sun.max.unsafe.*;
import com.sun.max.vm.*;
import com.sun.max.vm.actor.holder.*;
import com.sun.max.vm.object.*;
import com.sun.max.vm.reference.*;
import com.sun.max.vm.thread.*;

public interface HeapScheme extends VMScheme {

    /**
     * @return whether a thread belongs to the GC (or otherwise it belongs to the mutator)
     */
    boolean isGcThread(VmThread vmThread);

    /**
     * Given the size of the boot image, calculates the size of the auxiliary space that the substrate is to allocate
     * and pass to the target VM.
     */
    @PROTOTYPE_ONLY
    int auxiliarySpaceSize(int bootImageSize);

    /**
     * Initialize the auxiliary space, which is provided by the substrate.
     * This space can e.g. be used to support write barriers in the primordial phase,
     * for instance by holding a primordial card table that covers the boot image.
     */
    void initializeAuxiliarySpace(Pointer primordialVmThreadLocals, Pointer auxiliarySpace);

    /**
     * Perform thread-local initializations specific to the heap scheme when starting a new VM thread. For instance
     * install card table address.
     */
    void initializeVmThread(VmThread vmThread);

    /**
     * Allocate a new array object and fill in its header and initial data.
     */
    Object createArray(DynamicHub hub, int length);

    /**
     * Allocate a new tuple and fill in its header and initial data. Obtain the cell size from the given tuple class
     * actor.
     */
    Object createTuple(Hub hub);

    /**
     * Creates a hybrid object that is both a tuple and an array,
     * but leaving out the array part beyond the tuple for now.
     */
    <Hybrid_Type extends Hybrid> Hybrid_Type createHybrid(DynamicHub hub);

    /**
     * Expands the hybrid object to its full array length.
     * The implementation may modify the original object and return it
     * or it can create a new object that contains the same tuple values and return that.
     */
    <Hybrid_Type extends Hybrid> Hybrid_Type expandHybrid(Hybrid_Type hybrid, int length);

    /**
     * Creates a shallow clone of an object.
     * The identity hash value may differ.
     * The new object is not locked.
     */
    Object clone(Object object);

    /**
     * Prevent the GC from moving the object while executing the procedure. The procedure MUST NOT allocate any objects
     * and it's runtime MUST be very, very brief.
     *
     * The implementation may disable safepoints temporarily during this call.
     *
     * Note to GC Implementors: this is an optional feature that does not have to be supported, but it is encouraged.
     * For callers, it is less advantageous than pinning, but for implementors it poses less of a burden.
     *
     * Even if pinning is supported, flashing should still be provided as a backup strategy.
     *
     * If flashing is not supported, make flash() and always return false and declare
     *
     * @INLINE. Then all flashing client code will automatically be eliminated.
     */
    <Object_Type> boolean flash(Object_Type object, Procedure<Object_Type> procedure);

    /**
     * Note to GC implementors: you really don't need to implement pinning. It's an entirely optional/experimental
     * feature. However, if present, there are parts of the JVM that will automatically take advantage of it.
     *
     * If pinning is not supported, make pin() and isPinned() always return false and declare
     *
     * @INLINE for both. Then all pinning client code will automatically be eliminated.
     */

    /**
     * Prevent the GC from moving the given object.
     *
     * Allocating very small amounts in the same thread before unpinning is strongly discouraged but not strictly
     * forbidden.
     *
     * Pinning and then allocating may cause somewhat premature OutOfMemoryException. However, the implementation is
     * supposed to not let pinning succeed in the first place if there is any plausible danger of that happening.
     *
     * Pinning and then allocating large amounts is prone to cause premature OutOfMemoryException.
     *
     * Example:
     *
     * ATTENTION: The period of time an object can remained pinned must be "very short". Pinning may block other threads
     * that wait for GC to happen. Indefinite pinning will create deadlock!
     *
     * Calling this method on an already pinned object has undefined consequences.
     *
     * @return whether pinning succeeded - callers are supposed to have an alternative plan when it fails
     */
    boolean pin(Object object);

    /**
     * Allow the given object to be moved by the GC. Always quickly balance each call to the above with a call to this
     * method.
     *
     * Calling this method on an already unpinned object has undefined consequences.
     */
    void unpin(Object object);

    /**
     * @return whether the object is currently pinned
     */
    boolean isPinned(Object object);

    /**
     * Returns whether an address is anywhere in the heap.
     */
    boolean contains(Address address);

    boolean collectGarbage(Size requestedFreeSpace);

    Size reportFreeSpace();

    void runFinalization();

    /**
     * Returns a lower bound for how often exhaustive garbage removal has occurred so far.
     * Conservatively always returning 0 is allowed, but not desirable.
     */
    long numberOfGarbageTurnovers();

    @INLINE
    void writeBarrier(Reference reference);
}
