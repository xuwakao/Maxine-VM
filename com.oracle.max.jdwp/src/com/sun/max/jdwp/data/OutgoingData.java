/*
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.max.jdwp.data;

import java.io.*;

/**
 * This class defines outgoing JDWP data. The object can serialize itself to a {@link JDWPOutputStream} and identify the type of command.
 *
 * @author Thomas Wuerthinger
 */
public interface OutgoingData {

    /**
     * Writes this outgoing data to the given {@link JDWPOutputStream} object.
     * @param outputStream the stream to write the data on
     * @throws IOException this exception should be thrown, when there was a problem writing the data
     */
    void write(JDWPOutputStream outputStream) throws IOException;

    /**
     * Identifies the command set of the command that this outgoing data is part of.
     * @return the command set identifier
     */
    byte getCommandSetId();

    /**
     * Identifies the command that this outgoing data is part of.
     * @return the command identifier
     */
    byte getCommandId();
}
