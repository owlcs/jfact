package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.kernel.state.DLCompletionGraphSaveState;
import uk.ac.manchester.cs.jfact.kernel.state.DLCompletionTreeSaveState;
import uk.ac.manchester.cs.jfact.kernel.state.SaveList;
import uk.ac.manchester.cs.jfact.kernel.state.SaveState;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel_state {
    @Test
    public void shouldTestDLCompletionGraphSaveState() throws Exception {
        DLCompletionGraphSaveState testSubject0 = new DLCompletionGraphSaveState();
        String result0 = testSubject0.toString();
        testSubject0.setnNodes(1);
        testSubject0.setsNodes(1);
        testSubject0.setnEdges(1);
        int result1 = testSubject0.getnNodes();
        int result2 = testSubject0.getsNodes();
        int result3 = testSubject0.getnEdges();
    }

    @Test
    public void shouldTestDLCompletionTreeSaveState() throws Exception {
        DLCompletionTreeSaveState testSubject0 = new DLCompletionTreeSaveState();
        int result0 = testSubject0.level();
        int result1 = testSubject0.getCurLevel();
        testSubject0.setCurLevel(1);
        testSubject0.setnNeighbours(1);
        SaveState result2 = testSubject0.getLab();
        int result3 = testSubject0.getnNeighbours();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestSaveList() throws Exception {
        SaveList testSubject0 = new SaveList();
        DLCompletionTreeSaveState result0 = testSubject0.pop();
        DLCompletionTreeSaveState result1 = testSubject0.pop(1);
        Object result2 = testSubject0.pop();
        boolean result3 = testSubject0.add(mock(DLCompletionTreeSaveState.class));
        testSubject0.add(1, mock(DLCompletionTreeSaveState.class));
        Object result4 = testSubject0.get(1);
        Object result5 = testSubject0.clone();
        int result6 = testSubject0.indexOf(mock(Object.class));
        testSubject0.clear();
        int result7 = testSubject0.lastIndexOf(mock(Object.class));
        boolean result8 = testSubject0.contains(mock(Object.class));
        boolean result9 = testSubject0.addAll(mock(Collection.class));
        boolean result10 = testSubject0.addAll(1, mock(Collection.class));
        int result11 = testSubject0.size();
        Object[] result12 = testSubject0.toArray(mock(Object[].class));
        Object[] result13 = testSubject0.toArray();
        testSubject0.push(mock(DLCompletionTreeSaveState.class));
        DLCompletionTreeSaveState result14 = testSubject0.remove(1);
        boolean result15 = testSubject0.remove(mock(Object.class));
        Object result16 = testSubject0.remove();
        Object result17 = testSubject0.set(1, mock(DLCompletionTreeSaveState.class));
        Object result18 = testSubject0.poll();
        ListIterator result19 = testSubject0.listIterator(1);
        Object result20 = testSubject0.peek();
        Object result21 = testSubject0.getFirst();
        Object result22 = testSubject0.getLast();
        Object result23 = testSubject0.removeFirst();
        Object result24 = testSubject0.removeLast();
        testSubject0.addFirst(mock(DLCompletionTreeSaveState.class));
        testSubject0.addLast(mock(DLCompletionTreeSaveState.class));
        Object result25 = testSubject0.element();
        boolean result26 = testSubject0.offer(mock(DLCompletionTreeSaveState.class));
        boolean result27 = testSubject0.offerFirst(mock(DLCompletionTreeSaveState.class));
        boolean result28 = testSubject0.offerLast(mock(DLCompletionTreeSaveState.class));
        Object result29 = testSubject0.peekFirst();
        Object result30 = testSubject0.peekLast();
        Object result31 = testSubject0.pollFirst();
        Object result32 = testSubject0.pollLast();
        boolean result33 = testSubject0.removeFirstOccurrence(mock(Object.class));
        boolean result34 = testSubject0.removeLastOccurrence(mock(Object.class));
        Iterator result35 = testSubject0.descendingIterator();
        Iterator result36 = testSubject0.iterator();
        ListIterator result37 = testSubject0.listIterator();
        List result38 = testSubject0.subList(1, 1);
        String result39 = testSubject0.toString();
        boolean result40 = testSubject0.isEmpty();
        boolean result41 = testSubject0.removeAll(mock(Collection.class));
        boolean result42 = testSubject0.containsAll(mock(Collection.class));
        boolean result43 = testSubject0.retainAll(mock(Collection.class));
    }

    @Test
    public void shouldTestSaveState() throws Exception {
        SaveState testSubject0 = new SaveState();
        testSubject0.setSc(1);
        testSubject0.setCc(1);
        int result0 = testSubject0.getSc();
        int result1 = testSubject0.getCc();
        String result2 = testSubject0.toString();
    }
}
