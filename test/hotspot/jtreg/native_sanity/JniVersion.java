/*
 * Copyright (c) 2015, 2023, Oracle and/or its affiliates. All rights reserved.
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

 /*
 * @test
 * @run main/native JniVersion
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

public class JniVersion {

    public static final int JNI_VERSION_21 = 0x00150000;

    public static void main(String... args) throws Exception {
        System.loadLibrary("JniVersion");
        Integer iterations = Integer.getInteger("JniVersion.iterations", 0);
        long start = System.nanoTime();
        int res = getJniVersion(iterations);
        if (res != JNI_VERSION_21) {
            throw new Exception("Unexpected value returned from getJniVersion(): 0x" + Integer.toHexString(res));
        }
        if (iterations != 0) {
            long duration = System.nanoTime() - start;
            Path path = Paths.get(System.getProperty("user.home"), "JniVersion_benchmark.txt");
            String label = System.getProperty("JniVersion.label", "<unknown>");
            if (!Files.exists(path)) {
                String header = String.format("%-30s %-,15s %-15s # %s%n", "Timestamp", "iterations", "duration (ns)", "label");
                Files.writeString(path, header, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            String result = String.format("%-30s %-,15d %-,15d # %s%n", new Timestamp(System.currentTimeMillis()), iterations, duration, label);
            Files.writeString(path, result, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    static native int getJniVersion(int iterations);
}
