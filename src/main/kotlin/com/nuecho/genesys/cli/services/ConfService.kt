package com.nuecho.genesys.cli.services

import com.genesyslab.platform.applicationblocks.com.ConfService
import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory
import com.genesyslab.platform.applicationblocks.com.ConfigServerException
import com.genesyslab.platform.applicationblocks.com.IConfService
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery
import com.genesyslab.platform.commons.protocol.ChannelState
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol
import com.nuecho.genesys.cli.Logging
import com.nuecho.genesys.cli.preferences.environment.Environment
import com.nuecho.genesys.cli.services.GenesysServices.createConfServerProtocol

class ConfService private constructor(
    private val protocol: ConfServerProtocol,
    private val confService: ConfService
) : Service, IConfService by confService {
    constructor(environment: Environment) : this(createConfServerProtocol(environment))
    internal constructor(protocol: ConfServerProtocol) : this(
        protocol,
        ConfServiceFactory.createConfService(protocol) as ConfService
    )

    internal val isDisposed: Boolean
        get() = confService.isDisposed

    fun getPerson(employeeId: String): CfgPerson {
        val query = CfgPersonQuery()
        query.employeeId = employeeId
        return retrieveObject(CfgPerson::class.java, query)
                ?: throw ConfigServerException("Error while retrieving CfgPerson ($employeeId).")
    }

    fun getSwitch(name: String): CfgSwitch =
        retrieveObject(CfgSwitch::class.java, CfgSwitchQuery(name))
                ?: throw ConfigServerException("Error while retrieving CfgSwitch ($name)")

    override fun open() {
        Logging.info {
            "Connecting to Config Server [${protocol.userName}@${protocol.endpoint.host}:${protocol.endpoint.port}]"
        }

        try {
            protocol.open()
        } catch (exception: Exception) {
            throw ConfigServerException(
                "Error while connecting to Config Server [${protocol.endpoint.host}:${protocol.endpoint.port}]."
            ).initCause(exception)
        }

        Logging.debug { "Connected to Config Server." }
    }

    override fun close() {
        Logging.debug { "Disconnecting from Config Server" }

        try {
            if (protocol.state !== ChannelState.Closed) {
                protocol.close()
            }

            if (!isDisposed) {
                ConfServiceFactory.releaseConfService(confService)
            }
        } catch (exception: Exception) {
            throw ConfigServerException("Error while disconnecting from Config Server.").initCause(exception)
        }

        Logging.debug { "Disconnected from Config Server." }
    }
}
