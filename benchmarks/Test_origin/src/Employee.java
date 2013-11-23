public class Employee<E> {

	public static class Internal {

		public Internal(int sala) {
			salary = sala;
		}

		public int	salary;

		public synchronized void incSalaray() {
			salary++;
		}

		public synchronized int getSalary() {

			return salary;
		}
	}

	public static void main(String[] args) {
	}
}
