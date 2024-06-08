package com.mycompany.labassignment;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Hacker
 */
import com.mycompany.labassignment.JobStatistics;
import java.util.Scanner;

public class ParseLog {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter mode (a, b, c, d, e): ");
        String mode = scanner.nextLine();

        String filePath = "C:\\Users\\Hacker\\Downloads\\extracted_log";

        JobStatistics jobStatistics = new JobStatistics(filePath);
        switch (mode) {
            case "a":
                jobStatistics.countDoneStrings();
                break;
            case "b":
                jobStatistics.countPartitions();
                break;
            case "c":
                jobStatistics.countUserErrors();
                break;
            case "d":
                jobStatistics.calculateAverageExecutionTime(true);
                break;
            case "e":
                jobStatistics.jobSubmissionStats();
                break;
            default:
                System.out.println("Invalid mode. Available modes are: a, b, c, d, e");
        }
    }
}
