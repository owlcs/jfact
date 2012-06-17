package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;

import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

public class Taxonomy {
	/** array of taxonomy verteces */
	private final List<TaxonomyVertex> graph = new ArrayList<TaxonomyVertex>();
	List<ClassifiableEntry> Syns = new ArrayList<ClassifiableEntry>();
	/** aux. vertex to be included to taxonomy */
	protected TaxonomyVertex current;
	/// vertex with parent Top and child Bot, represents the fresh entity
	TaxonomyVertex FreshNode = new TaxonomyVertex();
	/** pointer to currently classified entry */
	protected ClassifiableEntry curEntry;
	/** number of tested entryes */
	protected int nEntries;
	/** number of completely-defined entries */
	protected long nCDEntries;
	/**
	 * optimisation flag: if entry is completely defined by it's told subsumers,
	 * no other classification required
	 */
	protected boolean useCompletelyDefined;
	/** behaviour flag: if true, insert temporary vertex into taxonomy */
	protected boolean willInsertIntoTaxonomy;
	/** stack for Taxonomy creation */
	private final LinkedList<ClassifiableEntry> waitStack = new LinkedList<ClassifiableEntry>();
	/** told subsumers corresponding to a given entry */
	protected final LinkedList<KnownSubsumers> ksStack = new LinkedList<KnownSubsumers>();
	/** labellers for marking taxonomy */
	protected long checkLabel = 1;
	protected long valueLabel = 1;
	private final JFactReasonerConfiguration options;

	/**
	 * apply ACTOR to subgraph starting from NODE as defined by flags; this
	 * version is intended to work only with SupConceptActor, which requires the
	 * method to return as soon as the apply() method returns false
	 */
	public boolean getRelativesInfo(final TaxonomyVertex node,
			final SupConceptActor actor, final boolean needCurrent,
			final boolean onlyDirect, final boolean upDirection) {
		// if current node processed OK and there is no need to continue -- exit
		// this is the helper to the case like getDomain():
		//   if there is a named concept that represent's a domain -- that's what we need
		if (needCurrent) {
			if (!actor.apply(node)) {
				return false;
			}
			if (onlyDirect) {
				return true;
			}
		}
		Queue<List<TaxonomyVertex>> queue = new LinkedList<List<TaxonomyVertex>>();
		queue.add(node.neigh(upDirection));
		while (queue.size() > 0) {
			List<TaxonomyVertex> neigh = queue.remove();
			final int size = neigh.size();
			for (int i = 0; i < size; i++) {
				TaxonomyVertex _node = neigh.get(i);
				// recursive applicability checking
				if (!_node.isChecked(this.checkLabel)) {
					// label node as visited
					_node.setChecked(this.checkLabel);
					// if current node processed OK and there is no need to continue -- exit
					// if node is NOT processed for some reasons -- go to another level
					if (!actor.apply(_node)) {
						return false;
					}
					if (onlyDirect) {
						continue;
					}
					// apply method to the proper neighbours with proper parameters
					queue.add(_node.neigh(upDirection));
				}
			}
		}
		this.clearCheckedLabel();
		return true;
	}

	/** apply ACTOR to subgraph starting from NODE as defined by flags; */
	public void getRelativesInfo(final TaxonomyVertex node, final Actor actor,
			final boolean needCurrent, final boolean onlyDirect, final boolean upDirection) {
		// if current node processed OK and there is no need to continue -- exit
		// this is the helper to the case like getDomain():
		//   if there is a named concept that represent's a domain -- that's what we need
		if (needCurrent && actor.apply(node) && onlyDirect) {
			return;
		}
		Queue<List<TaxonomyVertex>> queue = new LinkedList<List<TaxonomyVertex>>();
		queue.add(node.neigh(upDirection));
		while (queue.size() > 0) {
			List<TaxonomyVertex> neigh = queue.remove();
			// node.neigh(upDirection);
			final int size = neigh.size();
			for (int i = 0; i < size; i++) {
				TaxonomyVertex _node = neigh.get(i);
				// recursive applicability checking
				if (!_node.isChecked(this.checkLabel)) {
					// label node as visited
					_node.setChecked(this.checkLabel);
					// if current node processed OK and there is no need to continue -- exit
					// if node is NOT processed for some reasons -- go to another level
					if (actor.apply(_node) && onlyDirect) {
						continue;
					}
					// apply method to the proper neighbours with proper parameters
					queue.add(_node.neigh(upDirection));
				}
			}
		}
		this.clearCheckedLabel();
	}

