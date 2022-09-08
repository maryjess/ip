package mew;

import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.Timer;
import java.util.TimerTask;

import component.Command;
import component.Parser;
import component.Storage;
import component.Task;
import component.TaskList;
import component.Ui;
import javafx.application.Platform;
import mew.MewDateTimeParseException.InputOverFlowException;
import mew.MewDateTimeParseException.InvalidDateTimeFormatException;

/**
 * Main class Mew that runs the application.
 */
public class Mew {
    private final Storage storage;
    private TaskList tasks;

    /**
     * Constructs a new class Mew.
     * @param filePath Path of the storage.
     */
    public Mew(Path filePath) {
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        tasks = new TaskList();
    }

    /**
     * Checks whether the input given is an end input.
     * @param input Input given by the user
     * @return Boolean result of whether input is an end input or not
     */
    public boolean isEndInput(String input) {
        return Parser.parse(input).equals(Command.EXIT);
    }

    /**
     * Processes the response given from an input.
     * @param input Input given by the user
     * @return Response of Mew
     */
    public String processResponse(String input) {
        if (Parser.parse(input).equals(Command.LIST)) {
            return Ui.showTaskList(tasks);
        } else if (Parser.parse(input).equals(Command.MARK)) {
            try {
                int taskIndex = Parser.getTaskIndex(input);
                Task markedTask = tasks.markTask(taskIndex - 1);
                storage.saveData(tasks);
                return Ui.showMarkDone(markedTask);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                return Ui.askWhichTask();
            } catch (Exception e) {
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.UNMARK)) {
            try {
                int taskIndex = Parser.getTaskIndex(input);
                Task unmarkedTask = tasks.unmarkTask(taskIndex - 1);
                storage.saveData(tasks);
                return Ui.showUnmarkAsDone(unmarkedTask);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                return Ui.askWhichTask();
            } catch (Exception e) {
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.DELETE)) {
            try {
                int taskIndex = Parser.getTaskIndex(input);
                Task deletedTask = tasks.deleteTask(taskIndex - 1);
                storage.saveData(tasks);
                return Ui.showDeleteTaskDone(deletedTask, tasks.getNumberOfTasks());
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                return Ui.askWhichTask();
            } catch (Exception e) {
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.CREATE_EVENT)) {
            try {
                Task newTask = Parser.parseTask(input, "E");
                tasks.addTask(newTask);
                storage.saveData(tasks);
                return Ui.showAddTaskDone(newTask, tasks.getNumberOfTasks());
            } catch (InvalidDateTimeFormatException | InputOverFlowException e) {
                System.out.println("input datetime trouble");
                return Ui.showInvalidDatetimeInput();
            } catch (StringIndexOutOfBoundsException | DateTimeParseException e) {
                System.out.println("empty description event");
                return Ui.showEmptyEventDescriptionWarning();
            } catch (Exception e) {
                System.out.println("other exceptions");
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.CREATE_DEADLINE)) {
            try {
                Task newTask = Parser.parseTask(input, "D");
                tasks.addTask(newTask);
                storage.saveData(tasks);
                return Ui.showAddTaskDone(newTask, tasks.getNumberOfTasks());
            } catch (StringIndexOutOfBoundsException e) {
                return Ui.showEmptyDeadlineDescriptionWarning();
            } catch (InvalidDateTimeFormatException | InputOverFlowException | DateTimeParseException e) {
                return Ui.showInvalidDatetimeInput();
            } catch (Exception e) {
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.CREATE_TODO)) {
            try {
                Task newTask = Parser.parseTask(input, "T");
                tasks.addTask(newTask);
                storage.saveData(tasks);
                return Ui.showAddTaskDone(newTask, tasks.getNumberOfTasks());
            } catch (StringIndexOutOfBoundsException e) {
                return Ui.showEmptyToDoDescriptionWarning();
            } catch (InvalidDateTimeFormatException | InputOverFlowException | DateTimeParseException e) {
                return Ui.showInvalidDatetimeInput();
            } catch (Exception e) {
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.FIND)) {
            String keyword = Parser.getKeyword(input);
            TaskList foundTasks = tasks.findTasks(keyword);
            return Ui.showFoundTasks(foundTasks);
        } else if (Parser.parse(input).equals(Command.EDIT)) {
            try {
                String newDescription = Parser.getNewDescription(input);
                int taskIndex = Parser.getTaskIndex(input);
                Task editedTask = tasks.editTaskDescription(taskIndex - 1, newDescription);
                return Ui.showEditTaskDone(editedTask);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                return Ui.askWhichTask();
            } catch (Exception e) {
                return Ui.printException(e);
            }
        } else if (Parser.parse(input).equals(Command.UNKNOWN)) {
            return Ui.showUnknownCommand();
        }
        storage.saveData(tasks);
        return Ui.showExitStatement();
    }

    /**
     * Ends the application.
     */
    public void endApplication() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.exit();
                System.exit(0);
            }
        }, 700);
    }
}
