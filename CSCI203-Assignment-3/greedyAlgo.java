/**
 * @author MinZhan Foo UOW ID: 7058810
 */

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class greedyAlgo {
    private static final ArrayList<Student> studentList = new ArrayList<>();
    private static final ArrayList<String> moduleList = new ArrayList<>();
    private static final ArrayList<String> combination = new ArrayList<>();
    private static final ArrayList<String> finalCombination = new ArrayList<>();
    private static final ArrayList<Slot> slot = new ArrayList<>();
    private static final Map<String, Integer> inconnectivity = new HashMap<>();
    private static Map<String, Long> mapper = new HashMap<>();

    /**
     * Driver code
     * @param args read input file
     * @throws FileNotFoundException throws an error should there be no file
     */
    public static void main(String[] args) throws FileNotFoundException {
        init(args[0]);
        List<String> distinctModule = moduleList.stream().distinct().sorted().collect(Collectors.toList());
        Matrix matrix = new Matrix(distinctModule.size(), distinctModule);
        displayStats(matrix, distinctModule);
        displayGreedy(matrix, distinctModule);
    }

    /**
     * Initialise the input file into an arraylist of students with subjects, then sorting the subjects and
     * displays student information
     * @param args read input file
     * @throws FileNotFoundException throws an error should there be no file
     */
    private static void init(String args) throws FileNotFoundException {
        String line;
        FileReader file = new FileReader(args);
        BufferedReader br = new BufferedReader(file);
        int counter = -1;
        try {
            line = br.readLine();
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line, ", ");
                if (st.countTokens() == 1) {
                    String text = st.nextToken();
                    int tokenLength = text.length();
                    if (tokenLength == 1) {
                        System.out.println("Total number of students: " + Integer.parseInt(text));
                    } else if (tokenLength > 1) {
                        studentList.get(counter).setSubject(text);
                        moduleList.add(text);
                    }
                } else if (st.countTokens() > 1) {
                    counter++;
                    studentList.add(new Student(st.nextToken(), Integer.parseInt(st.nextToken())));
                }
                line = br.readLine();
            }
            br.close();
            file.close();
            studentList.forEach(student -> {
                Collections.sort(student.getSubjectList());
                System.out.println(student);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the combination of different subject for each student
     * @param inputList student's subject arraylist
     * @param temp      temporary subject arraylist
     * @param start     start index of subject arraylist
     * @param end       end index of subject arraylist
     * @param index     start index to loop the arraylist
     * @param r         C(n r) n choose r elements
     */
    private static void getCombination(ArrayList<String> inputList, ArrayList<String> temp, int start, int end, int index, int r) {
        if (index == r) {
            StringBuilder value = new StringBuilder();
            for (int i = 0; i < r; i++) value.append(temp.get(i)).append(" ");
            value.setLength(value.length() -1);
            finalCombination.add(value.toString());
            return;
        }

        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            temp.add(index, inputList.get(i));
            getCombination(inputList, temp, i + 1, end, index + 1, r);
        }
    }

    /**
     * Displays the adjacency matrix and inconnectivity value ranging from highest to lowest
     * @param matrix Matrix object to add edges as required
     * @param distinctModule arraylist of distinct module base on all student's subject list
     */
    private static void displayStats(Matrix matrix, List<String> distinctModule) {
        studentList.forEach(student -> getCombination(student.getSubjectList(), combination, 0, student.getSubjectList().size() - 1, 0, 2));
        mapper = finalCombination.stream().sorted().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        studentList.forEach(student -> {
            for (int i = 0; i < student.getSubjectList().size(); i++) {
                matrix.addEdge(distinctModule.indexOf(student.getSubjectList().get(i)), distinctModule.indexOf(student.getSubjectList().get(i)));
                for (int j = 1; j < student.getSubjectList().size(); j++) {
                    String subjectCode = student.getSubjectList().get(i) + " " + student.getSubjectList().get(j);
                    int finalI = i, finalJ = j;
                    mapper.forEach((key, value) -> {
                        if (subjectCode.equals(key))
                            matrix.addEdge(distinctModule.indexOf(student.getSubjectList().get(finalI)), distinctModule.indexOf(student.getSubjectList().get(finalJ)));
                    });
                }
            }
        });

        for (int i = 0; i < matrix.getArray().length; i++) {
            int counter = 0;
            for (int j = 0; j < matrix.getArray().length; j++) {
                if (matrix.getArray()[i][j] == 0) {
                    counter++;
                }
                inconnectivity.put(distinctModule.get(i), counter);
            }
        }
        List<Map.Entry<String, Integer>> inconnectivityList = new ArrayList<>(inconnectivity.entrySet());
        inconnectivityList.sort(Map.Entry.comparingByValue());
        Collections.reverse(inconnectivityList);

        System.out.println(matrix);
        System.out.println("Number of inconnectivity (Highest):\n" + inconnectivityList + "\n");
    }

    /**
     * Greedy algorithm, to select the optimal solution to solve exam schedule's imposed constraint and display
     * the final outcome
     * @param matrix Matrix object to remove edges as required
     * @param distinctModule arraylist of distinct module base on all student's subject list
     */
    private static void displayGreedy(Matrix matrix, List<String> distinctModule) {
        for (int i = 0; i < matrix.getArray().length; i++) {
            for (int j = 0; j < matrix.getArray().length; j++) {
                if (matrix.getArray()[i][j] == 0) {
                    slot.add(new Slot(distinctModule.get(i), distinctModule.get(j)));
                    matrix.removeEdge(i, j);
                }
            }
        }

        List<String> examModule = slot.stream().map(Slot::getModule1).collect(Collectors.toList());
        List<String> examModule2 = slot.stream().map(Slot::getModule2).collect(Collectors.toList());
        distinctModule.removeAll(examModule);
        distinctModule.removeAll(examModule2);
        if (distinctModule.size() == 1) slot.add(new Slot(distinctModule.get(0), " "));

        slot.forEach(slot -> studentList.forEach(student -> student.getSubjectList().forEach(subject -> {
            if (subject.equals(slot.getModule1())) slot.setStudent(student.getName());
            if (subject.equals(slot.getModule2())) slot.setStudent(student.getName());
        })));

        for (int i = 0; i < slot.size(); i++) {
            System.out.printf("Slot %d: ", i + 1);
            slot.get(i).printIt(i);
        }
    }
}

class Student {
    private final ArrayList<String> subjectList = new ArrayList<>();
    private final String name;
    private final int numOfSub;

    public Student(String name, int numOfSub) {
        this.name = name;
        this.numOfSub = numOfSub;
    }

    public String getName() {
        return name;
    }

    public void setSubject(String subject) {
        subjectList.add(subject);
    }

    public ArrayList<String> getSubjectList() {
        return subjectList;
    }

    @Override
    public String toString() {
        return String.format("Name: %s %nNumber of subjects: %d%nSubjects: %s%n", name, numOfSub, subjectList);
    }
}

class Matrix {
    private final int[][] array;
    private final List<String> moduleName;

    public Matrix(int modules, List<String> moduleName) {
        this.array = new int[modules][modules];
        this.moduleName = moduleName;
    }

    public void addEdge(int i, int j) {
        this.array[i][j] += 1;
        this.array[j][i] = array[i][j];
    }

    public void removeEdge(int i, int j) {
        this.array[i][j] = -1;
        this.array[j][i] = -1;
        for (int x = i; x < this.array.length; x++) {
            if (this.array[x][j] == 0) {
                this.array[x][j] = -1;
            }
        }
        for (int y = j; y < this.array.length; y++) {
            if (this.array[j][y] == 0) {
                this.array[j][y] = -1;
            }
        }
    }

    public int[][] getArray() {
        return array;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Adjacency Matrix\n").append("TO \\ FROM").append("|");
        moduleName.forEach(name -> string.append("  ").append(name).append("\t ").append("|"));
        string.append("\n");

        for (int i = 0; i < array.length; i++) {
            string.append(" ").append(moduleName.get(i)).append(" |");
            for (int j = 0; j < array.length; j++) {
                string.append(String.format("\t   %d\t ", array[i][j])).append("|");
            }
            string.append("\n");
        }
        return string.toString();
    }
}

class Slot {
    private final String module1;
    private final String module2;
    private final ArrayList<String> studentList;

    public Slot(String module1, String module2) {
        this.module1 = module1;
        this.module2 = module2;
        this.studentList = new ArrayList<>();
    }

    public String getModule1() {
        return module1;
    }

    public String getModule2() {
        return module2;
    }

    public void setStudent(String student) {
        studentList.add(student);
    }

    public ArrayList<String> getStudent() {
        return studentList;
    }

    public void printIt (int i) {
        if (getModule2().equals(" ")) {
            System.out.printf("%s ", getModule1());
            System.out.printf("%nTotal number of students in Slot %d: %d%n", i + 1, getStudent().size());
            System.out.printf("Students in Slot %d: %s%n%n", i + 1, getStudent());
        } else {
            System.out.println(getModule1() + " " + getModule2());
            System.out.printf("Total number of students in Slot %d: %d%n", i + 1,getStudent().size());
            System.out.printf("Students in Slot %d: %s%n%n", i + 1, getStudent());
        }
    }
}
