//$Id: QueryBinder.java,v 1.1 2006/04/17 12:11:11 daniel_wiese Exp $
package org.hibernate.cfg.annotations;

import java.util.HashMap;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SqlResultSetMapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.annotations.CacheModeType;
import org.hibernate.annotations.FlushModeType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.ExtendedMappings;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.loader.custom.SQLQueryReturn;
import org.hibernate.loader.custom.SQLQueryRootReturn;
import org.hibernate.loader.custom.SQLQueryScalarReturn;

/**
 * Query binder
 *
 * @author Emmanuel Bernard
 */
public abstract class QueryBinder {
	private static Log log = LogFactory.getLog( QueryBinder.class );

	public static void bindQuery(NamedQuery queryAnn, ExtendedMappings mappings) {
		if ( queryAnn == null ) return;
		if ( AnnotationBinder.isDefault( queryAnn.name() ) ) {
			throw new AnnotationException( "A named query must have a name when used in class or package level" );
		}
		//EJBQL Query
		QueryHint[] hints = queryAnn.hints();
		String queryName = queryAnn.query();
		NamedQueryDefinition query = new NamedQueryDefinition(
				queryName,
				getBoolean(queryName, "org.hibernate.cacheable", hints),
				getString(queryName, "org.hibernate.cacheRegion", hints),
				getInteger(queryName, "org.hibernate.timeout", hints),
				getInteger(queryName, "org.hibernate.fetchSize", hints),
				getFlushMode(queryName, hints),
				getCacheMode(queryName, hints),
				getBoolean( queryName, "org.hibernate.readOnly", hints ),
				getString( queryName, "org.hibernate.comment", hints ),
				null
		);
		mappings.addQuery( queryAnn.name(), query );
		if ( log.isInfoEnabled() ) log.info( "Binding Named query: " + queryAnn.name() + " => " + queryAnn.query() );
	}



	public static void bindNativeQuery(NamedNativeQuery queryAnn, ExtendedMappings mappings) {
		if ( queryAnn == null ) return;
		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
		if ( AnnotationBinder.isDefault( queryAnn.name() ) ) {
			throw new AnnotationException( "A named query must have a name when used in class or package level" );
		}
		NamedSQLQueryDefinition query;
		String resultSetMapping = queryAnn.resultSetMapping();
		QueryHint[] hints = queryAnn.hints();
		String queryName = queryAnn.query();
		if ( ! AnnotationBinder.isDefault( resultSetMapping ) ) {
			//sql result set usage
			query = new NamedSQLQueryDefinition(
					queryName,
					resultSetMapping,
					null,
					getBoolean(queryName, "org.hibernate.cacheable", hints),
					getString(queryName, "org.hibernate.cacheRegion", hints),
					getInteger(queryName, "org.hibernate.timeout", hints),
					getInteger(queryName, "org.hibernate.fetchSize", hints),
					getFlushMode(queryName, hints),
					getCacheMode(queryName, hints),
					getBoolean( queryName, "org.hibernate.readOnly", hints ),
					getString( queryName, "org.hibernate.comment", hints ),
					null,
					getBoolean(queryName, "org.hibernate.callable", hints)
			);
		}
		else if ( ! void.class.equals( queryAnn.resultClass() ) ) {
			//class mapping usage
			//FIXME should be done in a second pass due to entity name?
			final SQLQueryRootReturn entityQueryReturn =
					new SQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
			query = new NamedSQLQueryDefinition(
					queryName,
					new SQLQueryReturn[]{entityQueryReturn},
					new SQLQueryScalarReturn[0],
					null,
					getBoolean(queryName, "org.hibernate.cacheable", hints),
					getString(queryName, "org.hibernate.cacheRegion", hints),
					getInteger(queryName, "org.hibernate.timeout", hints),
					getInteger(queryName, "org.hibernate.fetchSize", hints),
					getFlushMode(queryName, hints),
					getCacheMode(queryName, hints),
					getBoolean( queryName, "org.hibernate.readOnly", hints ),
					getString( queryName, "org.hibernate.comment", hints ),
					null,
					getBoolean(queryName, "org.hibernate.callable", hints)
			);
		}
		else {
			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
		}
		mappings.addSQLQuery( queryAnn.name(), query );
		if (log.isInfoEnabled() ) log.info( "Binding named native query: " + queryAnn.name() + " => " + queryAnn.query() );
	}

