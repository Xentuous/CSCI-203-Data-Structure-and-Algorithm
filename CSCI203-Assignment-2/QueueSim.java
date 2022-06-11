import java.io.*;
import java.time.*;
import java.util.*;

public class QueueSim {
    private static final ArrayList<Customer> customerList = new ArrayList<>();
    private static final ArrayList<PrimaryServer> primaryServerList = new ArrayList<>();
    private static final ArrayList<SecondaryServer> secondaryServerList = new ArrayList<>();
    private static final Queue primaryQueue = new Queue();
    private static final Queue secondaryQueue = new Queue();
    private static int operationalTime = 0;
    private static int arrayListPosition = 0;
    private static int customerCounter = 0;
    private static int totalServiceTime = 0;
    private static int primaryQueueCounter = 0;
    private static int secondaryQueueCounter = 0;
    private static int primaryQueueMaxLength = 0;
    private static int secondaryQueueMaxLength = 0;
    private static int primaryQueueWaitingTime = 0;
    private static int primaryQueueWaitingLength = 0;
    private static int secondaryQueueWaitingTime = 0;
    private static int secondaryQueueWaitingLength = 0;

    /**
     * @param args Parse in cli argument into fread() method.
     *             To read A2data6.txt file and store into arraylist.
     * @throws FileNotFoundException throws error if no file found
     */
    private static void init(String args) throws FileNotFoundException {
        System.out.println(args + " is used for this simulation.");
        System.out.println("Start simulation using filename -> " + args);
        String line;
        FileReader file = new FileReader(args);
        BufferedReader br = new BufferedReader(file);

        try {
            line = br.readLine();
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                if (st.countTokens() <= 2) {
                    int numOfPrimary = Integer.parseInt(st.nextToken());
                    int numOfSecondary = Integer.parseInt(st.nextToken());
                    System.out.printf("This simulation uses %d primary server(s), and %d secondary server(s).%n", numOfPrimary, numOfSecondary);
                    for (int i = 0; i < numOfPrimary; i++) primaryServerList.add(new PrimaryServer(i));
                    for (int i = 0; i < numOfSecondary; i++) secondaryServerList.add(new SecondaryServer(i));
                } else {
                    int MIN = Integer.parseInt(st.nextToken());
                    int pri = Integer.parseInt(st.nextToken());
                    int sec = Integer.parseInt(st.nextToken());
                    if (MIN == 0 && pri == 0 && sec == 0) break;
                    customerList.add(new Customer(MIN, pri, sec));
                }
                line = br.readLine();
            }
            br.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * To feed queue base on operational timing with customer minute of arrival
     */
    private static void feed() {
        for (int i = arrayListPosition; i < customerList.size(); i++) {
            if (operationalTime == customerList.get(arrayListPosition).getMin()) {
                primaryQueue.enqueue(customerList.get(arrayListPosition));
                arrayListPosition++;
            }
        }
    }

    /**
     * Display relevant information as required in the assignment.
     */
    private static void printInfo(String DAY, String MONTH, int DATE, int HOUR, int MIN, int SEC, int YEAR) {
        System.out.println("\nTotal minute of simulation: " + operationalTime);
        System.out.println("Number of people served: " + customerCounter + "\n");

        System.out.println("Total service time: " + totalServiceTime);
        System.out.printf("Average total service time: %.2f minutes%n%n", (double) totalServiceTime / customerCounter);

        // Total customer time in primary queue / total number of customer waiting in primary queue
        System.out.printf("Average total time in primary server queue: %f minutes%n", (double) primaryQueueWaitingLength / primaryQueueWaitingTime);

        // Total customer time in secondary queue / total number of customer waiting in secondary queue
        System.out.printf("Average total time in secondary server queue: %f minutes%n", (double) secondaryQueueWaitingLength / secondaryQueueWaitingTime);
        float avgTotalTimeInQueue = (float) (((primaryQueueWaitingLength / primaryQueueWaitingTime) + (secondaryQueueWaitingLength / secondaryQueueWaitingTime)) / 2);
        // Total customer time in both queue / total number of customer waiting in both queue
        System.out.printf("Average total time in both queue: %f minutes%n%n", avgTotalTimeInQueue);

        System.out.printf("Total primary queue size: %d%n", primaryQueueCounter);
        System.out.printf("Total secondary queue size: %d%n%n", secondaryQueueCounter);

        // Total number of customers in primary queue / total number of simulation minutes
        System.out.printf("Average length of primary queue: %f%n", (double) primaryQueueWaitingLength / operationalTime);

        // Total number of customers in secondary queue / total number of simulation minutes
        System.out.printf("Average length of secondary queue: %f%n", (double) secondaryQueueWaitingLength / operationalTime);

        int totalCustInTwoQueue = primaryQueueWaitingLength + secondaryQueueWaitingLength;
        // Total number of customers in both queue / total number of simulation minutes
        System.out.printf("Average overall length of queue: %f%n%n", (double) totalCustInTwoQueue / operationalTime);

        // Longest queue in any minute
        System.out.printf("Maximum length of primary queue: %d%n", primaryQueueMaxLength);
        System.out.printf("Maximum length of secondary queue: %d%n", secondaryQueueMaxLength);
        System.out.printf("Average maximum length of queue: %d%n", (primaryQueueMaxLength + secondaryQueueMaxLength) / 2);
        System.out.printf("Maximum length of overall queue: %d%n%n", primaryQueueMaxLength + secondaryQueueMaxLength);

        // Total number of minutes the server is free
        System.out.printf("Total idle time for each server:%n");
        for (int i = 0; i < primaryServerList.size(); i++) System.out.printf("Primary server %d: %d minutes%n", i, primaryServerList.get(i).getIdleMinutes());

        System.out.println();
        for (int i = 0; i < secondaryServerList.size(); i++) System.out.printf("Secondary server %d: %d minutes%n", i, secondaryServerList.get(i).getIdleMinutes());

        System.out.printf("%nSimulation ends after %d minute(s) or %.5f hour(s) of simulation process.", operationalTime, (operationalTime / 60.0));
        System.out.printf("%nSimulation End Time: %s %s %d %02d:%02d:%02d %d%n%n", DAY, MONTH, DATE, HOUR, MIN, SEC, YEAR);
    }

    /**
     * To get the very first available primary server
     *
     * @return Very first available primary server or else return null
     */
    private static PrimaryServer getFirstAvailablePrimaryServer() {
        return primaryServerList.stream().filter(x -> !x.isBusy()).findFirst().orElse(null);
    }

    /**
     * To get the very first available secondary server
     *
     * @return Very first available secondary server or else return null
     */
    private static SecondaryServer getFirstAvailableSecondaryServer() {
        return secondaryServerList.stream().filter(x -> !x.isBusy()).findFirst().orElse(null);
    }

    /**
     * Check for ANY free primary servers, if got free server,
     * dequeue customer from primary queue and assign them
     */
    private static void primaryServer() {
        PrimaryServer server = getFirstAvailablePrimaryServer();
        if (server == null && !primaryQueue.isEmpty()) {
            primaryQueueWaitingTime++;
            primaryQueueWaitingLength += primaryQueue.getSize();
            primaryQueueCounter++;
        }

        if (server == null || primaryQueue.isEmpty()) return;
        if (primaryQueue.getSize() > primaryQueueMaxLength) primaryQueueMaxLength = primaryQueue.getSize();
        server.setCustomer(primaryQueue.dequeue());
        server.setMinutesWithPrimary(server.getCustomer().getPrimary());
        server.setBusy(true);
        customerCounter++;
    }

    /**
     * Handle primary servers that are currently serving time with customers
     * If server is ready to hand over to secondary, customer will proceed to
     * Secondary queue
     */
    private static void handlePrimaryServers() {
        primaryServerList.forEach(server -> {
            if (server.isBusy() && server.getMinutesWithPrimary() != 0) {
                server.reduceMinuteWithPrimary();
            } else if (server.isBusy() && server.getMinutesWithPrimary() == 0) {
                if (!secondaryQueue.isFull()) {
                    secondaryQueue.enqueue(server.getCustomer());
                    server.setCustomer(null);
                    server.setMinutesWithPrimary(0);
                    server.setBusy(false);
                }
            } else if (!server.isBusy()) {
                server.addIdleMinute();
            }
        });
    }

    /**
     * Check for ANY free secondary servers, if got free server,
     * dequeue customer from secondary queue and assign them
     */
    private static void secondaryServer() {
        SecondaryServer server = getFirstAvailableSecondaryServer();
        if (server == null && !secondaryQueue.isEmpty()) {
            secondaryQueueWaitingTime++;
            secondaryQueueWaitingLength += secondaryQueue.getSize();
            secondaryQueueCounter++;
        }
        if (server == null || secondaryQueue.isEmpty()) return;
        if (secondaryQueue.getSize() > secondaryQueueMaxLength) secondaryQueueMaxLength = secondaryQueue.getSize();
        server.setCustomer(secondaryQueue.dequeue());
        server.setMinutesWithSecondary(server.getCustomer().getSecondary());
        server.setBusy(true);
    }

    /**
     * Handle secondary servers that are currently serving time with customers
     * If server is ready to finish with customer, customer is free to go
     */
    private static void handleSecondaryServers() {
        if (secondaryQueue.isEmpty()) return;
        secondaryServerList.forEach(server -> {
            if (server.isBusy() && server.getMinutesWithSecondary() != 0) {
                server.reduceMinuteWithSecondary();
            } else if (server.isBusy() && server.getMinutesWithSecondary() == 0) {
                totalServiceTime += (server.getCustomer().getPrimary() + server.getCustomer().getSecondary());
                server.setMinutesWithSecondary(0);
                server.setCustomer(null);
                server.setBusy(false);
            } else if (!server.isBusy()) {
                server.addIdleMinute();
            }
        });
    }

    /**
     * Perform restaurant operational simulation based on A2data6.txt
     * 1 loop is 1 minute simulation time
     */
    private static void simulate() {
        while (operationalTime <= customerList.get(customerList.size() - 1).getMin()) {
            feed();
            primaryServer();
            handlePrimaryServers();
            secondaryServer();
            handleSecondaryServers();
            operationalTime++;
        }

        while (!secondaryQueue.isEmpty() || primaryServerList.stream().anyMatch(PrimaryServer::isBusy)) {
            handlePrimaryServers();
            secondaryServer();
            handleSecondaryServers();
            operationalTime++;
        }

        while (secondaryServerList.stream().anyMatch(SecondaryServer::isBusy)) {
            secondaryServerList.forEach(server -> {
                if (server.isBusy() && server.getMinutesWithSecondary() != 0) {
                    server.reduceMinuteWithSecondary();
                }
                if (server.isBusy() && server.getMinutesWithSecondary() == 0) {
                    totalServiceTime += (server.getCustomer().getPrimary() + server.getCustomer().getSecondary());
                    server.setMinutesWithSecondary(0);
                    server.setCustomer(null);
                    server.setBusy(false);
                } else if (!server.isBusy()) {
                    server.addIdleMinute();
                }
            });
            operationalTime++;
        }

        // Reduce extra operational time inside while loop iterations to exit the loop
        operationalTime -= 3;
    }

    /**
     * Driver code
     *
     * @param args reads in fileName.txt
     */
    public static void main(String[] args) {
        final LocalDateTime NOW = LocalDateTime.now();
        final DayOfWeek CURRENT_DAY = NOW.getDayOfWeek();
        final Month CURRENT_MONTH = NOW.getMonth();
        final String DAY = CURRENT_DAY.toString().charAt(0) + CURRENT_DAY.toString().toLowerCase().substring(1, 3);
        final String MONTH = CURRENT_MONTH.toString().charAt(0) + CURRENT_MONTH.toString().toLowerCase().substring(1, 3);
        final int DATE = NOW.getDayOfMonth(), HOUR = NOW.getHour(), MIN = NOW.getMinute(), SEC = NOW.getSecond(), YEAR = NOW.getYear();
        try {
            System.out.printf("Simulation Start Time: %s %s %d %02d:%02d:%02d %d%n%n", DAY, MONTH, DATE, HOUR, MIN, SEC, YEAR);
            init(args[0]);
            simulate();
            printInfo(DAY, MONTH, DATE, HOUR, MIN, SEC, YEAR);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class Queue {
    private int front;
    private int rear;
    private final Customer[] arr;
    private int items;
    private final int MAXSIZE;

    /**
     * Constructor
     *
     */
    public Queue() {
        this(10);
    }

    /**
     * Constructor
     *
     * @param queueLength size of queue
     */
    public Queue(int queueLength) {
        this.MAXSIZE = queueLength;
        this.arr = new Customer[queueLength];
        front = 0;
        rear = -1;
        items = 0;
    }


    /**
     * Insert an element from the front of queue
     *
     */
    public void enqueue(Customer customer) {
        if (isFull()) {
            return;
        }

        rear = (rear + 1) % MAXSIZE;
        arr[rear] = customer;
        items++;
    }

    /**
     * Remove an element from the front of queue
     *
     * @return front of the queue
     */
    public Customer dequeue() {
        if (isEmpty()) {
            return null;
        }

        Customer temp = arr[front];
        arr[front] = null;
        front = (front + 1) % MAXSIZE;
        items--;

        return temp;
    }

    /**
     * Returns the front element without removing it
     *
     * @return front of the queue
     */
    public Customer getFront() {
        if (isEmpty()) {
            return null;
        }
        return arr[front];
    }

    /**
     * Returns the rear element without removing it
     *
     * @return rear of the queue
     */
    public Customer getRear() {
        return arr[rear];
    }

    /**
     * Returns true if the queue is empty
     *
     * @return true if the queue is empty
     */
    public boolean isEmpty() {
        return items == 0;
    }

    /**
     * Returns true if the queue is full
     *
     * @return true if the queue is full
     */
    public boolean isFull() {
        return rear == MAXSIZE;
    }

    /**
     * Returns the number of elements in the queue
     *
     * @return number of elements in the queue
     */
    public int getSize() {
        return items;
    }
}

class Customer {
    private final int MIN;
    private final int primary;
    private final int secondary;

    public Customer(int MIN, int primary, int secondary) {
        this.MIN = MIN;
        this.primary = primary;
        this.secondary = secondary;
    }

    public int getMin() {
        return MIN;
    }

    public int getPrimary() {
        return primary;
    }

    public int getSecondary() {
        return secondary;
    }

}

class PrimaryServer {
    private Customer customer;
    private int minutesWithPrimary;
    private boolean isBusy;
    private int idleMinutes;
    private final int index;

    public PrimaryServer(int index) {
        isBusy = false;
        idleMinutes = 0;
        minutesWithPrimary = 0;
        this.index = index;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getMinutesWithPrimary() {
        return minutesWithPrimary;
    }

    public void setMinutesWithPrimary(int minutesWithPrimary) {
        this.minutesWithPrimary = minutesWithPrimary;
    }

    public void reduceMinuteWithPrimary() {
        this.minutesWithPrimary -= 1;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public int getIdleMinutes() {
        return idleMinutes;
    }

    public void addIdleMinute() {
        this.idleMinutes++;
    }

    public int getIndex() {
        return index;
    }
}

class SecondaryServer {
    private Customer customer;
    private int minutesWithSecondary;
    private boolean isBusy;
    private int idleMinutes;
    private final int index;

    public SecondaryServer(int index) {
        isBusy = false;
        idleMinutes = 0;
        minutesWithSecondary = 0;
        this.index = index;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getMinutesWithSecondary() {
        return minutesWithSecondary;
    }

    public void setMinutesWithSecondary(int minutesWithSecondary) {
        this.minutesWithSecondary = minutesWithSecondary;
    }

    public void reduceMinuteWithSecondary() {
        this.minutesWithSecondary -= 1;
    }

    public void addIdleMinute() {
        this.idleMinutes++;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public int getIdleMinutes() {
        return idleMinutes;
    }

    public int getIndex() {
        return index;
    }
}