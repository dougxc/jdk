/*
 * Copyright (c) 2021, 2021, Oracle and/or its affiliates. All rights reserved.
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


package com.oracle.svm.core.jvmstat;

import java.nio.ByteBuffer;

import jdk.internal.vm.compiler.nativeimage.ImageSingletons;
import jdk.internal.vm.compiler.nativeimage.Platform;
import jdk.internal.vm.compiler.nativeimage.Platforms;
import jdk.internal.vm.compiler.nativeimage.hosted.Feature;

import com.oracle.svm.core.annotate.AutomaticFeature;

public interface PerfDataSupport {
    ByteBuffer attach(String user, int lvmid, int mode);

    void detach(ByteBuffer bb);

    long highResCounter();

    long highResFrequency();

    ByteBuffer createLong(String name, int variability, int units, long value);

    ByteBuffer createByteArray(String name, int variability, int units, byte[] value, int maxLength);
}

class NoPerfDataSupport implements PerfDataSupport {
    @Platforms(Platform.HOSTED_ONLY.class)
    NoPerfDataSupport() {
    }

    @Override
    public ByteBuffer attach(String user, int lvmid, int mode) {
        throw new IllegalArgumentException("Performance data is not supported.");
    }

    @Override
    public void detach(ByteBuffer bb) {
        // nothing to do
    }

    @Override
    public long highResCounter() {
        return System.nanoTime();
    }

    @Override
    public long highResFrequency() {
        return 1L * 1000 * 1000 * 1000;
    }

    @Override
    public ByteBuffer createLong(String name, int variability, int units, long value) {
        throw new IllegalArgumentException("Performance data is not supported.");
    }

    @Override
    public ByteBuffer createByteArray(String name, int variability, int units, byte[] value, int maxLength) {
        throw new IllegalArgumentException("Performance data is not supported.");
    }
}

@AutomaticFeature
class PerfDataFeature implements Feature {
    @Override
    public void duringSetup(DuringSetupAccess access) {
        if (!ImageSingletons.contains(PerfDataSupport.class)) {
            ImageSingletons.add(PerfDataSupport.class, new NoPerfDataSupport());
        }
    }
}
