

/**
 * FibonacciHeap_nechamagans_ellas2_ratson
 * ellas2 - 316986660
 * nechamagans - 314718024
 * ratson - 307839092
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap{
	int size;
	HeapNode min;
	int numMarked;
	int numTrees;
	static int numCuts;
	static int numLinks;
	
	//constructor for the fib heap
	FibonacciHeap(){
		this.size = 0;
		this.min = null;
		this.numMarked = 0;
		this.numTrees = 0;
		numCuts = 0;
		numLinks = 0;
	}
	
   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	boolean isEmpty = false;
    	if (this.size == 0){
    		isEmpty = true;
    	}
    	return isEmpty;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode(key);
    	if (this.empty()){
    		this.min = newNode;
    	}
    	else{
    	this.mergeHeapNodes(newNode, this.findMin());
    	//update minimum
    	if (key < this.min.key){
    		this.min = newNode;
    	}
    	}
    	//increase size by 1
    	this.size += 1;
    	this.numTrees += 1;
    	return newNode; 
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	//we are deleting the only node in the heap
    	if (this.size() == 1){
    		this.min = null;
    		this.numMarked = 0;
    		this.numTrees = 0;
    	}
    	else if (this.size() > 1){
    		if (this.min.child != null){
    		if (this.min.mark == true){
    			this.numMarked -= 1;
    		}
    		this.numTrees += (this.min.rank - 1);
    		HeapNode childOfMin = this.min.child;
    		HeapNode tempNode = this.min.child;
    		while (tempNode.next != childOfMin){
    		if (tempNode.mark == true){
    			tempNode.mark = false;
    			this.numMarked -= 1;
    		}
    		tempNode.parent = null;
    		tempNode = tempNode.next;
    		
    		}
    		if (this.min.prev != this.min && this.min.next != this.min){
    		childOfMin.prev = this.min.prev;
    		this.min.prev.next = childOfMin;
    		tempNode.next = this.min.next;
    		this.min.next.prev = tempNode;
    		}
    		//link x to itself and detach it from its children
        	this.min.next = this.min;
        	this.min.prev = this.min;
        	this.min.child = null;
    		//Temp minimum
    		this.min = childOfMin;
    		
    	}  
    		//Min does not have children
    		else{
            	//fixing sibling pointers
            	this.min.next.prev = this.min.prev;
            	this.min.prev.next = this.min.next;
            	HeapNode tempMin = this.min.next;
        		//link x to itself
            	this.min.next = this.min;
            	this.min.prev = this.min;      	
            	this.min = tempMin;
            	this.numTrees -= 1;
    		}
    		
        	this.successiveLinking();
    		
    	}
    	this.size -= 1;
     	return; 
    }
    
    
    public void successiveLinking(){
    	if(this.numTrees >= 2){
    	//Find the maximum Rank
    	int maxRank = 0;
    	int[] ranksArr = this.countersRep();
    	for (int i = 41; i > 0; i--){
    		if (ranksArr[i] != 0){
    			maxRank = i;
    			break;
    		}
    	}
    	//Find the number of bits it takes to represent numTress
    	int numBits = 0;
    	int tempNumTrees = this.numTrees;
    	while ( tempNumTrees > 0) {
    		numBits++;
    		tempNumTrees = tempNumTrees >> 1;
    	}
    	//Create an array of an enough size
    	HeapNode[] Heaps_Arr = new HeapNode[maxRank + numBits + 1];
    						
        int i = 0;
    	HeapNode currentNode = this.min; 
    	HeapNode nextNode = this.min.next;
        int tempRank;
        int num = this.numTrees;
        while(i < num){
        	tempRank = currentNode.rank;
        	while (Heaps_Arr[tempRank] != null){
        		currentNode = mergeTrees(currentNode, Heaps_Arr[tempRank]);
        		Heaps_Arr[tempRank] = null;
        		tempRank++;
        		if (tempRank == num){
        			break;
        		}
        	}
        	Heaps_Arr[tempRank] = currentNode;
        	currentNode = nextNode;
        	nextNode = nextNode.next;
        	i++;
        }
    	
    	treeArrToHeap(Heaps_Arr);
    }
    }
   

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin(){
    	return this.min;
    } 
    
    //Takes two nodes representing trees of the same rank and hangs the bigger one (key wise) on the smaller one
    //results in a new tree with a rank that is bigger by one 
    public HeapNode mergeTrees(HeapNode first, HeapNode second){
    	numLinks += 1;
 		if(first.key>second.key){ //swap
 			HeapNode tempTree = first;
 			first = second;
 			second = tempTree;
 		}
 		first.rank += 1;
 		second.parent = first;
 		if (second.next != second){
 			first.next = second.next;
 	 		second.next.prev = first;
 	 		second.prev.next = first;	}
 		else{
 			first.next = first;
 			first.prev = first;
 		}
 		if(first.child == null){
 			second.next =second;
 			second.prev = second;
 		}else{
 			second.prev = first.child.prev;
 			second.next = first.child;
 			first.child.prev.next = second;
 			first.child.prev = second;
 		}
 
 		first.child = second;
 		//
 		this.numTrees -= 1;
 		return first;
    }
    
    //takes an array of heap nodes representing trees and rearranges it to a new ordered heap
    //finds the new min
    public void treeArrToHeap (HeapNode[] heapArr){
        int i = 0;
        this.numTrees = 0;
        //find first tree
        while(heapArr[i] == null){ 			
        	i += 1;
        	if(i == heapArr.length){
        		this.min=null;
        		return;
        }
        }
        this.min = heapArr[i];
        this.min.next = this.min;
        this.min.prev = this.min;
        this.numTrees += 1;
        i += 1;
        //find the rest
        while(i<heapArr.length){ 				
        	if(heapArr[i] != null){
	        	this.numTrees += 1;
	        	heapArr[i].next = this.min;
	        	heapArr[i].prev = this.min.prev;
	        	this.min.prev.next = heapArr[i];
	        	this.min.prev = heapArr[i];
	            if(heapArr[i].key<this.min.key){
	            	this.min = heapArr[i];
	            }
            }
            i += 1;
        }    
        
    }
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	// If the second heap is empty, we have nothing to do
    	if(heap2 == null || heap2.empty()){ 
    		return;
    	}
    	 //If this heap is empty, replace this heap with heap 2.
    	if(this.empty()){
    		this.min=heap2.findMin();
    	}else{
    		//need to merge
    		mergeHeapNodes(this.findMin(), heap2.findMin());
    		// update minimum
    		if(heap2.findMin().key < this.findMin().key){ 
    			this.min = heap2.findMin();
    		}
    	}
    	this.numTrees += heap2.numTrees;
    	this.numMarked += heap2.numMarked;
		size += heap2.size();
    }

    //Merge two nodes
    public void mergeHeapNodes (HeapNode node1, HeapNode node2){
    node2.prev.next = node1.next;
	node1.next.prev = node2.prev;
	node1.next = node2;
	node2.prev = node1;
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
	int[] arr = new int[42];
	if (this.empty()){
		return new int[0];
	}
	else{
	arr[this.min.rank] += 1;
	HeapNode currNode = this.min.next;
	while (currNode != this.min){
		int treeRank = currNode.rank;
		arr[treeRank] += 1;
		currNode = currNode.next;		
	}
	
        return arr;
	}
    }

	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {  
    	this.decreaseKey(x, Integer.MIN_VALUE);
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.key -= delta;
    	HeapNode parentOfX = x.parent;
    	// we aren't in the root and we need to cut
    	if (parentOfX != null && x.key < parentOfX.key){
    		this.cut(x);
    		this.cascadingCuts(parentOfX);
    	}
    	if (x.key < this.min.key){
    		this.min = x;
    	}
    	return; 
    }
    
    //cuts the node from its tree and merges it to the root list
    public void cut (HeapNode x){
    	if (x.mark == true){
    		x.mark = false;
    		numMarked -= 1;
    	}
    	if (x.parent != null){
    		numCuts += 1;
    		x.parent.rank -= 1;
    		if (x.parent.child == x){
    			if (x.next == x){
    				x.parent.child = null;
    			}
    			else{
    					x.parent.child = x.next;
    				}
    			
    		}
    	}
    	else{
    		this.numTrees -= 1;
    	}
    
    	//fixing sibling pointers
    	x.next.prev = x.prev;
    	x.prev.next = x.next;
    	
    	//link x to itself
    	x.next = x;
    	x.prev = x;
    	
    	this.mergeHeapNodes(x, this.min);
    }
    
    //checks whether the node's parent is marked and if it is,remove its mark and cut the node
    // Works recursively
    public void cascadingCuts (HeapNode x){
    	HeapNode parentOfX = x.parent;
    	if (parentOfX != null){
    		if (x.mark == true){
    			x.mark = false;
    			numMarked -= 1;
    			this.cut(x);
    			this.cascadingCuts(parentOfX);
    		}
    		else{
    			x.mark = true;
    			numMarked += 1;
    		}
    		}
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return (numTrees + 2*numMarked); 
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return numLinks; 
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return numCuts; 
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
  	
    	String info = null;
    	int key;
    	int rank;
    	boolean mark = false;
    	HeapNode child = null;
    	HeapNode next = this;
    	HeapNode prev = this;
    	HeapNode parent = null;
    	
    	HeapNode(int key){
    		this.key = key;		
    	}
    	
    	public int getKey() {
    	    return this.key;
    	}
    	
    }
}