	/** clear the CHECKED label from all the taxonomy vertex */
	protected final void clearCheckedLabel() {
		this.checkLabel++;
	}

	protected void clearLabels() {
		this.checkLabel++;
		this.valueLabel++;
	}

	/** initialise aux entry with given concept p */
	protected void setCurrentEntry(final ClassifiableEntry p) {
		this.current.clear();
		this.curEntry = p;
	}

	/** check if no classification needed (synonym, orphan, unsatisfiable) */
	protected boolean immediatelyClassified() {
		return this.classifySynonym();
	}

	/** check if it is possible to skip TD phase */
	protected boolean needTopDown() {
		return false;
	}

	/** explicitely run TD phase */
	protected void runTopDown() {}

	/** check if it is possible to skip BU phase */
	protected boolean needBottomUp() {
		return false;
	}

	/** explicitely run BU phase */
	protected void runBottomUp() {}

	/** actions that to be done BEFORE entry will be classified */
	protected void preClassificationActions() {}

	//--	DFS-based classification
	/** add top entry together with its known subsumers */
	private void addTop(final ClassifiableEntry p) {
		this.waitStack.push(p);
		this.ksStack.push(new ToldSubsumers(p.getToldSubsumers()));
	}

	/** remove top entry */
	protected void removeTop() {
		this.waitStack.pop();
		this.ksStack.pop();
	}

	/** check if it is necessary to log taxonomy action */
	protected boolean needLogging() {
		return true;
	}

	public Taxonomy(final ClassifiableEntry pTop, final ClassifiableEntry pBottom,
			final JFactReasonerConfiguration c) {
		this.options = c;
		this.current = new TaxonomyVertex();
		this.curEntry = null;
		this.nEntries = 0;
		this.nCDEntries = 0;
		this.useCompletelyDefined = false;
		this.willInsertIntoTaxonomy = true;
		this.graph.add(new TaxonomyVertex(pBottom)); // bottom
		this.graph.add(new TaxonomyVertex(pTop)); // top
		// set up fresh node
		this.FreshNode.addNeighbour(true, this.getTopVertex());
		this.FreshNode.addNeighbour(false, this.getBottomVertex());
	}

	/** special access to TOP of taxonomy */
	public final TaxonomyVertex getTopVertex() {
		return this.graph.get(1);
	}

	/** special access to BOTTOM of taxonomy */
	public final TaxonomyVertex getBottomVertex() {
		return this.graph.get(0);
	}

	/// get node for fresh entity E
	TaxonomyVertex getFreshVertex(final ClassifiableEntry e) {
		this.FreshNode.setSample(e, false);
		return this.FreshNode;
	}

	//--	classification interface
	// flags interface
	/** set Completely Defined flag */
	public void setCompletelyDefined(final boolean use) {
		this.useCompletelyDefined = use;
	}

	/** call this method after taxonomy is built */
	public void finalise() {
		// create links from leaf concepts to bottom
		final boolean upDirection = false;
		// TODO maybe useful to index Graph
		for (int i = 1; i < this.graph.size(); i++) {
			TaxonomyVertex p = this.graph.get(i);
			if (p.noNeighbours(upDirection)) {
				p.addNeighbour(upDirection, this.getBottomVertex());
				this.getBottomVertex().addNeighbour(!upDirection, p);
			}
		}
		this.willInsertIntoTaxonomy = false; // after finalisation one shouldn't add new entries to taxonomy
	}

