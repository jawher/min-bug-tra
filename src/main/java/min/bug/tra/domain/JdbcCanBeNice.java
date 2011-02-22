package min.bug.tra.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

public class JdbcCanBeNice {
	public static interface JdbcAction<T> {
		T doWithConnection(Connection connection) throws SQLException;
	}

	public static interface RowMapper<T> {
		T mapRow(ResultSet resultSet, int row) throws SQLException;
	}

	public static interface ConnectionProvider {
		Connection get() throws SQLException;
	}

	public static ConnectionProvider driverManagerConnectionProvider(
			final String driverClassName, final String url, final String user,
			final String password) {
		return new ConnectionProvider() {

			public Connection get() throws SQLException {
				try {
					Class.forName(driverClassName);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				return DriverManager.getConnection(url, user, password);
			}
		};
	}

	public static ConnectionProvider datasourceConnectionProvider(
			final DataSource dataSource) {
		return new ConnectionProvider() {

			public Connection get() throws SQLException {
				return dataSource.getConnection();
			}
		};
	}

	public static ConnectionProvider cachingConnectionProvider(
			final ConnectionProvider connectionProvider) {
		return new ConnectionProvider() {
			private Connection connection;
			private Lock lock = new ReentrantLock();

			public Connection get() throws SQLException {
				lock.lock();
				try {
					if (connection == null) {
						connection = connectionProvider.get();
					}
				} finally {
					lock.unlock();
				}
				return connection;
			}
		};
	}

	public static <T> T doWithConnection(JdbcAction<T> action,
			ConnectionProvider connectionProvider) {
		try {
			return action.doWithConnection(connectionProvider.get());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> JdbcAction<T> sqlTx(final JdbcAction<T> action) {
		return new JdbcAction<T>() {

			public T doWithConnection(Connection connection)
					throws SQLException {
				boolean originalAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);

				try {
					T res = action.doWithConnection(connection);
					connection.commit();
					return res;
				} catch (SQLException e) {
					try {
						connection.rollback();
					} catch (SQLException e1) {
						throw new RuntimeException(e);
					}
					throw e;
				} finally {
					connection.setAutoCommit(originalAutoCommit);
				}
			}

			@Override
			public String toString() {
				return "tx {" + action + "}";
			}
		};

	}

	public static JdbcAction<Integer> sqlUpdate(final String sql,
			final Object... params) {
		return new JdbcAction<Integer>() {

			public Integer doWithConnection(Connection connection)
					throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
				try {
					return ps.executeUpdate();
				} catch (SQLException e) {
					try {
						ps.close();
					} catch (SQLException e1) {

					}
					throw e;
				}
			}

			@Override
			public String toString() {
				return sql;
			}
		};
	}

	public static JdbcAction<Number> sqlUpdateAndReturnKey(final String sql,
			final Object... params) {
		return new JdbcAction<Number>() {

			public Number doWithConnection(Connection connection)
					throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
				try {
					ps.executeUpdate();
					ResultSet generatedKeys = ps.getGeneratedKeys();
					generatedKeys.next();
					return (Number) generatedKeys.getObject(1);
				} catch (SQLException e) {
					try {
						ps.close();
					} catch (SQLException e1) {

					}
					throw e;
				}
			}

			@Override
			public String toString() {
				return sql + " -> key";
			}
		};
	}

	public static <T> JdbcAction<List<T>> sqlQuery(final String sql,
			final RowMapper<T> rowMapper, final Object... params) {
		return new JdbcAction<List<T>>() {

			public List<T> doWithConnection(Connection connection)
					throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
				ResultSet rs = null;
				try {
					rs = ps.executeQuery();
					List<T> res = new ArrayList<T>();
					int row = 0;
					while (rs.next()) {
						res.add(rowMapper.mapRow(rs, row++));
					}
					return res;
				} finally {
					try {
						rs.close();
					} catch (SQLException e1) {

					}
					try {
						ps.close();
					} catch (SQLException e1) {

					}
				}
			}

			@Override
			public String toString() {
				return sql;
			}
		};
	}

	public static <T> JdbcAction<T> dontThrowSqlException(
			final JdbcAction<T> action) {
		return new JdbcAction<T>() {

			public T doWithConnection(Connection connection)
					throws SQLException {
				try {
					return action.doWithConnection(connection);
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public String toString() {
				return "noSqlException {" + action + "}";
			}
		};
	}

}
