package sem2proj;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParseLog {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter mode (a, b, c, d, e): ");
        String mode = scanner.nextLine();


        String filePath = "extracted_log";

        switch (mode) {
            case "a":
                countDoneStrings(filePath);
                break;
            case "b":
                countPartitions(filePath);
                break;
            case "c":
                countUserErrors(filePath);
                break;
            case "d":
                calculateAverageExecutionTime(filePath);
                break;
            case "e":
                jobSubmissionStats(filePath);
                break;
            default:
                System.out.println("Invalid mode. Available modes are: a, b, c, d, e");
        }
    }

    private static void countDoneStrings(String filePath) {
        int jobCompleteCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("done")) {
                    jobCompleteCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total number of completed jobs: " + jobCompleteCount);
    }

    private static void calculateAverageExecutionTime(String logFilePath) {
        List<Integer> Ids = new ArrayList<>();
        List<Date> StartTimes = new ArrayList<>();
        List<Date> EndTimes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("_slurm_rpc_submit_batch_job")) {
                    int jobId = extractId(line);
                    Date startTime = parseTime(line, dateFormat);
                    Ids.add(jobId);
                    StartTimes.add(startTime);
                    EndTimes.add(null); 
                } else if (line.contains("job_complete")) {
                    int jobId = extractId(line);
                    Date endTime = parseTime(line, dateFormat);
                    int index = Ids.indexOf(jobId);
                    if (index != -1) {
                        EndTimes.set(index, endTime);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        long totalExecutionTime = 0;
        int Count = 0;

        for (int i = 0; i < Ids.size(); i++) {
            Date startTime = StartTimes.get(i);
            Date endTime = EndTimes.get(i);

            if (endTime != null) {
                long executionTime = endTime.getTime() - startTime.getTime();
                totalExecutionTime += executionTime;
                Count++;
            }
        }

        if (Count > 0) {
            long averageExecutionTime = totalExecutionTime / Count;
            System.out.println("Average Execution Time: " + averageExecutionTime + " milliseconds");
        } else {
            System.out.println("No jobs found.");
        }
    }

    private static int extractId(String logLine) {
        String[] parts = logLine.split(" ");
        for (String part : parts) {
            if (part.startsWith("JobId=")) {
                return Integer.parseInt(part.substring(6));
            }
        }
        return -1;
    }

    private static Date parseTime(String logLine, SimpleDateFormat dateFormat) throws ParseException {
        String timestampStr = logLine.substring(1, 24);
        return dateFormat.parse(timestampStr);
    }

    private static void countPartitions(String filePath) {
        String[] partitions = {
                "cpu-opteron",
                "gpu-v100s",
                "gpu-k10",
                "gpu-titan",
                "cpu-epyc",
                "gpu-k40c"
        };

        Map<String, Integer> counts = new HashMap<>();
        for (String partition : partitions) {
            counts.put(partition, 0);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (String partition : partitions) {
                    if (line.contains(partition)) {
                        counts.put(partition, counts.get(partition) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String partition : partitions) {
            System.out.println("Total number of jobs in '" + partition + "' partition: " + counts.get(partition));
        }
    }

    private static void countUserErrors(String filePath) {
        Map<String, Integer> userErrorCount = new HashMap<>();
        int totalErrors = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("error: ")) {
                    int userIndex = line.indexOf("user='");
                    if (userIndex != -1) {
                        int userIdStart = userIndex + 6;
                        int userIdEnd = line.indexOf("'", userIdStart);
                        String userId = line.substring(userIdStart, userIdEnd);

                        userErrorCount.put(userId, userErrorCount.getOrDefault(userId, 0) + 1);
                        totalErrors++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total number of errors: " + totalErrors);
        System.out.println("Error count per user:");
        for (Map.Entry<String, Integer> entry : userErrorCount.entrySet()) {
            System.out.println("User " + entry.getKey() + ": " + entry.getValue() + " errors");
        }
    }
    
    private static void jobSubmissionStats(String filePath) {
        int totalJobs = 0;
        int successfulJobs = 0;
        int failedJobs = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("_slurm_rpc_submit_batch_job")) {
                    totalJobs++;
                } else if (line.contains("job_complete")) {
                    if (line.contains("done")) {
                        successfulJobs++;
                    } else {
                        failedJobs++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total number of job submissions: " + totalJobs);
        System.out.println("Number of successful job submissions: " + successfulJobs);
        System.out.println("Number of failed job submissions: " + failedJobs);
    }
}
