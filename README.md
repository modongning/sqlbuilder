# Sqlbuilder

Spring JdbcTemplate 操作小工具，根据对象模型，自动拼装sql插入，修改，查询语句

快速实现基本的查询（目前只支持单表操作）

## 实体添加注解

```java
	@DBTable(name = "t_user")
	public class User implements Serializable {
		@DBId
		private Long id;
		private String name;
		//数据库相关操作时忽略字段
		@DBTransient
		privare String flag;
	}
```

## DAO继承BaseDAOImpl

```java
	@Repository
	public class UserDAOImpl extends BaseDAOImpl<User> implements UserDAO {

        /**
         * @return 
         */
		@Override
		public List<User> queryByState() {
			String sql = "SELECT * FROM User u WHERE u.state IN(1,2)";
			JdbcHelper<User> helper = new JdbcHelper<User>(super.jdbcTemplate);
			return helper.doQuery(User.class, sql);
		}

	}
```

## 使用

```java
	
    @Autowired
    private UserDAO userDAO;

    public void dosomeing(){
        List<User> list = userDAO.queryByState();
    
        User queryUser = new User();
        queryUser.setName("Tom");
        
        //查询
        list = userDAO.query(queryUser);
        
        //TODO 业务逻辑	
    }

```


## 邮箱

modongning@163.com

