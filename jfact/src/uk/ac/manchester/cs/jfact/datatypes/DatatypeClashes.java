package uk.ac.manchester.cs.jfact.datatypes;

public enum DatatypeClashes {
    DT_TT(" DT-TT"), DT_Empty_interval(" DT-Empty_interval"), DT_C_MM(" DT-C-MM"), DT_TNT(
            " DT-TNT"), DT_C_IT(" DT-C-IT");
    private String label;

    DatatypeClashes(String s) {
        label = s;
    }

    @Override
    public String toString() {
        return label;
    }
}
