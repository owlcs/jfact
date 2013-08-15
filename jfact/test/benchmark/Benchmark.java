package benchmark;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class Benchmark {
    @Test
    public void testgenome() throws Exception {
        File mainPath = new File("onto/genome.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testhorrocksgalen() throws Exception {
        File mainPath = new File("onto/horrocks-galen.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testkoala_fixed() throws Exception {
        File mainPath = new File("onto/koala_fixed.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testkoala() throws Exception {
        File mainPath = new File("onto/koala.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testmap() throws Exception {
        File mainPath = new File("onto/map.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testmarker() throws Exception {
        File mainPath = new File("onto/marker.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testminiTambis() throws Exception {
        File mainPath = new File("onto/miniTambis.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Ignore
    @Test
    public void testOWL2Primer() throws Exception {
        // test disabled because jfact does not support datatype definition yet
        File mainPath = new File("onto/OWL2Primer.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testpeople() throws Exception {
        File mainPath = new File("onto/people.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testplant_trait() throws Exception {
        File mainPath = new File("onto/plant_trait.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testplant() throws Exception {
        File mainPath = new File("onto/plant.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testqtl() throws Exception {
        File mainPath = new File("onto/qtl.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testsequence() throws Exception {
        File mainPath = new File("onto/sequence.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    public static long compute(File file) throws Exception {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            manager.addIRIMapper(new AutoIRIMapper(new File("onto"), true));
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            OWLReasoner reasoner = new JFactFactory().createReasoner(ontology);
            long startTime = System.nanoTime();
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            long end = System.nanoTime();
            return end - startTime;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
    }

    @Test
    public void testtambis() throws Exception {
        File mainPath = new File("onto/tambis.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testtrait() throws Exception {
        File mainPath = new File("onto/trait.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testuniversity() throws Exception {
        File mainPath = new File("onto/university.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testweird_onto() throws Exception {
        File mainPath = new File("onto/weird_onto.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testfamily() throws Exception {
        File mainPath = new File("onto/family.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testpresent_clinical_finding_module() throws Exception {
        File mainPath = new File("onto/present_clinical_finding_module.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testchronic_module() throws Exception {
        File mainPath = new File("onto/chronic_module.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testacute_module() throws Exception {
        File mainPath = new File("onto/acute_module.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testperiodic() throws Exception {
        File mainPath = new File("onto/periodic.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testcelllineontology1541() throws Exception {
        File mainPath = new File("onto/celllineontology1541.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }

    @Test
    public void testtest() throws Exception {
        File mainPath = new File("onto/test.owl");
        long elapsed = compute(mainPath);
        System.out.println(mainPath.getName() + "\tTook \t" + elapsed / 1000000L
                + " ms to process.");
    }
}
