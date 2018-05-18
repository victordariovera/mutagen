package com.nuecho.genesys.cli.models.configuration

import com.genesyslab.platform.applicationblocks.com.objects.CfgDNAccessNumber
import com.nuecho.genesys.cli.getReference
import com.nuecho.genesys.cli.models.configuration.reference.SwitchReference

data class DNAccessNumber(
    val number: String,
    val switch: SwitchReference

) {
    constructor(dnAccessNumber: CfgDNAccessNumber) : this(
        number = dnAccessNumber.number,
        switch = dnAccessNumber.switch.getReference()
    )
}
