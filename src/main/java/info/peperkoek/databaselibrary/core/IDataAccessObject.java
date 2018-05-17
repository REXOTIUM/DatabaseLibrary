package info.peperkoek.databaselibrary.core;

import java.util.Collection;

/**
 * Interface for an object to access databases
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
public interface IDataAccessObject {
    
    /**
     * Returns a object that statisfies the query constructed with the item.
     * 
     * Note: if multiple objects in a table statisfy the query one is selected.
     * @param <T> The type of the object
     * @param <U> The type of the item
     * @param clazz Class of the object to get
     * @param item The item with fields used to query for the object T
     * @return The object of class T which satisfies the query constructed with the item.
     */
    public <T, U> T getObject(Class<T> clazz, U item);
    
    /**
     * Returns a object that statisfies the query.
     * 
     * Note: if multiple objects in a table statisfy the query one is selected.
     * @param <T> The type of the object
     * @param clazz Class of the object to get
     * @param query The query which the object has to satisfy
     * @return The object of class T which satisfies the query.
     */
    public <T> T getObject(Class<T> clazz, Query query);
    
    /**
     * Returns a list of all items in the table 
     * @param <T> The type of the object
     * @param clazz Class of the object to get
     * @return List of all objects in the table that corresponds to the class
     */
    public <T> Collection<T> getObjects(Class<T> clazz);
    
    /**
     * Returns a list of all objects statisfy the query constructed with the item.
     * @param <T> The type of the objects
     * @param <U> The type of the item
     * @param clazz Class of the objects to get
     * @param item The item with fields used to query for the objects
     * @return A list of all objects statisfy the query constructed with the item.
     */
    public <T, U> Collection<T> getObjects(Class<T> clazz, U item);
    
    /**
     * Returns a list of all objects that satisfy the query
     * @param <T> The type of the objects
     * @param clazz Class of the objects to get
     * @param query The query which the objects has to satisfy
     * @return List of objects that statisfy the query
     */
    public <T> Collection<T> getObjects(Class<T> clazz, Query query);
    
    /**
     * Inserts object into database.
     * @param <T> The type of the object
     * @param obj The object to insert
     * @return True if item is inserted.
     */
    public <T> boolean insertObject(T obj);
    
    /**
     * Inserts all objects in the array in the database
     * 
     * Note: if one object fails to insert the inserts are stopped
     * @param <T> The type of the object
     * @param obj The array of objects to insert
     * @return True if all items are inserted, false otherwise
     */
    public <T> boolean insertObjects(T[] obj);
    
    /**
     * Inserts all objects in the collection in the database
     * 
     * Note: if one object fails to insert the inserts are stopped
     * @param <T> The type of the object
     * @param obj The collection of objects to insert
     * @return True if all items are inserted, false otherwise
     */
    public <T> boolean insertObjects(Collection<T> obj);
    
    /**
     * Updates object in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * @param <T> The type of the object
     * @param obj The object to update
     * @return True if item is updated.
     */
    public <T> boolean updateObject(T obj);
    
    /**
     * Updates objects in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * Not: if one update fails the updates are stopped
     * @param <T> The type of the object
     * @param obj The array of objects to update
     * @return True if all items are updated, false otherwise
     */
    public <T> boolean updateObjects(T[] obj);
    
    /**
     * Updates objects in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * Not: if one update fails the updates are stopped
     * @param <T> The type of the object
     * @param obj The collection of objects to update
     * @return True if all items are updated, false otherwise
     */
    public <T> boolean updateObjects(Collection<T> obj);
    
    /**
     * Deletes object in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * @param <T> The type of the object
     * @param obj The object to delete
     * @return True if item is deleted.
     */
    public <T> boolean removeObject(T obj);
    
    /**
     * Deletes objects in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * Note: if one delete fails the deletions are stopped
     * @param <T> The type of the object
     * @param obj The array of objects to delete
     * @return True if all items are deleted, false otherwise
     */
    public <T> boolean removeObjects(T[] obj);
    
    /**
     * Deletes objects in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * Note: if one delete fails the deletions are stopped
     * @param <T> The type of the object
     * @param obj The collection of objects to delete
     * @return True if all items are deleted, false otherwise
     */
    public <T> boolean removeObjects(Collection<T> obj);
}