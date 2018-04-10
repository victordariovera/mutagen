package com.nuecho.genesys.cli.models.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.genesyslab.platform.applicationblocks.com.IConfService
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill
import com.nuecho.genesys.cli.asMap
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectUpdateStatus.CREATED
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectUpdateStatus.UNCHANGED
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.setProperty
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjects.toKeyValueCollection
import com.nuecho.genesys.cli.services.retrieveSkill
import com.nuecho.genesys.cli.toShortName

data class Skill(
    val name: String,
    val state: String? = null,
    @JsonSerialize(using = KeyValueCollectionSerializer::class)
    @JsonDeserialize(using = KeyValueCollectionDeserializer::class)
    val userProperties: Map<String, Any>? = null

) : ConfigurationObject {
    override val primaryKey: String
        @JsonIgnore
        get() = name

    constructor(skill: CfgSkill) : this(
        name = skill.name,
        state = skill.state?.toShortName(),
        userProperties = skill.userProperties?.asMap()
    )

    override fun updateCfgObject(service: IConfService): ConfigurationObjectUpdateResult {
        service.retrieveSkill(name)?.let {
            return ConfigurationObjectUpdateResult(UNCHANGED, it)
        }

        CfgSkill(service).let {
            setProperty("name", name, it)
            setProperty("userProperties", toKeyValueCollection(userProperties), it)
            setProperty("state", ConfigurationObjects.toCfgObjectState(state), it)
            return ConfigurationObjectUpdateResult(CREATED, it)
        }
    }
}