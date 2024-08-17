import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class HealthReport {
    private String date;
    private String diagnosis;
    private String doctor;
    private String treatment;

    public HealthReport(String date, String diagnosis, String doctor, String treatment) {
        this.date = date;
        this.diagnosis = diagnosis;
        this.doctor = doctor;
        this.treatment = treatment;
    }

    public void displayReport() {
        System.out.println("Date: " + date);
        System.out.println("Diagnosis: " + diagnosis);
        System.out.println("Doctor: " + doctor);
        System.out.println("Treatment: " + treatment);
    }

    public String toCSV() {
        return String.join(",", date, diagnosis, doctor, treatment);
    }
}

class Patient {
    private int id;
    private String name;
    private List<HealthReport> reports;
    private String contactInfo;

    public Patient(int id, String name, String contactInfo) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.reports = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addReport(HealthReport report) {
        reports.add(report);
    }

    public void displayReports() {
        System.out.println("Patient ID: " + id + ", Name: " + name + ", Contact: " + contactInfo);
        if (reports.isEmpty()) {
            System.out.println("No reports found.");
        } else {
            for (HealthReport report : reports) {
                report.displayReport();
                System.out.println("-------------------------");
            }
        }
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (HealthReport report : reports) {
            sb.append(id).append(",").append(name).append(",").append(contactInfo).append(",").append(report.toCSV()).append("\n");
        }
        return sb.toString();
    }
}

class HealthMonitoringSystem {
    private List<Patient> patients;
    private ExecutorService executorService;

    public HealthMonitoringSystem() {
        patients = new ArrayList<>();
        executorService = Executors.newCachedThreadPool();
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public Patient findPatientById(int id) {
        for (Patient patient : patients) {
            if (patient.getId() == id) {
                return patient;
            }
        }
        return null;
    }

    public List<Patient> findPatientByName(String name) {
        List<Patient> foundPatients = new ArrayList<>();
        for (Patient patient : patients) {
            if (patient.getName().equalsIgnoreCase(name)) {
                foundPatients.add(patient);
            }
        }
        return foundPatients;
    }

    public void addReportToPatient(int id, HealthReport report) {
        Patient patient = findPatientById(id);
        if (patient != null) {
            patient.addReport(report);
            System.out.println("Report added successfully.");
        } else {
            System.out.println("Patient not found.");
        }
    }

    public void displayPatientReports(int id) {
        Patient patient = findPatientById(id);
        if (patient != null) {
            patient.displayReports();
        } else {
            System.out.println("Patient not found.");
        }
    }

    public void exportDataToCSV(String filename) {
        executorService.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (Patient patient : patients) {
                    writer.write(patient.toCSV());
                }
                System.out.println("Data exported successfully to " + filename);
            } catch (IOException e) {
                System.err.println("Error exporting data: " + e.getMessage());
            }
        });
    }

    public void importDataFromCSV(String filename) {
        executorService.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String contactInfo = data[2];
                    String date = data[3];
                    String diagnosis = data[4];
                    String doctor = data[5];
                    String treatment = data[6];

                    Patient patient = findPatientById(id);
                    if (patient == null) {
                        patient = new Patient(id, name, contactInfo);
                        addPatient(patient);
                    }
                    patient.addReport(new HealthReport(date, diagnosis, doctor, treatment));
                }
                System.out.println("Data imported successfully from " + filename);
            } catch (IOException e) {
                System.err.println("Error importing data: " + e.getMessage());
            }
        });
    }

    public void generateSummaryReport() {
        executorService.submit(() -> {
            int totalPatients = patients.size();
            int totalReports = patients.stream().mapToInt(patient -> patient.reports.size()).sum();

            System.out.println("Summary Report:");
            System.out.println("Total Patients: " + totalPatients);
            System.out.println("Total Reports: " + totalReports);
        });
    }
}

public class Main {
    public static void main(String[] args) {
        HealthMonitoringSystem system = new HealthMonitoringSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Patient");
            System.out.println("2. Add Report to Patient");
            System.out.println("3. View Patient Reports");
            System.out.println("4. Search Patients by Name");
            System.out.println("5. Export Data to CSV");
            System.out.println("6. Import Data from CSV");
            System.out.println("7. Generate Summary Report");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter patient ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter patient name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter contact info: ");
                    String contactInfo = scanner.nextLine();
                    system.addPatient(new Patient(id, name, contactInfo));
                    break;
                case 2:
                    System.out.print("Enter patient ID: ");
                    int patientId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter report date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter diagnosis: ");
                    String diagnosis = scanner.nextLine();
                    System.out.print("Enter doctor name: ");
                    String doctor = scanner.nextLine();
                    System.out.print("Enter treatment: ");
                    String treatment = scanner.nextLine();
                    HealthReport report = new HealthReport(date, diagnosis, doctor, treatment);
                    system.addReportToPatient(patientId, report);
                    break;
                case 3:
                    System.out.print("Enter patient ID: ");
                    int reportId = scanner.nextInt();
                    system.displayPatientReports(reportId);
                    break;
                case 4:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter patient name: ");
                    String searchName = scanner.nextLine();
                    List<Patient> foundPatients = system.findPatientByName(searchName);
                    if (foundPatients.isEmpty()) {
                        System.out.println("No patients found with that name.");
                    } else {
                        for (Patient p : foundPatients) {
                            p.displayReports();
                        }
                    }
                    break;
                case 5:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter filename to export data: ");
                    String exportFilename = scanner.nextLine();
                    system.exportDataToCSV(exportFilename);
                    break;
                case 6:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter filename to import data: ");
                    String importFilename = scanner.nextLine();
                    system.importDataFromCSV(importFilename);
                    break;
                case 7:
                    system.generateSummaryReport();
                    break;
                case 8:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
