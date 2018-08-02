package com.nuecho.genesys.cli.models.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.genesyslab.platform.applicationblocks.com.ICfgObject
import com.genesyslab.platform.applicationblocks.com.IConfService
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost
import com.nuecho.genesys.cli.getFolderReference
import com.nuecho.genesys.cli.getReference
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.setFolder
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.setProperty
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toCfgHostType
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toCfgObjectState
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toKeyValueCollection
import com.nuecho.genesys.cli.models.configuration.reference.ApplicationReference
import com.nuecho.genesys.cli.models.configuration.reference.ConfigurationObjectReference
import com.nuecho.genesys.cli.models.configuration.reference.FolderReference
import com.nuecho.genesys.cli.models.configuration.reference.HostReference
import com.nuecho.genesys.cli.models.configuration.reference.referenceSetBuilder
import com.nuecho.genesys.cli.services.ConfService
import com.nuecho.genesys.cli.services.getObjectDbid
import com.nuecho.genesys.cli.toShortName

/**
 * Not in use HWID, address, contactPersonDBID and comment properties are not defined.
 */
data class Host(
    val name: String,
    val ipAddress: String? = null,
    val lcaPort: String? = null,
    val osInfo: OS? = null,
    val scs: ApplicationReference? = null,
    val type: String? = null,
    val state: String? = null,
    @JsonSerialize(using = CategorizedPropertiesSerializer::class)
    @JsonDeserialize(using = CategorizedPropertiesDeserializer::class)
    override val userProperties: CategorizedProperties? = null,
    override val folder: FolderReference? = null
) : ConfigurationObject {
    @get:JsonIgnore
    override val reference = HostReference(name)

    // FIXME ignoring resources property

    constructor(host: CfgHost) : this(
        name = host.name,
        ipAddress = host.iPaddress,
        lcaPort = host.lcaPort,
        osInfo = OS(host.oSinfo),
        scs = host.scs?.getReference(),
        type = host.type.toShortName(),
        state = host.state.toShortName(),
        userProperties = host.userProperties?.asCategorizedProperties(),
        folder = host.getFolderReference()
    )

    override fun createCfgObject(service: IConfService) =
        updateCfgObject(service, CfgHost(service)).also {
            setProperty("name", name, it)
            setProperty(TYPE, toCfgHostType(type), it)
        }

    override fun updateCfgObject(service: IConfService, cfgObject: ICfgObject) =
        (cfgObject as CfgHost).also { cfgHost ->
            setProperty("IPaddress", ipAddress, cfgHost)
            setProperty("LCAPort", lcaPort, cfgHost)
            setProperty("OSinfo", osInfo?.toCfgOs(service, cfgHost), cfgHost)
            setProperty("SCSDBID", service.getObjectDbid(scs), cfgHost)

            setProperty("userProperties", toKeyValueCollection(userProperties), cfgHost)
            setProperty("state", toCfgObjectState(state), cfgHost)
            setFolder(folder, cfgHost)
        }

    override fun cloneBare() = Host(
        name = name,
        lcaPort = lcaPort,
        osInfo = osInfo,
        type = type
    )

    override fun checkMandatoryProperties(configuration: Configuration, service: ConfService): Set<String> {
        val missingMandatoryProperties = mutableSetOf<String>()
        lcaPort ?: missingMandatoryProperties.add("lcaPort")
        osInfo ?: missingMandatoryProperties.add("osInfo")
        type ?: missingMandatoryProperties.add(TYPE)

        return missingMandatoryProperties
    }

    override fun getReferences(): Set<ConfigurationObjectReference<*>> =
        referenceSetBuilder()
            .add(folder)
            .add(scs)
            .toSet()
}
