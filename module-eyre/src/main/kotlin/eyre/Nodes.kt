package eyre



interface AstNode



class FileImportNode(val components: List<Intern>) : AstNode

class DllImportNode(val dll: Intern, val name: Intern) : AstNode

class NamespaceNode(val symbol: NamespaceSymbol, val nodes: List<AstNode>, val isSingleLine: Boolean) : AstNode