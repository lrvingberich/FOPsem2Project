package com.mycompany.labassignment;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JobStatistics {
    private String filePath;

    public JobStatistics(String filePath) {
        this.filePath = filePath;
    }

    public void countDoneStrings() {
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
        ChartGenerator.createBarChart("Completed Jobs", "Category", "Number of Jobs", "Completed", jobCompleteCount);
        ChartGenerator.createPieChart("Completed Jobs", "Completed", jobCompleteCount);
    }

    public void calculateAverageExecutionTime(boolean singleChart) {
        List<Integer> ids = new ArrayList<>();
        List<Date> startTimes = new ArrayList<>();
        List<Date> endTimes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("_slurm_rpc_submit_batch_job")) {
                    int jobId = extractId(line);
                    Date startTime = parseTime(line, dateFormat);
                    ids.add(jobId);
                    startTimes.add(startTime);
                    endTimes.add(null); 
                } else if (line.contains("job_complete")) {
                    int jobId = extractId(line);
                    Date endTime = parseTime(line, dateFormat);
                    int index = ids.indexOf(jobId);
                    if (index != -1) {
                        endTimes.set(index, endTime);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        long totalExecutionTime = 0;
        int count = 0;

        for (int i = 0; i < ids.size(); i++) {
            Date startTime = startTimes.get(i);
            Date endTime = endTimes.get(i);

            if (endTime != null) {
                long executionTime = endTime.getTime() - startTime.getTime();
                totalExecutionTime += executionTime;
                count++;
            }
        }

        if (count > 0) {
            long averageExecutionTime = totalExecutionTime / count;
            double averageExecutionTimeInSeconds = averageExecutionTime / 1000.0;
            double averageExecutionTimeInMinutes = averageExecutionTimeInSeconds / 60.0;

            System.out.println("Average Execution Time: " + averageExecutionTime + " milliseconds");
            System.out.println("Average Execution Time: " + averageExecutionTimeInSeconds + " seconds");
            System.out.println("Average Execution Time: " + averageExecutionTimeInMinutes + " minutes");

            // 调试信息，确保数据正确
            System.out.println("Displaying chart with average execution time in seconds: " + averageExecutionTimeInSeconds);

            // 根据 singleChart 参数决定是否只生成一个图表
            if (singleChart) {
                ChartGenerator.createBarChart("Average Execution Time", "Category", "Time (seconds)", "Average Execution Time", averageExecutionTimeInSeconds);
            } else {
                ChartGenerator.createBarChart("Average Execution Time", "Category", "Time (seconds)", "Average Execution Time", averageExecutionTimeInSeconds);
                ChartGenerator.createLineChart("Average Execution Time", "Category", "Time (seconds)", "Average Execution Time", averageExecutionTimeInSeconds);
            }
        } else {
            System.out.println("No jobs found.");
        }
    }

    public void countPartitions() {
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

        counts.forEach((partition, count) -> {
            System.out.println("Total number of jobs in '" + partition + "' partition: " + count);
        });

        ChartGenerator.createBarChart("Jobs by Partition", "Partition", "Number of Jobs", counts);
        ChartGenerator.createPieChart("Jobs by Partition", counts);
    }

    public void countUserErrors() {
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
        userErrorCount.forEach((user, count) -> {
            System.out.println("User " + user + ": " + count + " errors");
        });

        ChartGenerator.createBarChart("User Errors", "User", "Number of Errors", userErrorCount);
        ChartGenerator.createPieChart("User Errors", userErrorCount);
    }

    public void jobSubmissionStats() {
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

        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total", totalJobs);
        stats.put("Successful", successfulJobs);
        stats.put("Failed", failedJobs);

        ChartGenerator.createBarChart("Job Submissions", "Category", "Number of Jobs", stats);
        ChartGenerator.createPieChart("Job Submissions", stats);
    }

    private int extractId(String logLine) {
        String[] parts = logLine.split(" ");
        for (String part : parts) {
            if (part.startsWith("JobId=")) {
                return Integer.parseInt(part.substring(6));
            }
        }
        return -1;
    }

    private Date parseTime(String logLine, SimpleDateFormat dateFormat) throws ParseException {
        String timestampStr = logLine.substring(1, 24);
        return dateFormat.parse(timestampStr);
    }
}