	private void setupTopDown() {
		this.setToldSubsumers();
		if (!this.needTopDown()) {
			++this.nCDEntries;
			this.setNonRedundantCandidates();
		}
	}

	@Override
	public String toString() {
		StringBuilder o = new StringBuilder();
		o.append("Taxonomy consists of ");
		o.append(this.nEntries);
		o.append(" entries\n            of which ");
		o.append(this.nCDEntries);
		o.append(" are completely defined\n\nAll entries are in format:\n\"entry\" {n: parent_1 ... parent_n} {m: child_1 child_m}\n\n");
		TreeSet<TaxonomyVertex> sorted = new TreeSet<TaxonomyVertex>(
				new Comparator<TaxonomyVertex>() {
					public int compare(final TaxonomyVertex o1, final TaxonomyVertex o2) {
						return o1.getPrimer().getName()
								.compareTo(o2.getPrimer().getName());
					}
				});
		sorted.addAll(this.graph.subList(1, this.graph.size()));
		for (TaxonomyVertex p : sorted) {
			o.append(p);
		}
		o.append(this.getBottomVertex());
		return o.toString();
	}

	public void addCurrentToSynonym(final TaxonomyVertex syn) {
		if (this.queryMode()) {
			// no need to insert; just mark SYN as a host to curEntry
			syn.setHostVertex(this.curEntry);
		} else {
			syn.addSynonym(this.curEntry);
			this.options.getLog().print("\nTAX:set ", this.curEntry.getName(), " equal ",
					syn.getPrimer().getName());
		}
	}

	void insertCurrentNode() {
		this.current.setSample(this.curEntry, true); // put curEntry as a representative of Current
		if (!this.queryMode()) // insert node into taxonomy
		{
			this.current.incorporate(this.options);
			this.graph.add(this.current);
			// we used the Current so need to create a new one
			this.current = new TaxonomyVertex();
		}
	}

	/// @return true if taxonomy works in a query mode (no need to insert query vertex)
	public boolean queryMode() {
		return !this.willInsertIntoTaxonomy;
	}

	/// remove node from the taxonomy; assume no references to the node
	void removeNode(final TaxonomyVertex node) {
		this.graph.remove(node);
	}

	/// @return true if V is a direct parent of current wrt labels
	boolean isDirectParent(final TaxonomyVertex v) {
		for (TaxonomyVertex q : v.neigh(false)) {
			if (q.isValued(this.valueLabel) && q.getValue()) {
				return false;
			}
		}
		return true;
	}

	private void performClassification() {
		// do something before classification (tunable)
		this.preClassificationActions();
		++this.nEntries;
		this.options.getLog().print("\n\nTAX: start classifying entry ");
		this.options.getLog().print(this.curEntry.getName());
		// if no classification needed -- nothing to do
		if (this.immediatelyClassified()) {
			return;
		}
		// perform main classification
		this.generalTwoPhaseClassification();
		// create new vertex
		TaxonomyVertex syn = this.current.getSynonymNode();
		if (syn != null) {
			this.addCurrentToSynonym(syn);
		} else {
			this.insertCurrentNode();
		}
		// clear all labels
		this.clearLabels();
	}

	private void generalTwoPhaseClassification() {
		this.setupTopDown();
		if (this.needTopDown()) {
			this.getTopVertex().setValued(true, this.valueLabel);
			this.getBottomVertex().setValued(false, this.valueLabel);
			this.runTopDown();
		}
		this.clearLabels();
		if (this.needBottomUp()) {
			this.getBottomVertex().setValued(true, this.valueLabel);
			this.runBottomUp();
		}
		this.clearLabels();
	}

