package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

/**
 * Axiom utilities.
 */
public class Axioms {

    @Nonnull
    private static final AxiomInterface DUMMY = new AxiomInterface() {

        @Override
        public void setUsed(boolean Used) {}

        @Override
        public void setInSS(boolean flag) {}

        @Override
        public void setInModule(boolean inModule) {}

        @Override
        public void setId(int Id) {}

        @Override
        public void setAtom(TOntologyAtom atom) {}

        @Override
        public boolean isUsed() {
            return false;
        }

        @Override
        public boolean isInSS() {
            return false;
        }

        @Override
        public boolean isInModule() {
            return false;
        }

        @Override
        public TSignature getSignature() {
            return null;
        }

        @Override
        public OWLAxiom getOWLAxiom() {
            return null;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public TOntologyAtom getAtom() {
            return null;
        }

        @Override
        public <O> O accept(DLAxiomVisitorEx<O> visitor) {
            return null;
        }

        @Override
        public void accept(DLAxiomVisitor visitor) {}
    };

    /** @return a dummy implementation of AxiomInterface */
    @Nonnull
    public static AxiomInterface dummy() {
        return DUMMY;
    }
}
