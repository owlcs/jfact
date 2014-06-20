package jfact.plugin.protege;

import org.protege.editor.owl.ui.view.ontology.AbstractOntologyRenderingViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.Writer;

public class JFactRenderingViewComponent extends
		AbstractOntologyRenderingViewComponent {
	protected void renderOntology(OWLOntology ontology, Writer writer)
			throws Exception {
		writer.append("Not implemented for owlapi v3");
	}
}
