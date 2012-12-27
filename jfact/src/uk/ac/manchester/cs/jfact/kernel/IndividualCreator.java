package uk.ac.manchester.cs.jfact.kernel;

class IndividualCreator implements NameCreator<Individual> {
    @Override
    public Individual makeEntry(String name) {
        return new Individual(name);
    }
}
