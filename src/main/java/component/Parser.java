package component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mew.MewDateTimeParseException.InputOverFlowException;
import mew.MewDateTimeParseException.InvalidDateTimeFormatException;

/**
 * Parser class that parses user input.
 */
public class Parser {
    /**
     * Parses input and returns Command enumeration.
     * @param input Input of user
     * @return Type of input
     */
    public static Command parse(String input) {
        assert (input.length() > 0) : "Empty input";
        List<String> inputList = Stream.of(input.split(" "))
                .map(e -> new String(e))
                .collect(Collectors.toList());
        String command = inputList.get(0);
        switch (command) {
        case "event":
            return Command.CREATE_EVENT;
        case "deadline":
            return Command.CREATE_DEADLINE;
        case "todo":
            return Command.CREATE_TODO;
        case "delete":
            return Command.DELETE;
        case "mark":
            return Command.MARK;
        case "unmark":
            return Command.UNMARK;
        case "list":
            return Command.LIST;
        case "find":
            return Command.FIND;
        case "bye":
            return Command.EXIT;
        default:
            return Command.UNKNOWN;
        }
    }

    /**
     * Returns index of the intended task from command input string
     * @param input String input
     * @return integer index of task
     */
    public static int getTaskIndex(String input) {
        return Integer.parseInt(input.substring(input.length() - 1)) - 1;
    }
    public static String getKeyword(String input) {
        return input.substring(6);
    }

    /**
     * Returns a Task object according to the code of the task.
     * "D" is for deadline, "T" is for todo, and "E" is for event.
     * @param input String input
     * @param code String code of task
     * @return parsed Task object
     */
    public static Task parseTask(String input, String code)
            throws InputOverFlowException, InvalidDateTimeFormatException, DateTimeParseException {
        assert code.length() == 1 : "Invalid task code";
        Task newTask = null;
        if (code.equals("T")) {
            String description = input.substring(5);
            newTask = new ToDo(description);
            return newTask;
        }
        String separator = code.equals("E") ? "/at" : "/by";
        int indexOfDateTime = input.indexOf(separator);
        if (indexOfDateTime == -1) {
            throw new DateTimeParseException("No date given", input, input.length() - 1);
        }
        String stringDateTime = input.substring(indexOfDateTime + 4);
        LocalDateTime dateTime = Parser.processDateTime(stringDateTime);
        String description;
        if (code.equals("E")) {
            description = input.substring(6, indexOfDateTime - 1);
            newTask = new Event(dateTime, description);
        } else if (code.equals("D")) {
            description = input.substring(9, indexOfDateTime - 1);
            newTask = new Deadline(dateTime, description);
        }
        return newTask;
    }

    /**
     * Parses LocalDateTime object from a String formatted date time.
     * @param stringDateTime String formatted date and time
     * @return LocalDateTime parsed date time
     */
    public static LocalDateTime processDateTime(String stringDateTime)
            throws InputOverFlowException, InvalidDateTimeFormatException, DateTimeParseException {
        System.out.println(stringDateTime);
        String date = "None";
        String time = "None";
        if (stringDateTime.length() > 9) { // date and time and given
            if (stringDateTime.length() > 13) {
                throw new InputOverFlowException("Date time input too long");
            }
            date = stringDateTime.substring(0, stringDateTime.indexOf(" "));
            time = stringDateTime.substring(stringDateTime.indexOf(" ") + 1);
        } else if (stringDateTime.length() == 8) { // date given only
            date = stringDateTime;
        } else if (stringDateTime.length() <= 4 && stringDateTime.length() >= 1) { // time given only
            System.out.println("this condition");
            time = stringDateTime;
        } else if (stringDateTime.length() == 0) {
            throw new DateTimeParseException("Empty input", stringDateTime, 0);
        } else {
            throw new InvalidDateTimeFormatException("Invalid input");
        }
        if (!date.equals("None") && !time.equals("None")) { // date and time are given
            System.out.println("date and time are given");
            DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HHmm");
            LocalDate localDate = LocalDate.parse(date, formatDate);
            LocalTime localTime = LocalTime.parse(time, formatTime);
            return LocalDateTime.of(localDate, localTime);
        } else if (!date.equals("None")) { // just the date is given
            System.out.println("Just date is given");
            DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HHmm");
            LocalDate localDate = LocalDate.parse(date, formatDate);
            LocalTime localTime = LocalTime.parse("0000", formatTime);
            return LocalDateTime.of(localDate, localTime);
        } else { // just the time is given
            System.out.println("Just time is given");
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HHmm");
            LocalTime localTime = LocalTime.parse(time, formatTime);
            return LocalDateTime.of(LocalDate.now(), localTime);
        }
    }
}