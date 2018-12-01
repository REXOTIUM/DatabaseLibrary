package info.peperkoek.databaselibrary.testutils;

import info.peperkoek.databaselibrary.annotations.*;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public class ContainerObject {
    @PrimaryKey
    private Integer id;
    @Map(mapping = "name")
    private String objectName;
    @ForeignKey
    private ForeignObject object;
    @Nullable
    private String info;
    @Ignore
    private String ignore;
    @LinkTable(tableName = "linktable", clazz = LinkObject.class)
    private List<LinkObject> list;

    public Integer getId() {
        return id;
    }

    public String getObjectName() {
        return objectName;
    }

    public ForeignObject getObject() {
        return object;
    }

    public String getInfo() {
        return info;
    }

    public String getIgnore() {
        return ignore;
    }

    public List<LinkObject> getList() {
        return list;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setObject(ForeignObject object) {
        this.object = object;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

    public void setList(List<LinkObject> list) {
        this.list = list;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.objectName);
        hash = 47 * hash + Objects.hashCode(this.object);
        hash = 47 * hash + Objects.hashCode(this.info);
        hash = 47 * hash + Objects.hashCode(this.list);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerObject other = (ContainerObject) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.objectName, other.objectName)) {
            return false;
        }
        if (!Objects.equals(this.info, other.info)) {
            return false;
        }
        if (!Objects.equals(this.object, other.object)) {
            return false;
        }
        return Objects.equals(this.list, other.list);
    }
}