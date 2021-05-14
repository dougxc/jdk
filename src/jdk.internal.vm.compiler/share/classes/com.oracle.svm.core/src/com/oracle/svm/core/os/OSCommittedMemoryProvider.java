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


package com.oracle.svm.core.os;

import static jdk.internal.vm.compiler.word.WordFactory.nullPointer;
import static jdk.internal.vm.compiler.word.WordFactory.zero;

import jdk.internal.vm.compiler.nativeimage.CurrentIsolate;
import jdk.internal.vm.compiler.nativeimage.ImageSingletons;
import jdk.internal.vm.compiler.nativeimage.Platform;
import jdk.internal.vm.compiler.nativeimage.Platforms;
import jdk.internal.vm.compiler.nativeimage.c.type.WordPointer;
import jdk.internal.vm.compiler.nativeimage.hosted.Feature;
import jdk.internal.vm.compiler.word.Pointer;
import jdk.internal.vm.compiler.word.PointerBase;
import jdk.internal.vm.compiler.word.UnsignedWord;
import jdk.internal.vm.compiler.word.WordFactory;

import com.oracle.svm.core.Isolates;
import com.oracle.svm.core.SubstrateOptions;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.annotate.Uninterruptible;
import com.oracle.svm.core.c.function.CEntryPointCreateIsolateParameters;
import com.oracle.svm.core.c.function.CEntryPointErrors;
import com.oracle.svm.core.c.function.CEntryPointSetup;
import com.oracle.svm.core.util.UnsignedUtils;

public class OSCommittedMemoryProvider extends AbstractCommittedMemoryProvider {
    @Platforms(Platform.HOSTED_ONLY.class)
    public OSCommittedMemoryProvider() {
    }

    @Override
    @Uninterruptible(reason = "Still being initialized.")
    public int initialize(WordPointer isolatePointer, CEntryPointCreateIsolateParameters parameters) {
        if (!SubstrateOptions.SpawnIsolates.getValue()) {
            int result = protectSingleIsolateImageHeap();
            if (result == CEntryPointErrors.NO_ERROR) {
                isolatePointer.write(CEntryPointSetup.SINGLE_ISOLATE_SENTINEL);
            }
            return result;
        }
        return ImageHeapProvider.get().initialize(nullPointer(), zero(), isolatePointer, nullPointer());
    }

    @Override
    @Uninterruptible(reason = "Tear-down in progress.")
    public int tearDown() {
        if (!SubstrateOptions.SpawnIsolates.getValue()) {
            return CEntryPointErrors.NO_ERROR;
        }

        PointerBase heapBase = Isolates.getHeapBase(CurrentIsolate.getIsolate());
        return ImageHeapProvider.get().freeImageHeap(heapBase);
    }

    @Override
    public Pointer allocate(UnsignedWord size, UnsignedWord alignment, boolean executable) {
        int access = VirtualMemoryProvider.Access.READ | VirtualMemoryProvider.Access.WRITE;
        if (executable) {
            access |= VirtualMemoryProvider.Access.EXECUTE;
        }
        Pointer reserved = WordFactory.nullPointer();
        if (!UnsignedUtils.isAMultiple(getGranularity(), alignment)) {
            reserved = VirtualMemoryProvider.get().reserve(size, alignment);
            if (reserved.isNull()) {
                return nullPointer();
            }
        }
        Pointer committed = VirtualMemoryProvider.get().commit(reserved, size, access);
        if (committed.isNull()) {
            if (reserved.isNonNull()) {
                VirtualMemoryProvider.get().free(reserved, size);
            }
            return nullPointer();
        }
        assert reserved.isNull() || reserved.equal(committed);
        tracker.track(size);
        return committed;
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public boolean free(PointerBase start, UnsignedWord nbytes, UnsignedWord alignment, boolean executable) {
        if (VirtualMemoryProvider.get().free(start, nbytes) == 0) {
            tracker.untrack(nbytes);
            return true;
        }
        return false;
    }

    private final VirtualMemoryTracker tracker = new VirtualMemoryTracker();

    protected static class VirtualMemoryTracker {

        private UnsignedWord totalAllocated;

        protected VirtualMemoryTracker() {
            this.totalAllocated = WordFactory.zero();
        }

        public void track(UnsignedWord size) {
            totalAllocated = totalAllocated.add(size);
        }

        @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
        public void untrack(UnsignedWord size) {
            totalAllocated = totalAllocated.subtract(size);
        }
    }
}

@AutomaticFeature
class OSCommittedMemoryProviderFeature implements Feature {
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        if (!ImageSingletons.contains(CommittedMemoryProvider.class)) {
            ImageSingletons.add(CommittedMemoryProvider.class, new OSCommittedMemoryProvider());
        }
    }
}
