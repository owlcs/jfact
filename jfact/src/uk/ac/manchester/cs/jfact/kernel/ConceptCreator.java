package uk.ac.manchester.cs.jfact.kernel;

class ConceptCreator implements NameCreator<Concept> {
    @Override
    public Concept makeEntry(String name) {
        return new Concept(name);
    }
}