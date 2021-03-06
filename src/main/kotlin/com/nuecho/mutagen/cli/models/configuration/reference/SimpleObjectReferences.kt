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

package com.nuecho.mutagen.cli.models.configuration.reference

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.genesyslab.platform.applicationblocks.com.CfgFilterBasedQuery
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgAlarmCondition
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppPrototype
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingList
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaign
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator
import com.genesyslab.platform.applicationblocks.com.objects.CfgField
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPCustomer
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPIVRProfile
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPReseller
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost
import com.genesyslab.platform.applicationblocks.com.objects.CfgIVR
import com.genesyslab.platform.applicationblocks.com.objects.CfgObjectiveTable
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson
import com.genesyslab.platform.applicationblocks.com.objects.CfgPhysicalSwitch
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlaceGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgRole
import com.genesyslab.platform.applicationblocks.com.objects.CfgScript
import com.genesyslab.platform.applicationblocks.com.objects.CfgService
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill
import com.genesyslab.platform.applicationblocks.com.objects.CfgStatTable
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant
import com.genesyslab.platform.applicationblocks.com.objects.CfgTimeZone
import com.genesyslab.platform.applicationblocks.com.queries.CfgAccessGroupQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentGroupQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgAlarmConditionQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgAppPrototypeQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgCampaignQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNGroupQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgFieldQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgGVPIVRProfileQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgHostQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgIVRQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgObjectiveTableQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgPhysicalSwitchQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceGroupQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgRoleQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgScriptQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgServiceQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgSkillQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgStatTableQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgTenantQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgTimeZoneQuery
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType
import com.nuecho.mutagen.cli.models.configuration.ConfigurationObjectNotFoundException
import com.nuecho.mutagen.cli.services.ConfService
import java.util.TimeZone

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class AccessGroupReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgAccessGroup>(CfgAccessGroup::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgAccessGroupQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class AlarmConditionReference(name: String) :
    SimpleObjectReference<CfgAlarmCondition>(CfgAlarmCondition::class.java, name) {
    override fun toQuery(service: ConfService) = CfgAlarmConditionQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class ApplicationReference(name: String) :
    SimpleObjectReference<CfgApplication>(CfgApplication::class.java, name) {
    override fun toQuery(service: ConfService) = CfgApplicationQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class AppPrototypeReference(name: String) :
    SimpleObjectReference<CfgAppPrototype>(CfgAppPrototype::class.java, name) {
    override fun toQuery(service: ConfService) = CfgAppPrototypeQuery(primaryKey)
}

class AgentGroupReference(name: String, tenant: TenantReference?) :
    GroupReference, SimpleObjectReferenceWithTenant<CfgAgentGroup>(CfgAgentGroup::class.java, name, tenant) {

    override val primaryKey
        @JsonProperty("name")
        get() = super.primaryKey

    override fun toQuery(service: ConfService) = CfgAgentGroupQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

class PlaceGroupReference(name: String, tenant: TenantReference?) :
    GroupReference, SimpleObjectReferenceWithTenant<CfgPlaceGroup>(CfgPlaceGroup::class.java, name, tenant) {

    override val primaryKey
        @JsonProperty("name")
        get() = super.primaryKey

    override fun toQuery(service: ConfService) = CfgPlaceGroupQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class CampaignReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgCampaign>(CfgCampaign::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgCampaignQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

class CampaignGroupCampaignReference(name: String, val tenant: TenantReference) :
    SimpleObjectReference<CfgCampaign>(CfgCampaign::class.java, name) {

    override val primaryKey
        @JsonProperty("name")
        get() = super.primaryKey

    override fun toQuery(service: ConfService) = CfgCampaignQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }

    fun toCampaignReference() = CampaignReference(primaryKey, tenant)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class CallingListReference(name: String) :
    SimpleObjectReference<CfgCallingList>(CfgCallingList::class.java, name) {

    override fun toQuery(service: ConfService) =
        CfgFilterBasedQuery<CfgCallingList>(CfgObjectType.CFGCallingList).apply {
            this.setProperty("name", primaryKey)
        }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class DNGroupReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgDNGroup>(CfgDNGroup::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgDNGroupQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class EnumeratorReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgEnumerator>(CfgEnumerator::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgEnumeratorQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class FieldReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgField>(CfgField::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgFieldQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class GVPCustomerReference(name: String) :
    SimpleObjectReference<CfgGVPCustomer>(CfgGVPCustomer::class.java, name) {

    override fun toQuery(service: ConfService) =
        CfgFilterBasedQuery<CfgGVPCustomer>(CfgObjectType.CFGGVPCustomer).apply {
            this.setProperty("name", primaryKey)
        }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class GVPIVRProfileReference(name: String) :
    SimpleObjectReference<CfgGVPIVRProfile>(CfgGVPIVRProfile::class.java, name) {
    override fun toQuery(service: ConfService) = CfgGVPIVRProfileQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class GVPResellerReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgGVPReseller>(CfgGVPReseller::class.java, name, tenant) {

    override fun toQuery(service: ConfService) =
        CfgFilterBasedQuery<CfgGVPReseller>(CfgObjectType.CFGGVPReseller).apply {
            this.setProperty("name", primaryKey)
            this.setProperty("tenant_dbid", getTenantDbid(tenant, service))
        }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class HostReference(name: String) :
    SimpleObjectReference<CfgHost>(CfgHost::class.java, name) {
    override fun toQuery(service: ConfService) = CfgHostQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class IVRReference(name: String) :
    SimpleObjectReference<CfgIVR>(CfgIVR::class.java, name) {
    override fun toQuery(service: ConfService) = CfgIVRQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class ObjectiveTableReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgObjectiveTable>(CfgObjectiveTable::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgObjectiveTableQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class PersonReference(employeeId: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgPerson>(CfgPerson::class.java, employeeId, tenant) {
    override fun toQuery(service: ConfService) = CfgPersonQuery().apply {
        employeeId = primaryKey
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class PhysicalSwitchReference(name: String) :
    SimpleObjectReference<CfgPhysicalSwitch>(CfgPhysicalSwitch::class.java, name) {
    override fun toQuery(service: ConfService) = CfgPhysicalSwitchQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class PlaceReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgPlace>(CfgPlace::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgPlaceQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class RoleReference(name: String) :
    SimpleObjectReference<CfgRole>(CfgRole::class.java, name) {
    override fun toQuery(service: ConfService) = CfgRoleQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class ServiceReference(name: String) :
    SimpleObjectReference<CfgService>(CfgService::class.java, name) {
    override fun toQuery(service: ConfService) = CfgServiceQuery(primaryKey)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class ScriptReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgScript>(CfgScript::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgScriptQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

class AlarmConditionScriptReference(name: String, val tenant: TenantReference) :
    SimpleObjectReference<CfgScript>(CfgScript::class.java, name) {

    override val primaryKey
        @JsonProperty("name")
        get() = super.primaryKey

    override fun toQuery(service: ConfService) = CfgScriptQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }

    fun toScriptReference() = ScriptReference(primaryKey, tenant)
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class SkillReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgSkill>(CfgSkill::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgSkillQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class StatTableReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgStatTable>(CfgStatTable::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgStatTableQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class SwitchReference(name: String, tenant: TenantReference?) :
    SimpleObjectReferenceWithTenant<CfgSwitch>(CfgSwitch::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgSwitchQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceDeserializer::class)
class TenantReference(name: String) :
    SimpleObjectReference<CfgTenant>(CfgTenant::class.java, name) {
    override fun toQuery(service: ConfService) = CfgTenantQuery(primaryKey).apply { allTenants = 1 }
}

@JsonSerialize(using = SimpleObjectReferenceSerializer::class)
@JsonDeserialize(using = SimpleObjectReferenceWithTenantDeserializer::class)
class TimeZoneReference(
    name: String = TimeZone.getTimeZone("GMT").getDisplayName(false, TimeZone.SHORT),
    tenant: TenantReference? = null
) :
    SimpleObjectReferenceWithTenant<CfgTimeZone>(CfgTimeZone::class.java, name, tenant) {
    override fun toQuery(service: ConfService) = CfgTimeZoneQuery(primaryKey).apply {
        tenantDbid = getTenantDbid(tenant, service)
    }
}

private fun getTenantDbid(tenant: TenantReference?, service: ConfService) =
    service.getObjectDbid(tenant) ?: throw ConfigurationObjectNotFoundException(tenant)
