package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_restrict_allvalues_inst_obj {
    @Test
    public void testrdfbased_sem_restrict_allvalues_inst_obj() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                //end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#x\"/>\n"
                + "  </ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                //added
                + "<rdfs:subClassOf><owl:Restriction>"
                // end added
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/>\n"
                + "    <owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                //added
                + "</owl:Restriction></rdfs:subClassOf>"
                //end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                //end added
                + "  <ex:c rdf:about=\"http://www.example.org#x\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_inst_obj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the universal restriction on property p and class c, then for any triple w p x follows that x is an instance of c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
        //		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        //		Factory f = new Factory(m.getOWLDataFactory());
        //		OWLDataFactory df = m.getOWLDataFactory();
        //		OWLOntology o1 = m.createOntology();
        //		OWLClass z = f.getClass("z");
        //		OWLClass c = f.getClass("c");
        //		OWLNamedIndividual w = f.getInd("w");
        //		OWLNamedIndividual x = f.getInd("x");
        //		OWLObjectProperty p = f.getOProperty("p");
        //		m.addAxiom(o1, df.getOWLDeclarationAxiom(z));
        //		m.addAxiom(o1, df.getOWLDeclarationAxiom(c));
        //		m.addAxiom(o1, df.getOWLDeclarationAxiom(p));
        //		m.addAxiom(o1, df.getOWLDeclarationAxiom(w));
        //		m.addAxiom(o1, df.getOWLDeclarationAxiom(x));
        //		m.addAxiom(o1, df.getOWLClassAssertionAxiom(z, w));
        //		m.addAxiom(o1, df.getOWLObjectPropertyAssertionAxiom(p, w, x));
        //		OWLClassExpression ex = df.getOWLObjectAllValuesFrom(p, c);
        //		m.addAxiom(o1, df.getOWLSubClassOfAxiom(z, ex));
        //		OWLReasoner reasoner=Factory.factory().createReasoner(o1);
        //		System.out
        //				.println(reasoner.isEntailed(df.getOWLClassAssertionAxiom(c, x)));
        //		OWLOntology o2 = m.createOntology();
        //		m.addAxiom(o2, df.getOWLDeclarationAxiom(z));
        //		m.addAxiom(o2, df.getOWLDeclarationAxiom(c));
        //		m.addAxiom(o2, df.getOWLDeclarationAxiom(p));
        //		m.addAxiom(o2, df.getOWLDeclarationAxiom(w));
        //		m.addAxiom(o2, df.getOWLDeclarationAxiom(x));
        //
        //		m.addAxiom(o2, df.getOWLClassAssertionAxiom(c, x));
        //		StringDocumentTarget t1=new StringDocumentTarget();
        //		StringDocumentTarget t2=new StringDocumentTarget();
        //		m.saveOntology(o1, t1);
        //		m.saveOntology(o2, t2);
        //		JUnitRunner r = new JUnitRunner("","", id, tc, d);
        //		r.setReasonerFactory(Factory.factory());
        //		r.run(o1, o2);
    }
}
