package org.hibernate.cfg.annotations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.cfg.AnnotatedClassType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.CollectionSecondPass;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.ExtendedMappings;
import org.hibernate.cfg.IndexColumn;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.PropertyHolderBuilder;
import org.hibernate.cfg.PropertyPreloadedData;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.Backref;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.reflection.ReflectionManager;
import org.hibernate.reflection.XAnnotatedElement;
import org.hibernate.reflection.XClass;
import org.hibernate.reflection.XProperty;
import org.hibernate.util.CollectionHelper;
import org.hibernate.util.StringHelper;

/**
 * Collection binder
 *
 * @author inger
 * @author emmanuel
 */
public abstract class CollectionBinder {

	private static final Log log = LogFactory.getLog( CollectionBinder.class );

	protected Collection collection;
	protected String propertyName;
	FetchMode fetchMode;
	PropertyHolder propertyHolder;
	int batchSize;
	String where;
	private String mappedBy;
	private XClass collectionType;
	private XClass targetEntity;
	private ExtendedMappings mappings;
	private Ejb3JoinColumn[] inverseJoinColumns;
	private String cascadeStrategy;
	String cacheConcurrencyStrategy;
	Map<String, String> filters = new HashMap<String, String>();
	String cacheRegionName;
	private boolean oneToMany;
	protected IndexColumn indexColumn;
	private String orderBy;
	protected String hqlOrderBy;
	private boolean isSorted;
	private Class comparator;
	private boolean hasToBeSorted;
	protected boolean cascadeDeleteEnabled;
	protected String mapKeyPropertyName;
	private boolean insertable = true;
	private boolean updatable = true;
	private Ejb3JoinColumn[] fkJoinColumns;
	private boolean isExplicitAssociationTable;
	private Ejb3Column[] elementColumns;
	private boolean isEmbedded;
	private XProperty property;
	private boolean ignoreNotFound;
	private TableBinder tableBinder;

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}


	public void setCascadeStrategy(String cascadeStrategy) {
		this.cascadeStrategy = cascadeStrategy;
	}

	public void setPropertyAccessorName(String propertyAccessorName) {
		this.propertyAccessorName = propertyAccessorName;
	}

	private String propertyAccessorName;

	public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
		this.inverseJoinColumns = inverseJoinColumns;
	}

	public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
		this.joinColumns = joinColumns;
	}

	private Ejb3JoinColumn[] joinColumns;

	public void setPropertyHolder(PropertyHolder propertyHolder) {
		this.propertyHolder = propertyHolder;
	}

	public void setBatchSize(BatchSize batchSize) {
		this.batchSize = batchSize == null ? -1 : batchSize.size();
	}

	public void setEjb3OrderBy(javax.persistence.OrderBy orderByAnn) {
		if ( orderByAnn != null ) {
			hqlOrderBy = orderByAnn.value();
		}
	}

	public void setSqlOrderBy(OrderBy orderByAnn) {
		if ( orderByAnn != null ) {
			if ( ! AnnotationBinder.isDefault( orderByAnn.clause() ) ) orderBy = orderByAnn.clause();
		}
	}

	public void setSort(Sort sortAnn) {
		if ( sortAnn != null ) {
			isSorted = ! SortType.UNSORTED.equals( sortAnn.type() );
			if ( isSorted && SortType.COMPARATOR.equals( sortAnn.type() ) ) {
				comparator = sortAnn.comparator();
			}
		}
	}

	/**
	 * collection binder factory
	 */
	public static CollectionBinder getCollectionBinder(
			String entityName, PropertyData inferredData,
			boolean isIndexed
	) {
        XProperty property = inferredData.getProperty();
		if ( property.isArray() ) {
			if ( property.getElementClass().isPrimitive() ) {
				return new PrimitiveArrayBinder();
			}
			else {
                return new ArrayBinder();
			}
		}
        else if ( property.isCollection() ) {
			//TODO consider using an XClass
			Class returnedClass = property.getCollectionClass();
            if ( java.util.Set.class.equals( returnedClass ) ) {
                return new SetBinder();
            }
            else if ( java.util.SortedSet.class.equals( returnedClass ) ) {
                return new SetBinder( true );
            }
            else if ( java.util.Map.class.equals( returnedClass ) ) {
                return new MapBinder();
            }
            else if ( java.util.Collection.class.equals( returnedClass ) ) {
                return new BagBinder();
            }
            else if ( java.util.List.class.equals( returnedClass ) ) {
    			if ( isIndexed ) {
                    return new ListBinder();
    			}
    			else {
                    return new BagBinder();
    			}
    		}
    		else {
    			throw new AnnotationException(
    					returnedClass.getName() + " collection not yet supported: "
    							+ entityName + property.getName()
    			);
    		}
        }
        else {
            return null;
        }
	}

	protected CollectionBinder() {
	}

	protected CollectionBinder(boolean sorted) {
		this.hasToBeSorted = sorted;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

	public void setTableBinder(TableBinder tableBinder) {
		this.tableBinder = tableBinder;
	}

	public void setCollectionType(XClass collectionType) {
		this.collectionType = collectionType;
	}

	public void setTargetEntity(XClass targetEntity) {
		this.targetEntity = targetEntity;
	}

	public void setMappings(ExtendedMappings mappings) {
		this.mappings = mappings;
	}

	protected abstract Collection createCollection(PersistentClass persistentClass);

	public Collection getCollection() {
		return collection;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void bind() {
		this.collection = createCollection( propertyHolder.getPersistentClass() );
		log.debug( "Collection role: " + StringHelper.qualify( propertyHolder.getPath(), propertyName ) );
		collection.setRole( StringHelper.qualify( propertyHolder.getPath(), propertyName ) );

		//set laziness
		collection.setFetchMode( fetchMode );
		collection.setLazy( fetchMode == FetchMode.SELECT );
		collection.setBatchSize( batchSize );
		if ( orderBy != null && hqlOrderBy != null ) {
			throw new AnnotationException(
					"Cannot use sql order by clause in conjunction of EJB3 order by clause: " + safeCollectionRole()
			);
		}

		// set ordering
		if ( orderBy != null ) collection.setOrderBy( orderBy );
		if ( isSorted ) {
			collection.setSorted( true );
			if ( comparator != null ) {
				try {
					collection.setComparator( (Comparator) comparator.newInstance() );
				}
				catch (Exception e) {
					throw new AnnotationException(
							"Could not instantiate comparator class: "
									+ comparator.getName() + "(" + safeCollectionRole() + ")"
					);
				}
			}
		}
		else {
			if ( hasToBeSorted ) {
				throw new AnnotationException(
						"A sorted collection has to define @Sort: "
								+ safeCollectionRole()
				);
			}
		}

		//set cache
		if ( StringHelper.isNotEmpty( cacheConcurrencyStrategy ) ) {
			collection.setCacheConcurrencyStrategy( cacheConcurrencyStrategy );
			collection.setCacheRegionName( cacheRegionName );
		}

		//set filtering
		Iterator<Map.Entry<String, String>> iter = filters.entrySet().iterator();
		if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
		while ( iter.hasNext() ) {
			Map.Entry<String, String> filter = iter.next();
			String name = filter.getKey();
			String cond = filter.getValue();
			if ( AnnotationBinder.isDefault( cond ) ) {
				cond = mappings.getFilterDefinition( name ).getDefaultFilterCondition();
				if ( StringHelper.isEmpty( cond ) ) {
					throw new AnnotationException("no filter condition found for filter " + name + " in "
							+ StringHelper.qualify( propertyHolder.getPath(), propertyName ) );
				}
			}
			collection.addFilter( name, cond );
		}

		//work on association
		boolean isMappedBy = ! AnnotationBinder.isDefault( mappedBy );
		collection.setInverse( isMappedBy );
		String collType = getCollectionType().getName();

		//many to many may need some second pass informations
		if ( ! oneToMany && isMappedBy ) {
			mappings.addMappedBy( collType, mappedBy, propertyName );
		}
		//TODO reducce tableBinder != null and oneToMany
		//FIXME collection of elements shouldn't be executed as a secondpass
		SecondPass sp = getSecondPass(
							fkJoinColumns,
							joinColumns,
							inverseJoinColumns,
							elementColumns,
							isEmbedded,
							property, collType,
							fetchMode,
							ignoreNotFound, oneToMany,
							tableBinder, mappings
					);
		XClass collectionType = getCollectionType();
		if ( collectionType.isAnnotationPresent( Embeddable.class )
				|| property.isAnnotationPresent( CollectionOfElements.class ) ) {
			// do it right away, otherwise @ManyToon on composite element call addSecondPass 
			// and raise a ConcurrentModificationException
			sp.doSecondPass( CollectionHelper.EMPTY_MAP, CollectionHelper.EMPTY_MAP );
		}
		else {
			mappings.addSecondPass( sp, ! isMappedBy);
		}

		mappings.addCollection( collection );

		//property building
		PropertyBinder binder = new PropertyBinder();
		binder.setName( propertyName );
		binder.setValue( collection );
		binder.setCascade( cascadeStrategy );
		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
			collection.setOrphanDelete( true );
		}
		binder.setPropertyAccessorName( propertyAccessorName );
		binder.setInsertable( insertable );
		binder.setUpdatable( updatable );
		Property prop = binder.make();
		//we don't care about the join stuffs because the column is on the association table.
		propertyHolder.addProperty( prop );
	}

	private XClass getCollectionType() {
		if ( AnnotationBinder.isDefault( targetEntity ) ) {
			if ( collectionType != null ) {
				return collectionType;
			}
			else {
				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
						+ safeCollectionRole();
				throw new AnnotationException( errorMsg );
			}
		}
		else {
			return targetEntity;
		}
	}

	public SecondPass getSecondPass(
			final Ejb3JoinColumn[] fkJoinColumns, final Ejb3JoinColumn[] keyColumns,
			final Ejb3JoinColumn[] inverseColumns,
			final Ejb3Column[] elementColumns,
			final boolean isEmbedded,
			final XProperty property, final String collType,
			final FetchMode fetchMode, final boolean ignoreNotFound, final boolean unique,
			final TableBinder assocTableBinder, final ExtendedMappings mappings
	) {

		return new CollectionSecondPass( mappings, collection ) {

			public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
					throws MappingException {
				bindStarToManySecondPass(
						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
						isEmbedded, property, fetchMode, unique, assocTableBinder, ignoreNotFound, mappings
				);

			}
		};
	}

	/**
	 * return true if it's a Fk, false if it's an association table
	 */
	protected boolean bindStarToManySecondPass(
			Map persistentClasses, String collType, Ejb3JoinColumn[] fkJoinColumns,
			Ejb3JoinColumn[] keyColumns, Ejb3JoinColumn[] inverseColumns, Ejb3Column[] elementColumns,
			boolean isEmbedded,
			XProperty property, FetchMode fetchMode, boolean unique,
			TableBinder associationTableBinder, boolean ignoreNotFound, ExtendedMappings mappings
	) {
		boolean isEntity = persistentClasses.containsKey( collType );
		if ( isEntity
				&& oneToMany
				&& ! this.isExplicitAssociationTable
				&& ( joinColumns[0].isImplicit() && ! AnnotationBinder.isDefault( this.mappedBy )
				|| ! fkJoinColumns[0].isImplicit() ) ) {
			//this is a Foreign key
			bindOneToManySecondPass(
					getCollection(),
					persistentClasses,
					fkJoinColumns,
					collType,
					cascadeDeleteEnabled,
					ignoreNotFound, hqlOrderBy,
					mappings
			);
			return true;
		}
		else {
			//this is an association table
			bindManyToManySecondPass(
					this.collection,
					persistentClasses,
					keyColumns,
					inverseColumns,
					elementColumns,
					isEmbedded, collType,
					fetchMode,
					ignoreNotFound, unique,
					cascadeDeleteEnabled,
					associationTableBinder, property, mappings
			);
			return false;
		}
	}

	protected void bindOneToManySecondPass(
			Collection collection, Map persistentClasses, Ejb3JoinColumn[] fkJoinColumns,
			String collectionType,
			boolean cascadeDeleteEnabled, boolean ignoreNotFound, String hqlOrderBy, ExtendedMappings extendedMappings
	) {
		if ( log.isDebugEnabled() ) {
			log.debug(
					"Binding a OneToMany: " + propertyHolder.getEntityName() + "." + propertyName + " through a foreign key"
			);
		}
		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( collection.getOwner() );
		collection.setElement( oneToMany );
		oneToMany.setReferencedEntityName( collectionType );
		oneToMany.setIgnoreNotFound( ignoreNotFound );

		String assocClass = oneToMany.getReferencedEntityName();
		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
		String orderBy = buildOrderByClauseFromHql( hqlOrderBy, associatedClass, collection.getRole() );
		if ( orderBy != null ) collection.setOrderBy( orderBy );
		if ( mappings == null ) {
			throw new AssertionFailure(
					"CollectionSecondPass for oneToMany should not be called with null mappings"
			);
		}
		Map<String, Join> joins = mappings.getJoins( assocClass );
		if ( associatedClass == null ) {
			throw new MappingException(
					"Association references unmapped class: " + assocClass
			);
		}
		oneToMany.setAssociatedClass( associatedClass );
		for ( Ejb3JoinColumn column : fkJoinColumns ) {
			column.setPersistentClass( associatedClass, joins );
			column.setJoins( joins );
			collection.setCollectionTable( column.getTable() );
		}
		log.info(
				"Mapping collection: " + collection.getRole() + " -> " + collection.getCollectionTable().getName()
		);

		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, mappings );
		if ( !collection.isInverse()
				&& !collection.getKey().isNullable() ) {
			// for non-inverse one-to-many, with a not-null fk, add a backref!
			String entityName = oneToMany.getReferencedEntityName();
			PersistentClass referenced = mappings.getClass( entityName );
			Backref prop = new Backref();
			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + "Backref" );
			prop.setUpdateable( false );
			prop.setSelectable( false );
			prop.setCollectionRole( collection.getRole() );
			prop.setEntityName( collection.getOwner().getEntityName() );
			prop.setValue( collection.getKey() );
			referenced.addProperty( prop );
		}
	}

	public void setCache(Cache cacheAnn) {
		if ( cacheAnn != null ) {
			cacheRegionName = AnnotationBinder.isDefault( cacheAnn.region() ) ? null : cacheAnn.region();
			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
		}
		else {
			cacheConcurrencyStrategy = null;
			cacheRegionName = null;
		}
	}

	public void setFetchType(FetchType fetch) {
		if ( fetch == FetchType.EAGER ) {
			fetchMode = FetchMode.JOIN;
		}
		else {
			fetchMode = FetchMode.SELECT;
		}
	}

	public void addFilter(String name, String condition) {
		filters.put( name, condition );
	}

	public void setWhere(Where whereAnn) {
		if ( whereAnn != null ) {
			where = whereAnn.clause();
		}
	}

	public void setOneToMany(boolean oneToMany) {
		this.oneToMany = oneToMany;
	}

	public void setIndexColumn(IndexColumn indexColumn) {
		this.indexColumn = indexColumn;
	}

	public void setMapKey(MapKey key) {
		if ( key != null ) {
			mapKeyPropertyName = key.name();
		}
	}

	private static String buildOrderByClauseFromHql(String hqlOrderBy, PersistentClass associatedClass, String role) {
		String orderByString = null;
		if ( hqlOrderBy != null ) {
			List<String> properties = new ArrayList<String>();
			List<String> ordering = new ArrayList<String>();
			StringBuilder orderByBuffer = new StringBuilder();
			if ( "".equals( hqlOrderBy ) ) {
				//order by id
				Iterator it = associatedClass.getIdentifier().getColumnIterator();
				while ( it.hasNext() ) {
					Column col = (Column) it.next();
					orderByBuffer.append( col.getName() ).append( " asc" ).append( ", " );
				}
			}
			else {
				StringTokenizer st = new StringTokenizer( hqlOrderBy, " ,", false );
				String currentOrdering = null;
				//FIXME make this code decent
				while ( st.hasMoreTokens() ) {
					String token = st.nextToken();
					if ( isNonPropertyToken( token ) ) {
						if ( currentOrdering != null ) {
							throw new AnnotationException(
									"Error while parsing HQL orderBy clause: " + hqlOrderBy
											+ " (" + role + ")"
							);
						}
						currentOrdering = token;
					}
					else {
						//Add ordering of the previous
						if ( currentOrdering == null ) {
							//default ordering
							ordering.add( "asc" );
						}
						else {
							ordering.add( currentOrdering );
							currentOrdering = null;
						}
						properties.add( token );
					}
				}
				ordering.remove( 0 ); //first one is the algorithm starter
				// add last one ordering
				if ( currentOrdering == null ) {
					//default ordering
					ordering.add( "asc" );
				}
				else {
					ordering.add( currentOrdering );
					currentOrdering = null;
				}
				int index = 0;

				for ( String property : properties ) {
					Property p = BinderHelper.findPropertyByName( associatedClass, property );
					if ( p == null ) {
						throw new AnnotationException(
								"property from @OrderBy clause not found: "
										+ associatedClass.getEntityName() + "." + property
						);
					}

					Iterator propertyColumns = p.getColumnIterator();
					while ( propertyColumns.hasNext() ) {
						Column column = (Column) propertyColumns.next();
						orderByBuffer.append( column.getName() )
								.append( " " )
								.append( ordering.get( index ) )
								.append( ", " );
					}
					index++;
				}
			}
			orderByString = orderByBuffer.substring( 0, orderByBuffer.length() - 2 );
		}
		return orderByString;
	}

	private static boolean isNonPropertyToken(String token) {
		if ( " ".equals( token ) ) return true;
		if ( ",".equals( token ) ) return true;
		if ( token.equalsIgnoreCase( "desc" ) ) return true;
		if ( token.equalsIgnoreCase( "asc" ) ) return true;
		return false;
	}

	private static SimpleValue buildCollectionKey(
			Collection collValue, Ejb3JoinColumn[] joinColumns, boolean cascadeDeleteEnabled,
			ExtendedMappings mappings
	) {
		//binding key reference using column
		KeyValue keyVal;
		//give a chance to override the referenced property name
		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
					joinColumns[0].getPropertyHolder().getEntityName();
			String propRef = mappings.getPropertyReferencedAssociation(
					entityName,
					joinColumns[0].getMappedBy()
			);
			if ( propRef != null ) {
				collValue.setReferencedPropertyName( propRef );
				mappings.addPropertyReference( collValue.getOwnerEntityName(), propRef );
			}
		}
		String propRef = collValue.getReferencedPropertyName();
		if ( propRef == null ) {
			keyVal = collValue.getOwner().getIdentifier();
		}
		else {
			keyVal = (KeyValue) collValue.getOwner()
					.getProperty( propRef )
					.getValue();
		}
		DependantValue key = new DependantValue( collValue.getCollectionTable(), keyVal );
		key.setTypeName( null );
		Ejb3JoinColumn.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
		collValue.setKey( key );
		return key;
	}

	protected static void bindManyToManySecondPass(
			Collection collValue,
			Map persistentClasses,
			Ejb3JoinColumn[] joinColumns,
			Ejb3JoinColumn[] inverseJoinColumns,
			Ejb3Column[] elementColumns,
			boolean isEmbedded,
			String collType,
			FetchMode fetchMode,
			boolean ignoreNotFound, boolean unique,
			boolean cascadeDeleteEnabled,
			TableBinder associationTableBinder, XProperty property, ExtendedMappings mappings
	) throws MappingException {

		PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType );
		boolean isCollectionOfEntities = collectionEntity != null;
		if ( log.isDebugEnabled() ) {
			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
			if ( isCollectionOfEntities && unique ) {
				log.debug( "Binding a OneToMany: " + path + " through an association table" );
			}
			else if ( isCollectionOfEntities ) {
				log.debug( "Binding as ManyToMany: " + path );
			}
			else {
				log.debug( "Binding a collection of element: " + path );
			}
		}
		boolean mappedBy = ! AnnotationBinder.isDefault( joinColumns[0].getMappedBy() );
		if ( mappedBy ) {
			if ( ! isCollectionOfEntities ) {
				StringBuilder error = new StringBuilder( 80 )
						.append(
								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
						)
						.append( collValue.getOwnerEntityName() )
						.append( "." )
						.append( joinColumns[0].getPropertyName() );
				throw new AnnotationException( error.toString() );
			}
			Property otherSideProperty;
			try {
				otherSideProperty = collectionEntity.getProperty( joinColumns[0].getMappedBy() );
			}
			catch (MappingException e) {
				StringBuilder error = new StringBuilder( 80 );
				error.append( "mappedBy reference an unknown property: " )
						.append( collType ).append( "." ).append( joinColumns[0].getMappedBy() )
						.append( " in " )
						.append( collValue.getOwnerEntityName() )
						.append( "." )
						.append( joinColumns[0].getPropertyName() );
				throw new AnnotationException( error.toString() );
			}
			Table table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
			collValue.setCollectionTable( table );
			String entityName = collectionEntity.getEntityName();
			for ( Ejb3JoinColumn column : joinColumns ) {
				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
				column.setManyToManyOwnerSideEntityName( entityName );
			}
		}
		else {
			//TODO: only for implicit columns?
			//FIXME NamingStrategy
			for ( Ejb3JoinColumn column : joinColumns ) {
				String mappedByProperty = mappings.getFromMappedBy( collValue.getOwnerEntityName(), column.getPropertyName() );
				column.setMappedByPropertyName( mappedByProperty );
				Table ownerTable = collValue.getOwner().getTable();
				column.setMappedByTableName( mappings.getLogicalTableName( ownerTable ) );
//				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
//				column.setDefaultColumnHeader( header );
			}
			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
				//default value
				associationTableBinder.setDefaultName(
						mappings.getLogicalTableName( collValue.getOwner().getTable() ),
						collectionEntity != null ? mappings.getLogicalTableName( collectionEntity.getTable() ) : null,
						joinColumns[0].getPropertyName()
				);
			}
			collValue.setCollectionTable( associationTableBinder.bind() );
		}

		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, mappings );

		ManyToOne element = null;
		if ( isCollectionOfEntities ) {
			element =
					new ManyToOne( collValue.getCollectionTable() );
			collValue.setElement( element );
			element.setReferencedEntityName( collType );
			//element.setFetchMode( fetchMode );
			//element.setLazy( fetchMode != FetchMode.JOIN );
			//make the second join non lazy
			element.setFetchMode( FetchMode.JOIN );
			element.setLazy( false );
			element.setIgnoreNotFound( ignoreNotFound );
		}
		else {
			XClass elementClass;
			AnnotatedClassType classType;
//			Map<String, javax.persistence.Column[]> columnOverrides = PropertyHolderBuilder.buildColumnOverride(
//					property, StringHelper.qualify( collValue.getRole(), "element" )
//			);
			//FIXME the "element" is lost
			PropertyHolder holder = null;
			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType ) ) {
				classType = AnnotatedClassType.NONE;
				elementClass = null;
			}
			else {
				try {
					elementClass =  ReflectionManager.INSTANCE.classForName( collType, CollectionBinder.class );
				}
				catch (ClassNotFoundException e) {
					throw new AnnotationException( "Unable to find class: " + collType, e );
				}
				classType = mappings.getClassType( elementClass );

				holder = PropertyHolderBuilder.buildPropertyHolder(
						collValue,
						collValue.getRole(),
						elementClass,
						property
				);
				//force in case of attribute override
				boolean attributeOverride = property.isAnnotationPresent(AttributeOverride.class)
						|| property.isAnnotationPresent( AttributeOverrides.class);
				if ( isEmbedded || attributeOverride ) {
					classType = AnnotatedClassType.EMBEDDABLE;
				}
			}

			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
				EntityBinder entityBinder = new EntityBinder();
				Embeddable embeddable = (Embeddable) elementClass.getAnnotation( Embeddable.class );
				PersistentClass owner = collValue.getOwner();
				boolean isPropertyAnnotated;
				AccessType access = ( (XAnnotatedElement) elementClass).getAnnotation( AccessType.class );
				//FIXME support @Access for collection of elements
				//String accessType = access != null ? access.value() : null;
				if ( owner.getIdentifierProperty() != null) {
					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
				}
				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
					isPropertyAnnotated = prop.getPropertyAccessorName().equals("property");
				}
				else {
					throw new AssertionFailure( "Unable to guess collection property accessor name" );
				}

				//boolean propertyAccess = embeddable == null || AccessType.PROPERTY.equals( embeddable.access() );
				PropertyData inferredData = new PropertyPreloadedData( "property", "element", elementClass );
				//TODO be smart with isNullable
				Component component = AnnotationBinder.fillComponent(
						holder, inferredData, isPropertyAnnotated, isPropertyAnnotated ? "property" : "field", true,
						entityBinder, false, false,
						mappings
				);
				collValue.setElement( component );
			}
			else {
				SimpleValueBinder elementBinder = new SimpleValueBinder();
				elementBinder.setMappings( mappings );
				elementBinder.setReturnedClassName( collType );
				if ( elementColumns == null || elementColumns.length == 0 ) {
					elementColumns = new Ejb3Column[1];
					Ejb3Column column = new Ejb3Column();
					column.setImplicit( false );
					//not following the spec but more clean
					column.setNullable( true );
					column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
					column.setLogicalColumnName( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
					//TODO create an EMPTY_JOINS collection
					column.setJoins( new HashMap<String, Join>() );
					column.setMappings( mappings );
					column.bind();
					elementColumns[0] = column;
				}
				//override the table
				for ( Ejb3Column column : elementColumns ) {
					column.setTable( collValue.getCollectionTable() );
				}
				elementBinder.setColumns( elementColumns );
				elementBinder.setType( property, elementClass );
				collValue.setElement( elementBinder.make() );
			}
		}

		checkFilterConditions( collValue );

		//FIXME: do optional = false
		if ( isCollectionOfEntities ) {
			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, mappings );
		}

	}

	private static void checkFilterConditions(Collection collValue) {
		//for now it can't happen, but sometime soon...
		if ( ( collValue.getFilterMap().size() != 0 || StringHelper.isNotEmpty( collValue.getWhere() ) ) &&
				collValue.getFetchMode() == FetchMode.JOIN &&
				collValue.getElement().getFetchMode() != FetchMode.JOIN ) {
			throw new MappingException(
					"@ManyToMany defining filter or where without join fetching "
							+ "not valid within collection using join fetching[" + collValue.getRole() + "]"
			);
		}
	}

	private static void bindCollectionSecondPass(
			Collection collValue, PersistentClass collectionEntity, Ejb3JoinColumn[] joinColumns,
			boolean cascadeDeleteEnabled,
			ExtendedMappings mappings
	) {
		BinderHelper.createSyntheticPropertyReference(
				joinColumns, collValue.getOwner(), collectionEntity, collValue, false, mappings
		);
		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, mappings );
		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, mappings );
	}

	public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
		this.cascadeDeleteEnabled = onDeleteCascade;
	}

	private String safeCollectionRole() {
		if ( propertyHolder != null ) {
			return propertyHolder.getEntityName() + "." + propertyName;
		}
		else {
			return "";
		}
	}


	/**
	 * bind the inverse FK of a ManyToMany
	 * If we are in a mappedBy case, read the columns from the associated
	 * colletion element
	 * Otherwise delegates to the usual algorithm
	 */
	public static void bindManytoManyInverseFk(
			PersistentClass referencedEntity, Ejb3JoinColumn[] columns, SimpleValue value, boolean unique,
			ExtendedMappings mappings
	) {
		final String mappedBy = columns[0].getMappedBy();
		if ( StringHelper.isNotEmpty( mappedBy ) ) {
			final Property property = referencedEntity.getProperty( mappedBy );
			Iterator mappedByColumns = ( (Collection) property.getValue() ).getKey().getColumnIterator();
			while ( mappedByColumns.hasNext() ) {
				Column column = (Column) mappedByColumns.next();
				columns[0].linkValueUsingAColumnCopy( column, value );
			}
			String referencedPropertyName =
					mappings.getPropertyReferencedAssociation(
							"inverse__" + referencedEntity.getEntityName(), mappedBy
					);
			if ( referencedPropertyName != null ) {
				//TODO always a many to one?
				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
				mappings.addUniquePropertyReference( referencedEntity.getEntityName(), referencedPropertyName );
			}
			value.createForeignKey();
		}
		else {
			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, mappings );
			TableBinder.bindFk( referencedEntity, null, columns, value, unique, mappings );
		}
	}

	public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
		this.fkJoinColumns = ejb3JoinColumns;
	}

	public void setExplicitAssociationTable(boolean explicitAssocTable) {
		this.isExplicitAssociationTable = explicitAssocTable;
	}

	public void setElementColumns(Ejb3Column[] elementColumns) {
		this.elementColumns = elementColumns;
	}

	public void setEmbedded(boolean annotationPresent) {
		this.isEmbedded = annotationPresent;
	}

	public void setProperty(XProperty property) {
		this.property = property;
	}

	public void setIgnoreNotFound(boolean ignoreNotFound) {
		this.ignoreNotFound = ignoreNotFound;
	}
}