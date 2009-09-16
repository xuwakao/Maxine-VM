/*
 * Copyright (c) 2009 Sun Microsystems, Inc.  All rights reserved.
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
package com.sun.c1x.ci;

/**
 * The <code>Register</code> class definition.
 *
 * @author Marcelo Cintra
 * @author Thomas Wuerthinger
 *
 */
public final class CiRegister {

    /**
     * Invalid register.
     */
    public static final CiRegister None = new CiRegister(-1, -1, "noreg");

    /**
     * Stack register of the current method.
     */
    public static final CiRegister Stack = new CiRegister(-2, -2, "stackreg", RegisterFlag.CPU);

    /**
     * Stack register relative to the caller stack. When this register is used in relative addressing, it means that the
     * offset is based to stack register of the caller and not to the stack register of the current method.
     */
    public static final CiRegister CallerStack = new CiRegister(-3, -3, "caller-stackreg", RegisterFlag.CPU);

    public static final int FirstVirtualRegisterNumber = 40;

    public final int number;
    public final String name;
    public final int encoding;

    private final int flags;

    public enum RegisterFlag {
        CPU, Byte, XMM, MMX;

        public final int mask = 1 << (ordinal() + 1);
    }

    public CiRegister(int number, int encoding, String name, RegisterFlag... flags) {
        assert number < FirstVirtualRegisterNumber : "cannot have a register number greater or equal " + FirstVirtualRegisterNumber;
        this.number = number;
        this.name = name;
        this.flags = createMask(flags);
        this.encoding = encoding;
    }

    private int createMask(RegisterFlag... flags) {
        int result = 0;
        for (RegisterFlag f : flags) {
            result |= f.mask;
        }
        return result;
    }

    private boolean checkFlag(RegisterFlag f) {
        return (flags & f.mask) != 0;
    }

    public boolean isValid() {
        return number >= 0;
    }

    public boolean isXMM() {
        return checkFlag(RegisterFlag.XMM);
    }

    public boolean isCpu() {
        return checkFlag(RegisterFlag.CPU);
    }

    public boolean isByte() {
        return checkFlag(RegisterFlag.Byte);
    }

    public boolean isMMX() {
        return checkFlag(RegisterFlag.MMX);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Utility function for asserting that the given registers are all different.
     * 
     * @param registers
     *            an array of registers that should be checked for equal entries
     * @return false if an equal entry is found, true otherwise
     */
    public static boolean assertDifferentRegisters(CiRegister... registers) {

        for (int i = 0; i < registers.length; i++) {
            for (int j = 0; j < registers.length; j++) {
                if (i != j) {
                    if (registers[i] == registers[j]) {
                        assert false : "Registers " + i + " and " + j + " are both " + registers[i];
                        return false;
                    }
                }
            }
        }

        return true;
    }
}