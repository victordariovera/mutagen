package com.nuecho.genesys.cli.commands.config.import

import com.genesyslab.platform.applicationblocks.com.objects.CfgTransaction
import com.nuecho.genesys.cli.models.configuration.Transaction
import com.nuecho.genesys.cli.services.ServiceMocks.mockConfService

class ImportTransactionTest : ImportObjectSpec(
    CfgTransaction(mockConfService()),
    listOf(Transaction("foo"), Transaction("bar"))
)