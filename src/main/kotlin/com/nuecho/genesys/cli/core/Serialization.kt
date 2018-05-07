package com.nuecho.genesys.cli.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonEncoding
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.OutputStream

private val jsonObjectMapper = jacksonObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL) // do not serialize null
    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY) // both empty arrays and object won't be serialized
    .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT) // do not serialize false, 0,
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true) // consistent output
    .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true) // consistent output

private val yamlObjectMapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

fun defaultJsonObjectMapper(): ObjectMapper = jsonObjectMapper
fun defaultYamlObjectMapper(): ObjectMapper = yamlObjectMapper

fun defaultJsonGenerator(outputStream: OutputStream = System.out): JsonGenerator = JsonFactory()
    .createGenerator(outputStream, JsonEncoding.UTF8)
    .setCodec(defaultJsonObjectMapper())
    .setPrettyPrinter(DefaultPrettyPrinter())
