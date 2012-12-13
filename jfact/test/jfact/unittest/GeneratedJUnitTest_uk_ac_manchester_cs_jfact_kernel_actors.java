package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import uk.ac.manchester.cs.jfact.kernel.actors.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel_actors {
    @Test
    public void shouldTestInterfaceActor() throws Exception {
        Actor testSubject0 = mock(Actor.class);
        boolean result0 = testSubject0.apply(mock(TaxonomyVertex.class));
    }

    @Test
    public void shouldTestAddRoleActor() throws Exception {
        AddRoleActor testSubject0 = new AddRoleActor(mock(List.class));
        boolean result0 = testSubject0.apply(mock(TaxonomyVertex.class));
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestClassPolicy() throws Exception {
        ClassPolicy testSubject0 = new ClassPolicy();
        Expression result0 = testSubject0.buildTree(mock(ExpressionManager.class),
                mock(ClassifiableEntry.class));
        boolean result1 = testSubject0.applicable(mock(ClassifiableEntry.class));
        boolean result2 = testSubject0.needPlain();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestDataPropertyPolicy() throws Exception {
        DataPropertyPolicy testSubject0 = new DataPropertyPolicy();
        Expression result0 = testSubject0.buildTree(mock(ExpressionManager.class),
                mock(ClassifiableEntry.class));
        boolean result1 = testSubject0.applicable(mock(ClassifiableEntry.class));
        boolean result2 = testSubject0.needPlain();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestIndividualPolicy() throws Exception {
        IndividualPolicy testSubject0 = new IndividualPolicy(true);
        Expression result0 = testSubject0.buildTree(mock(ExpressionManager.class),
                mock(ClassifiableEntry.class));
        boolean result1 = testSubject0.applicable(mock(ClassifiableEntry.class));
        boolean result2 = testSubject0.needPlain();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestObjectPropertyPolicy() throws Exception {
        ObjectPropertyPolicy testSubject0 = new ObjectPropertyPolicy();
        Expression result0 = testSubject0.buildTree(mock(ExpressionManager.class),
                mock(ClassifiableEntry.class));
        boolean result1 = testSubject0.applicable(mock(ClassifiableEntry.class));
        boolean result2 = testSubject0.needPlain();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfacePolicy() throws Exception {
        Policy testSubject0 = mock(Policy.class);
        Expression result0 = testSubject0.buildTree(mock(ExpressionManager.class),
                mock(ClassifiableEntry.class));
        boolean result1 = testSubject0.applicable(mock(ClassifiableEntry.class));
        boolean result2 = testSubject0.needPlain();
    }

    @Test
    public void shouldTestRIActor() throws Exception {
        RIActor testSubject0 = new RIActor();
        boolean result0 = testSubject0.apply(mock(TaxonomyVertex.class));
        List result1 = testSubject0.getAcc();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestSupConceptActor() throws Exception {
        SupConceptActor testSubject0 = new SupConceptActor(mock(ClassifiableEntry.class));
        boolean result0 = testSubject0.apply(mock(TaxonomyVertex.class));
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestTaxonomyActor() throws Exception {
        TaxonomyActor testSubject0 = new TaxonomyActor(mock(ExpressionManager.class),
                mock(Policy.class));
        boolean result0 = testSubject0.apply(mock(TaxonomyVertex.class));
        Collection result1 = testSubject0.getClassSynonyms();
        Collection result2 = testSubject0.getClassElements();
        Collection result3 = testSubject0.getObjectPropertyElements();
        Collection result4 = testSubject0.getObjectPropertySynonyms();
        Collection result5 = testSubject0.getDataPropertyElements();
        Collection result6 = testSubject0.getDataPropertySynonyms();
        Collection result7 = testSubject0.getPlainIndividualElements();
        Collection result8 = testSubject0.getIndividualSynonyms();
        List result9 = testSubject0.tryEntry(mock(ClassifiableEntry.class));
        Collection result10 = testSubject0.getPlainClassElements();
        Collection result11 = testSubject0.getIndividualElements();
        String result12 = testSubject0.toString();
    }
}
