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

import static jdk.internal.vm.compiler.nativeimage.c.function.CFunction.Transition.NO_TRANSITION;

import jdk.internal.vm.compiler.nativeimage.Platform;
import jdk.internal.vm.compiler.nativeimage.Platforms;
import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.constant.CConstant;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunction;
import jdk.internal.vm.compiler.nativeimage.c.type.CCharPointer;
import jdk.internal.vm.compiler.nativeimage.c.type.CIntPointer;
import jdk.internal.vm.compiler.word.PointerBase;
import jdk.internal.vm.compiler.word.UnsignedWord;

import com.oracle.svm.core.windows.headers.LibC.WCharPointer;

//Checkstyle: stop

/**
 * Definitions manually translated from the Windows header file fileapi.h.
 */
@CContext(WindowsDirectives.class)
@Platforms(Platform.WINDOWS.class)
public class FileAPI {

    @CFunction
    public static native int WriteFile(int hFile, CCharPointer lpBuffer, UnsignedWord nNumberOfBytesToWrite,
                    CIntPointer lpNumberOfBytesWritten, PointerBase lpOverlapped);

    @CFunction
    public static native int FlushFileBuffers(int hFile);

    @CConstant
    public static native int STD_INPUT_HANDLE();

    @CConstant
    public static native int STD_OUTPUT_HANDLE();

    @CConstant
    public static native int STD_ERROR_HANDLE();

    @CFunction
    public static native int GetStdHandle(int stdHandle);

    @CFunction(transition = NO_TRANSITION)
    public static native int GetTempPathW(int nBufferLength, WCharPointer lpBuffer);
}
