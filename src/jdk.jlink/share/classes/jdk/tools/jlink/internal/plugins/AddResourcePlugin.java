/*
 * Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import jdk.tools.jlink.plugin.ResourcePool;
import jdk.tools.jlink.plugin.ResourcePoolBuilder;
import jdk.tools.jlink.plugin.ResourcePoolEntry;

/**
 * Base plugin to add a resource
 */
abstract class AddResourcePlugin extends AbstractPlugin {

    private final String path;
    protected String value;

    protected AddResourcePlugin(String name, String p) {
        super(name);
        path = p;
    }

    @Override
    public Category getType() {
        return Category.ADDER;
    }

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasRawArgument() {
        return true;
    }

    /**
     * Gets the contents of the resource denoted by {@link #path} in the current
     * Java runtime if it exists.
     *
     * @return the contents of the resource as a String or {@code null} if the
     *         resource does not exist
     */
    protected String readResource() throws AssertionError {
        try {
            FileSystem fs = FileSystems.newFileSystem(URI.create("jrt:/"), Collections.emptyMap());
            Path optionsPath = fs.getPath("/modules" + path);
            if (Files.exists(optionsPath)) {
                var optionsBytes = Files.readAllBytes(optionsPath);
                return new String(optionsBytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return null;
    }

    @Override
    public void configure(Map<String, String> config) {
        var v = config.get(getName());
        if (v == null)
            throw new AssertionError();
        value = v;
    }

    @Override
    public ResourcePool transform(ResourcePool in, ResourcePoolBuilder out) {
        in.transformAndCopy(Function.identity(), out);
        out.add(ResourcePoolEntry.create(path,
                                         value.getBytes(StandardCharsets.UTF_8)));
        return out.build();
    }

}
