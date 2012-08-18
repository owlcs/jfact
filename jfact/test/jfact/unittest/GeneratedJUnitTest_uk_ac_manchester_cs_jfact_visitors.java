package jfact.unittest;

import static org.mockito.Mockito.*;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.visitors.*;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_visitors {
    @Test
    public void shouldTestInterfaceDLAxiomVisitor() throws Exception {
        DLAxiomVisitor testSubject0 = mock(DLAxiomVisitor.class);
        testSubject0.visit(mock(AxiomDisjointUnion.class));
        testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        testSubject0.visit(mock(AxiomDisjointConcepts.class));
        testSubject0.visit(mock(AxiomEquivalentORoles.class));
        testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        testSubject0.visit(mock(AxiomDisjointORoles.class));
        testSubject0.visit(mock(AxiomDisjointDRoles.class));
        testSubject0.visit(mock(AxiomSameIndividuals.class));
        testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        testSubject0.visit(mock(AxiomFairnessConstraint.class));
        testSubject0.visit(mock(AxiomRoleInverse.class));
        testSubject0.visit(mock(AxiomORoleSubsumption.class));
        testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        testSubject0.visit(mock(AxiomORoleDomain.class));
        testSubject0.visit(mock(AxiomDRoleDomain.class));
        testSubject0.visit(mock(AxiomORoleRange.class));
        testSubject0.visit(mock(AxiomDRoleRange.class));
        testSubject0.visit(mock(AxiomRoleTransitive.class));
        testSubject0.visit(mock(AxiomRoleReflexive.class));
        testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        testSubject0.visit(mock(AxiomRoleSymmetric.class));
        testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        testSubject0.visit(mock(AxiomORoleFunctional.class));
        testSubject0.visit(mock(AxiomDRoleFunctional.class));
        testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
        testSubject0.visitOntology(mock(Ontology.class));
    }

    @Test
    public void shouldTestDLAxiomVisitorAdapter() throws Exception {
        DLAxiomVisitorAdapter testSubject0 = new DLAxiomVisitorAdapter();
        testSubject0.visit(mock(AxiomDeclaration.class));
        testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        testSubject0.visit(mock(AxiomDisjointConcepts.class));
        testSubject0.visit(mock(AxiomEquivalentORoles.class));
        testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        testSubject0.visit(mock(AxiomDisjointORoles.class));
        testSubject0.visit(mock(AxiomDisjointDRoles.class));
        testSubject0.visit(mock(AxiomSameIndividuals.class));
        testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        testSubject0.visit(mock(AxiomFairnessConstraint.class));
        testSubject0.visit(mock(AxiomRoleInverse.class));
        testSubject0.visit(mock(AxiomORoleSubsumption.class));
        testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        testSubject0.visit(mock(AxiomORoleDomain.class));
        testSubject0.visit(mock(AxiomDRoleDomain.class));
        testSubject0.visit(mock(AxiomORoleRange.class));
        testSubject0.visit(mock(AxiomDRoleRange.class));
        testSubject0.visit(mock(AxiomRoleTransitive.class));
        testSubject0.visit(mock(AxiomRoleReflexive.class));
        testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        testSubject0.visit(mock(AxiomRoleSymmetric.class));
        testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        testSubject0.visit(mock(AxiomORoleFunctional.class));
        testSubject0.visit(mock(AxiomDRoleFunctional.class));
        testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomDisjointUnion.class));
        testSubject0.visitOntology(mock(Ontology.class));
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceDLAxiomVisitorEx() throws Exception {
        DLAxiomVisitorEx testSubject0 = mock(DLAxiomVisitorEx.class);
        Object result0 = testSubject0.visit(mock(AxiomDeclaration.class));
        Object result1 = testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        Object result2 = testSubject0.visit(mock(AxiomDisjointConcepts.class));
        Object result3 = testSubject0.visit(mock(AxiomEquivalentORoles.class));
        Object result4 = testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        Object result5 = testSubject0.visit(mock(AxiomDisjointUnion.class));
        Object result6 = testSubject0.visit(mock(AxiomDisjointORoles.class));
        Object result7 = testSubject0.visit(mock(AxiomDisjointDRoles.class));
        Object result8 = testSubject0.visit(mock(AxiomSameIndividuals.class));
        Object result9 = testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        Object result10 = testSubject0.visit(mock(AxiomFairnessConstraint.class));
        Object result11 = testSubject0.visit(mock(AxiomRoleInverse.class));
        Object result12 = testSubject0.visit(mock(AxiomORoleSubsumption.class));
        Object result13 = testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        Object result14 = testSubject0.visit(mock(AxiomORoleDomain.class));
        Object result15 = testSubject0.visit(mock(AxiomDRoleDomain.class));
        Object result16 = testSubject0.visit(mock(AxiomORoleRange.class));
        Object result17 = testSubject0.visit(mock(AxiomDRoleRange.class));
        Object result18 = testSubject0.visit(mock(AxiomRoleTransitive.class));
        Object result19 = testSubject0.visit(mock(AxiomRoleReflexive.class));
        Object result20 = testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        Object result21 = testSubject0.visit(mock(AxiomRoleSymmetric.class));
        Object result22 = testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        Object result23 = testSubject0.visit(mock(AxiomORoleFunctional.class));
        Object result24 = testSubject0.visit(mock(AxiomDRoleFunctional.class));
        Object result25 = testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        Object result26 = testSubject0.visit(mock(AxiomConceptInclusion.class));
        Object result27 = testSubject0.visit(mock(AxiomInstanceOf.class));
        Object result28 = testSubject0.visit(mock(AxiomRelatedTo.class));
        Object result29 = testSubject0.visit(mock(AxiomRelatedToNot.class));
        Object result30 = testSubject0.visit(mock(AxiomValueOf.class));
        Object result31 = testSubject0.visit(mock(AxiomValueOfNot.class));
        Object result32 = testSubject0.visitOntology(mock(Ontology.class));
    }

    @Test
    public void shouldTestDLAxiomVisitorExAdapter() throws Exception {
        DLAxiomVisitorExAdapter testSubject0 = new DLAxiomVisitorExAdapter();
        Object result0 = testSubject0.visit(mock(AxiomDeclaration.class));
        Object result1 = testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        Object result2 = testSubject0.visit(mock(AxiomDisjointConcepts.class));
        Object result3 = testSubject0.visit(mock(AxiomEquivalentORoles.class));
        Object result4 = testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        Object result5 = testSubject0.visit(mock(AxiomDisjointUnion.class));
        Object result6 = testSubject0.visit(mock(AxiomDisjointORoles.class));
        Object result7 = testSubject0.visit(mock(AxiomDisjointDRoles.class));
        Object result8 = testSubject0.visit(mock(AxiomSameIndividuals.class));
        Object result9 = testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        Object result10 = testSubject0.visit(mock(AxiomFairnessConstraint.class));
        Object result11 = testSubject0.visit(mock(AxiomRoleInverse.class));
        Object result12 = testSubject0.visit(mock(AxiomORoleSubsumption.class));
        Object result13 = testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        Object result14 = testSubject0.visit(mock(AxiomORoleDomain.class));
        Object result15 = testSubject0.visit(mock(AxiomDRoleDomain.class));
        Object result16 = testSubject0.visit(mock(AxiomDRoleRange.class));
        Object result17 = testSubject0.visit(mock(AxiomRoleTransitive.class));
        Object result18 = testSubject0.visit(mock(AxiomRoleReflexive.class));
        Object result19 = testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        Object result20 = testSubject0.visit(mock(AxiomRoleSymmetric.class));
        Object result21 = testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        Object result22 = testSubject0.visit(mock(AxiomORoleFunctional.class));
        Object result23 = testSubject0.visit(mock(AxiomDRoleFunctional.class));
        Object result24 = testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        Object result25 = testSubject0.visit(mock(AxiomConceptInclusion.class));
        Object result26 = testSubject0.visit(mock(AxiomInstanceOf.class));
        Object result27 = testSubject0.visit(mock(AxiomRelatedTo.class));
        Object result28 = testSubject0.visit(mock(AxiomRelatedToNot.class));
        Object result29 = testSubject0.visit(mock(AxiomValueOf.class));
        Object result30 = testSubject0.visit(mock(AxiomValueOfNot.class));
        Object result31 = testSubject0.visit(mock(AxiomORoleRange.class));
        Object result32 = testSubject0.visitOntology(mock(Ontology.class));
        String result33 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceDLExpressionVisitor() throws Exception {
        DLExpressionVisitor testSubject0 = mock(DLExpressionVisitor.class);
        testSubject0.visit(mock(DataOneOf.class));
        testSubject0.visit(mock(ConceptBottom.class));
        testSubject0.visit(mock(ConceptName.class));
        testSubject0.visit(mock(ConceptNot.class));
        testSubject0.visit(mock(ConceptAnd.class));
        testSubject0.visit(mock(ConceptOr.class));
        testSubject0.visit(mock(ConceptOneOf.class));
        testSubject0.visit(mock(ConceptObjectSelf.class));
        testSubject0.visit(mock(ConceptObjectValue.class));
        testSubject0.visit(mock(ConceptObjectExists.class));
        testSubject0.visit(mock(ConceptObjectForall.class));
        testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        testSubject0.visit(mock(ConceptDataValue.class));
        testSubject0.visit(mock(ConceptDataExists.class));
        testSubject0.visit(mock(ConceptDataForall.class));
        testSubject0.visit(mock(ConceptDataMinCardinality.class));
        testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        testSubject0.visit(mock(ConceptDataExactCardinality.class));
        testSubject0.visit(mock(IndividualName.class));
        testSubject0.visit(mock(ObjectRoleTop.class));
        testSubject0.visit(mock(ObjectRoleBottom.class));
        testSubject0.visit(mock(ObjectRoleName.class));
        testSubject0.visit(mock(ObjectRoleInverse.class));
        testSubject0.visit(mock(ObjectRoleChain.class));
        testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        testSubject0.visit(mock(DataRoleTop.class));
        testSubject0.visit(mock(DataRoleBottom.class));
        testSubject0.visit(mock(DataRoleName.class));
        testSubject0.visit(mock(DataTop.class));
        testSubject0.visit(mock(DataBottom.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(Datatype.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(ConceptTop.class));
    }

    @Test
    public void shouldTestDLExpressionVisitorAdapter() throws Exception {
        DLExpressionVisitorAdapter testSubject0 = null;
        testSubject0.visit(mock(ConceptTop.class));
        testSubject0.visit(mock(ConceptBottom.class));
        testSubject0.visit(mock(ConceptName.class));
        testSubject0.visit(mock(ConceptNot.class));
        testSubject0.visit(mock(ConceptAnd.class));
        testSubject0.visit(mock(ConceptOr.class));
        testSubject0.visit(mock(ConceptOneOf.class));
        testSubject0.visit(mock(ConceptObjectSelf.class));
        testSubject0.visit(mock(ConceptObjectValue.class));
        testSubject0.visit(mock(ConceptObjectExists.class));
        testSubject0.visit(mock(ConceptObjectForall.class));
        testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        testSubject0.visit(mock(ConceptDataValue.class));
        testSubject0.visit(mock(ConceptDataExists.class));
        testSubject0.visit(mock(ConceptDataForall.class));
        testSubject0.visit(mock(ConceptDataMinCardinality.class));
        testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        testSubject0.visit(mock(ConceptDataExactCardinality.class));
        testSubject0.visit(mock(IndividualName.class));
        testSubject0.visit(mock(ObjectRoleTop.class));
        testSubject0.visit(mock(ObjectRoleBottom.class));
        testSubject0.visit(mock(ObjectRoleName.class));
        testSubject0.visit(mock(ObjectRoleInverse.class));
        testSubject0.visit(mock(ObjectRoleChain.class));
        testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        testSubject0.visit(mock(DataRoleTop.class));
        testSubject0.visit(mock(DataRoleBottom.class));
        testSubject0.visit(mock(DataRoleName.class));
        testSubject0.visit(mock(DataTop.class));
        testSubject0.visit(mock(DataBottom.class));
        testSubject0.visit(mock(Datatype.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(DataOneOf.class));
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceDLExpressionVisitorEx() throws Exception {
        DLExpressionVisitorEx testSubject0 = mock(DLExpressionVisitorEx.class);
        Object result0 = testSubject0.visit(mock(DataOneOf.class));
        Object result1 = testSubject0.visit(mock(ConceptBottom.class));
        Object result2 = testSubject0.visit(mock(ConceptName.class));
        Object result3 = testSubject0.visit(mock(ConceptNot.class));
        Object result4 = testSubject0.visit(mock(ConceptAnd.class));
        Object result5 = testSubject0.visit(mock(ConceptOr.class));
        Object result6 = testSubject0.visit(mock(ConceptOneOf.class));
        Object result7 = testSubject0.visit(mock(ConceptObjectSelf.class));
        Object result8 = testSubject0.visit(mock(ConceptObjectValue.class));
        Object result9 = testSubject0.visit(mock(ConceptObjectExists.class));
        Object result10 = testSubject0.visit(mock(ConceptObjectForall.class));
        Object result11 = testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        Object result12 = testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        Object result13 = testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        Object result14 = testSubject0.visit(mock(ConceptDataValue.class));
        Object result15 = testSubject0.visit(mock(ConceptDataExists.class));
        Object result16 = testSubject0.visit(mock(ConceptDataForall.class));
        Object result17 = testSubject0.visit(mock(ConceptDataMinCardinality.class));
        Object result18 = testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        Object result19 = testSubject0.visit(mock(ConceptDataExactCardinality.class));
        Object result20 = testSubject0.visit(mock(IndividualName.class));
        Object result21 = testSubject0.visit(mock(ObjectRoleTop.class));
        Object result22 = testSubject0.visit(mock(ObjectRoleBottom.class));
        Object result23 = testSubject0.visit(mock(ObjectRoleName.class));
        Object result24 = testSubject0.visit(mock(ObjectRoleInverse.class));
        Object result25 = testSubject0.visit(mock(ObjectRoleChain.class));
        Object result26 = testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        Object result27 = testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        Object result28 = testSubject0.visit(mock(DataRoleTop.class));
        Object result29 = testSubject0.visit(mock(DataRoleBottom.class));
        Object result30 = testSubject0.visit(mock(DataRoleName.class));
        Object result31 = testSubject0.visit(mock(DataTop.class));
        Object result32 = testSubject0.visit(mock(DataBottom.class));
        Object result33 = testSubject0.visit(mock(Literal.class));
        Object result34 = testSubject0.visit(mock(Datatype.class));
        Object result35 = testSubject0.visit(mock(DataNot.class));
        Object result36 = testSubject0.visit(mock(DataAnd.class));
        Object result37 = testSubject0.visit(mock(DataOr.class));
        Object result38 = testSubject0.visit(mock(ConceptTop.class));
    }

    @Test
    public void shouldTestDLExpressionVisitorExAdapter() throws Exception {
        DLExpressionVisitorExAdapter testSubject0 = null;
        Object result0 = testSubject0.visit(mock(ConceptTop.class));
        Object result1 = testSubject0.visit(mock(ConceptBottom.class));
        Object result2 = testSubject0.visit(mock(ConceptName.class));
        Object result3 = testSubject0.visit(mock(ConceptNot.class));
        Object result4 = testSubject0.visit(mock(ConceptAnd.class));
        Object result5 = testSubject0.visit(mock(ConceptOr.class));
        Object result6 = testSubject0.visit(mock(ConceptOneOf.class));
        Object result7 = testSubject0.visit(mock(ConceptObjectSelf.class));
        Object result8 = testSubject0.visit(mock(ConceptObjectValue.class));
        Object result9 = testSubject0.visit(mock(ConceptObjectExists.class));
        Object result10 = testSubject0.visit(mock(ConceptObjectForall.class));
        Object result11 = testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        Object result12 = testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        Object result13 = testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        Object result14 = testSubject0.visit(mock(ConceptDataValue.class));
        Object result15 = testSubject0.visit(mock(ConceptDataExists.class));
        Object result16 = testSubject0.visit(mock(ConceptDataForall.class));
        Object result17 = testSubject0.visit(mock(ConceptDataMinCardinality.class));
        Object result18 = testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        Object result19 = testSubject0.visit(mock(ConceptDataExactCardinality.class));
        Object result20 = testSubject0.visit(mock(IndividualName.class));
        Object result21 = testSubject0.visit(mock(ObjectRoleTop.class));
        Object result22 = testSubject0.visit(mock(ObjectRoleBottom.class));
        Object result23 = testSubject0.visit(mock(ObjectRoleName.class));
        Object result24 = testSubject0.visit(mock(ObjectRoleInverse.class));
        Object result25 = testSubject0.visit(mock(ObjectRoleChain.class));
        Object result26 = testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        Object result27 = testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        Object result28 = testSubject0.visit(mock(DataRoleTop.class));
        Object result29 = testSubject0.visit(mock(DataRoleBottom.class));
        Object result30 = testSubject0.visit(mock(DataRoleName.class));
        Object result31 = testSubject0.visit(mock(DataTop.class));
        Object result32 = testSubject0.visit(mock(DataBottom.class));
        Object result33 = testSubject0.visit(mock(Datatype.class));
        Object result34 = testSubject0.visit(mock(Literal.class));
        Object result35 = testSubject0.visit(mock(DataNot.class));
        Object result36 = testSubject0.visit(mock(DataAnd.class));
        Object result37 = testSubject0.visit(mock(DataOr.class));
        Object result38 = testSubject0.visit(mock(DataOneOf.class));
        String result39 = testSubject0.toString();
    }
}
