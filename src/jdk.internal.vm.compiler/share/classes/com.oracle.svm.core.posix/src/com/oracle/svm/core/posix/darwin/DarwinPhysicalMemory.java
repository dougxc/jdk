/*
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
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


package com.oracle.svm.core.posix.darwin;

import jdk.internal.vm.compiler.nativeimage.ImageSingletons;
import jdk.internal.vm.compiler.nativeimage.StackValue;
import jdk.internal.vm.compiler.nativeimage.c.struct.SizeOf;
import jdk.internal.vm.compiler.nativeimage.c.type.CIntPointer;
import jdk.internal.vm.compiler.nativeimage.c.type.WordPointer;
import jdk.internal.vm.compiler.nativeimage.hosted.Feature;
import jdk.internal.vm.compiler.word.UnsignedWord;
import jdk.internal.vm.compiler.word.WordFactory;

import com.oracle.svm.core.CErrorNumber;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.heap.PhysicalMemory;
import com.oracle.svm.core.log.Log;
import com.oracle.svm.core.posix.headers.Sysctl;
import com.oracle.svm.core.posix.headers.darwin.DarwinSysctl;
import com.oracle.svm.core.util.VMError;

class DarwinPhysicalMemory extends PhysicalMemory {

    static class PhysicalMemorySupportImpl implements PhysicalMemorySupport {

        @Override
        public UnsignedWord size() {
            CIntPointer namePointer = StackValue.get(2, CIntPointer.class);
            namePointer.write(0, DarwinSysctl.CTL_HW());
            namePointer.write(1, DarwinSysctl.HW_MEMSIZE());

            WordPointer physicalMemoryPointer = StackValue.get(WordPointer.class);
            WordPointer physicalMemorySizePointer = StackValue.get(WordPointer.class);
            physicalMemorySizePointer.write(SizeOf.unsigned(WordPointer.class));
            final int sysctlResult = Sysctl.sysctl(namePointer, 2, physicalMemoryPointer, physicalMemorySizePointer, WordFactory.nullPointer(), 0);
            if (sysctlResult != 0) {
                Log.log().string("DarwinPhysicalMemory.PhysicalMemorySupportImpl.size(): sysctl() returns with errno: ").signed(CErrorNumber.getCErrorNumber()).newline();
                throw VMError.shouldNotReachHere("DarwinPhysicalMemory.PhysicalMemorySupportImpl.size() failed.");
            }
            return physicalMemoryPointer.read();
        }
    }

    @AutomaticFeature
    static class PhysicalMemoryFeature implements Feature {
        @Override
        public void afterRegistration(AfterRegistrationAccess access) {
            ImageSingletons.add(PhysicalMemorySupport.class, new PhysicalMemorySupportImpl());
        }
    }
}
