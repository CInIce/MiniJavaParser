package symboltable;


import parser.MiniJavaParser;

public class Variable {

	String id;
	MiniJavaParser.TypeContext type;

	public Variable(String id, MiniJavaParser.TypeContext type) {
		this.id = id;
		this.type = type;
	}

	public String id() {
		return id;
	}

	public MiniJavaParser.TypeContext type() {
		return type;
	}

} // Variable