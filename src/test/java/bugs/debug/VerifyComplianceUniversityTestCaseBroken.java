package bugs.debug;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClassExpression;

import bugs.VerifyComplianceUniversityTestCase;

@SuppressWarnings("javadoc")
public class VerifyComplianceUniversityTestCaseBroken extends
        VerifyComplianceUniversityTestCase {

    @Test
    public void shouldPassgetSubClassesowlThingtrue() {
        equal(reasoner.getSubClasses(owlThing, true), PhoneBook, ResearchArea,
                Department, Person, Library, Schedule, Course);
    }

    @Test
    public void shouldPassgetSuperClassesProfessortrue() {
        equal(reasoner.getSuperClasses(Professor, true), TeachingFaculty);
    }

    @Test
    public void shouldPassgetSuperClassesProfessorInHCIorAItrue() {
        equal(reasoner.getSuperClasses(ProfessorInHCIorAI, true), Professor);
    }

    @Test
    public void shouldPassgetDataPropertyDomainshasTenurefalse() {
        equal(reasoner.getDataPropertyDomains(hasTenure, false), owlThing,
                Faculty, TeachingFaculty, Person, Professor);
    }

    @Test
    public void shouldPassProfessorSubClassOfhasTenureTrue() {
        OWLClassExpression c = df.getOWLDataSomeValuesFrom(hasTenure,
                df.getOWLDataOneOf(df.getOWLLiteral(true)));
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(Professor, c)));
        equal(reasoner.getSuperClasses(c, false), owlThing, Faculty,
                TeachingFaculty, Person, Professor);
    }

    @Test
    public void shouldPassgetDataPropertyDomainshasTenuretrue() {
        equal(reasoner.getDataPropertyDomains(hasTenure, true), Professor);
    }
}
