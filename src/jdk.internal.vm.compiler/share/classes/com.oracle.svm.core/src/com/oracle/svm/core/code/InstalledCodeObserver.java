/*
 * Copyright (c) 2016, 2017, Oracle and/or its affiliates. All rights reserved.
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


package com.oracle.svm.core.code;

import org.graalvm.compiler.code.CompilationResult;
import org.graalvm.compiler.debug.DebugContext;
import jdk.internal.vm.compiler.nativeimage.c.struct.RawField;
import jdk.internal.vm.compiler.nativeimage.c.struct.RawStructure;
import jdk.internal.vm.compiler.word.Pointer;
import jdk.internal.vm.compiler.word.PointerBase;

import com.oracle.svm.core.annotate.Uninterruptible;
import com.oracle.svm.core.c.struct.PinnedObjectField;
import com.oracle.svm.core.meta.SharedMethod;

/** Observes the life of one piece of runtime-compiled code. */
public interface InstalledCodeObserver {

    interface Factory {
        /** Creates an observer for the specified code. */
        InstalledCodeObserver create(DebugContext debug, SharedMethod method, CompilationResult compilation, Pointer code, int codeSize);
    }

    /**
     * Installs the observer and returns a handle to its state in unmanaged memory. Use the
     * {@linkplain InstalledCodeObserverHandle#getAccessor() accessor} to activate the observer and
     * for further actions.
     */
    InstalledCodeObserverHandle install();

    @RawStructure
    interface InstalledCodeObserverHandle extends PointerBase {
        /**
         * Provides an accessor object to perform actions with this handle. This object lives in the
         * image heap so it is safe to access even across different isolates of the same image.
         */
        @PinnedObjectField
        @RawField
        InstalledCodeObserverHandleAccessor getAccessor();

        @PinnedObjectField
        @RawField
        void setAccessor(InstalledCodeObserverHandleAccessor accessor);
    }

    @SuppressWarnings("unused")
    interface InstalledCodeObserverHandleAccessor {
        default void activate(InstalledCodeObserverHandle handle) {
        }

        default void release(InstalledCodeObserverHandle handle) {
        }

        default void detachFromCurrentIsolate(InstalledCodeObserverHandle handle) {
        }

        default void attachToCurrentIsolate(InstalledCodeObserverHandle handle) {
        }

        @Uninterruptible(reason = "Called from uninterruptible code", mayBeInlined = true)
        default void releaseOnTearDown(InstalledCodeObserverHandle handle) {
        }
    }
}
