package sem2proj;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParseLog {

    public static void main(String[] args) {
        
        String logFilePath = "extracted_log";
         Map<String, Integer> jobCountByPartition = new HashMap<>();

        System.out.printf("%-25s %-30s %-15s %-15s %-15s %-10s%n", 
            "Timestamp", "Action", "Job ID", "InitPrio", "usec", "Exit Status");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(logFilePath)))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] logParts = line.split(" ");

                if (logParts.length >= 3) {
                    String timestamp = logParts[0].replace("[", "") + " " + logParts[1].replace("]", "");
                    String action = logParts[2];

                    String jobId = "";
                    String initialPriority = "";
                    String microseconds = "";
                    String exitStatus = "";

                    for (String part : logParts) {
                        if (part.startsWith("JobId=")) {
                            jobId = part.substring("JobId=".length());
                        } else if (part.startsWith("InitPrio=")) {
                            initialPriority = part.substring("InitPrio=".length());
                        } else if (part.startsWith("usec=")) {
                            microseconds = part.substring("usec=".length());
                        } else if (part.startsWith("WEXITSTATUS")) {
                            exitStatus = part.substring("WEXITSTATUS".length());     
                        } else if(part.startsWith("Partition=")){
                            partition = part.substring("Partition=".length());  
                        } 
                    }

                    // Update job count by partition
                    if (!partition.isEmpty()) {
                        jobCountByPartition.put(partition, jobCountByPartition.getOrDefault(partition, 0) + 1);
                    }

                    System.out.printf("%-25s %-30s %-15s %-15s %-15s %-10s%n", 
                        timestamp, action, jobId, initialPriority, microseconds, exitStatus);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Print the job count by partition
        System.out.println("\nJobs by Partition:");
        for (Map.Entry<String, Integer> entry : jobCountByPartition.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
