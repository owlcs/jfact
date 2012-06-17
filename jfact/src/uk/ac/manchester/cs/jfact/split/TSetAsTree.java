//package uk.ac.manchester.cs.jfact.split;
//
//import java.util.Set;
//
//// implement model cache set as a tree-set
//public class TSetAsTree
//{
//
//
//		/// set implementation
//	Set<Integer> Base;
//		/// maximal number of elements
//	 int nElems;
//
//
//		/// empty c'tor taking max possible number of elements in the set
//	 TSetAsTree (  int size ) { nElems(size) ;}
//		/// copy c'tor
//	TSetAsTree (  TSetAsTree is ) { Base(is.Base) ;}
//		/// assignment
//	TSetAsTree& operator= (  TSetAsTree& is )
//	{
//		Base = is.Base;
//		return *this;
//	}
//
//
//		/// adds given index to the set
//	void insert (  int i )
//	{
//#	ifdef ENABLE_CHECKING
//		fpp_assert ( i > 0 );
//#	endif
//		Base.insert(i);
//	}
//		/// completes the set with [1,n)
//	void completeSet (  )
//	{
//		for (  int i = 1; i < nElems; ++i )
//			Base.insert(i);
//	}
//		/// adds the given set to the current one
//	TSetAsTree& operator |= (  TSetAsTree& is )
//	{
//		Base.insert ( is.Base.begin(), is.Base.end() );
//		return *this;
//	}
//		/// clear the set
//	void clear (  ) { Base.clear(); }
//
//		/// check whether the set is empty
//	boolean empty (  )  { return Base.isEmpty(); }
//		/// check whether I contains in the set
//	boolean contains (  int i )  { return Base.contains(i); }
//		/// check whether the intersection between the current set and IS is nonempty
//	boolean intersects (  TSetAsTree is ) 
//	{
//		if ( Base.empty() || is.Base.empty() )
//			return false;
//
//		BaseType::const_iterator p1 = Base.begin(), p1_end = Base.end(), p2 = is.Base.begin(), p2_end = is.Base.end();
//		while ( p1 != p1_end && p2 != p2_end )
//			if ( *p1 == *p2 )
//				return true;
//			else if ( *p1 < *p2 )
//				++p1;
//			else
//				++p2;
//
//		return false;
//	}
//		/// prints the set in a human-readable form
//	void print ( std::ostream& o ) 
//	{
//		o << "{";
//		if ( !empty() )
//		{
//			BaseType::const_iterator p = Base.begin(), p_end = Base.end();
//			o << *p;
//			while ( ++p != p_end )
//				o << ',' << *p;
//		}
//		o << "}";
//	}
//	typedef BaseType::const_iterator const_iterator;
//	const_iterator begin ( void ) const { return Base.begin(); }
//	const_iterator end ( void ) const { return Base.end(); }
//}