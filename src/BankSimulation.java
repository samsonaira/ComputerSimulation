import java.util.Random;

public class BankSimulation {
    public static void main(String[] args) {

        final int NUM_CUSTOMERS = 100;
        Random rand = new Random();

        int[] interArrival = new int[NUM_CUSTOMERS];
        int[] arrivalTime = new int[NUM_CUSTOMERS];
        int[] serviceTime = new int[NUM_CUSTOMERS];
        int[] serviceStart = new int[NUM_CUSTOMERS];
        int[] serviceEnd = new int[NUM_CUSTOMERS];
        int[] waitingTime = new int[NUM_CUSTOMERS];
        int[] timeInSystem = new int[NUM_CUSTOMERS];
        int[] idleTime = new int[NUM_CUSTOMERS];

        int totalWaiting = 0;
        int totalService = 0;
        int totalSystemTime = 0;
        int totalIdle = 0;
        int maxWaiting = 0;
        int customersWhoWaited = 0;

        // -----------------------------------------
        // STEP 1: Generate inter-arrival and service times
        // Inter-arrival ~ Uniform(1,8)
        // Service time  ~ Uniform(1,6)
        // -----------------------------------------
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            interArrival[i] = rand.nextInt(8) + 1; // 1 to 8
            serviceTime[i] = rand.nextInt(6) + 1;  // 1 to 6
        }

        // -----------------------------------------
        // STEP 2: Compute arrival times
        // -----------------------------------------
        arrivalTime[0] = interArrival[0];
        for (int i = 1; i < NUM_CUSTOMERS; i++) {
            arrivalTime[i] = arrivalTime[i - 1] + interArrival[i];
        }

        // -----------------------------------------
        // STEP 3: First customer
        // -----------------------------------------
        serviceStart[0] = arrivalTime[0];
        waitingTime[0] = 0;
        idleTime[0] = arrivalTime[0]; // server idle before first customer arrives
        serviceEnd[0] = serviceStart[0] + serviceTime[0];
        timeInSystem[0] = waitingTime[0] + serviceTime[0];

        totalService += serviceTime[0];
        totalSystemTime += timeInSystem[0];
        totalIdle += idleTime[0];

        // -----------------------------------------
        // STEP 4: Remaining customers
        // -----------------------------------------
        for (int i = 1; i < NUM_CUSTOMERS; i++) {

            // Service starts either when customer arrives or when previous service ends
            serviceStart[i] = Math.max(arrivalTime[i], serviceEnd[i - 1]);

            // Waiting time in queue
            waitingTime[i] = serviceStart[i] - arrivalTime[i];

            // Idle time of server before this customer starts
            if (arrivalTime[i] > serviceEnd[i - 1]) {
                idleTime[i] = arrivalTime[i] - serviceEnd[i - 1];
            } else {
                idleTime[i] = 0;
            }

            // Service completion time
            serviceEnd[i] = serviceStart[i] + serviceTime[i];

            // Total time in system
            timeInSystem[i] = waitingTime[i] + serviceTime[i];

            // Totals
            totalWaiting += waitingTime[i];
            totalService += serviceTime[i];
            totalSystemTime += timeInSystem[i];
            totalIdle += idleTime[i];

            // Maximum waiting time
            if (waitingTime[i] > maxWaiting) {
                maxWaiting = waitingTime[i];
            }

            // Count customers who had to wait
            if (waitingTime[i] > 0) {
                customersWhoWaited++;
            }
        }

        // -----------------------------------------
        // STEP 5: Compute statistics
        // -----------------------------------------
        double avgWaiting = (double) totalWaiting / NUM_CUSTOMERS;
        double avgService = (double) totalService / NUM_CUSTOMERS;
        double avgSystem = (double) totalSystemTime / NUM_CUSTOMERS;
        double probabilityWait = (double) customersWhoWaited / NUM_CUSTOMERS;
        int simulationTime = serviceEnd[NUM_CUSTOMERS - 1];
        double utilization = ((double) totalService / simulationTime) * 100.0;

        // -----------------------------------------
        // STEP 6: Print simulation table
        // -----------------------------------------
        System.out.println("================================ BANK QUEUE SIMULATION ================================");
        System.out.printf("%-6s %-12s %-10s %-10s %-12s %-10s %-10s %-12s %-10s%n",
                "Cust", "InterArr", "Arrival", "Service", "StartServ", "EndServ", "Wait", "TimeSys", "Idle");

        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            System.out.printf("%-6d %-12d %-10d %-10d %-12d %-10d %-10d %-12d %-10d%n",
                    (i + 1),
                    interArrival[i],
                    arrivalTime[i],
                    serviceTime[i],
                    serviceStart[i],
                    serviceEnd[i],
                    waitingTime[i],
                    timeInSystem[i],
                    idleTime[i]);
        }

        // -----------------------------------------
        // STEP 7: Print summary statistics
        // -----------------------------------------
        System.out.println("\n=========================== QUEUE STATISTICS ===========================");
        System.out.println("Number of Customers            : " + NUM_CUSTOMERS);
        System.out.printf("Average Waiting Time          : %.2f minutes%n", avgWaiting);
        System.out.printf("Average Service Time          : %.2f minutes%n", avgService);
        System.out.printf("Average Time in System        : %.2f minutes%n", avgSystem);
        System.out.println("Maximum Waiting Time          : " + maxWaiting + " minutes");
        System.out.printf("Probability Customer Waits    : %.2f%n", probabilityWait);
        System.out.printf("Server Utilization            : %.2f%%%n", utilization);
        System.out.println("Total Idle Time of Server     : " + totalIdle + " minutes");
        System.out.println("Total Simulation Time         : " + simulationTime + " minutes");
    }
}