	protected boolean classifySynonym() {
		final ClassifiableEntry syn = resolveSynonym(this.curEntry);
		if (syn.equals(this.curEntry)) {
			return false;
		}
		//assert willInsertIntoTaxonomy;
		assert syn.getTaxVertex() != null;
		this.addCurrentToSynonym(syn.getTaxVertex());
		return true;
	}

	private void setNonRedundantCandidates() {
		if (!this.curEntry.hasToldSubsumers()) {
			this.options.getLog().print("\nTAX: TOP");
		}
		this.options.getLog().print(" completely defines concept ");
		this.options.getLog().print(this.curEntry.getName());
		for (ClassifiableEntry p : this.ksStack.peek().s_begin()) {
			TaxonomyVertex par = p.getTaxVertex();
			if (par == null) {
				continue;
			}
			if (this.isDirectParent(par)) {
				this.current.addNeighbour(true, par);
			}
			//			boolean stillParent = true;
			//			for (TaxonomyVertex q : par.neigh(false)) {
			//				if (q.isValued(valueLabel)) {
			//					stillParent = false;
			//					break;
			//				}
			//			}
			//			if (stillParent) {
			//				current.addNeighbour(true, par);
			//			}
		}
	}

	private void setToldSubsumers() {
		Collection<ClassifiableEntry> top = this.ksStack.peek().s_begin();
		if (this.needLogging() && !top.isEmpty()) {
			this.options.getLog().print("\nTAX: told subsumers");
		}
		for (ClassifiableEntry p : top) {
			if (p.isClassified()) {
				if (this.needLogging()) {
					this.options.getLog().printTemplate(Templates.TOLD_SUBSUMERS,
							p.getName());
				}
				this.propagateTrueUp(p.getTaxVertex());
			}
		}
		//XXX this is misleading: in the C++ code the only imple,emtnation available will always say that top is empty here even if it never is.
		//		if (!top.isEmpty() && needLogging()) {
		//			LL.print(" and possibly ");
		//			for (ClassifiableEntry q : top) {
		//				LL.print(Templates.TOLD_SUBSUMERS, q.getName());
		//			}
		//		}
	}

	/// ensure that all TS of the top entry are classified. @return the reason of cycle or NULL.
	ClassifiableEntry prepareTS(final ClassifiableEntry cur) {
		// we just found that TS forms a cycle -- return stop-marker
		if (this.waitStack.contains(cur)) {
			return cur;
		}
		// starting from the topmost entry
		this.addTop(cur);
		// true iff CUR is a reason of the cycle
		boolean cycleFound = false;
		// for all the told subsumers...
		for (ClassifiableEntry p : this.ksStack.peek().s_begin()) {
			if (!p.isClassified()) // need to classify it first
			{
				if (p.isNonClassifiable()) {
					continue;
				}
				// prepare TS for *p
				ClassifiableEntry v = this.prepareTS(p);
				// if NULL is returned -- just continue
				if (v == null) {
					continue;
				}
				if (v == cur) // current cycle is finished, all saved in Syns
				{
					// after classification of CUR we need to mark all the Syns as synonyms
					cycleFound = true;
					// continue to prepare its classification
					continue;
				} else {
					// arbitrary vertex in a cycle: save in synonyms of a root cause
					this.Syns.add(cur);
					// don't need to classify it
					this.removeTop();
					// return the cycle cause
					return v;
				}
			}
		}
		// all TS are ready here -- let's classify!
		this.classifyTop();
		// now if CUR is the reason of cycle mark all SYNs as synonyms
		if (cycleFound) {
			TaxonomyVertex syn = cur.getTaxVertex();
			for (ClassifiableEntry q : this.Syns) {
				syn.addSynonym(q);
			}
			this.Syns.clear();
		}
		// here the cycle is gone
		return null;
	}

