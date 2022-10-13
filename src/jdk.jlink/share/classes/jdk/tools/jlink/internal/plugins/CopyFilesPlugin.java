/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
package jdk.tools.jlink.internal.plugins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import jdk.tools.jlink.plugin.PluginException;
import jdk.tools.jlink.plugin.ResourcePool;
import jdk.tools.jlink.plugin.ResourcePoolBuilder;
import jdk.tools.jlink.plugin.ResourcePoolEntry;
import jdk.tools.jlink.plugin.ResourcePoolEntry.Type;

/**
 * Jlink plugin to copy files in the current runtime image into the output image.
 * The files to copy are specified in {@code <java.home>/conf/jlink.copyfiles}.
 * There is one file specified per line with its path relative to the image root
 * (e.g., lib/server/libjvm.so).
 */
public final class CopyFilesPlugin extends AbstractPlugin {

    private static final String CONF_FILE_NAME = "jlink.copyfiles";
    private static final Path CONF_FILE = Paths.get(System.getProperty("java.home"), "conf", CONF_FILE_NAME);

    /**
     * List of relative path names for the files to copy.
     */
    List<String> files;

    public CopyFilesPlugin() {
        super("copy-files");
    }

    private List<String> getCopies() {
        if (files == null) {
            if (Files.exists(CONF_FILE)) {
                files = new ArrayList<>();
                try {
                    files = Files.readAllLines(CONF_FILE);
                } catch (IOException e) {
                    throw new PluginException("Error parsing " + CONF_FILE, e);
                }
            } else {
                files = Collections.emptyList();
            }
        }
        return files;
    }

    @Override
    public Set<State> getState() {
        return !getCopies().isEmpty() ? EnumSet.of(State.AUTO_ENABLED, State.FUNCTIONAL)
                                      : EnumSet.of(State.DISABLED);
    }

    @Override
    public Category getType() {
        return Category.ADDER;
    }

    @Override
    public boolean hasArguments() {
        return false;
    }

    @Override
    public void configure(Map<String, String> config) {
        String arg = config.get(getName());
        if (arg != null) {
            throw new IllegalArgumentException(getName() + ": " + arg);
        }
    }

    @Override
    public ResourcePool transform(ResourcePool in, ResourcePoolBuilder out) {
        if (files.isEmpty()) {
            throw new PluginException(getName() + " is disabled");
        }

        in.transformAndCopy(Function.identity(), out);

        // Copy the configuration file itself
        out.add(ResourcePoolEntry.create("/java.base/conf/" + CONF_FILE_NAME, Type.CONFIG, CONF_FILE));

        String javaHome = System.getProperty("java.home");
        for (String relativePath : files) {
            Path file = Paths.get(javaHome, relativePath);
            out.add(ResourcePoolEntry.create("/java.base/top/" + relativePath, Type.TOP, file));
        }
        return out.build();
    }
}
