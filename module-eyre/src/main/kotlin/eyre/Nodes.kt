package eyre



sealed interface AstNode

class ImportNode(val parts: InternArray) : AstNode

class IntNode(val value: Long) : AstNode

class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class StringNode(val value: Intern) : AstNode

class DotNode(val left: AstNode, val right: SymNode) : AstNode

class SymNode(val name: Intern, var symbol: Symbol? = null) : AstNode

class ConstNode(val symbol: Symbol, val value: AstNode) : AstNode



/*
Scope
 */



class NamespaceNode(val symbol: Namespace) : AstNode

object ScopeEndNode : AstNode



class FileNode(
	val file     : SrcFile,
	val nodes    : List<AstNode>,
	val symbols  : SymbolTable
) : AstNode



/*
Instruction
 */



class RegNode(val value: Register) : AstNode

class ImmNode(val value: AstNode) : AstNode

class MemNode(val width: Width?, val value: AstNode) : AstNode

class InsNode(
	val mnemonic : Mnemonic,
	val prefix   : Prefix?,
	val shortImm : Boolean,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode