package jfact.plugin.protege;

import java.io.Writer;

import org.protege.editor.owl.ui.view.ontology.AbstractOntologyRenderingViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author ignazio
 */
public class JFactRenderingViewComponent extends
        AbstractOntologyRenderingViewComponent {

    private static final long serialVersionUID = 40000L;

    @Override
    protected void renderOntology(OWLOntology ontology, Writer writer)
            throws Exception {
        writer.append("Not implemented for owlapi v3");
    }
}
