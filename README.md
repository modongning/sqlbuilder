# Sqlbuilder

简易版本的ORM，只支持单表简单的curd操作。添加注解，即可根据对象模型，自动拼装sql插入，修改，查询语句

## 实体添加注解

```java
@DBTable(name = "t_user") //声明数据表名
public class User implements Serializable {
	@DBId //声明ID
	private Long id;
	private String name;
	@DBTransient //数据库相关操作时忽略字段
	privare String flag;
}
```

## DAO继承BaseDAOImpl

继承`BaseDAOImpl`无需编写其他代码，既可以直接使用常规的CURD操作。如果需要特定的语句执行，则可以编写sql，然后交给`JdbcHelper`执行

```java
@Repository
public class UserDAOImpl extends BaseDAOImpl<User> implements UserDAO {

	/**
	 * 基础CURD不满足的情况下，也可自行实现。以下是demo
	 * 
	 * @return 
	 */
	@Override
	public List<User> queryByStateList() {
		String sql = "SELECT * FROM User u WHERE u.state IN(1,2)";
		JdbcHelper<User> helper = new JdbcHelper<User>(super.jdbcTemplate);
		return helper.doQuery(User.class, sql);
	}

}
```

## 在service中使用

定义好dao之后，就可以在业务中使用了

```java
	
@Autowired
private UserDAO userDAO;

public void dosomeing(){
    List<User> list = userDAO.queryByState();

    User queryUser = new User();
    queryUser.setName("Tom");

    //默认实现的查询
    list = userDAO.query(queryUser);
    //自定义实现的查询
    list = userDAO.queryByStateList();
    
    //TODO 。。。业务逻辑	
}

```


## 邮箱

modongning@163.com

