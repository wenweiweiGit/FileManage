package cn.intsmaze.core.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.intsmaze.core.dao.BaseDao;
import cn.intsmaze.core.page.PageResult;
import cn.intsmaze.core.util.QueryHelper;
import cn.intsmaze.file.entity.FileResource;
import cn.intsmaze.useremail.entity.UserEmail;

public  class BaseDaoImpl<T> extends HibernateDaoSupport implements BaseDao<T> {
	
	
	//***********************一个关键点
	Class<T> clazz;
	
	public BaseDaoImpl(){
		//使用反射获取T的真实类型，因为该类的子类在实例化时，子类的构造方法会默认调用父类的无参数的构造方法
		ParameterizedType pt =  (ParameterizedType)this.getClass().getGenericSuperclass();//获取到BaseDaoImpl<User>
		clazz = (Class<T>)pt.getActualTypeArguments()[0];
	}
	
	/**使用@Resource注入SessionFactory*/
	@Resource(name="sessionFactory")//注入到set方法中，set方法名任意。
	public final void setSessionFactoryDi(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);//就把sessionFactory的bean赋给了模板
		//HibernateDaoSupport中的HibernateDaoSupport变量
	}
	
	public T findObjectById(Serializable id) {
		return getHibernateTemplate().get(clazz, id);
	}

	public List<T> findObjects() {
		Query query = getSession().createQuery("FROM " + clazz.getSimpleName());
		return query.list();
	}
	
	public void save(T entity) {
		getHibernateTemplate().save(entity);
	}

	public void update(T entity) {
		getHibernateTemplate().update(entity);
	}

	/**使用主键ID，删除对象（删除一个或者多个对象）*/
	public void deleteObjectByIds(Serializable... ids) {
		if(ids!=null && ids.length>0){
			for(Serializable id:ids){
				//先查询
				Object entity = this.findObjectById(id);
				//再删除
				this.getHibernateTemplate().delete(entity);
			}
		}
	}
	
	/**删除（使用集合List进行删除）
	 * 内部是根据集合内每个对象的id属性进行删除的。
	 * 使用的HibernateDaoSupport类里面提供删除集合的方法。
	 * */
	public void deleteObjectByCollection(List<T> list) {
		this.getHibernateTemplate().deleteAll(list);
	}
	
	
	public List<T> findObjects(String hql, List<Object> parameters) {
		Query query = getSession().createQuery(hql);
		if(parameters != null){
			for(int i = 0; i < parameters.size(); i++){
				query.setParameter(i, parameters.get(i));
			}
		}	
		return query.list();
	}

	public List<T> findObjects(QueryHelper queryHelper) {
		Query query = getSession().createQuery(queryHelper.getQueryListHql());
		List<Object> parameters = queryHelper.getParameters();
		if(parameters != null){
			for(int i = 0; i < parameters.size(); i++){
				query.setParameter(i, parameters.get(i));
			}
		}
		return query.list();
	}
	
	
	public PageResult getPageResult(QueryHelper queryHelper, int pageNo, int pageSize) {
		
		Query query = getSession().createQuery(queryHelper.getQueryListHql());
		
		List<Object> parameters = queryHelper.getParameters();
		if(parameters != null){
			for(int i = 0; i < parameters.size(); i++){
				query.setParameter(i, parameters.get(i));
			}
		}
		
		if(pageNo < 1) pageNo = 1;
		
		query.setFirstResult((pageNo-1)*pageSize);//设置数据起始索引号
		query.setMaxResults(pageSize);//每一页的大小
		List items = query.list();
		
		//获取总记录数
		Query queryCount = getSession().createQuery(queryHelper.getQueryCountHql());
		if(parameters != null){
			for(int i = 0; i < parameters.size(); i++){
				queryCount.setParameter(i, parameters.get(i));
			}
		}
		
		long totalCount = (Long)queryCount.uniqueResult();
				
		return new PageResult(totalCount, pageNo, pageSize, items);
	}


	
}
