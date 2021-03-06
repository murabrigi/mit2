package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Event;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		int c=0;
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				c+=1;
				System.out.println(state.getName());
				for(Transition t : state.getOutgoingTransitions()) {
					State target=(State)t.getTarget();
					System.out.println(state.getName()+" -> "+target.getName());
				}
				if(state.getOutgoingTransitions().size()==0) {
					System.out.println("csapda: "+state.getName());
				}
				if(state.getName()=="") {
				    System.out.println("Ennek az állapotnak nincs neve. Javasolt név: Állapot"+c);
				}
			}
		}
		//4. feladat Összes belső változó és bemenő események kiírása
		for (Scope scope: s.getScopes()) {
			
			System.out.println("Változók: ");
			for(Property variable: scope.getVariables()) {
				System.out.println(variable.getName());
			}
			System.out.println("Események: ");
			for(Event event: scope.getEvents()) {
				System.out.println(event.getName());
			}
		}
		e_4_5(s);

		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static void e_4_5(Statechart s) {
		//belső változók és bemenő események kiolvasása - listába?
		ArrayList<String> variables = new ArrayList<String>();
		ArrayList<String> events = new ArrayList<String>();
		
		for (Scope scope: s.getScopes()) {
			
			for(Property variable: scope.getVariables()) {
				variables.add(variable.getName());
			}
			for(Event event: scope.getEvents()) {
				events.add(event.getName());
			}
		}
		//eleje
		System.out.println("public static void e_3_5(ExampleStatemachine s) {\r\n"+
		"	\r\n"+
		"		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));"+"\n"+
		"		String str;"+"\n"+
		"		try {"+"\n"+
		"		while((str=reader.readLine())!= null) {"+"\n"+
		"		switch(str) {"+"\n"
				);
		//case
		for(String event: events) {
			String capitalizedName = event.substring(0,1).toUpperCase()+event.substring(1);
			System.out.println("			case "+"\""+event+"\""+":"+"\n"+
								"				s.raise"+capitalizedName+"();"+"\n"+
								"				s.runCycle();"+"\n"+
								"				break;");
		}
		System.out.println("			case \"exit\":\r\n" + 
				"				System.exit(0);\r\n" + 
				"				break;\r\n" + 
				"			}");
		//változó érték
		for(String variable: variables) {
			String capitalizedName = variable.substring(0,1).toUpperCase()+variable.substring(1);
			System.out.println("		System.out.println(s.getSCInterface().get"+capitalizedName+"());");
		}
		System.out.println("		}\r\n" + 
				"		}\r\n" + 
				"		catch(Exception e){}\r\n" + 
				"	}");
	}
}
