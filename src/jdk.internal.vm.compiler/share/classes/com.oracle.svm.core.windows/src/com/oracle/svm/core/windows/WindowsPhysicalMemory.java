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


package com.oracle.svm.core.windows;

import jdk.internal.vm.compiler.nativeimage.ImageSingletons;
import jdk.internal.vm.compiler.nativeimage.StackValue;
import jdk.internal.vm.compiler.nativeimage.c.struct.SizeOf;
import jdk.internal.vm.compiler.nativeimage.hosted.Feature;
import jdk.internal.vm.compiler.word.UnsignedWord;
import jdk.internal.vm.compiler.word.WordFactory;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.heap.PhysicalMemory;
import com.oracle.svm.core.windows.headers.SysinfoAPI;

class WindowsPhysicalMemory extends PhysicalMemory {

    static class WindowsPhysicalMemorySupportImpl implements PhysicalMemorySupport {

        @Override
        public UnsignedWord size() {
            SysinfoAPI.MEMORYSTATUSEX memStatusEx = StackValue.get(SysinfoAPI.MEMORYSTATUSEX.class);
            memStatusEx.set_dwLength(SizeOf.get(SysinfoAPI.MEMORYSTATUSEX.class));
            SysinfoAPI.GlobalMemoryStatusEx(memStatusEx);
            return WordFactory.unsigned(memStatusEx.ullTotalPhys());
        }
    }

    @AutomaticFeature
    static class PhysicalMemoryFeature implements Feature {
        @Override
        public void afterRegistration(AfterRegistrationAccess access) {
            ImageSingletons.add(PhysicalMemorySupport.class, new WindowsPhysicalMemorySupportImpl());
        }
    }
}
