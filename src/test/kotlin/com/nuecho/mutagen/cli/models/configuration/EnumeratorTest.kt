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

package com.nuecho.mutagen.cli.models.configuration

import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator
import com.genesyslab.platform.configuration.protocol.types.CfgEnumeratorType.CFGENTRole
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState.CFGEnabled
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_DBID
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_TENANT_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgEnumerator
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockKeyValueCollection
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjects.toCfgEnumeratorType
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjects.toCfgObjectState
import com.nuecho.mutagen.cli.models.configuration.ConfigurationTestData.defaultProperties
import com.nuecho.mutagen.cli.models.configuration.reference.referenceSetBuilder
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveFolderByDbid
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveTenant
import com.nuecho.mutagen.cli.services.ServiceMocks.mockConfService
import com.nuecho.mutagen.cli.toShortName
import io.mockk.every
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

private const val NAME = "name"
private val enumerator = Enumerator(
    tenant = DEFAULT_TENANT_REFERENCE,
    name = NAME,
    displayName = "displayName",
    description = "description",
    type = CFGENTRole.toShortName(),
    state = CFGEnabled.toShortName(),
    userProperties = defaultProperties(),
    folder = DEFAULT_FOLDER_REFERENCE
)

class EnumeratorTest : ConfigurationObjectTest(
    configurationObject = enumerator,
    emptyConfigurationObject = Enumerator(tenant = DEFAULT_TENANT_REFERENCE, name = NAME),
    mandatoryProperties = setOf(DISPLAY_NAME, TYPE),
    importedConfigurationObject = Enumerator(mockCfgEnumerator())
) {
    @Test
    override fun `getReferences() should return all object's references`() {
        val expected = referenceSetBuilder()
            .add(enumerator.tenant)
            .add(enumerator.folder)
            .toSet()

        assertThat(enumerator.getReferences(), equalTo(expected))
    }

    @Test
    override fun `object with different unchangeable properties' values should return the right unchangeable properties`() =
        assertUnchangeableProperties(mockCfgEnumerator(), FOLDER)

    @Test
    fun `createCfgObject should properly create CfgEnumerator`() {
        val service = mockConfService()
        every { service.retrieveObject(CfgEnumerator::class.java, any()) } returns null
        mockRetrieveTenant(service)

        val cfgEnumerator = enumerator.createCfgObject(service)

        with(cfgEnumerator) {
            assertThat(name, equalTo(enumerator.name))
            assertThat(displayName, equalTo(enumerator.displayName))
            assertThat(description, equalTo(enumerator.description))
            assertThat(type, equalTo(toCfgEnumeratorType(enumerator.type)))
            assertThat(state, equalTo(toCfgObjectState(enumerator.state)))
            assertThat(userProperties.asCategorizedProperties(), equalTo(enumerator.userProperties))
        }
    }

    @Test
    fun `createCfgObject should use name when displayName is not specified`() {
        val service = mockConfService()
        every { service.retrieveObject(CfgEnumerator::class.java, any()) } returns null
        mockRetrieveTenant(service)

        val cfgEnumerator = Enumerator(DEFAULT_TENANT_REFERENCE, NAME).createCfgObject(service)

        with(cfgEnumerator) {
            assertThat(name, equalTo(NAME))
            assertThat(displayName, equalTo(NAME))
        }
    }
}

private fun mockCfgEnumerator() = mockCfgEnumerator(enumerator.name).apply {
    val service = mockConfService()
    mockRetrieveFolderByDbid(service)

    every { configurationService } returns service
    every { displayName } returns enumerator.displayName
    every { description } returns enumerator.description
    every { type } returns toCfgEnumeratorType(enumerator.type)
    every { state } returns toCfgObjectState(enumerator.state)
    every { userProperties } returns mockKeyValueCollection()
    every { folderId } returns DEFAULT_FOLDER_DBID
}
