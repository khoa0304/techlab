package cs.basic.datastructure.custom;

public class LinkedHashMapImpl<K,V> {
    
    private MapEntry<K,V>[] mapEntryArray;   
    private int capacity= 32;  
    private MapEntry<K,V> firstMapEntry; 
    private MapEntry<K,V> lastMapEntry;

    /*
     * before and after are used for maintaining insertion order.
     */
    class MapEntry<MapEntryKey, MapEntryValue> {
        MapEntryKey key;
        MapEntryValue value;
        MapEntry<MapEntryKey,MapEntryValue> next;
        MapEntry<MapEntryKey,MapEntryValue> before,after;
           
        public MapEntry(MapEntryKey key, MapEntryValue value, MapEntry<MapEntryKey,MapEntryValue> next){
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    

   @SuppressWarnings("unchecked")
   public LinkedHashMapImpl() {
      mapEntryArray = new MapEntry[capacity];
   }

  

   /**
    * Method allows you put key-value pair in LinkedHashMapCustom.
    * If the map already contains a mapping for the key, the old value is replaced.
    * Note: method does not allows you to put null key thought it allows null values.
    * Implementation allows you to put custom objects as a key as well.
    * Key Features: implementation provides you with following features:-
    *     >provide complete functionality how to override equals method.
    *  >provide complete functionality how to override hashCode method.
    * @param newKey
    * @param data
    */
   public void put(K newKey, V data){
      if(newKey==null)
          return;    //does not allow to store null.
     
      int hash=hash(newKey);
      
      MapEntry<K,V> newEntry = new MapEntry<K,V>(newKey, data, null);
      maintainOrderAfterInsert(newEntry);       
       if(mapEntryArray[hash] == null){
        mapEntryArray[hash] = newEntry;
       }else{
          MapEntry<K,V> previous = null;
          MapEntry<K,V> current = mapEntryArray[hash];
          while(current != null){ //we have reached last entry of bucket.
          if(current.key.equals(newKey)){                  
              if(previous==null){  //node has to be insert on first of bucket.
                    newEntry.next=current.next;
                    mapEntryArray[hash]=newEntry;
                    return;
              }
              else{
                  newEntry.next=current.next;
                  previous.next=newEntry;
                  return;
              }
          }
          previous=current;
            current = current.next;
        }
        previous.next = newEntry;
       }
   }

  
   /**
    * below method helps us in ensuring insertion order of LinkedHashMapCustom
    * after new key-value pair is added.
    */
   private void maintainOrderAfterInsert(MapEntry<K, V> newEntry) {
          
      if(firstMapEntry==null){
          firstMapEntry=newEntry;
          lastMapEntry=newEntry;
          return;
      }
     
      if(firstMapEntry.key.equals(newEntry.key)){
          deleteFirst();
          insertFirst(newEntry);
          return;
      }
     
      if(lastMapEntry.key.equals(newEntry.key)){
          deleteLast();
          insertLast(newEntry);
          return;
      }
     
      MapEntry<K, V> beforeDeleteEntry=    deleteSpecificEntry(newEntry);
      if(beforeDeleteEntry==null){
          insertLast(newEntry);
      }
      else{
          insertAfter(beforeDeleteEntry,newEntry);
      }
     
     
   }

   /**
    * below method helps us in ensuring insertion order of LinkedHashMapCustom,
    * after deletion of key-value pair.
    */
   private void maintainOrderAfterDeletion(MapEntry<K, V> deleteEntry) {
          
      if(firstMapEntry.key.equals(deleteEntry.key)){
          deleteFirst();
          return;
      }
     
      if(lastMapEntry.key.equals(deleteEntry.key)){
          deleteLast();
          return;
      }
     
      deleteSpecificEntry(deleteEntry);
     
   }

   /**
    * returns entry after which new entry must be added.
    */
   private void insertAfter(MapEntry<K, V> beforeDeleteEntry, MapEntry<K, V> newEntry) {
      MapEntry<K, V> current=firstMapEntry;
          while(current!=beforeDeleteEntry){
                 current=current.after;  //move to next node.
          }
          
          newEntry.after=beforeDeleteEntry.after;
          beforeDeleteEntry.after.before=newEntry;
          newEntry.before=beforeDeleteEntry;
          beforeDeleteEntry.after=newEntry;
          
   }


   /**
    * deletes entry from first.
    */
   private void deleteFirst(){

      if(firstMapEntry==lastMapEntry){ //only one entry found.
                 firstMapEntry=lastMapEntry=null;
                 return;
          }
          firstMapEntry=firstMapEntry.after;
          firstMapEntry.before=null;
          
   }
   
   /**
    * inserts entry at first.
    */
   private void insertFirst(MapEntry<K, V> newEntry){      
          
          if(firstMapEntry==null){ //no entry found
                 firstMapEntry=newEntry;
                 lastMapEntry=newEntry;
                 return;
          }
          
          newEntry.after=firstMapEntry;
          firstMapEntry.before=newEntry;
          firstMapEntry=newEntry;
          
   }

   /**
    * inserts entry at last.
    */
   private void insertLast(MapEntry<K, V> newEntry){
          
          if(firstMapEntry==null){
                 firstMapEntry=newEntry;
                 lastMapEntry=newEntry;
                 return;
          }
          lastMapEntry.after=newEntry;
          newEntry.before=lastMapEntry;
          lastMapEntry=newEntry;
                 
   }
   
   /**
    * deletes entry from last.
    */
   private void deleteLast(){
          
          if(firstMapEntry==lastMapEntry){
                 firstMapEntry=lastMapEntry=null;
                 return;
          }
          
          lastMapEntry=lastMapEntry.before;
          lastMapEntry.after=null;
   }
   

   /**
    * deletes specific entry and returns before entry.
    */
   private MapEntry<K, V> deleteSpecificEntry(MapEntry<K, V> newEntry){
                       
          MapEntry<K, V> current=firstMapEntry;
          while(!current.key.equals(newEntry.key)){
                 if(current.after==null){   //entry not found
                       return null;
                 }
                 current=current.after;  //move to next node.
          }
          
          MapEntry<K, V> beforeDeleteEntry=current.before;
          current.before.after=current.after;
          current.after.before=current.before;  //entry deleted
          return beforeDeleteEntry;
   }


   /**
    * Method returns value corresponding to key.
    * @param key
    */
   public V get(K key){
       int hash = hash(key);
       if(mapEntryArray[hash] == null){
        return null;
       }else{
        MapEntry<K,V> temp = mapEntryArray[hash];
        while(temp!= null){
            if(temp.key.equals(key))
                return temp.value;
            temp = temp.next; //return value corresponding to key.
        }         
        return null;   //returns null if key is not found.
       }
   }


   /**
    * Method removes key-value pair from HashMapCustom.
    * @param key
    */
   public boolean remove(K deleteKey){
      
      int hash=hash(deleteKey);
             
     if(mapEntryArray[hash] == null){
           return false;
     }else{
       MapEntry<K,V> previous = null;
       MapEntry<K,V> current = mapEntryArray[hash];
       
       while(current != null){ //we have reached last entry node of bucket.
          if(current.key.equals(deleteKey)){
              maintainOrderAfterDeletion(current);
              if(previous==null){  //delete first entry node.
                    mapEntryArray[hash]=mapEntryArray[hash].next;
                    return true;
              }
              else{
                    previous.next=current.next;
                  return true;
              }
          }
          previous=current;
            current = current.next;
         }
       return false;
     }
   
   }
  

   /**
    * Method displays all key-value pairs present in HashMapCustom.,
    * insertion order is not guaranteed, for maintaining insertion order
    * refer linkedHashMapCustom.
    * @param key
    */
   public void display(){
      
      MapEntry<K, V> currentEntry=firstMapEntry;
      while(currentEntry!=null){
          System.out.print("{"+currentEntry.key+"="+currentEntry.value+"}" +" ");
          currentEntry=currentEntry.after;
      }
   
   }
   /**
    * Method implements hashing functionality, which helps in finding the appropriate
    * bucket location to store our data.
    * This is very important method, as performance of HashMapCustom is very much
    * dependent on this method's implementation.
    * @param key
    */
   private int hash(K key){
       return Math.abs(key.hashCode()) % capacity;
   }

   public static void main(String[] args) {
	   
       LinkedHashMapImpl<Integer, Integer> linkedHashMapCustom = new LinkedHashMapImpl<Integer, Integer>();

       linkedHashMapCustom.put(21, 12);
       linkedHashMapCustom.put(25, 121);
       linkedHashMapCustom.put(30, 151);
       linkedHashMapCustom.put(33, 15);
       linkedHashMapCustom.put(35, 89);

       System.out.println("Display values corresponding to keys>");
       System.out.println("value corresponding to key 21="
                    + linkedHashMapCustom.get(21));
       System.out.println("value corresponding to key 51="
                    + linkedHashMapCustom.get(51));

       System.out.print("Displaying : ");
       linkedHashMapCustom.display();

       System.out.println("\n\nvalue corresponding to key 21 removed: "
                    + linkedHashMapCustom.remove(21));
       System.out.println("value corresponding to key 22 removed: "
                    + linkedHashMapCustom.remove(22));

       System.out.print("Displaying : ");
       linkedHashMapCustom.display();

}

}