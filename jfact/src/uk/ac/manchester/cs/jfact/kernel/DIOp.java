package uk.ac.manchester.cs.jfact.kernel;

enum DIOp {
    // concept expressions
    diNot("not"), diAnd("and"), diOr("or"), diExists("some"), diForall("all"), diGE(
            "atleast"), diLE("atmost"),
    // role expressions
    diInv,
    // individual expressions
    diOneOf,
    // wrong operation
    // diErrorOp(9),
    // end of the enum
    diEndOp,
    // wrong axiom
    // diErrorAx(9),
    // concept axioms
    diDefineC("defprimconcept"), diImpliesC("implies_c"), diEqualsC("equal_c"), diDisjointC,
    // role axioms
    diDefineR("defprimrole"), diTransitiveR("transitive"), diFunctionalR("functional"), diImpliesR(
            "implies_r"), diEqualsR("equal_r"), diDomainR("domain"), diRangeR("range"),
    // individual axioms
    diInstanceOf;
    private String s;

    private DIOp() {
        s = "";
    }

    private DIOp(String s) {
        this.s = s;
    }

    public String getString() {
        return s;
    }
}