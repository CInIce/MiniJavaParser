package symboltable;

import parser.MiniJavaParser;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;



public class Method {

	String id;
	MiniJavaParser.TypeContext type;
	Vector<Variable> params;
	Map<Object, Variable> vars;

	public Method(String id, MiniJavaParser.TypeContext type) {
		this.id = id;
		this.type = type;
		vars = new HashMap<>();
		params = new Vector<>();
	}

	public String getId() {
		return id;
	}

	public MiniJavaParser.TypeContext type() {
		return type;
	}

	public boolean addParam(String id, MiniJavaParser.TypeContext type) {
		if (containsParam(id))
			return false;
		else {
			params.addElement(new Variable(id, type));
			return true;
		}
	}

	public Enumeration getParams() {
		return params.elements();
	}

	public Variable getParamAt(int i) {
		if (i < params.size())
			return (Variable) params.elementAt(i);
		else
			return null;
	}

	public boolean addVar(String id, MiniJavaParser.TypeContext type) {
		if (vars.containsKey(id))
			return false;
		else {
			vars.put(id, new Variable(id, type));
			return true;
		}
	}

	public boolean containsVar(String id) {
		return vars.containsKey(id);
	}

	public boolean containsParam(String id) {
		for (int i = 0; i < params.size(); i++)
			if (((Variable) params.elementAt(i)).id.equals(id))
				return true;
		return false;
	}

	public Variable getVar(String id) {
		if (containsVar(id))
			return (Variable) vars.get(id);
		else
			return null;
	}

	public Variable getParam(String id) {

		for (int i = 0; i < params.size(); i++)
			if (((Variable) params.elementAt(i)).id.equals(id))
				return (Variable) (params.elementAt(i));

		return null;
	}

} // Method