package application.java;

import java.util.concurrent.Callable;
import javafx.concurrent.Task;

public class TaskRunner {

	public static <T> Task<T> create(Callable<T> callable) {
		return new Task<T>() {
			@Override
			public T call() throws Exception {
				return callable.call();
			}
		};
	}
}


