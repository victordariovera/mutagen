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

package com.nuecho.genesys.cli.preferences

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.Arrays.fill

@Suppress("UseDataClass")
class SecurePassword(private val characters: CharArray) {
    val value: String
        @JsonIgnore
        get() {
            val password = String(characters)
            fill(characters, ' ')
            return password.trim()
        }
}
