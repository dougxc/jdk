/*
 * Copyright (c) 2020, 2020, Oracle and/or its affiliates. All rights reserved.
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

// Checkstyle: stop

import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.struct.CField;
import jdk.internal.vm.compiler.nativeimage.c.struct.CStruct;
import jdk.internal.vm.compiler.word.PointerBase;

/**
 * Definitions for Windows verrsrc.h
 */
@CContext(WindowsDirectives.class)
public class VerRsrc {

    /** Contains version information for a file. */
    @CStruct
    public interface VS_FIXEDFILEINFO extends PointerBase {
        @CField
        int dwSignature();

        @CField
        int dwStrucVersion();

        @CField
        int dwFileVersionMS();

        @CField
        int dwFileVersionLS();

        @CField
        int dwProductVersionMS();

        @CField
        int dwProductVersionLS();

        @CField
        int dwFileFlagsMask();

        @CField
        int dwFileFlags();

        @CField
        int dwFileOS();

        @CField
        int dwFileType();

        @CField
        int dwFileSubtype();

        @CField
        int dwFileDateMS();

        @CField
        int dwFileDateLS();
    }
}
