package min.bug.tra.domain;

import static min.bug.tra.domain.JdbcCanBeNice.cachingConnectionProvider;
import static min.bug.tra.domain.JdbcCanBeNice.doWithConnection;
import static min.bug.tra.domain.JdbcCanBeNice.dontThrowSqlException;
import static min.bug.tra.domain.JdbcCanBeNice.sqlQuery;
import static min.bug.tra.domain.JdbcCanBeNice.sqlTx;
import static min.bug.tra.domain.JdbcCanBeNice.sqlUpdate;
import static min.bug.tra.domain.JdbcCanBeNice.sqlUpdateAndReturnKey;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import min.bug.tra.domain.JdbcCanBeNice.ConnectionProvider;
import min.bug.tra.domain.JdbcCanBeNice.RowMapper;

public class TaskRepository {

	private static ConnectionProvider connectionProvider = cachingConnectionProvider(JdbcCanBeNice.driverManagerConnectionProvider("org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:file:db/db", "", ""));

	private static final RowMapper<Task> TASK_MAPPER = new RowMapper<Task>() {
		public Task mapRow(ResultSet resultSet, int row) throws SQLException {
			return new Task(resultSet.getLong("id"), resultSet
					.getString("title"), resultSet.getString("description"),
					Task.TaskType.valueOf(resultSet.getString("task_type")),
					Task.TaskStatus.valueOf(resultSet.getString("task_status")));
		}
	};

	public List<Task> selectAll() {
		return doWithConnection(sqlQuery("select * from task", TASK_MAPPER),
				connectionProvider);
	}

	public Task selectById(Long id) {
		List<Task> list = doWithConnection(sqlQuery(
				"select * from task where id=?", TASK_MAPPER, id),
				connectionProvider);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new NoSuchElementException();
		}
	}

	public Task insert(Task task) {
		Long key = doWithConnection(
				sqlTx(sqlUpdateAndReturnKey(
						"insert into task(title, description, task_type, task_status) values(?, ?, ?, ?)",
						task.getTitle(), task.getDescription(), task.getType()
								.toString(), task.getStatus().toString())),
				connectionProvider).longValue();
		task.setId(key);
		return task;
	}

	public boolean update(Task task) {
		return doWithConnection(
				sqlTx(sqlUpdate(
						"update task set title=?, description=?, task_type=?, task_status=? where id=?",
						task.getTitle(), task.getDescription(), task.getType()
						.toString(), task.getStatus().toString(), task
								.getId())), connectionProvider) > 0;
	}

	public boolean deleteById(Long id) {
		return doWithConnection(sqlTx(sqlUpdate("delete from task where id=?",
				id)), connectionProvider) > 0;
	}
}
