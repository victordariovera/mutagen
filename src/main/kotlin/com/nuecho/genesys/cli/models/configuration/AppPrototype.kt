package com.nuecho.genesys.cli.models.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.genesyslab.platform.applicationblocks.com.IConfService
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppPrototype
import com.nuecho.genesys.cli.getFolderReference
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.setFolder
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.setProperty
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toCfgAppType
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toCfgObjectState
import com.nuecho.genesys.cli.models.configuration.reference.AppPrototypeReference
import com.nuecho.genesys.cli.models.configuration.reference.ConfigurationObjectReference
import com.nuecho.genesys.cli.models.configuration.reference.FolderReference
import com.nuecho.genesys.cli.models.configuration.reference.referenceSetBuilder
import com.nuecho.genesys.cli.services.retrieveObject
import com.nuecho.genesys.cli.toShortName

data class AppPrototype(
    val name: String,
    val type: String? = null,
    val version: String? = null,
    @JsonSerialize(using = CategorizedPropertiesSerializer::class)
    @JsonDeserialize(using = CategorizedPropertiesDeserializer::class)
    val options: CategorizedProperties? = null,
    val state: String? = null,
    @JsonSerialize(using = CategorizedPropertiesSerializer::class)
    @JsonDeserialize(using = CategorizedPropertiesDeserializer::class)
    override val userProperties: CategorizedProperties? = null,
    override val folder: FolderReference? = null
) : ConfigurationObject {
    @get:JsonIgnore
    override val reference = AppPrototypeReference(name)

    constructor(appPrototype: CfgAppPrototype) : this(
        name = appPrototype.name,
        type = appPrototype.type?.toShortName(),
        state = appPrototype.state?.toShortName(),
        version = appPrototype.version,
        options = appPrototype.options?.asCategorizedProperties(),
        userProperties = appPrototype.userProperties?.asCategorizedProperties(),
        folder = appPrototype.getFolderReference()
    )

    override fun updateCfgObject(service: IConfService) =
        (service.retrieveObject(reference) ?: CfgAppPrototype(service)).also {
            setProperty("name", name, it)
            setProperty(TYPE, toCfgAppType(type), it)
            setProperty(VERSION, version, it)
            setProperty("options", ConfigurationObjects.toKeyValueCollection(options), it)
            setProperty("state", toCfgObjectState(state), it)
            setProperty("userProperties", ConfigurationObjects.toKeyValueCollection(userProperties), it)
            setFolder(folder, it)
        }

    override fun checkMandatoryProperties(): Set<String> {
        val missingMandatoryProperties = mutableSetOf<String>()
        type ?: missingMandatoryProperties.add(TYPE)
        version ?: missingMandatoryProperties.add(VERSION)
        return missingMandatoryProperties
    }

    override fun getReferences(): Set<ConfigurationObjectReference<*>> =
        referenceSetBuilder()
            .add(folder)
            .toSet()
}