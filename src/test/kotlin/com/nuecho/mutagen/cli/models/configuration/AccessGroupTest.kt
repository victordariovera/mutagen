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

import com.genesyslab.platform.applicationblocks.com.IConfService
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgGroup
import com.genesyslab.platform.configuration.protocol.types.CfgAccessGroupType
import com.genesyslab.platform.configuration.protocol.types.CfgAccessGroupType.CFGAdministratorsGroup
import com.genesyslab.platform.configuration.protocol.types.CfgAccessGroupType.CFGDefaultGroup
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType.CFGPerson
import com.nuecho.mutagen.cli.models.configuration.ConfigurationAsserts.checkSerialization
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_DBID
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_OBJECT_DBID
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_TENANT_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgAccessGroup
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgID
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgPerson
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockEmptyCfgGroup
import com.nuecho.mutagen.cli.models.configuration.reference.PersonReference
import com.nuecho.mutagen.cli.models.configuration.reference.referenceSetBuilder
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveFolderByDbid
import com.nuecho.mutagen.cli.services.ServiceMocks.mockConfService
import com.nuecho.mutagen.cli.toShortName
import io.mockk.every
import io.mockk.objectMockk
import io.mockk.use
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

private const val NAME = "accessGroup"
private const val MEMBER1 = "member1"
private const val MEMBER2 = "member2"

private val accessGroup = AccessGroup(
    group = Group(
        tenant = DEFAULT_TENANT_REFERENCE,
        name = NAME,
        state = "enabled"
    ),
    members = listOf(
        PersonReference(MEMBER1, DEFAULT_TENANT_REFERENCE),
        PersonReference(MEMBER2, DEFAULT_TENANT_REFERENCE)
    ),
    type = CFGDefaultGroup.toShortName(),
    folder = DEFAULT_FOLDER_REFERENCE
)

class AccessGroupTest : ConfigurationObjectTest(
    configurationObject = accessGroup,
    emptyConfigurationObject = AccessGroup(tenant = DEFAULT_TENANT_REFERENCE, name = NAME),
    mandatoryProperties = emptySet()
) {
    @Test
    override fun `getReferences() should return all object's references`() {
        val expected = referenceSetBuilder()
            .add(accessGroup.members)
            .add(accessGroup.folder)
            .add(accessGroup.group.getReferences())
            .toSet()

        assertThat(accessGroup.getReferences(), equalTo(expected))
    }

    @Test
    override fun `object with different unchangeable properties' values should return the right unchangeable properties`() =
        mockCfgAccessGroup().let {
            every { it.type } returns CFGAdministratorsGroup
            assertUnchangeableProperties(it, FOLDER, TYPE)
        }

    @Test
    override fun `initialized object should properly serialize`() {
        val service = mockConfService()
        val member1 = mockCfgPerson(MEMBER1)
        val member2 = mockCfgPerson(MEMBER2)

        objectMockk(ConfigurationObjects).use {
            mockRetrieveFolderByDbid(service)
            every { service.retrieveObject(CFGPerson, any()) } returns member1 andThen member2

            val accessGroup = AccessGroup(mockAccessGroup(service))
            checkSerialization(accessGroup, "accessgroup")
        }
    }

    @Test
    fun `createCfgObject should properly create CfgAccessGroup`() {
        val member1Dbid = 102
        val member2Dbid = 103

        val service = mockConfService()
        every { service.getObjectDbid(DEFAULT_TENANT_REFERENCE) } returns DEFAULT_OBJECT_DBID
        every { service.getObjectDbid(accessGroup.members!![0]) } returns member1Dbid
        every { service.getObjectDbid(accessGroup.members!![1]) } returns member2Dbid
        every { service.retrieveObject(CfgAccessGroup::class.java, any()) } returns null

        val cfgAccessGroup = accessGroup.createCfgObject(service)

        with(cfgAccessGroup) {
            assertThat(type, equalTo(CFGDefaultGroup))
            assertThat(folderId, equalTo(DEFAULT_FOLDER_DBID))
            assertThat(memberIDs, hasSize(2))
            assertThat(memberIDs.toList()[0].dbid, equalTo(member1Dbid))
            assertThat(memberIDs.toList()[1].dbid, equalTo(member2Dbid))
            assertThat(memberIDs.toList()[0].type, equalTo(CFGPerson))
            assertThat(memberIDs.toList()[1].type, equalTo(CFGPerson))

            assertThat(groupInfo.name, equalTo(accessGroup.group.name))
            assertThat(groupInfo, equalTo(accessGroup.group.toUpdatedCfgGroup(service, CfgGroup(service, cfgAccessGroup))))
            assertThat(folderId, equalTo(DEFAULT_FOLDER_DBID))
        }
    }
}

private fun mockAccessGroup(service: IConfService): CfgAccessGroup {
    val cfgAccessGroup = mockCfgAccessGroup(accessGroup.group.name)

    val membersMock = accessGroup.members?.map { mockCfgID(CFGPerson) }
    val groupInfoMock = mockEmptyCfgGroup(accessGroup.group.name)

    return cfgAccessGroup.apply {
        every { configurationService } returns service
        every { memberIDs } returns membersMock
        every { type } returns CfgAccessGroupType.CFGDefaultGroup
        every { folderId } returns DEFAULT_FOLDER_DBID
        every { groupInfo } returns groupInfoMock
    }
}
