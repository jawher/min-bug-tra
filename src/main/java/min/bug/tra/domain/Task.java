package min.bug.tra.domain;

public class Task {
	public static enum TaskType {
		BUG, ENHANCEMENT, NEW_FEATURE;
	}

	public static enum TaskStatus {
		OPEN, CLOSED;
	}

	private Long id;
	private String title;
	private String description;
	private TaskType type;
	private TaskStatus status;

	public Task(Long id, String title, String description, TaskType type,
			TaskStatus status) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.type = type;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

}
