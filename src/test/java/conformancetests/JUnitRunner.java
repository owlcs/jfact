package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

@SuppressWarnings({ "javadoc" })
public class JUnitRunner {

    public void print(String premise) throws OWLException {
        OWLOntology o = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(premise));
        o.getOWLOntologyManager().saveOntology(o, new FunctionalSyntaxDocumentFormat(), new SystemOutDocumentTarget());
    }

    private final TestClasses t;
    private OWLReasonerFactory f;
    private final String testId;
    private final OWLOntology premise;
    private final OWLOntology consequences;
    private final String description;
    private OWLReasonerConfiguration config;
    private final OWLOntologyManager manager;

    public JUnitRunner(OWLOntologyManager m, String premise, String consequence, String testId, TestClasses t,
        String description) {
        manager = m;
        this.testId = testId;
        this.premise = premise(premise);
        consequences = premise(consequence);
        this.description = description;
        this.t = t;
    }

    public JUnitRunner(OWLOntologyManager m, OWLOntology premise, String consequence, String testId, TestClasses t,
        String description) {
        manager = m;
        this.testId = testId;
        this.premise = premise;
        consequences = premise(consequence);
        this.description = description;
        this.t = t;
    }

    public JUnitRunner(OWLOntologyManager m, OWLOntology premise, OWLOntology consequence, String testId, TestClasses t,
        String description) {
        manager = m;
        this.testId = testId;
        this.premise = premise;
        consequences = consequence;
        this.description = description;
        this.t = t;
    }

    public JUnitRunner(OWLOntologyManager m, InputStream premise, String consequence, String testId, TestClasses t,
        String description) {
        manager = m;
        this.testId = testId;
        this.premise = premise(premise);
        consequences = premise(consequence);
        this.description = description;
        this.t = t;
    }

    public void setConfig(OWLReasonerConfiguration c) {
        config = c;
    }

    public void setReasonerFactory(OWLReasonerFactory f) {
        this.f = f;
    }

    private static boolean isConsistent(OWLReasoner reasoner) {
        return reasoner.isConsistent();
    }

    private static boolean isEntailed(OWLReasoner reasoner, OWLAxiom conclusion) {
        return reasoner.isEntailed(conclusion);
    }

    public OWLOntology premise(InputStream in) {
        try {
            return manager.loadOntologyFromOntologyDocument(in);
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    public OWLOntology premise(String p) {
        try {
            if (p.isEmpty()) {
                return null;
            }
            StringDocumentSource documentSource = new StringDocumentSource(p);
            OWLOntology o = manager.loadOntologyFromOntologyDocument(documentSource);
            // if (p.length() > 1000) {
            // System.out.println("JUnitRunner.premise() " + new
            // Exception().fillInStackTrace().getStackTrace()[2]
            // + " " + p.length() + " " + o.getOntologyID());
            // try {
            // System.out.println("asString(m, \"/" + testId + ".owl\")");
            // Files.write(p, new File(testId + ".owl"),
            // StandardCharsets.UTF_8);
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        OWLOntology premiseOntology = null;
        // OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        // m.setSilentMissingImportsHandling(true);
        try {
            if (premise != null) {
                premiseOntology = premise;
                // OWL2DLProfile profile = new OWL2DLProfile();
                // OWLProfileReport report =
                // profile.checkOntology(premiseOntology);
                // if (report.getViolations().size() > 0) {
                // System.out.println("JUnitRunner.run() " + testId);
                // System.out.println("JUnitRunner.run() premise violations:\n"
                // + report.toString());
                // throw new RuntimeException("errors!\n" + report.toString());
                // }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            // System.out.println("JUnitRunner.run() premise:\n" + premise);
            throw new RuntimeException(e);
        }
        OWLOntology conclusionOntology = null;
        try {
            if (consequences != null) {
                conclusionOntology = consequences;
                // OWL2DLProfile profile = new OWL2DLProfile();
                // OWLProfileReport report =
                // profile.checkOntology(conclusionOntology);
                // if (report.getViolations().size() > 0) {
                // System.out.println("JUnitRunner.run() " + testId +
                // report.getViolations().size());
                // System.out.println("JUnitRunner.run() conclusion
                // violations:\n" + report.toString());
                // throw new RuntimeException("errors!");
                // }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
        this.run(premiseOntology, conclusionOntology);
    }

    protected void run(OWLOntology premiseOntology, OWLOntology conclusionOntology) {
        StringBuilder b = new StringBuilder();
        b.append("JUnitRunner.logTroubles() premise");
        b.append('\n');
        premiseOntology.axioms().forEach(ax1 -> b.append(ax1).append('\n'));
        if (config == null) {
            config = new SimpleConfiguration();
        }
        OWLReasoner reasoner = f.createReasoner(premiseOntology, config);
        actual(conclusionOntology, b, reasoner);
        // reasoner = roundtrip(reasoner);
        // actual(conclusionOntology, b, reasoner);
        premiseOntology.getOWLOntologyManager().removeOntologyChangeListener((OWLOntologyChangeListener) reasoner);
    }

    @Nullable
    private static OWLReasoner roundtrip(OWLReasoner r) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(out);
            stream.writeObject(r);
            stream.flush();
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(in);
            return (OWLReasoner) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void actual(OWLOntology conclusionOntology, StringBuilder b, OWLReasoner reasoner) {
        switch (t) {
            case CONSISTENCY: {
                AtomicBoolean consistent = new AtomicBoolean(isConsistent(reasoner));
                if (!consistent.get()) {
                    String message = b.toString() + logTroubles(true, consistent, t, null);
                    assertTrue(message, consistent.get());
                }
            }
                break;
            case INCONSISTENCY: {
                AtomicBoolean consistent = new AtomicBoolean(isConsistent(reasoner));
                if (consistent.get()) {
                    String message = b.toString() + logTroubles(false, consistent, t, null);
                    assertFalse(message, consistent.get());
                }
            }
                break;
            case NEGATIVE_IMPL: {
                AtomicBoolean consistent = new AtomicBoolean(isConsistent(reasoner));
                if (!consistent.get()) {
                    String message = b.toString() + logTroubles(true, consistent, t, null);
                    assertTrue(message, consistent.get());
                }
                AtomicBoolean entailed = new AtomicBoolean(false);
                conclusionOntology.logicalAxioms().forEach(ax -> {
                    boolean temp = isEntailed(reasoner, ax);
                    entailed.compareAndSet(false, temp);
                    if (temp) {
                        b.append(logTroubles(false, entailed, t, ax));
                    }
                });
                assertFalse(b.toString(), entailed.get());
            }
                break;
            case POSITIVE_IMPL: {
                AtomicBoolean consistent = new AtomicBoolean(isConsistent(reasoner));
                if (!consistent.get()) {
                    String message = b.toString() + logTroubles(true, consistent, t, null);
                    assertTrue(message, consistent.get());
                }
                AtomicBoolean entailed = new AtomicBoolean(true);
                conclusionOntology.logicalAxioms().forEach(ax -> {
                    boolean temp = isEntailed(reasoner, ax);
                    entailed.compareAndSet(true, temp);
                    if (!temp) {
                        b.append(logTroubles(true, entailed, t, ax));
                    }
                });
                assertTrue(b.toString(), entailed.get());
            }
                break;
            default:
                break;
        }
    }

    public String logTroubles(boolean expected, AtomicBoolean actual, TestClasses testclass, @Nullable OWLAxiom axiom) {
        StringBuilder b = new StringBuilder();
        b.append("JUnitRunner.logTroubles() \t");
        b.append(testclass);
        b.append('\t');
        b.append(testId);
        b.append("\n ======================================\n");
        b.append(description);
        b.append("\nexpected: ");
        b.append(expected);
        b.append("\t actual: ");
        b.append(actual.get());
        if (axiom != null) {
            b.append("\n for axiom: ");
            b.append(axiom.toString());
        }
        String string = b.toString();
        System.out.println(string);
        return string;
    }

    public void printPremise() throws OWLOntologyStorageException {
        premise.saveOntology(new FunctionalSyntaxDocumentFormat(), new SystemOutDocumentTarget());
    }

    public void printConsequence() throws OWLOntologyStorageException {
        if (consequences != null) {
            consequences.saveOntology(new FunctionalSyntaxDocumentFormat(), new SystemOutDocumentTarget());
        }
    }
}
