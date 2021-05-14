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


package com.oracle.svm.core.posix;

import jdk.internal.vm.compiler.nativeimage.Platform;
import jdk.internal.vm.compiler.nativeimage.Platforms;
import jdk.internal.vm.compiler.nativeimage.StackValue;
import jdk.internal.vm.compiler.word.SignedWord;
import jdk.internal.vm.compiler.word.WordFactory;

import com.oracle.svm.core.CErrorNumber;
import com.oracle.svm.core.annotate.Uninterruptible;
import com.oracle.svm.core.posix.headers.Errno;
import com.oracle.svm.core.posix.headers.darwin.DarwinStat;
import com.oracle.svm.core.posix.headers.linux.LinuxStat;
import com.oracle.svm.core.util.VMError;

public final class PosixStat {
    public static boolean isOpen(int fd) {
        int result;
        if (Platform.includedIn(Platform.LINUX.class)) {
            LinuxStat.stat64 stat = StackValue.get(LinuxStat.stat64.class);
            result = LinuxStat.fstat64(fd, stat);
        } else if (Platform.includedIn(Platform.DARWIN.class)) {
            DarwinStat.stat64 stat = StackValue.get(DarwinStat.stat64.class);
            result = DarwinStat.fstat64(fd, stat);
        } else {
            throw VMError.shouldNotReachHere("Unsupported platform");
        }

        return result == 0 || CErrorNumber.getCErrorNumber() != Errno.EBADF();
    }

    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public static SignedWord getSize(int fd) {
        long size = -1;
        if (Platform.includedIn(Platform.LINUX.class)) {
            LinuxStat.stat64 stat = StackValue.get(LinuxStat.stat64.class);
            if (LinuxStat.NoTransitions.fstat64(fd, stat) == 0) {
                size = stat.st_size();
            }
        } else if (Platform.includedIn(Platform.DARWIN.class)) {
            DarwinStat.stat64 stat = StackValue.get(DarwinStat.stat64.class);
            if (DarwinStat.NoTransitions.fstat64(fd, stat) == 0) {
                size = stat.st_size();
            }
        }
        return WordFactory.signed(size);
    }

    @Platforms(Platform.HOSTED_ONLY.class)
    private PosixStat() {
    }
}