	public static void bindNativeQuery(org.hibernate.annotations.NamedNativeQuery queryAnn, ExtendedMappings mappings) {
		if ( queryAnn == null ) return;
		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
		if ( AnnotationBinder.isDefault( queryAnn.name() ) ) {
			throw new AnnotationException( "A named query must have a name when used in class or package level" );
		}
		NamedSQLQueryDefinition query;
		String resultSetMapping = queryAnn.resultSetMapping();
		if ( ! AnnotationBinder.isDefault( resultSetMapping ) ) {
			//sql result set usage
			query = new NamedSQLQueryDefinition(
					queryAnn.query(),
					resultSetMapping,
					null,
					queryAnn.cacheable(),
					AnnotationBinder.isDefault( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
					queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
					queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
					getFlushMode( queryAnn.flushMode() ),
					getCacheMode( queryAnn.cacheMode() ),
					queryAnn.readOnly(),
					AnnotationBinder.isDefault( queryAnn.comment() ) ? null : queryAnn.comment(),
					null,
					queryAnn.callable()
			);
		}
		else if ( ! void.class.equals( queryAnn.resultClass() ) ) {
			//class mapping usage
			//FIXME should be done in a second pass due to entity name?
			final SQLQueryRootReturn entityQueryReturn =
					new SQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
			query = new NamedSQLQueryDefinition(
					queryAnn.query(),
					new SQLQueryReturn[]{entityQueryReturn},
					new SQLQueryScalarReturn[0],
					null,
					queryAnn.cacheable(),
					AnnotationBinder.isDefault( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
					queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
					queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
					getFlushMode( queryAnn.flushMode() ),
					getCacheMode( queryAnn.cacheMode() ),
					queryAnn.readOnly(),
					AnnotationBinder.isDefault( queryAnn.comment() ) ? null : queryAnn.comment(),
					null,
					queryAnn.callable()
			);
		}
		else {
			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
		}
		mappings.addSQLQuery( queryAnn.name(), query );
		if (log.isInfoEnabled() ) log.info( "Binding named native query: " + queryAnn.name() + " => " + queryAnn.query() );
	}

	public static void bindQueries(NamedQueries queriesAnn, ExtendedMappings mappings) {
		if ( queriesAnn == null ) return;
		for ( NamedQuery q : queriesAnn.value() ) {
			bindQuery( q, mappings );
		}
	}

	public static void bindNativeQueries(NamedNativeQueries queriesAnn, ExtendedMappings mappings) {
		if ( queriesAnn == null ) return;
		for ( NamedNativeQuery q : queriesAnn.value() ) {
			bindNativeQuery( q, mappings );
		}
	}

	public static void bindNativeQueries(
			org.hibernate.annotations.NamedNativeQueries queriesAnn, ExtendedMappings mappings
	) {
		if ( queriesAnn == null ) return;
		for ( org.hibernate.annotations.NamedNativeQuery q : queriesAnn.value() ) {
			bindNativeQuery( q, mappings );
		}
	}

	public static void bindQuery(org.hibernate.annotations.NamedQuery queryAnn, ExtendedMappings mappings) {
		if ( queryAnn == null ) return;
		if ( AnnotationBinder.isDefault( queryAnn.name() ) ) {
			throw new AnnotationException( "A named query must have a name when used in class or package level" );
		}

		FlushMode flushMode;
		flushMode = getFlushMode( queryAnn.flushMode() );

		NamedQueryDefinition query = new NamedQueryDefinition(
				queryAnn.query(),
				queryAnn.cacheable(),
				AnnotationBinder.isDefault( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
				queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
				queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
				flushMode,
				getCacheMode( queryAnn.cacheMode() ),
				queryAnn.readOnly(),
				AnnotationBinder.isDefault( queryAnn.comment() ) ? null : queryAnn.comment(),
				null
		);

		mappings.addQuery( queryAnn.name(), query );
		if ( log.isInfoEnabled() ) log.info( "Binding named query: " + queryAnn.name() + " => " + queryAnn.query() );
	}

	private static FlushMode getFlushMode(FlushModeType flushModeType) {
		FlushMode flushMode;
		switch ( flushModeType ) {
			case ALWAYS:
				flushMode = FlushMode.ALWAYS;
				break;
			case AUTO:
				flushMode = FlushMode.AUTO;
				break;
			case COMMIT:
				flushMode = FlushMode.COMMIT;
				break;
			case NEVER:
				flushMode = FlushMode.NEVER;
				break;
			default:
				throw new AssertionFailure( "Unknown flushModeType: " + flushModeType );
		}
		return flushMode;
	}

	private static CacheMode getCacheMode(CacheModeType cacheModeType) {
		switch ( cacheModeType ) {
			case GET:
				return CacheMode.GET;
			case IGNORE:
				return CacheMode.IGNORE;
			case NORMAL:
				return CacheMode.NORMAL;
			case PUT:
				return CacheMode.PUT;
			case REFRESH:
				return CacheMode.REFRESH;
			default:
				throw new AssertionFailure( "Unknown cacheModeType: " + cacheModeType );
		}
	}



	public static void bindQueries(org.hibernate.annotations.NamedQueries queriesAnn, ExtendedMappings mappings) {
		if ( queriesAnn == null ) return;
		for ( org.hibernate.annotations.NamedQuery q : queriesAnn.value() ) {
			bindQuery( q, mappings );
		}
	}

	public static void bindSqlResultsetMapping(SqlResultSetMapping ann, ExtendedMappings mappings) {
		mappings.addSecondPass( new ResultsetMappingSecondPass( ann, mappings ) );
	}

	private static CacheMode getCacheMode(String query, QueryHint[] hints) {
		for (QueryHint hint : hints) {
			if ( "org.hibernate.cacheMode".equals( hint.name() ) ) {
				if ( hint.value().equalsIgnoreCase( CacheMode.GET.toString() ) ) {
					return CacheMode.GET;
				}
				else if ( hint.value().equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
					return CacheMode.IGNORE;
				}
				else if ( hint.value().equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
					return CacheMode.NORMAL;
				}
				else if ( hint.value().equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
					return CacheMode.PUT;
				}
				else if ( hint.value().equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
					return CacheMode.REFRESH;
				}
				else {
					throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hint.name() );
				}
			}
		}
		return null;
	}

	private static FlushMode getFlushMode(String query, QueryHint[] hints) {
		for (QueryHint hint : hints) {
			if ( "org.hibernate.flushMode".equals( hint.name() ) ) {
				if ( hint.value().equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
					return FlushMode.ALWAYS;
				}
				else if ( hint.value().equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
					return FlushMode.AUTO;
				}
				else if ( hint.value().equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
					return FlushMode.COMMIT;
				}
				else if ( hint.value().equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
					return FlushMode.NEVER;
				}
				else {
					throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hint.name() );
				}
			}
		}
		return null;
	}

	private static boolean getBoolean(String query, String hintName, QueryHint[] hints) {
		for (QueryHint hint : hints) {
			if ( hintName.equals( hint.name() ) ) {
				if ( hint.value().equalsIgnoreCase( "true" ) ) {
					return true;
				}
				else if ( hint.value().equalsIgnoreCase( "false" ) ) {
					return false;
				}
				else {
					throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hint.name() );
				}
			}
		}
		return false;
	}

	private static String getString(String query, String hintName, QueryHint[] hints) {
		for (QueryHint hint : hints) {
			if (hintName.equals( hint.name() ) ) {
				return hint.value();
			}
		}
		return null;
	}

	private static Integer getInteger(String query, String hintName, QueryHint[] hints) {
		for (QueryHint hint : hints) {
			if (hintName.equals( hint.name() ) ) {
				try {
					return Integer.decode( hint.value() );
				}
				catch (NumberFormatException nfe) {
					throw new AnnotationException( "Not an integer in hint: " + query + ":" + hint.name(), nfe );
				}
			}
		}
		return null;
	}
}
