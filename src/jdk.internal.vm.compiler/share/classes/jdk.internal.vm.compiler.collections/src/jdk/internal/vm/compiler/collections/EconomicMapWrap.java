/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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


















package jdk.internal.vm.compiler.collections;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Wraps an existing {@link Map} as an {@link EconomicMap}.
 *
 * @since 21.1
 */
public class EconomicMapWrap<K, V> implements EconomicMap<K, V> {

    private final Map<K, V> map;

    /** @since 21.1 */
    public EconomicMapWrap(Map<K, V> map) {
        this.map = map;
    }

    /** @since 21.1 */
    @Override
    public V get(K key) {
        V result = map.get(key);
        return result;
    }

    /** @since 21.1 */
    @Override
    public V put(K key, V value) {
        V result = map.put(key, value);
        return result;
    }

    /** @since 21.1 */
    @Override
    public V putIfAbsent(K key, V value) {
        V result = map.putIfAbsent(key, value);
        return result;
    }

    /** @since 21.1 */
    @Override
    public int size() {
        int result = map.size();
        return result;
    }

    /** @since 21.1 */
    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /** @since 21.1 */
    @Override
    public void clear() {
        map.clear();
    }

    /** @since 21.1 */
    @Override
    public V removeKey(K key) {
        V result = map.remove(key);
        return result;
    }

    /** @since 21.1 */
    @Override
    public Iterable<V> getValues() {
        return map.values();
    }

    /** @since 21.1 */
    @Override
    public Iterable<K> getKeys() {
        return map.keySet();
    }

    /** @since 21.1 */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /** @since 21.1 */
    @Override
    public MapCursor<K, V> getEntries() {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        return new MapCursor<K, V>() {

            private Map.Entry<K, V> current;

            @Override
            public boolean advance() {
                boolean result = iterator.hasNext();
                if (result) {
                    current = iterator.next();
                }

                return result;
            }

            @Override
            public K getKey() {
                return current.getKey();
            }

            @Override
            public V getValue() {
                return current.getValue();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    /** @since 21.1 */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll(function);
    }
}
