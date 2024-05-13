package core.gen

import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Writes formatted strings that represent source code to a [Writer].
 */
open class CodeWriter(private val writer: Writer) : AutoCloseable by writer {


	/**
	 * Constructs a [CodeWriter] given a [Path] to which the generated code will be written.
	 */
	constructor(path: Path) : this(Files.newBufferedWriter(path))

	/**
	 * Constructs a [CodeWriter] given a [Path] in [String] form to which the generated code will be written.
	 */
	constructor(path: String) : this(Paths.get(path))



	/**
	 * A class that specifies the spacing in-between declarations and between declarations and tbe start and end of
	 * indentation blocks.
	 */
	data class Style(val decSpacing: Int, val blockSpacing: Int)



	/*
	Variables
	 */



	/**
	 * If the next write function should be preceded by writing a newline and indentation.
	 */
	private var atNewline = false



	/**
	 * The number of indents that will be inserted whenever a newline is written. This variable is modified in a
	 * stack-like fashion to create nested indentations.
	 */
	var indent = 0



	/**
	 * The number of blank lines to insert before the next [declaration] or closing brace block.
	 */
	var spacing = 0



	/**
	 * Determines the spacing between declarations as well as the spacing at the start and end of bracing blocks.
	 */
	var currentStyle = Style(1, 1)



	/*
	Styles
	 */



	/**
	 * Convenience getter for a [Style] with spacings of zero. Useful when using this [CodeWriter] as a receiver.
	 */
	val noStyle get() = Style(0, 0)



	/**
	 * Convenience constructor for a [Style]. Useful when using this [CodeWriter] as a receiver.
	 */
	fun style(decSpacing: Int, braceSpacing: Int = 0) = Style(decSpacing, braceSpacing)



	/*
	Newline
	 */



	/**
	 * Used whenever a newline is created. Writes a newline character followed by the current [indent].
	 */
	private fun writeNewline() {
		writer.write('\n'.code)

		for(i in 0 until indent)
			writer.write('\t'.code)
	}



	/**
	 * Writes a newline and indentation if [atNewline].
	 */
	private fun checkNewline() {
		if(atNewline) {
			writeNewline()
			atNewline = false
		}
	}



	/**
	 * Writes a newline character and appropriate [indentation][indent]. If nothing else is written afterwards, then the
	 * newline will not be written. This avoids empty lines at the end of a source file.
	 */
	fun newline() {
		checkNewline()
		atNewline = true
	}



	/**
	 * Multiple version of [newline].
	 */
	fun newlines(count: Int) {
		checkNewline()

		for(i in 0 until count) {
			writeNewline()
		}
	}



	/**
	 * Stops a newline from being written during the next write operation if [atNewline] is currently true.
	 */
	fun resetNewline() {
		atNewline = false
	}



	/*
	Basic writing
	 */



	/**
	 * Writes a [string].
	 */
	fun write(string: String) {
		checkNewline()
		writer.write(string)
	}



	/**
	 * Writes a [char].
	 */
	fun write(char: Char) {
		checkNewline()
		writer.write(char.code)
	}



	/**
	 * Writes a [string] and creates a newline.
	 */
	fun writeln(string: String) {
		write(string)
		atNewline = true
	}



	/**
	 * Writes a char and creates a newline.
	 */
	fun writeln(char: Char) {
		write(char)
		atNewline = true
	}



	/**
	 * Writes a single newline
	 */
	fun writeln() = newline()



	/**
	 * Writes a multi-line string literal. The string is trimmed by its smallest indent. All of the lines in the string
	 * are also indented by the current [indent].
	 */
	fun writeMultiline(string: String) {
		string.trimIndent().lines().forEach(::writeln)
	}



	/*
	Block functions
	 */



	/**
	 * Increments the [indent], executes the block, and decrements the [indent].
	 */
	inline fun indented(block: () -> Unit) {
		indent++
		block()
		indent--
	}



	/**
	 * Sets the [currentStyle] to the given [style], executes the [block], then resets the [currentStyle] to its
	 * original value.
	 */
	inline fun styled(style: Style, block: () -> Unit) {
		val previousStyle = currentStyle
		currentStyle = style
		block()
		currentStyle = previousStyle
	}



	/**
	 * Writes an opening brace, executes the [block], and writes a closing brace. Also inserts a number of newlines
	 * before the closing brace according to the [currentStyle]. Newlines following the opening brace are not inserted
	 * until a [declaration] is called. This avoids having double the intended spacing if the block doesn't contain any
	 * declarations.
	 */
	inline fun braced(block: () -> Unit) {
		writeln(" {")
		indented(block)
		newlines(currentStyle.blockSpacing)
		writeln('}')
	}



	/**
	 * A declaration is a section of code, e.g. a function, variable, class, etc. All written contents should be nested
	 * within declaration blocks. Declarations are used to control line spacings between sections of code and between
	 * opening and closing braces. The [style] is applied as the [currentStyle] during the execution of the [block].
	 */
	inline fun declaration(style: Style = currentStyle, block: () -> Unit) {
		styled(style) {
			newlines(spacing)
			spacing = style.blockSpacing
			block()
		}

		spacing = currentStyle.decSpacing
	}



	/*
	Non-block declarations
	 */



	/**
	 * Single-line version of [declaration].
	 */
	fun declaration(line: String) = declaration { writeln(line) }

	/**
	 * Multiple-line string version of [declaration].
	 */
	fun multilineDeclaration(lines: String) = declaration { writeMultiline(lines) }



	/*
	Groups
	 */



	/**
	 * Creates a non-indented, non-braced [declaration] in which the given [block] is executed. Any declarations made
	 * inside the [block] are separated by the specified [spacing]. This is useful for grouping together related
	 * declarations.
	 */
	inline fun group(spacing: Int, block: () -> Unit) = declaration(Style(spacing, 0), block)



	/**
	 * Version of [group] that also inserts a [multilineComment] immediately before the declaration begins. This is
	 * useful for prefacing a group of related declarations.
	 */
	inline fun group(comment: String, spacing: Int, block: () -> Unit) {
		multilineComment(comment)
		group(spacing, block)
	}



	/*
	Comments
	 */



	/**
	 * Writes a single-line comment as a declaration. Does nothing by default.
	 */
	open fun comment(comment: String) { }



	/**
	 * Writes a multi-line comment as a declaration. Does nothing by default.
	 */
	open fun multilineComment(lines: List<String>) { }



	/**
	 * Writes a multi-line comment as a declaration. Does nothing by default.
	 */
	open fun multilineComment(singleLine: String) = multilineComment(listOf(singleLine))



	/**
	 * Writes a multi-line doc string as a declaration. Does nothing by default.
	 */
	open fun doc(lines: List<String>) { }



	/**
	 * Writes a single-line doc string as a declaration. Does nothing by default.
	 */
	open fun doc(singleLine: String) = doc(listOf(singleLine))



	/**
	 * Writes a single-line comment that reads 'this file has been automatically generated'. Convenience function for
	 * prefacing auto-generated files.
	 */
	open fun autogenComment() = comment("This file has been automatically generated.")


}