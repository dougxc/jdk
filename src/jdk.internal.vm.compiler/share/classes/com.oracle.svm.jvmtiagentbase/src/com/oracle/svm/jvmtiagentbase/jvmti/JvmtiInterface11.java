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


package com.oracle.svm.jvmtiagentbase.jvmti;

import com.oracle.svm.jni.nativeapi.JNIObjectHandle;
import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.function.CFunctionPointer;
import jdk.internal.vm.compiler.nativeimage.c.function.InvokeCFunctionPointer;
import jdk.internal.vm.compiler.nativeimage.c.struct.CField;
import jdk.internal.vm.compiler.nativeimage.c.struct.CStruct;
import jdk.internal.vm.compiler.nativeimage.c.type.CCharPointer;
import jdk.internal.vm.compiler.nativeimage.c.type.CIntPointer;
import jdk.internal.vm.compiler.word.PointerBase;

@CStruct(value = "jvmtiInterface_1")
@CContext(JvmtiDirectives11.class)
public interface JvmtiInterface11 extends JvmtiInterface {
    // Checkstyle: stop

    @CField("GetNamedModule")
    GetNamedModuleFunctionPointer GetNamedModule();

    interface GetNamedModuleFunctionPointer extends CFunctionPointer {
        @InvokeCFunctionPointer
        JvmtiError invoke(JvmtiEnv jvmtiEnv, JNIObjectHandle classLoader, CCharPointer packageName, PointerBase modulePtr);
    }

    @CField("GetAllModules")
    GetAllModulesFunctionPointer GetAllModules();

    interface GetAllModulesFunctionPointer extends CFunctionPointer {
        @InvokeCFunctionPointer
        JvmtiError invoke(JvmtiEnv jvmtiEnv, CIntPointer moduleCountPtr, PointerBase modulesPtr);
    }

    @CField("AddModuleOpens")
    AddModuleOpensFunctionPointer AddModuleOpens();

    interface AddModuleOpensFunctionPointer extends CFunctionPointer {
        @InvokeCFunctionPointer
        JvmtiError invoke(JvmtiEnv jvmtiEnv, JNIObjectHandle module, CCharPointer pkgName, JNIObjectHandle toModule);
    }
}
