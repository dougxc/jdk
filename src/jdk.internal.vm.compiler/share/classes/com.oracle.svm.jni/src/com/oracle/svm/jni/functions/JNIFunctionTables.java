/*
 * Copyright (c) 2017, 2017, Oracle and/or its affiliates. All rights reserved.
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


package com.oracle.svm.jni.functions;

import org.graalvm.compiler.word.Word;
import jdk.internal.vm.compiler.nativeimage.CurrentIsolate;
import jdk.internal.vm.compiler.nativeimage.ImageSingletons;
import jdk.internal.vm.compiler.nativeimage.Platform;
import jdk.internal.vm.compiler.nativeimage.Platforms;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunctionPointer;
import jdk.internal.vm.compiler.nativeimage.c.struct.SizeOf;
import jdk.internal.vm.compiler.word.Pointer;
import jdk.internal.vm.compiler.word.UnsignedWord;
import jdk.internal.vm.compiler.word.WordBase;

import com.oracle.svm.core.FrameAccess;
import com.oracle.svm.core.hub.DynamicHub;
import com.oracle.svm.core.hub.LayoutEncoding;
import com.oracle.svm.core.util.VMError;
import com.oracle.svm.jni.nativeapi.JNIInvokeInterface;
import com.oracle.svm.jni.nativeapi.JNIJavaVM;
import com.oracle.svm.jni.nativeapi.JNINativeInterface;

/**
 * Performs the initialization of the JNI function table structures at runtime.
 */
public final class JNIFunctionTables {

    static void create() {
        ImageSingletons.add(JNIFunctionTables.class, new JNIFunctionTables());
    }

    public static JNIFunctionTables singleton() {
        return ImageSingletons.lookup(JNIFunctionTables.class);
    }

    /*
     * Space for C data structures that are passed out to C code at run time. Because these arrays
     * are in the image heap, they are never moved by the GC at run time.
     */
    private final WordBase[] javaVMData;
    private final WordBase[] invokeInterfaceDataMutable;
    final CFunctionPointer[] invokeInterfaceDataPrototype;
    final CFunctionPointer[] functionTableData;

    @Platforms(Platform.HOSTED_ONLY.class)
    private JNIFunctionTables() {
        javaVMData = new WordBase[wordArrayLength(SizeOf.get(JNIJavaVM.class))];
        invokeInterfaceDataMutable = new WordBase[wordArrayLength(SizeOf.get(JNIInvokeInterface.class))];
        invokeInterfaceDataPrototype = new CFunctionPointer[wordArrayLength(SizeOf.get(JNIInvokeInterface.class))];
        functionTableData = new CFunctionPointer[wordArrayLength(SizeOf.get(JNINativeInterface.class))];
    }

    @Platforms(Platform.HOSTED_ONLY.class)
    private static int wordArrayLength(int sizeInBytes) {
        int wordSize = FrameAccess.wordSize();
        VMError.guarantee(sizeInBytes % wordSize == 0);
        return sizeInBytes / wordSize;
    }

    private JNIJavaVM globalJavaVM;

    public JNIJavaVM getGlobalJavaVM() {
        JNIJavaVM javaVM = globalJavaVM;
        if (javaVM.isNull()) {
            /*
             * The function pointer table filled during image generation must be in the read-only
             * part of the image heap, because code relocations are needed for it. To work around
             * this limitation, we copy the read-only table filled during image generation to a
             * writable table of the same size.
             */
            for (int i = 0; i < invokeInterfaceDataPrototype.length; i++) {
                invokeInterfaceDataMutable[i] = invokeInterfaceDataPrototype[i];
            }

            javaVM = (JNIJavaVM) dataAddress(javaVMData);
            JNIInvokeInterface invokes = (JNIInvokeInterface) dataAddress(invokeInterfaceDataMutable);
            invokes.setIsolate(CurrentIsolate.getIsolate());
            javaVM.setFunctions(invokes);

            globalJavaVM = javaVM;
        }
        return javaVM;
    }

    private JNINativeInterface globalFunctionTable;

    public JNINativeInterface getGlobalFunctionTable() {
        JNINativeInterface functionTable = globalFunctionTable;
        if (functionTable.isNull()) {
            /*
             * The JNI function table is filled during image generation and is ready to use, so we
             * do not need to copy it to a modifiable part of the image heap.
             */
            functionTable = (JNINativeInterface) dataAddress(functionTableData);

            globalFunctionTable = functionTable;
        }
        return functionTable;
    }

    /**
     * Returns the absolute address of the first array element of the provided array. The array must
     * be in the image heap, i.e., never moved by the GC.
     */
    private static Pointer dataAddress(WordBase[] dataArray) {
        final DynamicHub hub = DynamicHub.fromClass(dataArray.getClass());
        final UnsignedWord offsetOfFirstArrayElement = LayoutEncoding.getArrayElementOffset(hub.getLayoutEncoding(), 0);
        return Word.objectToUntrackedPointer(dataArray).add(offsetOfFirstArrayElement);
    }
}
