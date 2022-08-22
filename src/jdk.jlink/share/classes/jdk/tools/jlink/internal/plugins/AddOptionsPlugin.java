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

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Plugin to add VM command-line options, by storing them in a resource
 * that's read by the VM at startup. The plugin prepend options from
 * the resource if it exists in the current Java runtime.
 */
public final class AddOptionsPlugin extends AddResourcePlugin {

    /**
     * Value of the options resource in the current Java runtime.
     */
    private final String jrtValue;

    public AddOptionsPlugin() {
        super("add-options", "/java.base/jdk/internal/vm/options");
        this.jrtValue = readResource();
    }

    @Override
    public Set<State> getState() {
        if (jrtValue == null) {
            return super.getState();
        }
        // Auto-enable if the current Java runtime has an options resource.
        return EnumSet.of(State.AUTO_ENABLED, State.FUNCTIONAL);
    }

    @Override
    public void configure(Map<String, String> config) {
        var v = config.get(getName());
        if (v == null && jrtValue == null)
            throw new AssertionError();
        if (jrtValue != null) {
            if (v == null) {
                v = jrtValue;
            } else {
                v = jrtValue + " " + v;
            }
        }
        value = v;
    }
}
