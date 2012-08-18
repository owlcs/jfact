package jfact.unittest;

import static org.mockito.Mockito.*;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;

@SuppressWarnings({ "unused", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_dep {
    @Test
    public void shouldTestDepSet() throws Exception {
        DepSet testSubject0 = new DepSet(mock(FastSetSimple.class));
        DepSet testSubject1 = new DepSet();
        testSubject0.add(mock(FastSetSimple.class));
        testSubject0.add(mock(DepSet.class));
        int result0 = testSubject0.get(mock(int.class));
        String result1 = testSubject0.toString();
        testSubject0.clear();
        boolean result2 = testSubject0.isEmpty();
        boolean result3 = testSubject0.contains(mock(int.class));
        int result4 = testSubject0.size();
        DepSet result5 = DepSet.create(mock(int.class));
        DepSet result6 = DepSet.create();
        DepSet result7 = DepSet.create(mock(FastSetSimple.class));
        DepSet result8 = DepSet.create(mock(DepSet.class));
        DepSet result9 = DepSet.plus(mock(DepSet.class), mock(DepSet.class));
        FastSetSimple result10 = testSubject0.getDelegate();
        int result11 = testSubject0.level();
        testSubject0.restrict(mock(int.class));
    }
}
