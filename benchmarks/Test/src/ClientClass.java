public class ClientClass {

    public static void main(String[] args) {
        final Employee.Internal goodEmployee = new Employee.Internal(10);
        Thread t1 = new Thread() {

            public void run() {
                clientMethod(goodEmployee);
            }
        };
        Thread t2 = new Thread() {

            public void run() {
                incorrectCompositions.confirm.dynamicenforce.RuntimeLibraryBothBlocking.checkAtR(Thread.currentThread(), goodEmployee);
                int x = goodEmployee.getSalary();
                System.out.println(x);
            }
        };
        t1.start();
        t2.start();
    }

    public static void clientMethod(Employee.Internal arg) {
    incorrectCompositions.confirm.dynamicenforce.RuntimeLibraryBothBlocking.enterComposition(Thread.currentThread());
incorrectCompositions.confirm.dynamicenforce.RuntimeLibraryBothBlocking.bidAtP(Thread.currentThread(), arg);
        arg.incSalaray();
        System.out.println("middle of the composition");
        incorrectCompositions.confirm.dynamicenforce.RuntimeLibraryBothBlocking.bidAtC(Thread.currentThread(), arg);
        arg.incSalaray();
    incorrectCompositions.confirm.dynamicenforce.RuntimeLibraryBothBlocking.exitComposition(Thread.currentThread()); 
}
}
