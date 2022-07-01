package asm

import core.Core



private const val fileName = "lexer"

private const val directory = "module-asm/src/main/asm"



fun main() = Core.nasmRun("$directory/$fileName", "$directory/out/$fileName", "-I$directory/utils", "-I$directory")