package bugs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

class VerifyComplianceUniversityTestCase extends VerifyComplianceBase {

    @Nonnull
    String input = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
        + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
        + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
        + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
        + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + '\n' + '\n'
        + "Ontology(<urn:university>\n" + '\n' + "Declaration(Class(<urn:university#AIStudent>))\n"
        + "Declaration(Class(<urn:university#AI_Dept>))\n"
        + "Declaration(Class(<urn:university#AssistantProfessor>))\n"
        + "Declaration(Class(<urn:university#CS_Course>))\n"
        + "Declaration(Class(<urn:university#CS_Department>))\n"
        + "Declaration(Class(<urn:university#CS_Library>))\n"
        + "Declaration(Class(<urn:university#CS_Student>))\n"
        + "Declaration(Class(<urn:university#CS_StudentTakingCourses>))\n"
        + "Declaration(Class(<urn:university#Course>))\n"
        + "Declaration(Class(<urn:university#Department>))\n"
        + "Declaration(Class(<urn:university#EE_Course>))\n"
        + "Declaration(Class(<urn:university#EE_Department>))\n"
        + "Declaration(Class(<urn:university#EE_Library>))\n"
        + "Declaration(Class(<urn:university#Faculty>))\n"
        + "Declaration(Class(<urn:university#FacultyPhoneBook>))\n"
        + "Declaration(Class(<urn:university#HCIStudent>))\n"
        + "Declaration(Class(<urn:university#Lecturer>))\n"
        + "Declaration(Class(<urn:university#LecturerTaking4Courses>))\n"
        + "Declaration(Class(<urn:university#Library>))\n"
        + "Declaration(Class(<urn:university#Person>))\n"
        + "Declaration(Class(<urn:university#PhoneBook>))\n"
        + "Declaration(Class(<urn:university#Professor>))\n"
        + "Declaration(Class(<urn:university#ProfessorInHCIorAI>))\n"
        + "Declaration(Class(<urn:university#ResearchArea>))\n"
        + "Declaration(Class(<urn:university#Schedule>))\n"
        + "Declaration(Class(<urn:university#Student>))\n"
        + "Declaration(Class(<urn:university#TeachingFaculty>))\n"
        + "Declaration(Class(<urn:university#UniversityPhoneBook>))\n"
        + "Declaration(ObjectProperty(<urn:university#advisorOf>))\n"
        + "Declaration(ObjectProperty(<urn:university#affiliatedWith>))\n"
        + "Declaration(ObjectProperty(<urn:university#hasAdvisor>))\n"
        + "Declaration(ObjectProperty(<urn:university#hasDegree>))\n"
        + "Declaration(ObjectProperty(<urn:university#hasResearchArea>))\n"
        + "Declaration(ObjectProperty(<urn:university#memberOf>))\n"
        + "Declaration(ObjectProperty(<urn:university#offeredIn>))\n"
        + "Declaration(ObjectProperty(<urn:university#offersCourse>))\n"
        + "Declaration(ObjectProperty(<urn:university#takesCourse>))\n"
        + "Declaration(ObjectProperty(<urn:university#worksFor>))\n"
        + "Declaration(DataProperty(<urn:university#hasTenure>))\n"
        + "Declaration(NamedIndividual(<urn:university#AI>))\n"
        + "Declaration(NamedIndividual(<urn:university#Graphics>))\n"
        + "Declaration(NamedIndividual(<urn:university#HCI>))\n"
        + "Declaration(NamedIndividual(<urn:university#Network>))\n"
        + "SubClassOf(<urn:university#AIStudent> <urn:university#CS_Student>)\n"
        + "SubClassOf(<urn:university#AIStudent> ObjectSomeValuesFrom(<urn:university#hasAdvisor> <urn:university#ProfessorInHCIorAI>))\n"
        + "DisjointClasses(<urn:university#AIStudent> <urn:university#HCIStudent>)\n"
        + "EquivalentClasses(<urn:university#AI_Dept> ObjectIntersectionOf(ObjectHasValue(<urn:university#hasResearchArea> <urn:university#AI>) <urn:university#CS_Department>))\n"
        + "EquivalentClasses(<urn:university#AssistantProfessor> ObjectIntersectionOf(DataHasValue(<urn:university#hasTenure> \"false\"^^xsd:boolean) <urn:university#TeachingFaculty>))\n"
        + "SubClassOf(<urn:university#AssistantProfessor> <urn:university#TeachingFaculty>)\n"
        + "DisjointClasses(<urn:university#AssistantProfessor> <urn:university#Lecturer>)\n"
        + "DisjointClasses(<urn:university#AssistantProfessor> <urn:university#Professor>)\n"
        + "SubClassOf(<urn:university#CS_Course> <urn:university#Course>)\n"
        + "SubClassOf(<urn:university#CS_Course> ObjectSomeValuesFrom(<urn:university#offeredIn> <urn:university#CS_Department>))\n"
        + "SubClassOf(<urn:university#CS_Department> <urn:university#Department>)\n"
        + "SubClassOf(<urn:university#CS_Department> ObjectSomeValuesFrom(<urn:university#affiliatedWith> <urn:university#CS_Library>))\n"
        + "DisjointClasses(<urn:university#CS_Department> <urn:university#EE_Department>)\n"
        + "SubClassOf(<urn:university#CS_Library> <urn:university#Library>)\n"
        + "SubClassOf(<urn:university#CS_Library> ObjectSomeValuesFrom(<urn:university#affiliatedWith> <urn:university#EE_Library>))\n"
        + "SubClassOf(<urn:university#CS_Student> <urn:university#Student>)\n"
        + "SubClassOf(<urn:university#CS_Student> ObjectAllValuesFrom(<urn:university#takesCourse> <urn:university#CS_Course>))\n"
        + "SubClassOf(<urn:university#CS_StudentTakingCourses> <urn:university#CS_Student>)\n"
        + "SubClassOf(<urn:university#CS_StudentTakingCourses> ObjectMinCardinality(1 <urn:university#takesCourse>))\n"
        + "SubClassOf(<urn:university#EE_Course> <urn:university#Course>)\n"
        + "SubClassOf(<urn:university#EE_Course> ObjectSomeValuesFrom(<urn:university#offeredIn> <urn:university#EE_Department>))\n"
        + "EquivalentClasses(<urn:university#EE_Department> ObjectSomeValuesFrom(<urn:university#affiliatedWith> <urn:university#EE_Library>))\n"
        + "SubClassOf(<urn:university#EE_Department> <urn:university#Department>)\n"
        + "SubClassOf(<urn:university#EE_Library> <urn:university#Library>)\n"
        + "SubClassOf(<urn:university#Faculty> <urn:university#Person>)\n"
        + "SubClassOf(<urn:university#FacultyPhoneBook> <urn:university#PhoneBook>)\n"
        + "SubClassOf(<urn:university#HCIStudent> <urn:university#CS_Student>)\n"
        + "SubClassOf(<urn:university#HCIStudent> ObjectSomeValuesFrom(<urn:university#hasAdvisor> <urn:university#ProfessorInHCIorAI>))\n"
        + "EquivalentClasses(<urn:university#Lecturer> ObjectIntersectionOf(DataHasValue(<urn:university#hasTenure> \"false\"^^xsd:boolean) <urn:university#TeachingFaculty>))\n"
        + "SubClassOf(<urn:university#Lecturer> <urn:university#TeachingFaculty>)\n"
        + "DisjointClasses(<urn:university#Lecturer> <urn:university#Professor>)\n"
        + "SubClassOf(<urn:university#LecturerTaking4Courses> <urn:university#Lecturer>)\n"
        + "SubClassOf(<urn:university#LecturerTaking4Courses> ObjectExactCardinality(4 <urn:university#takesCourse>))\n"
        + "EquivalentClasses(<urn:university#Professor> ObjectIntersectionOf(DataHasValue(<urn:university#hasTenure> \"true\"^^xsd:boolean) <urn:university#TeachingFaculty>))\n"
        + "SubClassOf(<urn:university#Professor> <urn:university#TeachingFaculty>)\n"
        + "SubClassOf(<urn:university#ProfessorInHCIorAI> <urn:university#Professor>)\n"
        + "SubClassOf(<urn:university#ProfessorInHCIorAI> ObjectAllValuesFrom(<urn:university#advisorOf> <urn:university#AIStudent>))\n"
        + "SubClassOf(<urn:university#ProfessorInHCIorAI> ObjectAllValuesFrom(<urn:university#advisorOf> <urn:university#HCIStudent>))\n"
        + "SubClassOf(<urn:university#Student> <urn:university#Person>)\n"
        + "SubClassOf(<urn:university#Student> ObjectMinCardinality(1 <urn:university#hasDegree>))\n"
        + "SubClassOf(<urn:university#TeachingFaculty> <urn:university#Faculty>)\n"
        + "SubClassOf(<urn:university#TeachingFaculty> ObjectSomeValuesFrom(<urn:university#hasResearchArea> <urn:university#ResearchArea>))\n"
        + "SubClassOf(<urn:university#TeachingFaculty> ObjectMaxCardinality(3 <urn:university#takesCourse>))\n"
        + "SubClassOf(<urn:university#UniversityPhoneBook> <urn:university#PhoneBook>)\n"
        + "InverseObjectProperties(<urn:university#advisorOf> <urn:university#hasAdvisor>)\n"
        + "TransitiveObjectProperty(<urn:university#affiliatedWith>)\n"
        + "DataPropertyDomain(<urn:university#hasTenure> <urn:university#TeachingFaculty>)\n"
        + "DataPropertyRange(<urn:university#hasTenure> xsd:boolean)\n"
        + "ClassAssertion(owl:Thing <urn:university#AI>)\n"
        + "ClassAssertion(owl:Thing <urn:university#Graphics>)\n"
        + "ClassAssertion(owl:Thing <urn:university#HCI>)\n"
        + "ClassAssertion(owl:Thing <urn:university#Network>))";

