package application.java;

public class Task {
	int id;
	String name;
	int menuID;
	int progress;
	String link;
	
	public Task(int id, String name, int menuID, int progress, String link) {
		this.id = id;
		this.name = name;
		this.menuID = menuID;
		this.progress = progress;
		this.link = link;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMenuID() {
		return menuID;
	}

	public void setMenuID(int menuID) {
		this.menuID = menuID;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
