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

import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_DBID
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_TENANT_REFERENCE
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgAgentGroup
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgPerson
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockCfgSwitch
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectMocks.mockEmptyCfgGroup
import com.nuecho.mutagen.cli.models.configuration.reference.PersonReference
import com.nuecho.mutagen.cli.models.configuration.reference.referenceSetBuilder
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveDN
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveFolderByDbid
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveObjectiveTable
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrievePerson
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveScript
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveStatTable
import com.nuecho.mutagen.cli.services.ConfServiceExtensionMocks.mockRetrieveTenant
import com.nuecho.mutagen.cli.services.ServiceMocks
import com.nuecho.mutagen.cli.services.ServiceMocks.mockConfService
import io.mockk.every
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

private const val NAME = "name"
private val agentGroup = AgentGroup(
    agents = listOf(
        PersonReference("agent1", DEFAULT_TENANT_REFERENCE),
        PersonReference("agent2", DEFAULT_TENANT_REFERENCE),
        PersonReference("agent3", DEFAULT_TENANT_REFERENCE)
    ),
    group = Group(
        tenant = DEFAULT_TENANT_REFERENCE,
        name = "name",
        state = "enabled"
    ),
    folder = DEFAULT_FOLDER_REFERENCE
)

class AgentGroupTest : ConfigurationObjectTest(
    configurationObject = agentGroup,
    emptyConfigurationObject = AgentGroup(tenant = DEFAULT_TENANT_REFERENCE, name = NAME),
    mandatoryProperties = emptySet(),
    importedConfigurationObject = AgentGroup(mockCfgAgentGroup())
) {
    @Test
    override fun `getReferences() should return all object's references`() {
        val expected = referenceSetBuilder()
            .add(agentGroup.agents)
            .add(agentGroup.group.getReferences())
            .add(agentGroup.folder)
            .toSet()

        assertThat(agentGroup.getReferences(), equalTo(expected))
    }

    @Test
    override fun `object with different unchangeable properties' values should return the right unchangeable properties`() =
        assertUnchangeableProperties(mockCfgAgentGroup(), FOLDER)

    val service = ServiceMocks.mockConfService()

    @Test
    fun `createCfgObject should properly create CfgAgentGroup`() {
        val cfgSwitch = mockCfgSwitch("switch")
        every { service.retrieveObject(CfgSwitch::class.java, any()) } returns cfgSwitch
        every { service.retrieveObject(CfgAgentGroup::class.java, any()) } returns null

        val personDbid = 102
        val dnDbid = 103
        val objectiveTableDbid = 104
        val statTableDbid = 105
        val script1Dbid = 106
        mockRetrieveTenant(service)
        mockRetrievePerson(service, personDbid)
        mockRetrieveDN(service, cfgSwitch, dnDbid)
        mockRetrieveObjectiveTable(service, objectiveTableDbid)
        mockRetrieveStatTable(service, statTableDbid)
        mockRetrieveScript(service, script1Dbid)

        val cfgAgentGroup = agentGroup.createCfgObject(service)

        with(cfgAgentGroup) {
            assertThat(agentDBIDs, contains(personDbid, personDbid, personDbid))
            assertThat(groupInfo, equalTo(agentGroup.group.toUpdatedCfgGroup(service, CfgGroup(service, this))))
            assertThat(folderId, equalTo(DEFAULT_FOLDER_DBID))
        }
    }
}

private fun mockCfgAgentGroup(): CfgAgentGroup {
    val service = mockConfService()
    mockRetrieveFolderByDbid(service)

    val groupInfoMock = mockEmptyCfgGroup(agentGroup.group.name)
    val agentsMock = agentGroup.agents?.map { ref -> mockCfgPerson(ref.primaryKey) }

    return mockCfgAgentGroup(agentGroup.group.name).apply {
        every { configurationService } returns service
        every { agents } returns agentsMock
        every { folderId } returns DEFAULT_FOLDER_DBID
        every { groupInfo } returns groupInfoMock
    }
}
