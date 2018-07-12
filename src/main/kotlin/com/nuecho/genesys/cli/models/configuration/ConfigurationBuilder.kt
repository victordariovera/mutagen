package com.nuecho.genesys.cli.models.configuration

import com.genesyslab.platform.applicationblocks.com.ICfgObject
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgActionCode
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppPrototype
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNGroup
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator

import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumeratorValue
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPCustomer
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPIVRProfile
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPReseller
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson
import com.genesyslab.platform.applicationblocks.com.objects.CfgPhysicalSwitch
import com.genesyslab.platform.applicationblocks.com.objects.CfgRole
import com.genesyslab.platform.applicationblocks.com.objects.CfgScript
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant
import com.genesyslab.platform.applicationblocks.com.objects.CfgTransaction

class ConfigurationBuilder {
    private val accessGroups = ArrayList<AccessGroup>()
    private val actionCodes = ArrayList<ActionCode>()
    private val agentGroups = ArrayList<AgentGroup>()
    private val appPrototypes = ArrayList<AppPrototype>()
    private val dnGroups = ArrayList<DNGroup>()
    private val dns = ArrayList<DN>()
    private val enumerators = ArrayList<Enumerator>()
    private val enumeratorValues = ArrayList<EnumeratorValue>()
    private val folders = ArrayList<Folder>()
    private val gvpCustomers = ArrayList<GVPCustomer>()
    private val gvpResellers = ArrayList<GVPReseller>()
    private val gvpIVRProfiles = ArrayList<GVPIVRProfile>()
    private val hosts = ArrayList<Host>()
    private val persons = ArrayList<Person>()
    private val physicalSwitches = ArrayList<PhysicalSwitch>()
    private val roles = ArrayList<Role>()
    private val scripts = ArrayList<Script>()
    private val skills = ArrayList<Skill>()
    private val switches = ArrayList<Switch>()
    private val tenants = ArrayList<Tenant>()
    private val transactions = ArrayList<Transaction>()

    fun add(cfgObject: ICfgObject) =
        when (cfgObject) {
            is CfgAccessGroup -> accessGroups += AccessGroup(cfgObject)
            is CfgActionCode -> actionCodes += ActionCode(cfgObject)
            is CfgAgentGroup -> agentGroups += AgentGroup(cfgObject)
            is CfgAppPrototype -> appPrototypes += AppPrototype(cfgObject)
            is CfgDNGroup -> dnGroups += DNGroup(cfgObject)
            is CfgDN -> dns += DN(cfgObject)
            is CfgEnumerator -> enumerators += Enumerator(cfgObject)
            is CfgEnumeratorValue -> enumeratorValues += EnumeratorValue(cfgObject)
            is CfgFolder -> folders += Folder(cfgObject)
            is CfgGVPCustomer -> gvpCustomers += GVPCustomer(cfgObject)
            is CfgGVPIVRProfile -> gvpIVRProfiles += GVPIVRProfile(cfgObject)
            is CfgGVPReseller -> gvpResellers += GVPReseller(cfgObject)
            is CfgHost -> hosts += Host(cfgObject)
            is CfgPerson -> persons += Person(cfgObject)
            is CfgPhysicalSwitch -> physicalSwitches += PhysicalSwitch(cfgObject)
            is CfgRole -> roles += Role(cfgObject)
            is CfgScript -> scripts += Script(cfgObject)
            is CfgSkill -> skills += Skill(cfgObject)
            is CfgSwitch -> switches += Switch(cfgObject)
            is CfgTenant -> tenants += Tenant(cfgObject)
            is CfgTransaction -> transactions += Transaction(cfgObject)
            else -> Unit
        }

    fun build(metadata: Metadata) = Configuration(
        metadata,
        accessGroups.sorted(),
        actionCodes.sorted(),
        agentGroups.sorted(),
        appPrototypes.sorted(),
        dnGroups.sorted(),
        dns.sorted(),
        enumerators.sorted(),
        enumeratorValues.sorted(),
        folders.sorted(),
        gvpCustomers.sorted(),
        gvpIVRProfiles.sorted(),
        gvpResellers.sorted(),
        hosts.sorted(),
        persons.sorted(),
        physicalSwitches.sorted(),
        roles.sorted(),
        scripts.sorted(),
        skills.sorted(),
        switches.sorted(),
        tenants.sorted(),
        transactions.sorted()
    )
}
