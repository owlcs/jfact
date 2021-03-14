package jfact.plugin.protege;

import java.io.Writer;

import javax.annotation.Nullable;

import org.protege.editor.owl.ui.view.ontology.AbstractOntologyRenderingViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author ignazio
 */
public class JFactRenderingViewComponent extends AbstractOntologyRenderingViewComponent {

    @Override
    protected void renderOntology(@Nullable OWLOntology ontology, @Nullable Writer writer)
        throws Exception {
        if (writer != null) {
            writer.append("Not implemented for owlapi v3");
        }
    }
}
