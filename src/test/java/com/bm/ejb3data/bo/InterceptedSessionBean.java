package com.bm.ejb3data.bo;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Stateless
@Interceptors({InterceptedSessionBean.DummyInterceptor.class})
public class InterceptedSessionBean implements IMySessionBean {

	public static class DummyInterceptor {

	}

	public List<StockWKNBo> getAllStocks() {
		return null;
	}

	public DataSource getDs() {
		return null;
	}

	public EntityManager getEm() {
		return null;
	}

}
