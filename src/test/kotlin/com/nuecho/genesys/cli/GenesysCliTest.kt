package com.nuecho.genesys.cli

import com.nuecho.genesys.cli.CliOutputCaptureWrapper.captureOutput
import com.nuecho.genesys.cli.CliOutputCaptureWrapper.execute
import com.nuecho.genesys.cli.Logging.debug
import com.nuecho.genesys.cli.Logging.info
import io.mockk.every
import io.mockk.spyk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Test

private const val DEBUG_LOG_ENTRY = "This is a debug log entry."
private const val INFO_LOG_ENTRY = "This is an info log entry."

class GenesysCliTest {

    @Test
    fun `executing GenesysCli with no argument should print usage`() {
        val output = execute()

        assertThat(output, containsString(SYNOPSIS))
        assertThat(output, containsString(FOOTER))
        assertThat(output, containsString(EXTRA_FOOTER))
        assertThat(output, containsString(BANNER.lfToPlatformEol()))
    }

    @Test
    fun `executing GenesysCli with -h argument should print usage`() {
        val output = execute("-h")
        assertThat(output, containsString(SYNOPSIS))
        assertThat(output, containsString(FOOTER))
        assertThat(output, not(containsString(EXTRA_FOOTER)))
        assertThat(output, not(containsString(BANNER.lfToPlatformEol())))
    }

    @Test
    fun `executing GenesysCli with -v argument should print version`() {
        val expectedOutput = "mutagen version 0.0.0"

        val output = execute("-v")
        assertThat(output, startsWith(expectedOutput))
    }

    @Test
    fun `executing GenesysCli without --stacktrace should print only print error message`() {
        val message = "An error occured."
        val output = testException(message)
        assertThat(output.trim(), equalTo(message))
    }

    @Test
    fun `executing GenesysCli with --stacktrace should print the whole stacktrace`() {
        val message = "An error occured."
        val output = testException(message, "--stacktrace")
        assertThat(output, startsWith("java.lang.RuntimeException: $message"))
    }

    @Test
    fun `executing GenesysCli with --info should print info traces`() {
        val output = testLogging("--info")
        assertThat(output, hasItem(INFO_LOG_ENTRY))
        assertThat(output, not(hasItem(DEBUG_LOG_ENTRY)))
    }

    @Test
    fun `executing GenesysCli with --debug should print info and debug traces`() {
        val output = testLogging("--debug")
        assertThat(output, hasItems(DEBUG_LOG_ENTRY, INFO_LOG_ENTRY))
    }

    @Test
    fun `executing GenesysCli with --debug and --info should print info and debug traces`() {
        val output = testLogging("--debug", "--info")
        assertThat(output, hasItems(INFO_LOG_ENTRY, DEBUG_LOG_ENTRY))
    }
}

private fun testException(message: String, vararg args: String): String {
    val command = spyk(GenesysCli())

    every {
        command.call()
    } throws RuntimeException(message)

    val (returnCode, output) = captureOutput { GenesysCli.execute(command, *args) }
    assertThat(returnCode, equalTo(1))
    return output
}

private fun testLogging(vararg args: String): List<String> {
    val command = spyk(GenesysCli())

    every {
        command.execute()
    } answers {
        debug { DEBUG_LOG_ENTRY }
        info { INFO_LOG_ENTRY }
        0
    }

    val (returnCode, output) = captureOutput { GenesysCli.execute(command, *args) }
    assertThat(returnCode, equalTo(0))
    return output.split(System.lineSeparator())
}

private fun String.lfToPlatformEol(): String = this.replace("\n", System.lineSeparator())
