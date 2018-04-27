package com.nuecho.genesys.cli.commands.config.import

import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill
import com.nuecho.genesys.cli.models.configuration.Skill
import com.nuecho.genesys.cli.services.ServiceMocks.mockConfService

class ImportSkillTest : ImportObjectSpec(CfgSkill(mockConfService()), listOf(Skill("foo"), Skill("bar")))