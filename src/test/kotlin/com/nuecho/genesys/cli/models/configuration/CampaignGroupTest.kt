package com.nuecho.genesys.cli.models.configuration

import com.genesyslab.platform.applicationblocks.com.IConfService
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaignGroup
import com.genesyslab.platform.configuration.protocol.types.CfgDialMode.CFGDMPredict
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState.CFGEnabled
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType.CFGAgentGroup
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType.CFGFolder
import com.genesyslab.platform.configuration.protocol.types.CfgOperationMode.CFGOMManual
import com.genesyslab.platform.configuration.protocol.types.CfgOptimizationMethod.CFGOMOverdialRate
import com.nuecho.genesys.cli.models.configuration.ConfigurationAsserts.checkSerialization
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_FOLDER_REFERENCE
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_OBJECT_DBID
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_TENANT
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_TENANT_DBID
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.DEFAULT_TENANT_REFERENCE
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgAgentGroup
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgApplication
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgCampaignGroup
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgDN
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgFolder
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgGVPIVRProfile
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgScript
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockCfgTenant
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectMocks.mockKeyValueCollection
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toCfgDNType
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toCfgObjectState
import com.nuecho.genesys.cli.models.configuration.reference.AgentGroupReference
import com.nuecho.genesys.cli.models.configuration.reference.ApplicationReference
import com.nuecho.genesys.cli.models.configuration.reference.CampaignGroupCampaignReference
import com.nuecho.genesys.cli.models.configuration.reference.DNReference
import com.nuecho.genesys.cli.models.configuration.reference.GVPIVRProfileReference
import com.nuecho.genesys.cli.models.configuration.reference.ScriptReference
import com.nuecho.genesys.cli.models.configuration.reference.SwitchReference
import com.nuecho.genesys.cli.services.ConfServiceExtensionMocks.mockConfigurationObjectRepository
import com.nuecho.genesys.cli.services.ConfServiceExtensionMocks.mockRetrieveAgentGroup
import com.nuecho.genesys.cli.services.ConfServiceExtensionMocks.mockRetrieveCampaign
import com.nuecho.genesys.cli.services.ConfigurationObjectRepository
import com.nuecho.genesys.cli.services.ServiceMocks.mockConfService
import com.nuecho.genesys.cli.services.getObjectDbid
import com.nuecho.genesys.cli.toShortName
import io.mockk.every
import io.mockk.objectMockk
import io.mockk.staticMockk
import io.mockk.use
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

private const val AGENT_GROUP_DBID = 108
private const val CAMPAIGN_DBID = 109
private const val ORIG_DN_DBID = 110
private const val IVR_PROFILE_DBID = 111
private const val SCRIPT1_DBID = 112
private const val SCRIPT2_DBID = 113
private const val SERVER_DBID = 114

private const val CAMPAIGN = "campaign"
private val CAMPAIGN_REFERENCE = CampaignGroupCampaignReference(CAMPAIGN, DEFAULT_TENANT_REFERENCE)
private const val SCRIPT1 = "script1"
private const val SCRIPT2 = "script2"
private val DIAL_MODE = CFGDMPredict
private val ENABLED_STATE = CFGEnabled
private val OPERATION_MODE = CFGOMManual
private val OPT_METHOD = CFGOMOverdialRate

private val campaignGroup = CampaignGroup(
    name = "campaignGroup",
    campaign = CAMPAIGN_REFERENCE,
    description = "description",
    dialMode = DIAL_MODE.toShortName(),
    group = AgentGroupReference("group", DEFAULT_TENANT_REFERENCE),
    interactionQueue = ScriptReference(SCRIPT1, DEFAULT_TENANT_REFERENCE),
    ivrProfile = GVPIVRProfileReference("ivrProfile"),
    maxQueueSize = 1,
    minRecBuffSize = 4,
    numOfChannels = 1,
    operationMode = OPERATION_MODE.toShortName(),
    optMethod = OPT_METHOD.toShortName(),
    optMethodValue = 1,
    optRecBuffSize = 6,
    origDN = DNReference(
        number = "dn",
        switch = SwitchReference("switch", DEFAULT_TENANT_REFERENCE),
        type = "acdqueue", // cfgACDQueue cfgRoutingPoint
        tenant = DEFAULT_TENANT_REFERENCE
    ),
    script = ScriptReference(SCRIPT2, DEFAULT_TENANT_REFERENCE),
    servers = listOf(ApplicationReference("applicationServer")),
    tenant = DEFAULT_TENANT_REFERENCE,
    state = ENABLED_STATE.toShortName(),
    userProperties = ConfigurationTestData.defaultProperties(),
    folder = DEFAULT_FOLDER_REFERENCE
)

