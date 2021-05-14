/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
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



package com.oracle.svm.core.windows.headers;

import jdk.internal.vm.compiler.nativeimage.Platform;
import jdk.internal.vm.compiler.nativeimage.Platforms;
import jdk.internal.vm.compiler.nativeimage.StackValue;
import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunction;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunction.Transition;
import jdk.internal.vm.compiler.nativeimage.c.struct.CStruct;
import jdk.internal.vm.compiler.nativeimage.c.struct.SizeOf;
import jdk.internal.vm.compiler.nativeimage.c.type.CCharPointer;

// Checkstyle: stop

@CContext(WindowsDirectives.class)
@Platforms(Platform.WINDOWS.class)
public class WinSock {

    /**
     * Structure containing information about the WinSock implementation
     */
    @CStruct("WSADATA")
    public interface WSADATA extends CCharPointer {
    }

    /**
     * Initialize the WinSock library
     */
    @CFunction(transition = Transition.NO_TRANSITION)
    public static native int WSAStartup(int wVersionRequired, WSADATA lpWSAData);

    public static int init() {
        WSADATA lpWSAData = (WSADATA) StackValue.get(SizeOf.get(WSADATA.class), CCharPointer.class);
        return WSAStartup(0x202 /* Version 2.2 */, lpWSAData);
    }

}
