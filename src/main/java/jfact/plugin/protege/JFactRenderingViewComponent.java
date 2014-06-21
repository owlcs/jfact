package jfact.plugin.protege;

import java.io.Writer;

import org.protege.editor.owl.ui.view.ontology.AbstractOntologyRenderingViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

public class JFactRenderingViewComponent extends
        AbstractOntologyRenderingViewComponent {

    protected void renderOntology(OWLOntology ontology, Writer writer)
            throws Exception {
        writer.append("Not implemented for owlapi v3");
    }
}
