package uk.ac.manchester.cs.jfact.kernel.actors;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.HasName;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import conformance.Original;
import conformance.PortedFrom;

/** @author ignazio
 * @param <T> */
@Original
public abstract class ActorImpl<T extends HasName> implements Actor {
    // XXX verify if subclasses should use these structures
    /** 2D array to return */
    @PortedFrom(file = "Actor.h", name = "acc")
    protected List<List<T>> acc = new ArrayList<List<T>>();
    /** 1D array to return */
    @PortedFrom(file = "Actor.h", name = "plain")
    protected List<T> plain = new ArrayList<T>();
    /** temporary vector to keep synonyms */
    @PortedFrom(file = "Actor.h", name = "syn")
    protected List<T> syn;
    /** flag to look at concept-like or role-like entities */
    @PortedFrom(file = "Actor.h", name = "isRole")
    boolean isRole;
    /** flag to look at concepts or object roles */
    @PortedFrom(file = "Actor.h", name = "isStandard")
    boolean isStandard;
    /** flag to throw exception at the 1st found */
    @PortedFrom(file = "Actor.h", name = "interrupt")
    boolean interrupt;

    protected List<String> buildArray(List<T> vec) {
        List<String> ret = new ArrayList<String>(vec.size());
        for (int i = 0; i < vec.size(); ++i) {
            ret.add(vec.get(i).getName());
        }
        return ret;
    }

    @Override
    public List<String> getSynonyms() {
        return buildArray(acc.isEmpty() ? syn : acc.get(0));
    }

    @Override
    public List<List<String>> getElements2D() {
        List<List<String>> ret = new ArrayList<List<String>>();
        for (int i = 0; i < acc.size(); ++i) {
            ret.add(buildArray(acc.get(i)));
        }
        return ret;
    }

    @Override
    public List<String> getElements1D() {
        List<T> vec = new ArrayList<T>();
        for (List<T> p : acc) {
            vec.addAll(p);
        }
        return buildArray(vec);
    }

    @Override
    public void needConcepts() {
        isRole = false;
        isStandard = true;
    }

    @Override
    public void needIndividuals() {
        isRole = false;
        isStandard = false;
    }

    @Override
    public void needObjectRoles() {
        isRole = true;
        isStandard = true;
    }

    @Override
    public void needDataRoles() {
        isRole = true;
        isStandard = false;
    }

    @Override
    public void setInterruptAfterFirstFound(boolean value) {
        interrupt = value;
    }

    @PortedFrom(file = "Actor.cpp", name = "getPlain")
    @Override
    public List<Individual> getPlain() {
        assert !isRole && !isStandard;
        List<Individual> vec = new ArrayList<Individual>();
        for (List<T> p : acc) {
            for (T q : p) {
                if (q instanceof Individual) {
                    vec.add((Individual) q);
                }
            }
        }
        return vec;
    }
}