	public void classifyEntry(final ClassifiableEntry p) {
		assert this.waitStack.isEmpty();
		// don't classify artificial concepts
		if (p.isNonClassifiable()) {
			return;
		}
		this.prepareTS(p);
		//		addTop(p);
		//		while (!waitStack.isEmpty()) {
		//			if (checkToldSubsumers()) {
		//				classifyTop();
		//			} else {
		//				classifyCycle();
		//			}
		//		}
	}

	//	private boolean checkToldSubsumers() {
	//		assert !waitStack.isEmpty();
	//		boolean ret = true;
	//		for (ClassifiableEntry r : ksStack.peek().s_begin()) {
	//			assert r != null;
	//			if (!r.isClassified()) {
	//				if (waitStack.contains(r)) {
	//					addTop(r);
	//					ret = false;
	//					break;
	//				}
	//				addTop(r);
	//				ret = checkToldSubsumers();
	//				break;
	//			}
	//		}
	//		return ret;
	//	}
	private void classifyTop() {
		assert !this.waitStack.isEmpty();
		// load last concept
		this.setCurrentEntry(this.waitStack.peek());
		if (this.options.isTMP_PRINT_TAXONOMY_INFO()) {
			this.options.getLog().print("\nTrying classify",
					this.curEntry.isCompletelyDefined() ? " CD " : " ",
					this.curEntry.getName(), "... ");
		}
		this.performClassification();
		if (this.options.isTMP_PRINT_TAXONOMY_INFO()) {
			this.options.getLog().print("done");
		}
		this.removeTop();
	}

	//	private void classifyCycle() {
	//		assert !waitStack.isEmpty();
	//		ClassifiableEntry p = waitStack.peek();
	//		classifyTop();
	//		StringBuilder b = new StringBuilder("\n* Concept definitions cycle found: ");
	//		b.append(p.getName());
	//		b.append('\n');
	//		while (!waitStack.isEmpty()) {
	//			b.append(", ");
	//			b.append(waitStack.peek().getName());
	//			b.append('\n');
	//			waitStack.peek().setTaxVertex(p.getTaxVertex());
	//			removeTop();
	//		}
	//		throw new ReasonerInternalException(b.toString());
	//	}
	protected void propagateTrueUp(final TaxonomyVertex node) {
		// if taxonomy class already checked -- do nothing
		if (node.isValued(this.valueLabel)) {
			assert node.getValue();
			return;
		}
		// overwise -- value it...
		node.setValued(true, this.valueLabel);
		// ... and value all parents
		List<TaxonomyVertex> list = node.neigh(true);
		for (int i = 0; i < list.size(); i++) {
			this.propagateTrueUp(list.get(i));
		}
	}

	/// abstract class to represent the known subsumers of a concept
	abstract class KnownSubsumers {
		/// begin of the Sure subsumers interval
		abstract List<ClassifiableEntry> s_begin();

		/// begin of the Possible subsumers interval
		abstract List<ClassifiableEntry> p_begin();

		// flags
		/// whether there are no sure subsumers
		boolean s_empty() {
			return this.s_begin().isEmpty();
		}

		/// whether there are no possible subsumers
		boolean p_empty() {
			return this.p_begin().isEmpty();
		}

		/// @return true iff CE is the possible subsumer
		boolean isPossibleSub(final ClassifiableEntry ce) {
			return true;
		}
	}

	/// class to represent the TS's
	class ToldSubsumers extends KnownSubsumers {
		/// two iterators for the TS of a concept
		List<ClassifiableEntry> beg;

		public ToldSubsumers(final Collection<ClassifiableEntry> b) {
			this.beg = new ArrayList<ClassifiableEntry>(b);
		}

		/// begin of the Sure subsumers interval
		@Override
		List<ClassifiableEntry> s_begin() {
			return this.beg;
		}

		/// end of the Sure subsumers interval
		/// begin of the Possible subsumers interval
		@Override
		List<ClassifiableEntry> p_begin() {
			return Collections.emptyList();
		}
	}
}