    @Override
    protected OWLOntology load(String in) throws OWLOntologyCreationException {
        return loadFromString(input);
    }

    @Override
    @Nonnull
    protected String input() {
        return "/AF_university.owl.xml";
    }

//@formatter:off
  @Nonnull  protected OWLClass assistantProfessor = C("urn:university#AssistantProfessor");
  @Nonnull  protected OWLClass aiStudent = C("urn:university#AIStudent");
  @Nonnull  protected OWLClass aiDept = C("urn:university#AI_Dept");
  @Nonnull  protected OWLClass CS_Department = C("urn:university#CS_Department");
  @Nonnull  protected OWLClass HCIStudent = C("urn:university#HCIStudent");
  @Nonnull  protected OWLClass CS_StudentTakingCourses = C("urn:university#CS_StudentTakingCourses");
  @Nonnull  protected OWLClass LecturerTaking4Courses = C("urn:university#LecturerTaking4Courses");
  @Nonnull  protected OWLClass Lecturer = C("urn:university#Lecturer");
  @Nonnull  protected OWLClass CS_Course = C("urn:university#CS_Course");
  @Nonnull  protected OWLClass Professor = C("urn:university#Professor");
  @Nonnull  protected OWLClass ProfessorInHCIorAI = C("urn:university#ProfessorInHCIorAI");
  @Nonnull  protected OWLClass UniversityPhoneBook = C("urn:university#UniversityPhoneBook");
  @Nonnull  protected OWLClass ResearchArea = C("urn:university#ResearchArea");
  @Nonnull  protected OWLClass TeachingFaculty = C("urn:university#TeachingFaculty");
  @Nonnull  protected OWLClass Department = C("urn:university#Department");
  @Nonnull  protected OWLClass Person = C("urn:university#Person");
  @Nonnull  protected OWLClass CS_Student = C("urn:university#CS_Student");
  @Nonnull  protected OWLClass Student = C("urn:university#Student");
  @Nonnull  protected OWLClass PhoneBook = C("urn:university#PhoneBook");
  @Nonnull  protected OWLClass EE_Course = C("urn:university#EE_Course");
  @Nonnull  protected OWLClass Faculty = C("urn:university#Faculty");
  @Nonnull  protected OWLClass CS_Library = C("urn:university#CS_Library");
  @Nonnull  protected OWLClass EE_Department = C("urn:university#EE_Department");
  @Nonnull  protected OWLClass FacultyPhoneBook = C("urn:university#FacultyPhoneBook");
  @Nonnull  protected OWLClass Schedule = C("urn:university#Schedule");
  @Nonnull  protected OWLClass Library = C("urn:university#Library");
  @Nonnull  protected OWLClass Course = C("urn:university#Course");
  @Nonnull  protected OWLClass EE_Library = C("urn:university#EE_Library");
  @Nonnull  protected OWLDataProperty hasTenure = df.getOWLDataProperty(IRI.create("urn:university#hasTenure"));
//@formatter:on
    @Test
    void shouldPassgetBottomClass() {
        equal(reasoner.getBottomClassNode(), assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course);
    }

