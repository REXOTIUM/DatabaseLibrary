package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.LogLevel;
import info.peperkoek.databaselibrary.testutils.*;
import info.peperkoek.databaselibrary.utils.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public class DataAccessObjectTest {
    protected DataAccessObject dao;
    private List<ContainerObject> containers;
    private List<ForeignObject> foreigns;
    private List<LinkObject> links;
    
    public void test() {
        assertEquals("ID first container mismatch", containers.get(0).getId().toString(), dao.getID(containers.get(0)));
        assertTrue("Second container does not exist", dao.doesObjectExist(containers.get(1)));
        ContainerObject object = new ContainerObject();
        object.setInfo("test");
        assertFalse("Container with info \"test\" exists", dao.doesObjectExist(object));
        assertNull("Container with info \"test\" has id", dao.getID(object));
        
        ForeignObject fo = dao.getObject(ForeignObject.class, foreigns.get(0));
        assertEquals("Foreign objects do not match. Got with item", foreigns.get(0), fo);
        Query q = new Query("Select * from objecten where id = " + Query.QUERY_PARAMETER_PLACEHOLDER);
        DataObject d = new DataObject();
        d.setTest(foreigns.get(1).getId().toString());
        q.addElement(0, d);
        ForeignObject fo2 = dao.getObject(ForeignObject.class, q);
        assertEquals("Foreign objects do not match. Got with query", foreigns.get(1), fo2);
        
        Collection<LinkObject> objects = dao.getObjects(LinkObject.class);
        for(LinkObject lo : links) {
            if(!objects.remove(lo)) {
                fail("Linkobject not in list");
            }
        }
        assertEquals("Collection of linkobjects still has objects in list", 0, objects.size());
        
        updateData();
    }
    
    protected void createData() {
        containers = new ArrayList<>();
        foreigns = new ArrayList<>();
        links = new ArrayList<>();
        
        DataObject d1 = new DataObject();
        d1.setTest("dataobject1");
        DataObject d2 = new DataObject();
        d2.setTest("dataobject2");
        DataObject d3 = new DataObject();
        d3.setTest("dataobject3");
        
        ForeignObject f1 = new ForeignObject();
        f1.setName("first");
        f1.setObject(d1);
        ForeignObject f2 = new ForeignObject();
        f2.setName("second");
        f2.setObject(d2);
        ForeignObject f3 = new ForeignObject();
        f3.setName("third");
        f3.setObject(d3);
        foreigns.add(f1);
        foreigns.add(f2);
        foreigns.add(f3);
        
        LinkObject l1 = new LinkObject();
        l1.setOption("option 1");
        l1.setOn(true);
        LinkObject l2 = new LinkObject();
        l2.setOption("option 2");
        l2.setOn(false);
        LinkObject l3 = new LinkObject();
        l3.setOption("option 3");
        l3.setOn(true);
        LinkObject l4 = new LinkObject();
        l4.setOption("option 4");
        l4.setOn(false);
        LinkObject l5 = new LinkObject();
        l5.setOption("option 5");
        l5.setOn(true);
        links.add(l1);
        links.add(l2);
        links.add(l3);
        links.add(l4);
        links.add(l5);
        
        ArrayList<LinkObject> list1 = new ArrayList<>();
        list1.add(l1);
        list1.add(l3);
        list1.add(l5);
        ArrayList<LinkObject> list2 = new ArrayList<>();
        list2.add(l2);
        list2.add(l4);
        ArrayList<LinkObject> list3 = new ArrayList<>();
        list3.add(l1);
        list3.add(l2);
        list3.add(l3);
        list3.add(l4);
        list3.add(l5);
        
        ContainerObject co1 = new ContainerObject();
        co1.setId(0);
        co1.setObjectName("co1");
        co1.setInfo("This is the first object");
        co1.setIgnore("This will be ignored in the first object");
        co1.setObject(f1);
        co1.setList(list1);
        ContainerObject co2 = new ContainerObject();
        co2.setId(1);
        co2.setObjectName("co2");
        co2.setInfo("This is the second object");
        co2.setIgnore("This will be ignored in the second object");
        co2.setObject(f2);
        co2.setList(list2);
        ContainerObject co3 = new ContainerObject();
        co3.setId(2);
        co3.setObjectName("co3");
        co3.setInfo("This is the third object");
        co3.setIgnore("This will be ignored in the third object");
        co3.setObject(f3);
        co3.setList(list3);
        containers.add(co1);
        containers.add(co2);
        containers.add(co3);
    }
    
    protected void insertData() {
        dao.insertObjects(foreigns.toArray());
        dao.insertObjects(containers);
    }
    
    private void updateData() {
        dao.updateObjects(foreigns.toArray());
        dao.updateObjects(containers);
    }
    
    protected void removeData() {
        dao.removeObjects(containers);
        dao.removeObjects(foreigns.toArray());
        dao.removeObjects(links);
    }
}