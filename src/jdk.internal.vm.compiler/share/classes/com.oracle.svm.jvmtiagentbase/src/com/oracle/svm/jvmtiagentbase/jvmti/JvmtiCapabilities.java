/*
 * Copyright (c) 2019, 2020, Oracle and/or its affiliates. All rights reserved.
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

import jdk.internal.vm.compiler.nativeimage.c.CContext;
import jdk.internal.vm.compiler.nativeimage.c.struct.CBitfield;
import jdk.internal.vm.compiler.nativeimage.c.struct.CStruct;
import jdk.internal.vm.compiler.word.PointerBase;

@CStruct("jvmtiCapabilities")
@CContext(JvmtiDirectives.class)
public interface JvmtiCapabilities extends PointerBase {
    @CBitfield("can_generate_breakpoint_events")
    void setCanGenerateBreakpointEvents(int value);

    @CBitfield("can_access_local_variables")
    void setCanAccessLocalVariables(int value);

    @CBitfield("can_force_early_return")
    void setCanForceEarlyReturn(int value);

    @CBitfield("can_generate_native_method_bind_events")
    void setCanGenerateNativeMethodBindEvents(int value);

    @CBitfield("can_get_bytecodes")
    void setCanGetBytecodes(int value);

    @CBitfield("can_get_constant_pool")
    void setCanGetConstantPool(int value);

    @CBitfield("can_generate_all_class_hook_events")
    void setCanGenerateAllClassHookEvents(int value);

    @CBitfield("can_get_source_file_name")
    void setCanGetSourceFileName(int value);

    @CBitfield("can_get_line_numbers")
    void setCanGetLineNumbers(int value);
}
