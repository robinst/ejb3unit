//$Id: InjectedDataSourceConnectionProvider.java,v 1.1 2006/04/17 12:11:15 daniel_wiese Exp $
package org.hibernate.ejb.connection;

import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.DatasourceConnectionProvider;

/**
 * A connection provider that uses an injected <tt>DataSource</tt>.
 * Setters has to be called before configure()
 * @see org.hibernate.connection.ConnectionProvider
 * @author Emmanuel Bernard
 */
public class InjectedDataSourceConnectionProvider extends DatasourceConnectionProvider {
	private String user;
	private String pass;

	private static final Log log = LogFactory.getLog(InjectedDataSourceConnectionProvider.class);

	public void setDataSource(DataSource ds) {
		super.setDataSource( ds );
	}

	public void configure(Properties props) throws HibernateException {
		user = props.getProperty(Environment.USER);
		pass = props.getProperty(Environment.PASS);

		if ( getDataSource() == null ) throw new HibernateException( "No datasource provided" );
		log.info( "Using provided datasource" );
	}

}
