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
import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunction;
import jdk.internal.vm.compiler.nativeimage.c.struct.CPointerTo;
import jdk.internal.vm.compiler.nativeimage.c.type.CCharPointer;
import jdk.internal.vm.compiler.word.PointerBase;
import jdk.internal.vm.compiler.word.UnsignedWord;

import com.oracle.svm.core.annotate.Uninterruptible;

//Checkstyle: stop

/**
 * Basic functions from the standard Visual Studio C Run-Time library
 */
@CContext(WindowsDirectives.class)
@Platforms(Platform.WINDOWS.class)
public class LibC {
    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native <T extends PointerBase> T malloc(UnsignedWord size);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native <T extends PointerBase> T calloc(UnsignedWord nmemb, UnsignedWord size);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native <T extends PointerBase> T realloc(PointerBase ptr, UnsignedWord size);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native void free(PointerBase ptr);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native void exit(int status);

    public static final int EXIT_CODE_ABORT = 99;

    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public static void abort() {
        exit(EXIT_CODE_ABORT);
    }

    @CFunction(value = "_strdup", transition = CFunction.Transition.NO_TRANSITION)
    public static native CCharPointer strdup(CCharPointer src);

    @CPointerTo(nameOfCType = "wchar_t")
    public interface WCharPointer extends PointerBase {
    }

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native WCharPointer _wgetenv(WCharPointer varname);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native UnsignedWord wcslen(WCharPointer varname);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native WCharPointer wcsncat(WCharPointer strDest, WCharPointer strSource, UnsignedWord count);
}
