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


package com.oracle.svm.graal.isolated;

import jdk.internal.vm.compiler.nativeimage.ObjectHandle;
import jdk.internal.vm.compiler.nativeimage.c.struct.RawField;
import jdk.internal.vm.compiler.nativeimage.c.struct.RawStructure;
import jdk.internal.vm.compiler.word.Pointer;
import jdk.internal.vm.compiler.word.PointerBase;

import com.oracle.svm.core.c.NonmovableArray;

@RawStructure
interface ForeignIsolateReferenceAdjusterData extends PointerBase {
    @RawField
    int getCount();

    @RawField
    void setCount(int count);

    @RawField
    NonmovableArray<Pointer> getAddresses();

    @RawField
    void setAddresses(NonmovableArray<Pointer> array);

    @RawField
    NonmovableArray<ObjectHandle> getHandles();

    @RawField
    void setHandles(NonmovableArray<ObjectHandle> array);
}
