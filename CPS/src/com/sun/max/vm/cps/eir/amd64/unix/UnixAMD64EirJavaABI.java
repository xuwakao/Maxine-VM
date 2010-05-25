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
package com.sun.max.vm.cps.eir.amd64.unix;

import com.sun.max.collect.*;
import com.sun.max.vm.*;
import com.sun.max.vm.cps.eir.amd64.*;

/**
 * @author Bernd Mathiske
 */
public class UnixAMD64EirJavaABI extends UnixAMD64EirABI {

    public UnixAMD64EirJavaABI(VMConfiguration vmConfiguration) {
        super(vmConfiguration);
    }

    /**
     * Allocated registers from the allocatable set are caller saved.
     */
    @Override
    public PoolSet<AMD64EirRegister> callerSavedRegisters() {
        return allocatableRegisters();
    }

    /**
     * No callee saved registers.
     */
    protected Sequence<AMD64EirRegister> calleeSavedRegisters = Sequence.Static.empty(AMD64EirRegister.class);

    @Override
    public Sequence<AMD64EirRegister> calleeSavedRegisters() {
        return calleeSavedRegisters;
    }
}