    @Test
    void shouldPassgetUnsatisfiableClasses() {
        equal(reasoner.getUnsatisfiableClasses(), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassisSatisfiableProfessor() {
        assertTrue(reasoner.isSatisfiable(Professor));
    }

    @Test
    void shouldPassisSatisfiableProfessorInHCIorAI() {
        assertTrue(reasoner.isSatisfiable(ProfessorInHCIorAI));
    }

    @Test
    void shouldPassgetSubClassesowlThingfalse() {
        equal(reasoner.getSubClasses(owlThing, false), Student, PhoneBook, UniversityPhoneBook,
            CS_Student, ResearchArea, TeachingFaculty, Department, Person, Professor, EE_Course,
            Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department, HCIStudent,
            CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing, CS_Course,
            CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule,
            Course);
    }

    @Test
    void shouldPassgetDisjointClassesowlThing() {
        equal(reasoner.getDisjointClasses(owlThing), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesUniversityPhoneBookfalse() {
        equal(reasoner.getSubClasses(UniversityPhoneBook, false), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesUniversityPhoneBooktrue() {
        equal(reasoner.getSubClasses(UniversityPhoneBook, true), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesUniversityPhoneBook() {
        equal(reasoner.getDisjointClasses(UniversityPhoneBook), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesCS_Studentfalse() {
        equal(reasoner.getSubClasses(CS_Student, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesCS_Studenttrue() {
        equal(reasoner.getSubClasses(CS_Student, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesCS_Student() {
        equal(reasoner.getDisjointClasses(CS_Student), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesResearchAreafalse() {
        equal(reasoner.getSubClasses(ResearchArea, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesResearchAreatrue() {
        equal(reasoner.getSubClasses(ResearchArea, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesResearchArea() {
        equal(reasoner.getDisjointClasses(ResearchArea), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesDepartmentfalse() {
        equal(reasoner.getSubClasses(Department, false), CS_Library, assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course, EE_Department);
    }

    @Test
    void shouldPassgetDisjointClassesDepartment() {
        equal(reasoner.getDisjointClasses(Department), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesTeachingFacultyfalse() {
        equal(reasoner.getSubClasses(TeachingFaculty, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, ProfessorInHCIorAI, Professor);
    }

    @Test
    void shouldPassgetSubClassesTeachingFacultytrue() {
        equal(reasoner.getSubClasses(TeachingFaculty, true), Professor);
    }

    @Test
    void shouldPassgetDisjointClassesTeachingFaculty() {
        equal(reasoner.getDisjointClasses(TeachingFaculty), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesHCIStudentfalse() {
        equal(reasoner.getSuperClasses(HCIStudent, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesHCIStudenttrue() {
        equal(reasoner.getSuperClasses(HCIStudent, true), EE_Library, CS_Student, CS_Library,
            UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook, EE_Course,
            Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesHCIStudent() {
        equal(reasoner.getEquivalentClasses(HCIStudent), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesHCIStudent() {
        equal(reasoner.getDisjointClasses(HCIStudent), owlThing, Student, PhoneBook, CS_Student,
            UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person, Professor,
            EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library,
            Schedule, Course);
    }

    @Test
    void shouldPassgetSubClassesPersonfalse() {
        equal(reasoner.getSubClasses(Person, false), Student, Faculty, CS_Student,
            assistantProfessor, aiStudent, aiDept, CS_Department, HCIStudent,
            CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing, CS_Course,
            TeachingFaculty, ProfessorInHCIorAI, Professor);
    }

    @Test
    void shouldPassgetDisjointClassesPerson() {
        equal(reasoner.getDisjointClasses(Person), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesProfessorfalse() {
        equal(reasoner.getSubClasses(Professor, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, ProfessorInHCIorAI);
    }

    @Test
    void shouldPassgetSuperClassesProfessorfalse() {
        equal(reasoner.getSuperClasses(Professor, false), owlThing, Faculty, TeachingFaculty,
            Person);
    }

    @Test
    void shouldPassgetSubClassesProfessortrue() {
        equal(reasoner.getSubClasses(Professor, true), ProfessorInHCIorAI);
    }

    @Test
    void shouldPassgetEquivalentClassesProfessor() {
        equal(reasoner.getEquivalentClasses(Professor), Professor);
    }

    @Test
    void shouldPassgetDisjointClassesProfessor() {
        equal(reasoner.getDisjointClasses(Professor), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesEE_Coursefalse() {
        equal(reasoner.getSubClasses(EE_Course, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesEE_Coursetrue() {
        equal(reasoner.getSubClasses(EE_Course, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesEE_Course() {
        equal(reasoner.getDisjointClasses(EE_Course), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesFacultyfalse() {
        equal(reasoner.getSubClasses(Faculty, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, TeachingFaculty, ProfessorInHCIorAI, Professor);
    }

    @Test
    void shouldPassgetDisjointClassesFaculty() {
        equal(reasoner.getDisjointClasses(Faculty), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesEE_Libraryfalse() {
        equal(reasoner.getSubClasses(EE_Library, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesEE_Librarytrue() {
        equal(reasoner.getSubClasses(EE_Library, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesEE_Library() {
        equal(reasoner.getDisjointClasses(EE_Library), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesEE_Departmentfalse() {
        equal(reasoner.getSubClasses(EE_Department, false), CS_Library, assistantProfessor,
            aiStudent, aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses,
            LecturerTaking4Courses, Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesEE_Department() {
        equal(reasoner.getDisjointClasses(EE_Department), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesFacultyPhoneBookfalse() {
        equal(reasoner.getSubClasses(FacultyPhoneBook, false), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesFacultyPhoneBooktrue() {
        equal(reasoner.getSubClasses(FacultyPhoneBook, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesFacultyPhoneBook() {
        equal(reasoner.getDisjointClasses(FacultyPhoneBook), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesLecturerfalse() {
        equal(reasoner.getSuperClasses(Lecturer, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesLecturertrue() {
        equal(reasoner.getSuperClasses(Lecturer, true), EE_Library, CS_Student, CS_Library,
            UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook, EE_Course,
            Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesLecturer() {
        equal(reasoner.getEquivalentClasses(Lecturer), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesLecturer() {
        equal(reasoner.getDisjointClasses(Lecturer), owlThing, Student, PhoneBook, CS_Student,
            UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person, Professor,
            EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library,
            Schedule, Course);
    }

    @Test
    void shouldPassgetSubClassesLibraryfalse() {
        equal(reasoner.getSubClasses(Library, false), EE_Library, CS_Library, assistantProfessor,
            aiStudent, aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses,
            LecturerTaking4Courses, Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesLibrary() {
        equal(reasoner.getDisjointClasses(Library), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesLecturerTaking4Coursesfalse() {
        equal(reasoner.getSuperClasses(LecturerTaking4Courses, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesLecturerTaking4Coursestrue() {
        equal(reasoner.getSuperClasses(LecturerTaking4Courses, true), EE_Library, CS_Student,
            CS_Library, UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook,
            EE_Course, Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesLecturerTaking4Courses() {
        equal(reasoner.getEquivalentClasses(LecturerTaking4Courses), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesLecturerTaking4Courses() {
        equal(reasoner.getDisjointClasses(LecturerTaking4Courses), owlThing, Student, PhoneBook,
            CS_Student, UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook,
            Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSubClassesSchedulefalse() {
        equal(reasoner.getSubClasses(Schedule, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesScheduletrue() {
        equal(reasoner.getSubClasses(Schedule, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesSchedule() {
        equal(reasoner.getDisjointClasses(Schedule), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesassistantProfessorfalse() {
        equal(reasoner.getSuperClasses(assistantProfessor, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesassistantProfessortrue() {
        equal(reasoner.getSuperClasses(assistantProfessor, true), EE_Library, CS_Student,
            CS_Library, UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook,
            EE_Course, Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesassistantProfessor() {
        equal(reasoner.getEquivalentClasses(assistantProfessor), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesassistantProfessor() {
        equal(reasoner.getDisjointClasses(assistantProfessor), owlThing, Student, PhoneBook,
            CS_Student, UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook,
            Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSubClassesStudentfalse() {
        equal(reasoner.getSubClasses(Student, false), CS_Student, assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesStudent() {
        equal(reasoner.getDisjointClasses(Student), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesPhoneBookfalse() {
        equal(reasoner.getSubClasses(PhoneBook, false), UniversityPhoneBook, assistantProfessor,
            aiStudent, aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses,
            LecturerTaking4Courses, Lecturer, owlNothing, CS_Course, FacultyPhoneBook);
    }

    @Test
    void shouldPassgetDisjointClassesPhoneBook() {
        equal(reasoner.getDisjointClasses(PhoneBook), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesaiDeptfalse() {
        equal(reasoner.getSuperClasses(aiDept, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesaiDepttrue() {
        equal(reasoner.getSuperClasses(aiDept, true), EE_Library, CS_Student, CS_Library,
            UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook, EE_Course,
            Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesaiDept() {
        equal(reasoner.getEquivalentClasses(aiDept), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesaiDept() {
        equal(reasoner.getDisjointClasses(aiDept), owlThing, Student, PhoneBook, CS_Student,
            UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person, Professor,
            EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library,
            Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesCS_Departmentfalse() {
        equal(reasoner.getSuperClasses(CS_Department, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesCS_Departmenttrue() {
        equal(reasoner.getSuperClasses(CS_Department, true), EE_Library, CS_Student, CS_Library,
            UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook, EE_Course,
            Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesCS_Department() {
        equal(reasoner.getEquivalentClasses(CS_Department), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesCS_Department() {
        equal(reasoner.getDisjointClasses(CS_Department), owlThing, Student, PhoneBook, CS_Student,
            UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person, Professor,
            EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library,
            Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesaiStudentfalse() {
        equal(reasoner.getSuperClasses(aiStudent, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesaiStudenttrue() {
        equal(reasoner.getSuperClasses(aiStudent, true), EE_Library, CS_Student, CS_Library,
            UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook, EE_Course,
            Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesaiStudent() {
        equal(reasoner.getEquivalentClasses(aiStudent), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesaiStudent() {
        equal(reasoner.getDisjointClasses(aiStudent), owlThing, Student, PhoneBook, CS_Student,
            UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person, Professor,
            EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library,
            Schedule, Course);
    }

    @Test
    void shouldPassgetSubClassesCS_Libraryfalse() {
        equal(reasoner.getSubClasses(CS_Library, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesCS_Librarytrue() {
        equal(reasoner.getSubClasses(CS_Library, true), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesCS_Library() {
        equal(reasoner.getDisjointClasses(CS_Library), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSubClassesProfessorInHCIorAIfalse() {
        equal(reasoner.getSubClasses(ProfessorInHCIorAI, false), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesProfessorInHCIorAIfalse() {
        equal(reasoner.getSuperClasses(ProfessorInHCIorAI, false), owlThing, Faculty,
            TeachingFaculty, Person, Professor);
    }

    @Test
    void shouldPassgetSubClassesProfessorInHCIorAItrue() {
        equal(reasoner.getSubClasses(ProfessorInHCIorAI, true), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetEquivalentClassesProfessorInHCIorAI() {
        equal(reasoner.getEquivalentClasses(ProfessorInHCIorAI), ProfessorInHCIorAI);
    }

    @Test
    void shouldPassgetDisjointClassesProfessorInHCIorAI() {
        equal(reasoner.getDisjointClasses(ProfessorInHCIorAI), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesCS_StudentTakingCoursesfalse() {
        equal(reasoner.getSuperClasses(CS_StudentTakingCourses, false), owlThing, Student,
            PhoneBook, UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department,
            Person, Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesCS_StudentTakingCoursestrue() {
        equal(reasoner.getSuperClasses(CS_StudentTakingCourses, true), EE_Library, CS_Student,
            CS_Library, UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook,
            EE_Course, Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesCS_StudentTakingCourses() {
        equal(reasoner.getEquivalentClasses(CS_StudentTakingCourses), assistantProfessor, aiStudent,
            aiDept, CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses,
            Lecturer, owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesCS_StudentTakingCourses() {
        equal(reasoner.getDisjointClasses(CS_StudentTakingCourses), owlThing, Student, PhoneBook,
            CS_Student, UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook,
            Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSubClassesCoursefalse() {
        equal(reasoner.getSubClasses(Course, false), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course, EE_Course);
    }

    @Test
    void shouldPassgetDisjointClassesCourse() {
        equal(reasoner.getDisjointClasses(Course), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetSuperClassesCS_Coursefalse() {
        equal(reasoner.getSuperClasses(CS_Course, false), owlThing, Student, PhoneBook,
            UniversityPhoneBook, CS_Student, ResearchArea, TeachingFaculty, Department, Person,
            Professor, EE_Course, Faculty, EE_Library, CS_Library, EE_Department,
            ProfessorInHCIorAI, FacultyPhoneBook, Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesCS_Coursetrue() {
        equal(reasoner.getSuperClasses(CS_Course, true), EE_Library, CS_Student, CS_Library,
            UniversityPhoneBook, ResearchArea, ProfessorInHCIorAI, FacultyPhoneBook, EE_Course,
            Schedule);
    }

    @Test
    void shouldPassgetEquivalentClassesCS_Course() {
        equal(reasoner.getEquivalentClasses(CS_Course), assistantProfessor, aiStudent, aiDept,
            CS_Department, HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer,
            owlNothing, CS_Course);
    }

    @Test
    void shouldPassgetDisjointClassesCS_Course() {
        equal(reasoner.getDisjointClasses(CS_Course), owlThing, Student, PhoneBook, CS_Student,
            UniversityPhoneBook, ResearchArea, TeachingFaculty, Department, Person, Professor,
            EE_Course, Faculty, EE_Library, assistantProfessor, aiStudent, aiDept, CS_Department,
            HCIStudent, CS_StudentTakingCourses, LecturerTaking4Courses, Lecturer, owlNothing,
            CS_Course, CS_Library, EE_Department, ProfessorInHCIorAI, FacultyPhoneBook, Library,
            Schedule, Course);
    }

    @Test
    void shouldPassgetSubDataPropertieshasTenurefalse() {
        equal(reasoner.getSubDataProperties(hasTenure, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertieshasTenurefalse() {
        equal(reasoner.getSuperDataProperties(hasTenure, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertieshasTenuretrue() {
        equal(reasoner.getSubDataProperties(hasTenure, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertieshasTenuretrue() {
        equal(reasoner.getSuperDataProperties(hasTenure, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubClassesowlThingtrue() {
        equal(reasoner.getSubClasses(owlThing, true), PhoneBook, ResearchArea, Department, Person,
            Library, Schedule, Course);
    }

    @Test
    void shouldPassgetSuperClassesProfessortrue() {
        equal(reasoner.getSuperClasses(Professor, true), TeachingFaculty);
    }

    @Test
    void shouldPassgetSuperClassesProfessorInHCIorAItrue() {
        equal(reasoner.getSuperClasses(ProfessorInHCIorAI, true), Professor);
    }

    @Test
    void shouldPassgetDataPropertyDomainshasTenurefalse() {
        equal(reasoner.getDataPropertyDomains(hasTenure, false), owlThing, Faculty, TeachingFaculty,
            Person);
    }

    @Test
    void shouldPassProfessorSubClassOfhasTenureTrue() {
        OWLClassExpression c =
            df.getOWLDataSomeValuesFrom(hasTenure, df.getOWLDataOneOf(df.getOWLLiteral(true)));
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(Professor, c)));
        equal(reasoner.getSuperClasses(c, false), owlThing, Faculty, TeachingFaculty, Person);
    }

    @Test
    void shouldPassgetDataPropertyDomainshasTenuretrue() {
        equal(reasoner.getDataPropertyDomains(hasTenure, true), TeachingFaculty);
    }

    @Test
    void shouldPassgetDataPropertyDomainshasTenureFalse() {
        equal(reasoner.getDataPropertyDomains(hasTenure, false), owlThing, Faculty, TeachingFaculty,
            Person);
    }
}
