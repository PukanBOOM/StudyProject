package ru.levin.apps.comparator.mappers.v1.exceptions

import ru.levin.apps.comparator.common.models.ComparatorCommand
import kotlin.reflect.KClass

class UnknownRequestException(clazz: KClass<*>) : RuntimeException(
    "Class $clazz cannot be mapped to ComparatorContext"
)

class UnknownCommandException(command: ComparatorCommand) : RuntimeException(
    "Wrong command $command at mapping toTransport stage"
)