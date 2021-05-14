/*
 * Copyright (c) 2013, 2017, Oracle and/or its affiliates. All rights reserved.
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


package com.oracle.svm.core.posix.headers;

import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunction;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunction.Transition;
import jdk.internal.vm.compiler.nativeimage.c.struct.AllowNarrowingCast;
import jdk.internal.vm.compiler.nativeimage.c.struct.AllowWideningCast;
import jdk.internal.vm.compiler.nativeimage.c.struct.CField;
import jdk.internal.vm.compiler.nativeimage.c.struct.CStruct;
import jdk.internal.vm.compiler.word.PointerBase;

// Checkstyle: stop

/**
 * Definitions manually translated from the C header file sys/time.h.
 */
@CContext(PosixDirectives.class)
public class Time {

    @CStruct(addStructKeyword = true)
    public interface timeval extends PointerBase {
        @CField
        long tv_sec();

        @CField
        void set_tv_sec(long value);

        @CField
        @AllowWideningCast
        long tv_usec();

        @CField
        @AllowNarrowingCast
        void set_tv_usec(long value);

        timeval addressOf(int index);
    }

    public interface timezone extends PointerBase {
    }

    @CStruct(addStructKeyword = true)
    public interface timespec extends PointerBase {
        @CField
        long tv_sec();

        @CField
        void set_tv_sec(long value);

        @CField
        long tv_nsec();

        @CField
        void set_tv_nsec(long value);
    }

    public static class NoTransitions {
        @CFunction(transition = CFunction.Transition.NO_TRANSITION)
        public static native int gettimeofday(timeval tv, timezone tz);

        @CFunction(transition = Transition.NO_TRANSITION)
        public static native int nanosleep(timespec requestedtime, timespec remaining);
    }
}
