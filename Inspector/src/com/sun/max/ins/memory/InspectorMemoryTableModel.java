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
package com.sun.max.ins.memory;

import javax.swing.table.*;

import com.sun.max.memory.*;
import com.sun.max.tele.*;
import com.sun.max.unsafe.*;


/**
 * Access to table data models that represent regions of memory in the VM, one region per row.
 *
 * @author Michael Van De Vanter
 */
public interface InspectorMemoryTableModel extends TableModel {

    /**
     * Returns the memory location corresponding to a row in the model of VM memory.
     *
     * @param row a row in the table model of memory
     * @return the first location in VM memory corresponding to a row in the table model
     */
    Address getAddress(int row);

    /**
     * Returns the region of memory corresponding to a row in the model of VM memory.
     *
     * @param row a row in the table model of memory
     * @return the first location in VM memory corresponding to a row in the table model
     */
    MemoryRegion getMemoryRegion(int row);

    /**
     * Returns a memory watchpoint, if any, whose coverage intersects memory corresponding
     * to a row in the model of VM memory.
     *
     * @param row a row in the table model of memory
     * @return a memory watchpoint whose region intersects the memory for this row in the model, null if none.
     */
    MaxWatchpoint getWatchpoint(int row);

    /**
     * Returns an address in VM memory from which offsets for this model are computed.
     *
     * @return a memory address understood to be the zero offset for this model
     * @see #getOffset(int)
     */
    Address getOrigin();

    /**
     * Returns an offset in bytes, from an origin in VM memory, for the beginning of the memory
     * corresponding to this row in the model.
     *
     * @param row a row in the table model of memory
     * @return location of the memory for this row, specified in a byte offset from an origin in VM memory
     * @see InspectorMemoryTableModel#getOrigin()
     */
    Offset getOffset(int row);

    /**
     * Locates the row, if any, that represent a range of memory that includes a specific location in VM memory.
     *
     * @param address a location in VM memory.
     * @return the row in the model corresponding to hte location, -1 if none
     */
    int findRow(Address address);

}