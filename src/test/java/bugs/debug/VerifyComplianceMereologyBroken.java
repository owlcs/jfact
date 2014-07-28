package bugs.debug;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import bugs.VerifyComplianceMereology;

@SuppressWarnings("javadoc")
public class VerifyComplianceMereologyBroken extends VerifyComplianceMereology {

    @Test
    public void shouldPassgetSubClassesThingtrue() {
        equal(reasoner.getSubClasses(Thing, true), Physical_Entity,
                Abstract_Entity, Occurrence, Mental_Entity);
    }

    @Test
    public void shouldPassgetSuperClassesWholetrue() {
        equal(reasoner.getSuperClasses(Whole, true), Abstract_Entity);
    }

    @Test
    public void shouldPassisEntailedSubClassOfCompositionAbstract_Entity() {
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(Composition,
                Abstract_Entity)));
    }

    @Test
    public void shouldPassisEntailedSubClassOfPairAbstract_Entity() {
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(Pair,
                Abstract_Entity)));
    }

    @Test
    public void shouldPassgetSuperClassesParttrue() {
        equal(reasoner.getSuperClasses(Part, true), Abstract_Entity);
    }
}
