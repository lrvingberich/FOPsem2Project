package com.mycompany.labassignment;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.Map;

public class ChartGenerator {

    public static void createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, String category, Number value) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(value, "Value", category);

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        displayChart(barChart, title);
    }

    public static void createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((category, value) -> dataset.addValue(value, "Value", category));

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        displayChart(barChart, title);
    }

    public static void createPieChart(String title, String category, Number value) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(category, value);

        JFreeChart pieChart = ChartFactory.createPieChart(
                title,
                dataset,
                true, true, false);

        displayChart(pieChart, title);
    }

    public static void createPieChart(String title, Map<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);

        JFreeChart pieChart = ChartFactory.createPieChart(
                title,
                dataset,
                true, true, false);

        displayChart(pieChart, title);
    }

    public static void createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, String category, Number value) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(value, "Value", category);

        JFreeChart lineChart = ChartFactory.createLineChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        displayChart(lineChart, title);
    }

    private static void displayChart(JFreeChart chart, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        ChartPanel chartPanel = new ChartPanel(chart);
        frame.add(chartPanel);
        frame.setVisible(true);
    }
}
