package com.nuecho.genesys.cli.commands.config.import

import com.genesyslab.platform.applicationblocks.com.CfgObject
import com.nuecho.genesys.cli.GenesysCliCommand
import com.nuecho.genesys.cli.Logging
import com.nuecho.genesys.cli.commands.config.Config
import com.nuecho.genesys.cli.core.defaultJsonObjectMapper
import com.nuecho.genesys.cli.models.configuration.Configuration
import com.nuecho.genesys.cli.models.configuration.ConfigurationObject
import com.nuecho.genesys.cli.models.configuration.ConfigurationObjectUpdateStatus.CREATED
import com.nuecho.genesys.cli.services.ConfService
import com.nuecho.genesys.cli.services.defaultTenantDbid
import com.nuecho.genesys.cli.toShortName
import picocli.CommandLine
import java.io.File

@CommandLine.Command(
    name = "import",
    description = ["[INCUBATION] Import configuration objects."]
)
class Import : GenesysCliCommand() {
    @CommandLine.ParentCommand
    private var config: Config? = null

    @CommandLine.Parameters(
        arity = "1",
        index = "0",
        paramLabel = "inputFile",
        description = ["Input configuration file."]
    )
    private var inputFile: File? = null

    override fun getGenesysCli() = config!!.getGenesysCli()

    override fun execute() {
        withEnvironmentConfService {
            val configuration = defaultJsonObjectMapper().readValue(inputFile, Configuration::class.java)
            importConfiguration(configuration, it)
        }
    }

    companion object {
        fun importConfiguration(configuration: Configuration, service: ConfService) {

            Logging.info { "Beginning import." }

            val count = importConfigurationObjects(configuration.skills.values, service) +
                    importConfigurationObjects(configuration.roles.values, service) +
                    importConfigurationObjects(configuration.persons.values, service)

            println("Completed. $count object(s) imported.")
        }

        internal fun importConfigurationObjects(
            objects: Collection<ConfigurationObject>,
            service: ConfService
        ): Int {
            var count = 0

            objects.forEach {
                val primaryKey = it.primaryKey
                val (status, cfgObject) = it.updateCfgObject(service)
                val type = cfgObject.objectType.toShortName()

                if (status != CREATED) {
                    objectImportProgress(type, primaryKey, true)
                    return@forEach
                }

                Logging.info { "Creating $type '$primaryKey'." }
                save(applyTenant(cfgObject))
                objectImportProgress(type, primaryKey)
                count++
            }

            return count
        }

        private fun objectImportProgress(type: String, key: String, skip: Boolean = false) {
            val prefix = if (skip) "=" else "+"
            println("$prefix $type.$key")
        }

        internal fun save(cfgObject: CfgObject) = applyTenant(cfgObject).save()

        internal fun applyTenant(cfgObject: CfgObject): CfgObject =
            cfgObject.apply { setProperty("tenantDBID", cfgObject.configurationService.defaultTenantDbid) }
    }
}