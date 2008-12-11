package com.bm.introspectors;

import com.bm.introspectors.relations.EntityReleationInfo;
import java.util.List;

/**
 * This class represents information about persistent fields.
 * 
 * @author Daniel Wiese
 * @since 07.10.2005
 */
public class PersistentPropertyInfo {

	private int length = 255;

	private boolean isNullable = false;

	private boolean isEmbeddedClass = false;

	private boolean isReleation = false;

	private EntityReleationInfo entityReleationInfo = null;

	private String dbName;

        private List<DbMappingInfo> dbMappingInfoList;

	/**
	 * Returns the isNullable.
	 * 
	 * @return Returns the isNullable.
	 */
	public boolean isNullable() {
		return isNullable;
	}

	/**
	 * Sets the isNullable.
	 * 
	 * @param isNullable
	 *            The isNullable to set.
	 */
	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	/**
	 * Returns the length.
	 * 
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * The length to set.
	 * 
	 * @param length
	 *            The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}

        /**
         * Add the DB mapping info for the field.
         * @param dbMappingInfo
         */
        public void addDbMappingInfo(DbMappingInfo dbMappingInfo) {
                dbMappingInfoList.add(dbMappingInfo);
        }

        /**
         * Get the DB mapping info for the column 'name'.
         * @param name name of the column
         */
        public DbMappingInfo getDbMappingInfo(String name) {
            for (DbMappingInfo dbMappingInfo : dbMappingInfoList) {
                if (name.equals(dbMappingInfo.getName())) {
                    return dbMappingInfo;
                }
            }

            return null;
        }

        /**
         * Returns the mapping info list.
         *
         * @return list of all mappings
         */
        public List<DbMappingInfo> getDbMappingInfoList() {
            return dbMappingInfoList;
        }

        /**
         * Set the mapping info list
         * @param dbMappingInfoList list of all mappings for the field
         */
        public void setDbMappingInfoList(List<DbMappingInfo> dbMappingInfoList) {
            this.dbMappingInfoList = dbMappingInfoList;
        }

	/**
	 * Returns the dbName.
	 * 
	 * @return Returns the dbName.
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * The dbName to set.
	 * 
	 * @param dbName
	 *            The dbName to set.
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Returns the isEmbeddedClass.
	 * 
	 * @return Returns the isEmbeddedClass.
	 */
	public boolean isEmbeddedClass() {
		return isEmbeddedClass;
	}

	/**
	 * The isEmbeddedClass to set.
	 * 
	 * @param isEmbeddedClass
	 *            The isEmbeddedClass to set.
	 */
	public void setEmbeddedClass(boolean isEmbeddedClass) {
		this.isEmbeddedClass = isEmbeddedClass;
	}

	/**
	 * Returns true if thisproperty represents a relation and returns true if
	 * this is a case (the EntityReleationInfo is not null and contains the
	 * relevant informations).
	 * 
	 * @return Returns the isReleation.
	 */
	public boolean isReleation() {
		return isReleation;
	}

	/**
	 * Returns the entityReleationInfo.
	 * 
	 * @return Returns the entityReleationInfo.
	 */
	public EntityReleationInfo getEntityReleationInfo() {
		return entityReleationInfo;
	}

	/**
	 * The entityReleationInfo to set.
	 * 
	 * @param entityReleationInfo
	 *            The entityReleationInfo to set.
	 */
	public void setEntityReleationInfo(EntityReleationInfo entityReleationInfo) {
		this.entityReleationInfo = entityReleationInfo;
		if (this.entityReleationInfo == null) {
			this.isReleation = false;
		} else {
			this.isReleation = true;
		}
	}

}
