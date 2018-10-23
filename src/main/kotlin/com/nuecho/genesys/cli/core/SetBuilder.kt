/*
 * Copyright (C) 2018 Nu Echo Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nuecho.genesys.cli.core

class SetBuilder<T> {
    private val items: MutableSet<T> = mutableSetOf()

    fun add(item: T?): SetBuilder<T> {
        if (item != null) items += item
        return this
    }

    fun add(item: Iterable<T>?): SetBuilder<T> {
        if (item != null) items.addAll(item)
        return this
    }

    fun toSet() = items.toSet() // To immutable set.
}

fun <T> setBuilder() = SetBuilder<T>()
