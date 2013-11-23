public class Employee<E> {

    public static class Internal {

        public Internal(int sala) {
            salary = sala;
        }

        public int salary;

        public synchronized void incSalaray() {
            AC_1_field=100;
salary++;
        }

        public synchronized int getSalary() {
            AC_1_field=100;
return salary;
        }
        int AC_1_field;
}

    public static void main(String[] args) {
    }
}
