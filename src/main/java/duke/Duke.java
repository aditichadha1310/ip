package duke;

import exception.DukeException;
import exception.EmptyTaskDescriptionException;
import exception.NoTaskFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Duke {
    /**
     * Task type Array list to store the tasks the user will create
     */
    private static ArrayList<Task> scheduledTasks = new ArrayList<>();
    private static final String FILE_PATH = "duke.txt";
    public static final String GREET_MESSAGE = "Hello! I'm Duke\nWhat can I do for you?";
    public static final String EXIT_MESSAGE = " Bye. Hope to see you again soon!";
    public static final String HORIZONTAL_LINE = "____________________________________________________________";
    public static final String TASK_COMPLETED = "1";
    public static final String TASK_INCOMPLETE = "0";
    public static final String TASK_MARKED_COMPLETE = "X";

    public static final String COMMAND_LIST = "list";
    public static final String COMMAND_BYE = "bye";
    public static final String COMMAND_DONE = "done";
    public static final String COMMAND_DELETE = "delete";
    public static final String ERROR_INVALID_TASK_STATEMENT = " ☹ OOPS!!! I'm sorry, but I don't know what that means :-(";
    public static final String ERROR_INVALID_TASK_NUMBER = "Sorry, no task is assigned at this number, you might want to re-check?";
    public static final String ERROR_EMPTY_TASKLIST = "Sorry, no tasks have been added to the list as yet!\n" +
            "You can add tasks to this list simply by typing and pressing \"Enter\"!!";

    public static final String INITIAL_TODO = "T";
    public static final String INITIAL_DEADLINE = "D";
    public static final String INITIAL_EVENT = "E";


    public static final String DELIMITER_ARROW = "=>";
    public static final String DELIMITER_SPACE = " ";
    public static final String DELIMITER_FORWARD_SLASH = "/";
    public static final String DELIMITER_BY = "/by";
    public static final String DELIMITER_AT = "/at";
    public static final String DELIMITER_DOT = ".";

    public static final String MESSAGE_TASK_REMOVED = "Noted. I've removed this task:";
    public static final String MESSAGE_TASK_ADDED = "Got it. I've added this task:";
    public static final String MESSAGE_LIST_ALL_TASKS = "Here are the tasks in your list:";
    public static final String MESSAGE_TASK_MARKED_DONE = "Nice! I have marked this task as done:";
    

    /**
     * This is the main function responsible for the execution of this program
     */
    public static void main(String[] args) {
        loadData();
        greet();
        runDuke();
        greetBye();
    }

    /**
     * Greets the user by printing some introductory messages
     */
    private static void greet() {
        printLine();
        System.out.println(GREET_MESSAGE);
        printLine();
    }

    /**
     * Prints a line on the screen
     */
    public static void printLine() {
        System.out.println(HORIZONTAL_LINE);
    }


    public static void loadData() {
        try {
            loadPreviousData();
        } catch (FileNotFoundException e) {
            File file = new File(FILE_PATH);
            try {
                file.createNewFile();
            } catch (IOException ee) {
                System.out.println("Cannot create a new file");
            }
        }
    }

    public static void loadPreviousData() throws FileNotFoundException {
        File file = new File(FILE_PATH);
        Scanner sc = new Scanner(file);

        while (sc.hasNext()) {
            loadSavedTasksToList(sc.nextLine());
        }
    }

    public static void saveTaskToDisk() throws IOException {
        FileWriter fw = new FileWriter(FILE_PATH);
        String lineToWrite = "";
        for (Task task : scheduledTasks) {
            lineToWrite = "";
            if (task.taskType == TaskType.TODO) {
                lineToWrite = INITIAL_TODO + DELIMITER_SPACE;
            } else if (task.taskType == TaskType.DEADLINE) {
                lineToWrite = INITIAL_DEADLINE + DELIMITER_SPACE;
            } else {
                lineToWrite = INITIAL_EVENT + DELIMITER_SPACE;
            }

            lineToWrite = lineToWrite + DELIMITER_ARROW + DELIMITER_SPACE;
            String taskStatus = task.getStatus();

            if (taskStatus.equals(TASK_MARKED_COMPLETE)) {
                lineToWrite = lineToWrite + TASK_COMPLETED;
            } else {
                lineToWrite = lineToWrite + TASK_INCOMPLETE;
            }

            String taskDescription = task.description;

            lineToWrite = lineToWrite + " => " + taskDescription;
            if (task instanceof Deadline) {
                lineToWrite = lineToWrite + " => " + ((Deadline) task).by;
            } else if (task instanceof Event) {
                lineToWrite = lineToWrite + " => " + ((Event) task).at;
            }

            lineToWrite += "\n";
            fw.write(lineToWrite);

        }
        fw.close();
    }

    public static void callSaveTaskToList() {
        try {
            saveTaskToDisk();
        } catch (IOException e) {
            System.out.println("Unable to save data to the disk");
        }
    }


    public static void loadSavedTasksToList(String input) {
        String[] splitInput = input.split(DELIMITER_ARROW);
        String taskType = splitInput[0].trim();
        String taskStatus = splitInput[1].trim();
        String taskDescription = splitInput[2].trim();

        switch (taskType) {
        case INITIAL_TODO:
            scheduledTasks.add(new Todo(taskDescription));
            break;
        case INITIAL_DEADLINE:
            String timeDueBy = splitInput[3];
            scheduledTasks.add(new Deadline(taskDescription, timeDueBy));
            break;
        case INITIAL_EVENT:
            String timeDueAt = splitInput[3];
            scheduledTasks.add(new Event(taskDescription, timeDueAt));
            break;
        default:
        }

        if (taskStatus.equals(TASK_COMPLETED)) {
            scheduledTasks.get(scheduledTasks.size() - 1).markAsDone();
        }

    }

    /**
     * Takes in input from the user and executes the given instructions.
     * If user input is "list", it calls the list() function to list all the tasks.
     * If user input is done x, where x is a valid task number, it calls the MarkTaskAsDone() function to mark the task as done.
     * If user has scheduled a task by writing a statement beginning with "event", "deadline" or "todo",
     * then it adds the task in the tasks list by calling the addTaskToList() function.
     *
     * @param userInput            userInput stores the input String entered by the user.
     * @param taskCompletionStatus taskCompletionStatus Stores the status of the task, true if completed, false otherwise.
     */
    private static void runDuke() {
        int i;
        String userInput;
        String taskCompletionStatus = "";
        Scanner in = new Scanner(System.in);
        userInput = in.nextLine();


        while (!(userInput.equalsIgnoreCase(COMMAND_BYE))) {
            try {
                if (userInput.equalsIgnoreCase(COMMAND_LIST)) {
                    list();
                } else {
                    if (userInput.startsWith(COMMAND_DONE)) {
                        markTaskAsDone(userInput);
                    } else {
                        if (userInput.startsWith(COMMAND_DELETE)) {
                            deleteTask(userInput);
                        } else {
                            addTaskToList(userInput);
                        }
                    }
                }
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            } catch (IOException ee) {
                System.out.println("Could not save data to file");
            } catch (NoTaskFoundException eg) {
                System.out.println(eg.getMessage());
            } catch (EmptyTaskDescriptionException e) {
                System.out.println(e.getMessage());
            }
            printLine();
            userInput = in.nextLine();
        }

    }

    /**
     * Greets the user goodbye and the code finishes its execution.
     */
    private static void greetBye() {
        printLine();
        System.out.println(EXIT_MESSAGE);
        printLine();
    }

    /**
     * Adds the task to the task list if it is a valid task creation statement given by the user.
     * Displays appropriate message if task is valid and is created successfully, vice versa.
     * Displays the total number of tasks in the list as well.
     *
     * @param isTaskValid IsTaskValid stores true if the task statement entered by the user is a valid task creation statement and false, otherwise.
     * @param index       Index stores the index of the "/" in the entered String
     */
    private static void addTaskToList(String userInput) throws DukeException, EmptyTaskDescriptionException {

        int index;
        boolean isTaskValid = true;
        String firstWord;
        String[] split = userInput.split(DELIMITER_SPACE, 2);
        String taskDescription;
        String timeDueAt;
        String timeDueBy;
        firstWord = split[0].toLowerCase();

        switch (firstWord) {
        case "todo":
            if (split.length < 2 || split[1].isEmpty()) {
                throw new EmptyTaskDescriptionException("☹ OOPS!!! The description of a todo cannot be empty.");
            } else {
                scheduledTasks.add(new Todo(split[1]));
            }
            callSaveTaskToList();
            break;

        case "deadline":
            index = userInput.indexOf(DELIMITER_FORWARD_SLASH);
            if (split.length < 2 || split[1].isEmpty() == true || index == -1) {
                throw new EmptyTaskDescriptionException("☹ OOPS!!! The description or the deadline of the task cannot be empty.");
            }
            int indexOfSpace = userInput.indexOf(DELIMITER_SPACE) + 1;
            taskDescription = split[1].split(DELIMITER_BY, 2)[0];
            timeDueBy = split[1].split(DELIMITER_BY, 2)[1];
            if (taskDescription.isEmpty() || timeDueBy.isEmpty()) {
                throw new DukeException("☹ OOPS!!! The description of the task seems incomplete.");
            }
            scheduledTasks.add(new Deadline(userInput.substring(indexOfSpace, index), userInput.substring(index + 3)));
            callSaveTaskToList();
            break;

        case "event":
            index = userInput.indexOf(DELIMITER_FORWARD_SLASH);
            if (split.length < 2 || split[1].isEmpty() || index == -1) {
                throw new EmptyTaskDescriptionException("☹ OOPS!!! The description and time schedule of the event cannot be empty.");
            }
            indexOfSpace = userInput.indexOf(DELIMITER_SPACE) + 1;
            taskDescription = split[1].split(DELIMITER_AT, 2)[0];
            timeDueAt = split[1].split(DELIMITER_AT, 2)[1];
            if (taskDescription.isEmpty() || timeDueAt.isEmpty()) {
                throw new DukeException("☹ OOPS!!! The description or time schedule of the event seems incomplete.");
            }
            scheduledTasks.add(new Event(userInput.substring(indexOfSpace, index), userInput.substring(index + 3)));
            callSaveTaskToList();
            break;

        default:
            isTaskValid = false;
            throw new DukeException(ERROR_INVALID_TASK_STATEMENT);
        }

        if (isTaskValid) {
            printLine();

            System.out.println(MESSAGE_TASK_ADDED);
            System.out.println(DELIMITER_SPACE + scheduledTasks.get(scheduledTasks.size() - 1));
            System.out.println("Now you have " + scheduledTasks.size() + " tasks in the list.");
        }
    }


    /**
     * Updates the status of the task by marking it as done in the task list.
     *
     * @param taskNumberCompleted TaskNumberCompleted stores the task number which has been completed by the user.
     */
    private static void markTaskAsDone(String userInput) throws NoTaskFoundException {
        printLine();
        int taskNumberCompleted = Integer.parseInt(userInput.substring(userInput.indexOf(" ") + 1));

        if ((taskNumberCompleted <= scheduledTasks.size()) && (taskNumberCompleted > 0)) {
            scheduledTasks.get(taskNumberCompleted - 1).markAsDone();
            System.out.println(MESSAGE_TASK_MARKED_DONE);
            System.out.println(scheduledTasks.get(taskNumberCompleted - 1));
            callSaveTaskToList();
        } else {
            throw new NoTaskFoundException(ERROR_INVALID_TASK_NUMBER);
        }
    }


    /**
     * Deletes the task from the task list.
     *
     * @param deleteTask DeleteTask stores the task number which is supposed to be deleted.
     */
    private static void deleteTask(String userInput) throws IOException, NoTaskFoundException {
        printLine();
        int deleteTask = Integer.parseInt(userInput.substring(userInput.indexOf(" ") + 1));

        if ((deleteTask <= scheduledTasks.size()) && (deleteTask > 0)) {
            Task taskToBeDeleted = scheduledTasks.get(deleteTask - 1);
            System.out.println(MESSAGE_TASK_REMOVED);
            System.out.println(taskToBeDeleted
            );
            scheduledTasks.remove(deleteTask - 1);
            System.out.println("Now you have " + scheduledTasks.size() + " tasks in the list.");
            saveTaskToDisk();
        } else {
            throw new NoTaskFoundException(ERROR_INVALID_TASK_NUMBER);
        }
    }


    /**
     * Lists all the tasks in the task list along with their task completion status.
     * The tasks are enlisted which reveal if they are a "todo", "event" or a "deadline".
     *
     * @param taskCompletionStatus TaskCompletionStatus stores true if the task is completed, false otherwise.
     */
    private static void list() throws NoTaskFoundException {
        int i;
        String taskCompletionStatus;
        printLine();
        if (scheduledTasks.size() == 0) {
            throw new NoTaskFoundException(ERROR_EMPTY_TASKLIST);
        } else {
            System.out.println(MESSAGE_LIST_ALL_TASKS);
            i = 0;
            for (Task task : scheduledTasks) {
                taskCompletionStatus = task.getStatus();
                System.out.print((i + 1) + DELIMITER_DOT);
                System.out.println(task);
                i++;
            }
        }
    }
}
