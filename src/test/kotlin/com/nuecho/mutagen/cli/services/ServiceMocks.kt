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

package com.nuecho.mutagen.cli.services

import com.genesyslab.platform.configuration.protocol.types.CfgObjectType.CFGFolder
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgFolder
import com.nuecho.mutagen.cli.preferences.environment.Environment
import io.mockk.spyk

object ServiceMocks {
    fun mockConfService(withDefaultFolderReference: Boolean = true): ConfService {
        val service = spyk(ConfService(Environment(host = "test", user = "test", rawPassword = "test"), true))
        if (withDefaultFolderReference) {
            service.configurationObjectRepository[DEFAULT_FOLDER_REFERENCE] = mockCfgFolder("site", CFGFolder)
        }
        return service
    }
}
