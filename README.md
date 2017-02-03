# Sqlbuilder
学习之路，大牛请忽略或指教，谢谢

Spring JdbcTemplate 操作小工具，根据对象模型，自动拼装sql插入，修改，查询语句

目前只支持单表操作

##USAGE

```java
	@DBTable(name = "t_user")
	public class User implements Serializable {
		@DBId
		private Long id;
		private String name;
		//数据库相关操作时忽略字段
		@DBTransient
		privare String flag;
		...
	}
```

```java

	public class BaseDAOImpl<T> implements BaseDAO<T> {
		@Autowired
		protected JdbcTemplate jdbcTemplate;
		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		@Override
		public List<T> query(T t) {
			JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
			return helper.query(t);
		}

		@Override
		public int save(T t) {
			JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
			return helper.save(t);
		}

		@Override
		public int[] batchSave(List<T> list) {
			JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
			return helper.batchSave(list);
		}

		@Override
		public int update(T t) {
			JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
			return helper.update(t);
		}

		@Override
		public int[] batchUpdate(List<T> list) {
			JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
			return helper.batchUpdate(list);
		}

		@Override
		public long saveReturnKey(T t) {
			JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
			return helper.saveReturnKey(t);
		}
	}
```

```java
	@Repository
	public class UserDAOImpl extends BaseDAOImpl<User> implements UserDAO {

		@Override
		public List<User> queryByState() {
			String sql = "SELECT * FROM User u WHERE u.state IN(1,2)";
			JdbcHelper<User> helper = new JdbcHelper<User>(super.jdbcTemplate);
			return helper.doQuery(User.class, sql);
		}

		...

	}
```

```java
	
	@Autowired
	private UserDAO userDAO;

	...

	List<User> list = userDAO.queryByState();


	User queryUser = new User();
	queryUser.setName("Tom");
	list = userDAO.query(queryUser);

	...

```


## Exchange

modongning@163.com

