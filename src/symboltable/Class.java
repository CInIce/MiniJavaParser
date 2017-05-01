package symboltable;

import parser.MiniJavaParser;

import java.util.Enumeration;
import java.util.Hashtable;


public class Class {

	String id;
	Hashtable<Object, Method> methods;
	Hashtable<Object, Variable> globals;
	String parent;

	public Class(String id, String p) {
		this.id = id;
		parent = p;
		methods = new Hashtable<Object, Method>();
		globals = new Hashtable<Object, Variable>();
	}

	public Class() {
	}

	public String getId() {
		return id;
	}

	public boolean addMethod(String id, MiniJavaParser.TypeContext type) {
		if (containsMethod(id))
			return false;
		else {
			methods.put(id, new Method(id, type));
			return true;
		}
	}

	public Enumeration getMethods() {
		return methods.keys();
	}

	public Method getMethod(String id) {
		if (containsMethod(id))
			return (Method) methods.get(id);
		else
			return null;
	}

	public boolean addVar(String id, MiniJavaParser.TypeContext type) {
		if (globals.containsKey(id))
			return false;
		else {
			globals.put(id, new Variable(id, type));
			return true;
		}
	}

	public Variable getVar(String id) {
		if (containsVar(id))
			return (Variable) globals.get(id);
		else
			return null;
	}

	public boolean containsVar(String id) {
		return globals.containsKey(id);
	}

	public boolean containsMethod(String id) {
		return methods.containsKey(id);
	}

	public String parent() {
		return parent;
	}
} // Class