class CampaignGroupTest : ConfigurationObjectTest(
    campaignGroup,
    CampaignGroup(campaign = CAMPAIGN_REFERENCE, name = campaignGroup.name),
    setOf(GROUP)
) {
    @Test
    override fun `initialized object should properly serialize`() {
        val service = mockConfService()
        val folder = mockCfgFolder()
        val agentGroup = mockCfgAgentGroup("group")

        objectMockk(ConfigurationObjects).use {
            every { service.retrieveObject(CFGFolder, any()) } returns folder
            every { service.retrieveObject(CFGAgentGroup, AGENT_GROUP_DBID) } returns agentGroup

            val campaignGroup = CampaignGroup(mockCfgCampaignGroup(service))
            checkSerialization(campaignGroup, "campaigngroup")
        }
    }

    @Test
    fun `createCfgObject should properly create CfgCampaignGroup`() {

        val service = mockConfService()
        mockRetrieveAgentGroup(service, AGENT_GROUP_DBID)
        mockRetrieveCampaign(service, CAMPAIGN_DBID)

        staticMockk("com.nuecho.genesys.cli.services.ConfServiceExtensionsKt").use {
            every { service.getObjectDbid(campaignGroup.interactionQueue) } answers { SCRIPT1_DBID }
            every { service.getObjectDbid(campaignGroup.ivrProfile) } answers { IVR_PROFILE_DBID }
            every { service.getObjectDbid(campaignGroup.origDN) } answers { ORIG_DN_DBID }
            every { service.getObjectDbid(campaignGroup.script) } answers { SCRIPT2_DBID }
            every { service.getObjectDbid(campaignGroup.servers!![0]) } answers { SERVER_DBID }
            every { service.getObjectDbid(campaignGroup.tenant) } answers { DEFAULT_TENANT_DBID }

            objectMockk(ConfigurationObjectRepository).use {
                mockConfigurationObjectRepository()

                val cfgCampaignGroup = campaignGroup.createCfgObject(service)

                with(cfgCampaignGroup) {
                    assertThat(campaignDBID, equalTo(CAMPAIGN_DBID))
                    assertThat(interactionQueueDBID, equalTo(SCRIPT1_DBID))
                    assertThat(ivrProfileDBID, equalTo(IVR_PROFILE_DBID))
                    assertThat(groupDBID, equalTo(AGENT_GROUP_DBID))
                    assertThat(origDNDBID, equalTo(ORIG_DN_DBID))
                    assertThat(scriptDBID, equalTo(SCRIPT2_DBID))
                    assertThat(serverDBIDs.toList(), equalTo(listOf(SERVER_DBID)))

                    assertThat(campaignDBID, equalTo(CAMPAIGN_DBID))
                    assertThat(description, equalTo(campaignGroup.description))
                    assertThat(dialMode, equalTo(CFGDMPredict))
                    assertThat(maxQueueSize, equalTo(campaignGroup.maxQueueSize))
                    assertThat(minRecBuffSize, equalTo(campaignGroup.minRecBuffSize))
                    assertThat(numOfChannels, equalTo(campaignGroup.numOfChannels))
                    assertThat(operationMode, equalTo(OPERATION_MODE))
                    assertThat(optMethod, equalTo(OPT_METHOD))
                    assertThat(optMethodValue, equalTo(campaignGroup.optMethodValue))
                    assertThat(optRecBuffSize, equalTo(campaignGroup.optRecBuffSize))

                    assertThat(name, equalTo(campaignGroup.name))
                    assertThat(state, equalTo(toCfgObjectState(campaignGroup.state)))
                    assertThat(userProperties.asCategorizedProperties(), equalTo(campaignGroup.userProperties))
                }
            }
        }
    }
}

private fun mockCfgCampaignGroup(service: IConfService): CfgCampaignGroup {
    val cfgCampaignGroup = mockCfgCampaignGroup(campaignGroup.name)
    val cfgSwitch = ConfigurationObjectMocks.mockCfgSwitch("switch")

    val mockInteractionQueue = mockCfgScript(SCRIPT1)
    val mockScript = mockCfgScript(SCRIPT2)
    val mockIvrProfile = mockCfgGVPIVRProfile(campaignGroup.ivrProfile!!.primaryKey)
    val mockOrigDN = mockCfgDN(campaignGroup.origDN!!.number, toCfgDNType(campaignGroup.origDN!!.type)!!)
        .apply {
            every { name } returns campaignGroup.origDN!!.name
            every { switch } returns cfgSwitch
        }
    val mockServer = mockCfgApplication(campaignGroup.servers!![0].primaryKey)
    val mockTenant = mockCfgTenant(DEFAULT_TENANT)

    return cfgCampaignGroup.apply {
        every { configurationService } returns service
        every { folderId } returns DEFAULT_OBJECT_DBID
        every { name } returns campaignGroup.name
        every { campaignDBID } returns CAMPAIGN_DBID
        every { description } returns campaignGroup.description
        every { dialMode } returns CFGDMPredict
        every { groupDBID } returns AGENT_GROUP_DBID
        every { groupType } returns CFGAgentGroup
        every { interactionQueue } returns mockInteractionQueue
        every { ivrProfile } returns mockIvrProfile
        every { maxQueueSize } returns campaignGroup.maxQueueSize
        every { minRecBuffSize } returns campaignGroup.minRecBuffSize
        every { numOfChannels } returns campaignGroup.numOfChannels
        every { operationMode } returns OPERATION_MODE
        every { optMethod } returns OPT_METHOD
        every { optMethodValue } returns campaignGroup.optMethodValue
        every { optRecBuffSize } returns campaignGroup.optRecBuffSize
        every { origDN } returns mockOrigDN
        every { script } returns mockScript
        every { servers } returns listOf(mockServer)
        every { state } returns ENABLED_STATE
        every { tenant } returns mockTenant
        every { userProperties } returns mockKeyValueCollection()
    }
}